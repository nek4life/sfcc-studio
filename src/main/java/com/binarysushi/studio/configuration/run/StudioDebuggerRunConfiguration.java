package com.binarysushi.studio.configuration.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StudioDebuggerRunConfiguration extends RunConfigurationBase implements RunConfigurationWithSuppressedDefaultRunAction {
    public StudioDebuggerRunConfiguration(Project project, ConfigurationFactory configurationFactory, String name) {
        super(project, configurationFactory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new StudioDebuggerEditorPanel();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new RunProfileState() {
            @Nullable
            @Override
            public ExecutionResult execute(Executor executor, @NotNull ProgramRunner<?> runner) throws ExecutionException {
                return null;
            }
        };
    }
}
