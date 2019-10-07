package com.binarysushi.studio.debugger;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

public class StudioScriptDebuggerRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "StudioScriptDebugger";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return false;
    }
}
