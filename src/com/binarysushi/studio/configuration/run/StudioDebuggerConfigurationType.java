package com.binarysushi.studio.configuration.run;

import com.binarysushi.studio.StudioBundle;
import com.binarysushi.studio.StudioIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StudioDebuggerConfigurationType implements ConfigurationType {
    @NotNull
    @Override
    public String getDisplayName() {
        return StudioBundle.message("studio.configuration.run.name");
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return StudioBundle.message("studio.configuration.run.description");
    }

    @Override
    public Icon getIcon() {
        return StudioIcons.STUDIO_ICON;
    }

    @NotNull
    @Override
    public String getId() {
        return "StudioDebugger";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new StudioDebuggerConfigurationFactory(this)};
    }
}
