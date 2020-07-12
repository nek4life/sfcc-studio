package com.binarysushi.studio.debugger

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerBreakpointType
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.MessageType
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.breakpoints.*


class SDAPIDebugger(private val session: XDebugSession, private val process: StudioDebuggerProcess) {
    private val config = session.project.service<StudioConfigurationProvider>()
    private val debuggerClient = SDAPIClient(
        config.hostname,
        config.username,
        config.password
    )

    var connectionState = DebuggerConnectionState.DISCONNECTED;
    var currentThreads = HashMap<Int, ScriptThread>()
    var pendingThreads = HashMap<Int, ScriptThread>()

    public fun connect(awaitingBreakpoints: MutableList<XLineBreakpoint<XBreakpointProperties<*>?>>) {
        ApplicationManager.getApplication().executeOnPooledThread {
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
            debuggerClient.deleteSession()
            connectionState = DebuggerConnectionState.DISCONNECTED
            session.reportMessage("Debug session stopped", MessageType.INFO)
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
            println(exception.message)
        }
    }

    private tailrec fun threadResetLoop() {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            debuggerClient.resetThreads()
            Thread.sleep(25000)
            threadResetLoop()
        }
    }

    private tailrec fun threadLoop() {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            debuggerClient.getThreads(onSuccess = { threads ->
                if (threads != null) {
                    for (thread in threads) {
                        if (!currentThreads.containsKey(thread.id) && thread.status == "halted") {
                            val suspendContext = StudioDebuggerSuspendContext(process, thread)
                            val breakpoint = findBreakpoint(thread.callStack[0].location.scriptPath)
                            if (breakpoint != null) {
                                session.breakpointReached(breakpoint, null, suspendContext)
                            } else {
                                session.positionReached(suspendContext)
                            }

                            currentThreads[thread.id] = thread
                        } else if (pendingThreads.containsKey(thread.id) && thread.status == "halted") {
                            val suspendContext = StudioDebuggerSuspendContext(process, thread)
                            val breakpoint = findBreakpoint(thread.callStack[0].location.scriptPath)
                            if (breakpoint != null) {
                                session.breakpointReached(breakpoint, null, suspendContext)
                            } else {
                                session.positionReached(suspendContext)
                            }
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
            })

            Thread.sleep(3000)
            threadLoop()
        }
    }

    fun resume(scriptThread: ScriptThread) {
        this.pendingThreads[scriptThread.id] = scriptThread
        debuggerClient.resume(scriptThread.id)
    }

    private fun findBreakpoint(scriptPath: String): XLineBreakpoint<out XBreakpointProperties<Any>>? {
        val manager = XDebuggerManager.getInstance(session.project).breakpointManager
        val type: XLineBreakpointType<*>? = XDebuggerUtil.getInstance().findBreakpointType(
            StudioDebuggerBreakpointType::class.java
        )

        if (type != null) {
            val breakpoints = manager.getBreakpoints(type)
            for (breakpoint in breakpoints) {
                if (breakpoint.fileUrl.contains(scriptPath)) {
                    return breakpoint
                }
            }
        }
        return null
    }


}

enum class DebuggerConnectionState {
    CONNECTED, DISCONNECTED
}
