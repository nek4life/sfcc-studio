package com.binarysushi.studio.debugger.breakpoint

import com.binarysushi.studio.debugger.client.SDAPIClient
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import java.nio.file.Paths

class StudioDebuggerBreakpointHandler(private val project: Project, private val client: SDAPIClient) :
    XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>?>>(
        StudioDebuggerBreakpointType::class.java
    ) {

    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>?>) {
        val relativeFilePath = breakpoint.presentableFilePath
            .substring(Paths.get(project.basePath.toString(), "cartridges").toString().length)
        client.createBreakpoint(breakpoint.line, relativeFilePath)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>?>, temporary: Boolean) {
        val relativeFilePath = breakpoint.presentableFilePath
            .substring(Paths.get(project.basePath.toString(), "cartridges").toString().length)
        client.deleteBreakpoint(breakpoint.line, relativeFilePath);
    }
}
