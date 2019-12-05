package com.binarysushi.studio.debugger.breakpoint;

import com.binarysushi.studio.debugger.client.SDAPIClient;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;

public class StudioDebuggerBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<XBreakpointProperties>> {
    private final Project myProject;
    private final SDAPIClient myClient;

    public StudioDebuggerBreakpointHandler(Project project, SDAPIClient client) {
        super(StudioDebuggerBreakpointType.class);
        myProject = project;
        myClient = client;
    }

    @Override
    public void registerBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> breakpoint) {
        String filePath = breakpoint.getPresentableFilePath();
        breakpoint.getLine();
        myClient.createBreakpoint();
    }

    @Override
    public void unregisterBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> breakpoint, boolean temporary) {

    }
}
