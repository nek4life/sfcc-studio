package com.binarysushi.studio.projectWizard;

import com.binarysushi.studio.settings.StudioSettingsPanel;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

public class StudioModuleWizardStep extends ModuleWizardStep {
    private final StudioModuleBuilder myBuilder;
    private final WizardContext myWizardContext;
    private final StudioSettingsPanel mySettingsPanel = new StudioSettingsPanel();

    public StudioModuleWizardStep(WizardContext wizardContext, final StudioModuleBuilder builder) {
        myBuilder = builder;
        myWizardContext = wizardContext;
    }

    @Override
    public JComponent getComponent() {
        return mySettingsPanel.createPanel();
    }

    @Override
    public void updateDataModel() {
        final ProjectBuilder projectBuilder = myWizardContext.getProjectBuilder();

        if (projectBuilder instanceof StudioModuleBuilder) {
            ((StudioModuleBuilder) projectBuilder).updateSettings(
                    mySettingsPanel.getHostname(),
                    mySettingsPanel.getUsername(),
                    mySettingsPanel.getPassword(),
                    mySettingsPanel.getVersion(),
                    mySettingsPanel.getAutoUploadEnabled()
            );
        }
    }


    /**
     * Perform form validation here
     */
    @Override
    public boolean validate() throws ConfigurationException {
//        if (mySettingsPanel.getUsername().isEmpty()) {
//            throw new ConfigurationException("Specify Username");
//        }

        return super.validate();
    }
}
