package com.binarysushi.studio.projectWizard;

import com.binarysushi.studio.StudioBundle;
import com.binarysushi.studio.StudioIcons;
import com.binarysushi.studio.configuration.StudioConfigurationProvider;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StudioModuleBuilder extends ModuleBuilder {
    private String myHostname;
    private String myUsername;
    private String myPassword;
    private String myVersion;
    private Boolean myAutoUpload;

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        ContentEntry entry = doAddContentEntry(modifiableRootModel);
        StudioConfigurationProvider configurationProvider = StudioConfigurationProvider.getInstance(modifiableRootModel.getProject());
        configurationProvider.setHostname(myHostname);
        configurationProvider.setUsername(myUsername);
        configurationProvider.setPassword(myPassword);
        configurationProvider.setVersion(myVersion);
        configurationProvider.setAutoUploadEnabled(myAutoUpload);
    }

    public void updateConfiguration(String hostname, String username, String password, String version, boolean autoUpload) {
        myHostname = hostname;
        myUsername = username;
        myPassword= password;
        myVersion = version;
        myAutoUpload = autoUpload;
    }

    @Override
    public String getName() {
        return StudioBundle.message("studio.project.name");
    }

    @Override
    public String getPresentableName() {
        return StudioBundle.message("studio.project.name");
    }

    @Override
    public String getDescription() {
        return StudioBundle.message("studio.project.description");
    }

    @Override
    public Icon getNodeIcon() {
        return StudioIcons.STUDIO_ICON;
    }

    @Override
    public ModuleType getModuleType() {
        return StudioModuleType.getInstance();
    }

    @Override
    public String getParentGroup() {
        return WebModuleBuilder.GROUP_NAME;
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{ new StudioModuleWizardStep(wizardContext, this) };
    }
}
