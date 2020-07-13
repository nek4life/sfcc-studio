package com.binarysushi.studio.debugger.breakpoint

import com.binarysushi.studio.debugger.StudioDebuggerProcess
import com.intellij.javascript.debugger.breakpoints.JavaScriptBreakpointType
import com.intellij.javascript.debugger.breakpoints.JavaScriptLineBreakpointProperties
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class StudioDebuggerJSLineBreakpointHandler(private val process: StudioDebuggerProcess) :
    XBreakpointHandler<XLineBreakpoint<JavaScriptLineBreakpointProperties>>(JavaScriptBreakpointType::class.java) {

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        process.addBreakpoint(breakpoint)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>, temporary: Boolean) {
        process.removeBreakpoint(breakpoint)
    }
}
