package com.binarysushi.studio.debugger;

import com.binarysushi.studio.configuration.run.StudioDebuggerRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NotNull;

public class StudioScriptDebuggerRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "StudioScriptDebuggerRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(DefaultDebugExecutor.EXECUTOR_ID) && profile instanceof StudioDebuggerRunConfiguration;
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();

        RunContentDescriptor descriptor = XDebuggerManager.getInstance(environment.getProject()).startSession(environment, new XDebugProcessStarter() {
            @NotNull
            public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
//                final SDAPIClient client = new SDAPIClient("", "", "", "StudioDebuggerClient");
//                client.createSession();
                return new StudioDebuggerProcess(session);
            }
        }).getRunContentDescriptor();

        environment.setContentToReuse(descriptor);
    }
}
