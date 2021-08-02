package com.binarysushi.studio.configuration.projectSettings

import com.binarysushi.studio.StudioBundle.message
import com.binarysushi.studio.configuration.projectSettings.StudioCartridgePanel
import com.binarysushi.studio.StudioBundle
import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class StudioCartridgeConfigurable(private val myProject: Project) : SearchableConfigurable, Configurable.NoScroll,
    Disposable {
    private var myCartridgePanel: StudioCartridgePanel?
    override fun dispose() {
        myCartridgePanel = null
    }

    override fun getId(): String {
        return "StudioCartridgeConfigurable"
    }

    override fun getDisplayName(): @Nls String? {
        return message("studio.server.cartridges.panel.title")
    }

    override fun createComponent(): JComponent? {
        return myCartridgePanel!!.createPanel()
    }

    override fun isModified(): Boolean {
        return myCartridgePanel!!.isModified
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val myConfigurationProvider = StudioConfigurationProvider.getInstance(myProject)
        myConfigurationProvider.setCartridgeRoots(myCartridgePanel!!.listItems)
    }

    init {
        myCartridgePanel = StudioCartridgePanel(myProject, StudioConfigurationProvider.getInstance(myProject))
    }
}