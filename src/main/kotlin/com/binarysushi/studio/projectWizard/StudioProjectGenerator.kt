package com.binarysushi.studio.projectWizard

import com.binarysushi.studio.StudioBundle.message
import com.binarysushi.studio.StudioIcons
import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationPanel
import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.ide.util.projectWizard.WebProjectTemplate
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ProjectGeneratorPeer
import javax.swing.Icon
import javax.swing.JComponent

class StudioProjectGenerator : WebProjectTemplate<StudioProjectWizardData?>() {
    override fun getName(): String {
        return message("studio.project.name")
    }

    override fun getDescription(): String? {
        return message("studio.project.description")
    }

    override fun getIcon(): Icon {
        return StudioIcons.STUDIO_ICON
    }

    override fun generateProject(
        project: Project,
        baseDir: VirtualFile,
        data: StudioProjectWizardData,
        module: Module
    ) {
        val configurationProvider = StudioConfigurationProvider.getInstance(project)
        configurationProvider.hostname = data.hostname
        configurationProvider.username = data.username
        configurationProvider.password = data.password
        configurationProvider.version = data.version
        configurationProvider.autoUploadEnabled = data.autoUploadEnabled
    }

    override fun createPeer(): ProjectGeneratorPeer<StudioProjectWizardData?> {
        return StudioProjectGeneratePeer()
    }

    override fun createModuleBuilder(): ModuleBuilder {
        return StudioModuleBuilder()
    }

    private inner class StudioProjectGeneratePeer : ProjectGeneratorPeer<StudioProjectWizardData?> {
        private val myConfigurationPanel = StudioConfigurationPanel()
        override fun getComponent(): JComponent {
            return myConfigurationPanel.createPanel()
        }

        override fun buildUI(settingsStep: SettingsStep) {}
        override fun getSettings(): StudioProjectWizardData {
            return StudioProjectWizardData(
                myConfigurationPanel.hostname,
                myConfigurationPanel.username,
                myConfigurationPanel.password,
                myConfigurationPanel.version,
                myConfigurationPanel.autoUploadEnabled
            )
        }

        override fun validate(): ValidationInfo? {
            return null
        }

        override fun isBackgroundJobRunning(): Boolean {
            return false
        }
    }
}
