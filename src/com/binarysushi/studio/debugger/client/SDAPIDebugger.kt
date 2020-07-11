package com.binarysushi.studio.debugger.client

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.debugger.StudioDebuggerProcess
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.MessageType
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class SDAPIDebugger(private val session: XDebugSession, private val process: StudioDebuggerProcess) {
    private val config = session.project.service<StudioConfigurationProvider>()
    private val debuggerClient = SDAPIClient(config.hostname, config.username, config.password)

    var connectionState = DebuggerConnectionState.DISCONNECTED;

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
        try {
            threadLoop()
            threadResetLoop()
        } catch (exception: Exception) {
            session.stop()
        }
    }
    private tailrec fun threadResetLoop() {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            debuggerClient.resetThreads()
            Thread.sleep(30000)
            threadResetLoop()
        }
    }

    private tailrec fun threadLoop() {
        if (connectionState === DebuggerConnectionState.CONNECTED) {
            debuggerClient.getThreads { data ->
                println(data)
            }

            Thread.sleep(3000)
            threadLoop()
        }
    }
}

enum class DebuggerConnectionState {
    CONNECTED, DISCONNECTED
}
