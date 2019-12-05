package com.binarysushi.studio.debugger.client.model;

import java.util.List;

public class Breakpoints {
    private List<Breakpoint> breakpoints;

    public List<Breakpoint> getBreakpoints() {
        return breakpoints;
    }

    public void setBreakpoints(List<Breakpoint> breakpoints) {
        this.breakpoints = breakpoints;
    }
}
