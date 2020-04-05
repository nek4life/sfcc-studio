package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerBreakpointHandler
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.MessageType
import com.intellij.util.ArrayUtil
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider

class StudioDebuggerProcess(session: XDebugSession) : XDebugProcess(session) {
    private val debuggerClient = session.project.service<SDAPIClient>()
    private val breakpointHandler = StudioDebuggerBreakpointHandler(session.project, debuggerClient)

    override fun sessionInitialized() {
        session.reportMessage("Debug session started", MessageType.INFO)
//         Trying to figure out how to poll for threads.
//         debuggerClient.listen()
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return StudioDebuggerEditorsProvider()
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>?> {
        val breakpointHandlers = super.getBreakpointHandlers()
        return ArrayUtil.append(breakpointHandlers, breakpointHandler)
    }

    override fun stop() {
        debuggerClient.deleteSession()
        session.reportMessage("Debug session stopped", MessageType.INFO)
    }
}
