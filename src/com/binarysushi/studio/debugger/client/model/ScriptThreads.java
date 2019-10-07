package com.binarysushi.studio.debugger.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScriptThreads {
    @JsonProperty("script_threads")
    private List<ScriptThreads> scriptThreads;

    public List<ScriptThreads> getScriptThreads() {
        return scriptThreads;
    }

    public void setScriptThreads(List<ScriptThreads> scriptThreads) {
        this.scriptThreads = scriptThreads;
    }
}
