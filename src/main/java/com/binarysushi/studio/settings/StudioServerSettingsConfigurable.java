package com.binarysushi.studio.settings;

import com.binarysushi.studio.StudioBundle;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StudioServerSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {
    private StudioSettingsPanel mySettingsPanel;
    private final Project myProject;

    public StudioServerSettingsConfigurable(Project project) {
        myProject = project;
        mySettingsPanel = new StudioSettingsPanel();
    }

    @Override
    public void dispose() {
        mySettingsPanel = null;
    }

    @NotNull
    @Override
    public String getId() {
        return "SFCC";
    }

    @Override
    public String getDisplayName() {
        return StudioBundle.message("studio.settings.server.panel.title");
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mySettingsPanel.createPanel();
    }

    @Override
    public boolean isModified() {
        StudioSettingsProvider settingsProvider = StudioSettingsProvider.getInstance(myProject);
        return !mySettingsPanel.getHostname().equals(settingsProvider.getHostname()) ||
                !mySettingsPanel.getUsername().equals(settingsProvider.getUsername()) ||
                !mySettingsPanel.getPassword().equals(settingsProvider.getPassword()) ||
                !mySettingsPanel.getVersion().equals(settingsProvider.getVersion()) ||
                !mySettingsPanel.getAutoUploadEnabled() == settingsProvider.getAutoUploadEnabled();
    }

    @Override
    public void reset() {
        StudioSettingsProvider settingsProvider = StudioSettingsProvider.getInstance(myProject);
        mySettingsPanel.setAutoUploadEnabled(settingsProvider.getAutoUploadEnabled());
        mySettingsPanel.setHostname(settingsProvider.getHostname());
        mySettingsPanel.setUsername(settingsProvider.getUsername());
        mySettingsPanel.setPassword(settingsProvider.getPassword());
        mySettingsPanel.setVersion(settingsProvider.getVersion());
    }

    @Override
    public void apply() throws ConfigurationException {
        StudioSettingsProvider settingsProvider = StudioSettingsProvider.getInstance(myProject);
        settingsProvider.setHostname(mySettingsPanel.getHostname());
        settingsProvider.setUsername(mySettingsPanel.getUsername());
        settingsProvider.setPassword(mySettingsPanel.getPassword());
        settingsProvider.setVersion(mySettingsPanel.getVersion());
        settingsProvider.setAutoUploadEnabled(mySettingsPanel.getAutoUploadEnabled());
    }
}
