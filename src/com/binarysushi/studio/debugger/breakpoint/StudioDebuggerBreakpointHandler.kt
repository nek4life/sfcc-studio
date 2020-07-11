package com.binarysushi.studio.debugger.breakpoint

import com.binarysushi.studio.debugger.StudioDebuggerProcess
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class StudioDebuggerBreakpointHandler(private val process: StudioDebuggerProcess) :
    XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>?>>(StudioDebuggerBreakpointType::class.java) {

    override fun registerBreakpoint(xLineBreakpoint: XLineBreakpoint<XBreakpointProperties<*>?>) {
        process.addBreakpoint(xLineBreakpoint)
    }

    override fun unregisterBreakpoint(xLineBreakpoint: XLineBreakpoint<XBreakpointProperties<*>?>, temporary: Boolean) {
        process.removeBreakpoint(xLineBreakpoint)
    }
}
