package com.binarysushi.studio.configuration.projectSettings;

import com.binarysushi.studio.StudioBundle;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StudioServerConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {
    private StudioConfigurationPanel myConfigurationPanel;
    private final Project myProject;

    public StudioServerConfigurable(Project project) {
        myProject = project;
        myConfigurationPanel = new StudioConfigurationPanel();
    }

    @Override
    public void dispose() {
        myConfigurationPanel = null;
    }

    @NotNull
    @Override
    public String getId() {
        return "SFCC";
    }

    @Override
    public String getDisplayName() {
        return StudioBundle.message("studio.configuration.server.panel.title");
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return myConfigurationPanel.createPanel();
    }

    @Override
    public boolean isModified() {
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(myProject);
        return !myConfigurationPanel.getHostname().equals(configurationProvider.getHostname()) ||
                !myConfigurationPanel.getUsername().equals(configurationProvider.getUsername()) ||
                !myConfigurationPanel.getPassword().equals(configurationProvider.getPassword()) ||
                !myConfigurationPanel.getVersion().equals(configurationProvider.getVersion()) ||
                !myConfigurationPanel.getAutoUploadEnabled() == configurationProvider.getAutoUploadEnabled();
    }

    @Override
    public void reset() {
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(myProject);
        myConfigurationPanel.setAutoUploadEnabled(configurationProvider.getAutoUploadEnabled());
        myConfigurationPanel.setHostname(configurationProvider.getHostname());
        myConfigurationPanel.setUsername(configurationProvider.getUsername());
        myConfigurationPanel.setPassword(configurationProvider.getPassword());
        myConfigurationPanel.setVersion(configurationProvider.getVersion());
    }

    @Override
    public void apply() throws ConfigurationException {
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(myProject);
        configurationProvider.setHostname(myConfigurationPanel.getHostname());
        configurationProvider.setUsername(myConfigurationPanel.getUsername());
        configurationProvider.setPassword(myConfigurationPanel.getPassword());
        configurationProvider.setVersion(myConfigurationPanel.getVersion());
        configurationProvider.setAutoUploadEnabled(myConfigurationPanel.getAutoUploadEnabled());
    }
}
