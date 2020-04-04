package com.binarysushi.studio.debugger.breakpoint

import com.binarysushi.studio.debugger.client.SDAPIClient
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class StudioDebuggerBreakpointHandler(private val project: Project, private val client: SDAPIClient) :
    XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>?>>(
        StudioDebuggerBreakpointType::class.java
    ) {
    private val idKey: Key<Int> = Key.create("STUDIO_BP_ID")

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>?>) {
        client.createBreakpoint(breakpoint)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>?>, temporary: Boolean) {
        client.deleteBreakpoint(breakpoint)
    }
}
