package com.binarysushi.studio.debugger;

import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerBreakpointHandler;
import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerEditorsProvider;
import com.binarysushi.studio.debugger.client.SDAPIClient;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.jetbrains.annotations.NotNull;

public class StudioDebuggerProcess extends XDebugProcess {
    private SDAPIClient myClient = null;
    private StudioDebuggerBreakpointHandler myBreakpointHandler = null;

    /**
     *  Constructor for testing until I can figure out how to retrieve server credentials from system settings.

     */
    protected StudioDebuggerProcess(@NotNull XDebugSession session) {
        super(session);
    }

    protected StudioDebuggerProcess(@NotNull XDebugSession session, SDAPIClient client) {
        super(session);
        myClient = client;
        myBreakpointHandler = new StudioDebuggerBreakpointHandler(session.getProject(), client);
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return new StudioDebuggerEditorsProvider();
    }

    @NotNull
    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        final XBreakpointHandler<?>[] breakpointHandlers = super.getBreakpointHandlers();
        return ArrayUtil.append(breakpointHandlers, myBreakpointHandler);
    }

    @Override
    public void stop() {
        myClient.deleteSession();
    }
}
