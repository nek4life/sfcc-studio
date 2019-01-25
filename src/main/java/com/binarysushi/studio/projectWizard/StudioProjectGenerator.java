package com.binarysushi.studio.projectWizard;

import com.binarysushi.studio.StudioBundle;
import com.binarysushi.studio.StudioIcons;
import com.binarysushi.studio.configuration.StudioConfigurationPanel;
import com.binarysushi.studio.configuration.StudioConfigurationProvider;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.ProjectGeneratorPeer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StudioProjectGenerator extends WebProjectTemplate<StudioProjectWizardData> {

    @NotNull
    @Override
    public String getName() {
        return StudioBundle.message("studio.project.name");
    }

    @Override
    public String getDescription() {
        return StudioBundle.message("studio.project.description");
    }

    @Override
    public Icon getIcon() {
        return StudioIcons.STUDIO_ICON;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull StudioProjectWizardData data, @NotNull Module module) {
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(project);
        configurationProvider.setHostname(data.hostname);
        configurationProvider.setUsername(data.username);
        configurationProvider.setPassword(data.password);
        configurationProvider.setVersion(data.version);
        configurationProvider.setAutoUploadEnabled(data.autoUploadEnabled);
    }

    @NotNull
    @Override
    public ProjectGeneratorPeer createPeer() {
        return new StudioProjectGeneratePeer();
    }

    @NotNull
    @Override
    public ModuleBuilder createModuleBuilder() {
        return new StudioModuleBuilder();
    }

    @SuppressWarnings("deprecation")
    private class StudioProjectGeneratePeer implements GeneratorPeer {

        private final StudioConfigurationPanel myConfigurationPanel = new StudioConfigurationPanel();

        @NotNull
        @Override
        public JComponent getComponent() {
            return myConfigurationPanel.createPanel();
        }

        @Override
        public void buildUI(@NotNull SettingsStep settingsStep) { }

        @NotNull
        @Override
        public Object getSettings() {
            return new StudioProjectWizardData(
                    myConfigurationPanel.getHostname(),
                    myConfigurationPanel.getUsername(),
                    myConfigurationPanel.getPassword(),
                    myConfigurationPanel.getVersion(),
                    myConfigurationPanel.getAutoUploadEnabled());
        }

        @Nullable
        @Override
        public ValidationInfo validate() {
            return null;
        }

        @Override
        public boolean isBackgroundJobRunning() {
            return false;
        }

        @Override
        public void addSettingsStateListener(@NotNull SettingsStateListener listener) {

        }
    }
}
