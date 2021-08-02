package com.binarysushi.studio.webdav

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.instance.clients.TopLevelDavFolders
import com.binarysushi.studio.instance.clients.WebDavClient
import com.binarysushi.studio.instance.code.CodeManager
import com.binarysushi.studio.toolWindow.StudioConsoleService
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.util.proxy.CommonProxy
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UploadCartridgeAction : AnAction() {
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isVisible = file!!.isDirectory
    }

    override fun actionPerformed(e: AnActionEvent) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(
            e.project,
            "Upload cartridge",
            true
        ) {
            override fun run(indicator: ProgressIndicator) {
                val configurationProvider = e.project!!.service<StudioConfigurationProvider>()
                val consoleView = e.project!!.service<StudioConsoleService>().consoleView
                val webDavClient = WebDavClient(
                    configurationProvider.hostname,
                    configurationProvider.username,
                    configurationProvider.password,
                    proxySelector = CommonProxy.getInstance()
                )

                indicator.isIndeterminate = false

                e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)?.forEach {
                    indicator.fraction = .0

                    val zipFile = CodeManager.zipCartridge(File(it!!.path))

                    indicator.fraction = .50

                    CodeManager.deployCartridge(
                        webDavClient,
                        configurationProvider.version,
                        zipFile
                    )

                    indicator.fraction = 1.0

                    val timeFormat = SimpleDateFormat("hh:mm:ss")

                    consoleView.print(
                        "[${timeFormat.format(Date())}] Uploaded ${webDavClient.baseURI}${TopLevelDavFolders.CARTRIDGES}/${configurationProvider.version}/${it.name}\n",
                        ConsoleViewContentType.NORMAL_OUTPUT
                    )
                }
            }
        })
    }
}