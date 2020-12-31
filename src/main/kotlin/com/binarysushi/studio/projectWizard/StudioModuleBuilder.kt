package com.binarysushi.studio.projectWizard

import com.binarysushi.studio.StudioBundle.message
import com.binarysushi.studio.StudioIcons
import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.WebModuleBuilder
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import javax.swing.Icon

class StudioModuleBuilder : ModuleBuilder() {
    private var myHostname: String? = null
    private var myUsername: String? = null
    private var myPassword: String? = null
    private var myVersion: String? = null
    private var myAutoUpload: Boolean? = null

    @Throws(ConfigurationException::class)
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        doAddContentEntry(modifiableRootModel)
        val configurationProvider =
            StudioConfigurationProvider.getInstance(modifiableRootModel.project)
        configurationProvider.hostname = myHostname
        configurationProvider.username = myUsername
        configurationProvider.password = myPassword
        configurationProvider.version = myVersion
        configurationProvider.autoUploadEnabled = myAutoUpload!!
    }

    fun updateConfiguration(
        hostname: String?,
        username: String?,
        password: String?,
        version: String?,
        autoUpload: Boolean
    ) {
        myHostname = hostname
        myUsername = username
        myPassword = password
        myVersion = version
        myAutoUpload = autoUpload
    }

    override fun getName(): String {
        return message("studio.project.name")
    }

    override fun getPresentableName(): String {
        return message("studio.project.name")
    }

    override fun getDescription(): String {
        return message("studio.project.description")
    }

    override fun getNodeIcon(): Icon {
        return StudioIcons.STUDIO_ICON
    }

    override fun getModuleType(): ModuleType<*> {
        return StudioModuleType.Companion.instance
    }

    override fun getParentGroup(): String {
        return WebModuleBuilder.GROUP_NAME
    }

    override fun createWizardSteps(
        wizardContext: WizardContext,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
        return arrayOf(StudioModuleWizardStep(wizardContext, this))
    }
}
