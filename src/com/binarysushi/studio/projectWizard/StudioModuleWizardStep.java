package com.binarysushi.studio.projectWizard;

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationPanel;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

public class StudioModuleWizardStep extends ModuleWizardStep {
    private final StudioModuleBuilder myBuilder;
    private final WizardContext myWizardContext;
    private final StudioConfigurationPanel myConfigurationPanel = new StudioConfigurationPanel();

    public StudioModuleWizardStep(WizardContext wizardContext, final StudioModuleBuilder builder) {
        myBuilder = builder;
        myWizardContext = wizardContext;
    }

    @Override
    public JComponent getComponent() {
        return myConfigurationPanel.createPanel();
    }

    @Override
    public void updateDataModel() {
        final ProjectBuilder projectBuilder = myWizardContext.getProjectBuilder();

        if (projectBuilder instanceof StudioModuleBuilder) {
            ((StudioModuleBuilder) projectBuilder).updateConfiguration(
                    myConfigurationPanel.getHostname(),
                    myConfigurationPanel.getUsername(),
                    myConfigurationPanel.getPassword(),
                    myConfigurationPanel.getVersion(),
                    myConfigurationPanel.getAutoUploadEnabled()
            );
        }
    }


    /**
     * Perform form validation here
     */
    @Override
    public boolean validate() throws ConfigurationException {
//        if (myConfigurationPanel.getUsername().isEmpty()) {
//            throw new ConfigurationException("Specify Username");
//        }

        return super.validate();
    }
}
