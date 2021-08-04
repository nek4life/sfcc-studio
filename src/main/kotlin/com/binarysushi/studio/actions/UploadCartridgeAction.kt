package com.binarysushi.studio.actions

import com.binarysushi.studio.cartridges.CartridgePathUtil
import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.instance.clients.WebDavClient
import com.binarysushi.studio.instance.code.CodeManager
import com.binarysushi.studio.toolWindow.StudioConsoleService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.util.proxy.CommonProxy
import java.io.File

class UploadCartridgeAction : DumbAwareAction() {
    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)

        // Show action if file is a directory and is part of the active cartridge roots
        e.presentation.isVisible = files?.all { file ->
            val activeCartridgeRoots = e.project?.let { CartridgePathUtil.getActiveCartridgeRoots(it) }
            file.isDirectory && activeCartridgeRoots?.contains(file.path) == true
        } == true

        // Pluralize text if more than one cartridge is selected
        if (files!!.size > 1) {
            e.presentation.text = "Upload Cartridges"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val configurationProvider = e.project!!.service<StudioConfigurationProvider>()
        val consoleView = e.project!!.service<StudioConsoleService>().consoleView
        val webDavClient = WebDavClient(
            configurationProvider.hostname,
            configurationProvider.username,
            configurationProvider.password,
            proxySelector = CommonProxy.getInstance()
        )

        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        var title = "Uploading cartridge"

        if (files?.size!! > 1) {
            title = "Upload cartridges"
        }

        files.forEach {
            ProgressManager.getInstance().run(object : Task.Backgroundable(
                e.project,
                title,
                true
            ) {
                override fun run(indicator: ProgressIndicator) {
                    CodeManager.deployCartridge(
                        webDavClient,
                        configurationProvider.version,
                        File(it.path),
                        indicator,
                        consoleView
                    )
                }
            })
        }
    }
}