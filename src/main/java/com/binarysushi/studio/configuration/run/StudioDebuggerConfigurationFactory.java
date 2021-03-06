package com.binarysushi.studio.configuration.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class StudioDebuggerConfigurationFactory extends ConfigurationFactory {
    protected StudioDebuggerConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public String getId() {
        return "StudioDebugger";
    }

    @NotNull
    @Override
    public String getName() {
        return "StudioDebugger";
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new StudioDebuggerRunConfiguration(project, this, "StudioDebugger");
    }
}
