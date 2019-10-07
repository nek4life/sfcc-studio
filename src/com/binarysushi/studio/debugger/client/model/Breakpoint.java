package com.binarysushi.studio.debugger.client.model;


import com.fasterxml.jackson.annotation.JsonProperty;


public class Breakpoint {
    private int id;
    private String condition;

    @JsonProperty(value = "line_number", required = true)
    private int lineNumber;

    @JsonProperty(value = "script_path", required = true)
    private String scriptPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
}
