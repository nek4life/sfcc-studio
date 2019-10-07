package com.binarysushi.studio.debugger.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
    @JsonProperty("function_name")
    private String functionName;

    @JsonProperty("line_number")
    private String lineNumber;

    @JsonProperty("script_path")
    private String scriptPath;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
}
