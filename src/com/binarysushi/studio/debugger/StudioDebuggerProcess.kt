package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerBreakpointHandler
import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerEditorsProvider
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.intellij.openapi.components.service
import com.intellij.util.ArrayUtil
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider

class StudioDebuggerProcess : XDebugProcess {

    private var myClient: SDAPIClient
    private var myBreakpointHandler: StudioDebuggerBreakpointHandler

    constructor(session: XDebugSession) : super(session) {
        myClient = session.project.service<SDAPIClient>()
        myBreakpointHandler = StudioDebuggerBreakpointHandler(session.project)
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return StudioDebuggerEditorsProvider()
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>?> {
        val breakpointHandlers = super.getBreakpointHandlers()
        return ArrayUtil.append(breakpointHandlers, myBreakpointHandler)
    }



    override fun stop() {
        myClient.deleteSession()
    }
}
