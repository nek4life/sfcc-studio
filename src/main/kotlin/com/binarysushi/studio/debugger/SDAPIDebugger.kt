package com.binarysushi.studio.debugger

import com.binarysushi.studio.cartridges.CartridgePathUtil
import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.javascript.debugger.breakpoints.JavaScriptBreakpointType
import com.intellij.javascript.debugger.breakpoints.JavaScriptLineBreakpointProperties
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SDAPIDebugger(private val session: XDebugSession, private val process: StudioDebugProcess) {
    private val config = StudioConfigurationProvider.getInstance(session.project)
    private val debuggerClient = SDAPIClient(
        config.hostname,
        config.username,
        config.password
    )
    private val idKey: Key<Int> = Key.create("STUDIO_BP_ID")
    private val awaitingBreakpoints = mutableListOf<XLineBreakpoint<JavaScriptLineBreakpointProperties>>()
    private val timeFormat = SimpleDateFormat("hh:mm:ss")

    var connectionState = DebuggerConnectionState.DISCONNECTED
    var processState = DebuggerProcessState.STOPPED
    var currentThreads = ConcurrentHashMap<Int, ScriptThread>()
    var pendingThreads = ConcurrentHashMap<Int, ScriptThread>()

    private companion object {
        const val THREAD_RESET_TIMEOUT = 30000L
        const val THREAD_TIMEOUT = 3000L
    }

    fun addAwaitingBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        awaitingBreakpoints.add(xLineBreakpoint)
    }

    private fun printToConsole(message: String) {
        session.consoleView.print("[${timeFormat.format(Date())}] ${message}\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    fun connect() {
        ApplicationManager.getApplication().executeOnPooledThread {
            // TODO add connection retry here
            debuggerClient.createSession() { response ->
                if (response.isSuccessful) {
                    connectionState = DebuggerConnectionState.CONNECTED
                    processState = DebuggerProcessState.WAITING

                    printToConsole("Setting breakpoints...")

                    for (breakpoint in awaitingBreakpoints) {
                        process.addBreakpoint(breakpoint)
                    }

                    session.reportMessage("Session started", MessageType.INFO)
                    printToConsole("Waiting for hits...")

                    debug()
                } else {
                    session.reportError("Session failed to start")
                    session.stop()
                }
            }
        }
    }

    fun disconnect() {
        ApplicationManager.getApplication().executeOnPooledThread {
            printToConsole("Debugger shutting down...")
            debuggerClient.deleteSession(
                onSuccess = {
                    connectionState = DebuggerConnectionState.DISCONNECTED
                    printToConsole("Debug session has ended")
                    session.reportMessage("Debug session stopped", MessageType.INFO)
                },
                onFailure = {
                    connectionState = DebuggerConnectionState.DISCONNECTED
                    printToConsole("Debug session has ended")
                    session.reportMessage("Debug session stopped", MessageType.INFO)
                }
            )
        }
    }

    private fun debug() {
        ApplicationManager.getApplication().executeOnPooledThread() {
            resetThreads()
        }

        try {
            threadLoop()
        } catch (exception: Exception) {
            session.stop()
        }
    }

    private fun resetThreads() {
        try {
            threadResetLoop()
        } catch (exception: Exception) {
            // TODO figure out if session should stop here as well if there's an error
            println(exception.message)
        }
    }

    private tailrec fun threadResetLoop() {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            debuggerClient.resetThreads()
            Thread.sleep(THREAD_RESET_TIMEOUT)
            threadResetLoop()
        }
    }

    private tailrec fun threadLoop() {
        if (processState == DebuggerProcessState.WAITING) {
            getThreads("threadLoop")
        }
        Thread.sleep(THREAD_TIMEOUT)
        threadLoop()
    }

    private fun suspendDebugger(thread: ScriptThread) {
        ApplicationManager.getApplication().runReadAction {
            val suspendContext = StudioSuspendContext(process, thread, currentThreads)

            val breakpoint =
                findBreakpoint(thread.callStack[0].location.scriptPath, thread.callStack[0].location.lineNumber)
            if (breakpoint != null) {
                session.breakpointReached(breakpoint, null, suspendContext)
            } else {
                session.positionReached(suspendContext)
            }
        }
    }

    private fun getThreads(requestTag: String? = null) {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            // TODO This logic seems prone to race conditions when I set a faster timeout setting
            debuggerClient.getThreads(
                requestTag,
                onSuccess = { threads ->
                    for (thread in threads!!) {
                        if (!currentThreads.containsKey(thread.id) && thread.status == "halted") {
                            currentThreads[thread.id] = thread
                            suspendDebugger(thread)
                        } else if (pendingThreads.containsKey(thread.id) && thread.status == "halted") {
                            currentThreads[thread.id] = thread
                            pendingThreads.remove(thread.id)
                            suspendDebugger(thread)
                        } else if (currentThreads.containsKey(thread.id) && thread.status == "halted" && threads.size > 1 && !session.isSuspended) {
                            // This last condition ensures that if there were multiple threads and a thread reaches the end of its
                            // steps that the remaining threads are caught and suspended. I'm not sure if this is the way most debuggers work or not...
                            suspendDebugger(thread)
                        }
                    }

                    for ((_, value) in currentThreads) {
                        if (!threads.any { it.id == value.id }) {
                            currentThreads.remove(value.id)
                        }
                    }
                },
                onError = {
                    if (it.type == "DebuggerDisabledException") {
                        session.stop()
                        // TODO change these to resources

                        printToConsole("The current debug session has become inactive. Please restart your debug session.\n")
                        session.reportMessage("Please restart debug session\n", MessageType.ERROR)
                    }
                })
        }
    }

    fun resume(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        processState = DebuggerProcessState.EXECUTING
        debuggerClient.cancelRequests("threadLoop")

        debuggerClient.resume(
            scriptThread.id,
            onSuccess = {
                getThreads()
                processState = DebuggerProcessState.WAITING
            },
            onError = {
                processState = DebuggerProcessState.WAITING
            },
            onFailure = {
                processState = DebuggerProcessState.WAITING
            }
        )
    }

    fun stepInto(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        processState = DebuggerProcessState.EXECUTING
        debuggerClient.cancelRequests("threadLoop")
        debuggerClient.stepInto(scriptThread.id, onSuccess = {
            getThreads()
            processState = DebuggerProcessState.WAITING
        }, onError = {
            processState = DebuggerProcessState.WAITING
        }, onFailure = {
            processState = DebuggerProcessState.WAITING
        })
    }

    fun stepOver(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        processState = DebuggerProcessState.EXECUTING
        debuggerClient.cancelRequests("threadLoop")
        debuggerClient.stepOver(scriptThread.id, onSuccess = {
            getThreads()
            processState = DebuggerProcessState.WAITING
        }, onError = {
            processState = DebuggerProcessState.WAITING
        }, onFailure = {
            processState = DebuggerProcessState.WAITING
        })
    }

    fun stepOut(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        processState = DebuggerProcessState.EXECUTING
        debuggerClient.cancelRequests("threadLoop")
        debuggerClient.stepOut(scriptThread.id, onSuccess = {
            getThreads()
            processState = DebuggerProcessState.WAITING
        }, onError = {
            processState = DebuggerProcessState.WAITING
        }, onFailure = {
            processState = DebuggerProcessState.WAITING
        })
    }

    private fun findBreakpoint(scriptPath: String, lineNumber: Int): XLineBreakpoint<out XBreakpointProperties<Any>>? {
        val manager = XDebuggerManager.getInstance(session.project).breakpointManager
        val type: XLineBreakpointType<*>? = XDebuggerUtil.getInstance().findBreakpointType(
            JavaScriptBreakpointType::class.java
        )

        if (type != null) {
            val breakpoints = manager.getBreakpoints(type)
            for (breakpoint in breakpoints) {
                if (breakpoint.fileUrl.contains(scriptPath) && breakpoint.line == lineNumber - 1) {
                    return breakpoint
                }
            }
        }
        return null
    }

    fun addBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            val line = xLineBreakpoint.line
            val cartridgeRootPath =
                CartridgePathUtil.getCartridgeRootPathForFile(session.project, xLineBreakpoint.presentableFilePath)

            // TODO Add messaging about breakpoint not set if file is not part of current cartridge root settings
            if (cartridgeRootPath != null) {
                // Replace is for Windows...
                val filePath = xLineBreakpoint.presentableFilePath.replace("\\", "/")


                debuggerClient.createBreakpoint(line + 1, "/$filePath", onSuccess = { breakpoint ->
                    xLineBreakpoint.putUserData(idKey, breakpoint.id!!)
                    session.setBreakpointVerified(xLineBreakpoint)
                    session.updateBreakpointPresentation(
                        xLineBreakpoint,
                        AllIcons.Debugger.Db_verified_breakpoint,
                        null
                    )

                    val virtualFile = VirtualFileManager.getInstance().findFileByUrl(xLineBreakpoint.fileUrl)
                    val fileHyperLink = OpenFileHyperlinkInfo(session.project, virtualFile!!, xLineBreakpoint.line)

                    session.consoleView.print("[${timeFormat.format(Date())}] ", ConsoleViewContentType.NORMAL_OUTPUT)
                    session.consoleView.print("Listening on: ", ConsoleViewContentType.NORMAL_OUTPUT)
                    session.consoleView.printHyperlink("${filePath}#${xLineBreakpoint.line + 1}", fileHyperLink)
                    session.consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
                }, onError = {
                    if (it.type == "DebuggerDisabledException") {
                        session.stop()
                        printToConsole("The current debug session has become inactive. Please restart your debug session.\n")
                        session.reportMessage("Please restart debug session\n", MessageType.ERROR)
                    }
                })
            }
        } else {
            awaitingBreakpoints.add(xLineBreakpoint)
        }
    }

    fun removeBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            val breakpointId = xLineBreakpoint.getUserData(idKey)
            if (breakpointId != null) {
                debuggerClient.deleteBreakpoint(breakpointId)
            }
        }
    }
}

enum class DebuggerConnectionState {
    CONNECTED, DISCONNECTED
}

enum class DebuggerProcessState {
    STOPPED, WAITING, EXECUTING
}
