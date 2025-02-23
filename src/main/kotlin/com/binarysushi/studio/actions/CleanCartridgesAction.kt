package com.binarysushi.studio.actions

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.instance.StudioServerNotifier
import com.binarysushi.studio.instance.clients.WebDavClient
import com.binarysushi.studio.instance.code.CodeManager
import com.binarysushi.studio.toolWindow.StudioConsoleService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.util.net.JdkProxyProvider
import java.io.File

class CleanCartridgesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project

        ProgressManager.getInstance().run(object : Task.Backgroundable(
            e.project,
            "Cleaning cartridges",
            true
        ) {
            override fun run(indicator: ProgressIndicator) {
                val configurationProvider = StudioConfigurationProvider.getInstance(project!!)
                val consoleView = project.service<StudioConsoleService>().consoleView
                val cartridgeRoots = configurationProvider.cartridgeRoots
                if (configurationProvider.cartridgeRoots.isEmpty()) {
                    StudioServerNotifier.notify("No Cartridges Found")
                    return
                }

                val webDavClient = WebDavClient(
                    configurationProvider.hostname,
                    configurationProvider.username,
                    configurationProvider.password,
                    proxySelector = JdkProxyProvider.getInstance().proxySelector
                )

                CodeManager.deployVersion(
                    webDavClient,
                    configurationProvider.version,
                    cartridgeRoots.map { File(it) },
                    indicator,
                    consoleView
                )
            }
        })
    }
}
