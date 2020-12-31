package com.binarysushi.studio.projectWizard

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationPanel
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.options.ConfigurationException
import javax.swing.JComponent

class StudioModuleWizardStep(private val myWizardContext: WizardContext, private val myBuilder: StudioModuleBuilder) :
    ModuleWizardStep() {
    private val myConfigurationPanel = StudioConfigurationPanel()
    override fun getComponent(): JComponent {
        return myConfigurationPanel.createPanel()
    }

    override fun updateDataModel() {
        val projectBuilder = myWizardContext.projectBuilder
        if (projectBuilder is StudioModuleBuilder) {
            projectBuilder.updateConfiguration(
                myConfigurationPanel.hostname,
                myConfigurationPanel.username,
                myConfigurationPanel.password,
                myConfigurationPanel.version,
                myConfigurationPanel.autoUploadEnabled
            )
        }
    }

    /**
     * Perform form validation here
     */
    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
//        if (myConfigurationPanel.getUsername().isEmpty()) {
//            throw new ConfigurationException("Specify Username");
//        }
        return super.validate()
    }
}
