package com.binarysushi.studio.debugger

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.javascript.debugger.breakpoints.JavaScriptBreakpointType
import com.intellij.javascript.debugger.breakpoints.JavaScriptLineBreakpointProperties
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.Key
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import java.nio.file.Paths


class SDAPIDebugger(private val session: XDebugSession, private val process: StudioDebugProcess) {
    private val config = session.project.service<StudioConfigurationProvider>()
    private val debuggerClient = SDAPIClient(
        config.hostname,
        config.username,
        config.password
    )
    private val idKey: Key<Int> = Key.create("STUDIO_BP_ID")
    private val awaitingBreakpoints = mutableListOf<XLineBreakpoint<JavaScriptLineBreakpointProperties>>()

    var connectionState = DebuggerConnectionState.DISCONNECTED;
    var currentThreads = HashMap<Int, ScriptThread>()
    var pendingThreads = HashMap<Int, ScriptThread>()

    private companion object {
        const val THREAD_RESET_TIMEOUT = 30000L
        const val THREAD_TIMEOUT = 3000L
    }

    fun addAwaitingBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        awaitingBreakpoints.add(xLineBreakpoint)
    }

    fun connect() {
        ApplicationManager.getApplication().executeOnPooledThread {
            // TODO add retry here
            // TODO investigate the need to kill existing client before creating a new one
            debuggerClient.createSession() { response ->
                if (response.isSuccessful) {
                    connectionState = DebuggerConnectionState.CONNECTED;

                    session.consoleView.print("Settings breakpoints...\n", ConsoleViewContentType.NORMAL_OUTPUT)

                    for (breakpoint in awaitingBreakpoints) {
                        process.addBreakpoint(breakpoint)
                    }

                    session.reportMessage("Session started", MessageType.INFO)
                    session.consoleView.print("Waiting for hits...\n", ConsoleViewContentType.NORMAL_OUTPUT)

                    debug()
                } else {
                    session.reportError("Session failed to start")
                    session.stop()
                }
            };
        }
    }

    public fun disconnect() {
        ApplicationManager.getApplication().executeOnPooledThread {
            session.consoleView.print("Debugger shutting down...\n", ConsoleViewContentType.NORMAL_OUTPUT)
            debuggerClient.deleteSession(onSuccess = {
                connectionState = DebuggerConnectionState.DISCONNECTED
                session.consoleView.print("Debug session has ended\n", ConsoleViewContentType.NORMAL_OUTPUT)
                session.reportMessage("Debug session stopped", MessageType.INFO)
            }, onFailure = {
                connectionState = DebuggerConnectionState.DISCONNECTED
                session.consoleView.print("Debug session has ended\n", ConsoleViewContentType.NORMAL_OUTPUT)
                session.reportMessage("Debug session stopped", MessageType.INFO)
            })
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
        getThreads()
        Thread.sleep(THREAD_TIMEOUT)
        threadLoop()
    }


    private fun suspendDebugger(thread: ScriptThread) {
        ApplicationManager.getApplication().runReadAction {
            val suspendContext = StudioSuspendContext(process, thread)
            val breakpoint =
                findBreakpoint(thread.callStack[0].location.scriptPath, thread.callStack[0].location.lineNumber)
            if (breakpoint != null) {
                session.breakpointReached(breakpoint, null, suspendContext)
            } else {
                session.positionReached(suspendContext)
            }
        }
    }

    private fun getThreads() {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            // TODO This logic seems prone to race conditions when I set a faster timeout setting
            debuggerClient.getThreads(onSuccess = { threads ->
                if (threads != null) {
                    for (thread in threads) {
                        if (!currentThreads.containsKey(thread.id) && thread.status == "halted") {
                            suspendDebugger(thread)
                            currentThreads[thread.id] = thread
                        } else if (pendingThreads.containsKey(thread.id) && thread.status == "halted") {
                            suspendDebugger(thread)
                            currentThreads[thread.id] = thread;
                            this.pendingThreads.remove(thread.id);
                        }
                    }

                    for ((_, value) in currentThreads) {
                        val currentThreadId = value.id
                        if (!threads.any { it.id == currentThreadId }) {
                            currentThreads.remove(currentThreadId)
                        }
                    }
                }
            }, onError = {
                if (it.type == "DebuggerDisabledException") {
                    session.stop()
                    // TODO change these to resources
                    session.consoleView.print(
                        "The current debug session has become inactive. Please restart your debug session.\n",
                        ConsoleViewContentType.NORMAL_OUTPUT
                    )
                    session.reportMessage("Please restart debug session\n", MessageType.ERROR)
                }
            })
        }
    }

    fun resume(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        debuggerClient.resume(scriptThread.id, onSuccess = {
            getThreads()
        })
    }

    fun stepInto(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        debuggerClient.stepInto(scriptThread.id, onSuccess = {
            getThreads()
        })
    }

    fun stepOver(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        debuggerClient.stepOver(scriptThread.id, onSuccess = {
            getThreads()
        })
    }

    fun stepOut(scriptThread: ScriptThread) {
        pendingThreads[scriptThread.id] = scriptThread
        debuggerClient.stepOut(scriptThread.id, onSuccess = {
            getThreads()
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
        // TODO remove hardcoded reference to cartridges folder if possible.
        val line = xLineBreakpoint.line
        val path = xLineBreakpoint.presentableFilePath.substring(
            Paths.get(session.project.basePath.toString(), "cartridges").toString().length
        ).replace("\\", "/")

        debuggerClient.createBreakpoint(line + 1, path, onSuccess = { breakpoint ->
            xLineBreakpoint.putUserData(idKey, breakpoint.id!!)
            session.setBreakpointVerified(xLineBreakpoint)
            session.updateBreakpointPresentation(xLineBreakpoint, AllIcons.Debugger.Db_verified_breakpoint, null)
        }, onError = {
            if (it.type == "DebuggerDisabledException") {
                session.stop()
                session.consoleView.print(
                    "The current debug session has become inactive. Please restart your debug session.\n",
                    ConsoleViewContentType.NORMAL_OUTPUT
                )
                session.reportMessage("Please restart debug session\n", MessageType.ERROR)
            }
        })
    }

    fun removeBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        val breakpointId = xLineBreakpoint.getUserData(idKey)
        if (breakpointId != null) {
            debuggerClient.deleteBreakpoint(breakpointId)
        }
    }
}

enum class DebuggerConnectionState {
    CONNECTED, DISCONNECTED
}
