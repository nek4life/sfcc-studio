package com.binarysushi.studio.debugger.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScriptThread {
    private int id;
    private String status;

    @JsonProperty("stack_frame")
    private StackFrame stackFrame;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StackFrame getStackFrame() {
        return stackFrame;
    }

    public void setStackFrame(StackFrame stackFrame) {
        this.stackFrame = stackFrame;
    }
}
