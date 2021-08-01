package com.binarysushi.studio.webdav.clean

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.instance.StudioServerNotifier
import com.binarysushi.studio.instance.clients.TopLevelDavFolders
import com.binarysushi.studio.instance.clients.WebDavClient
import com.binarysushi.studio.instance.code.CodeManager
import com.binarysushi.studio.toolWindow.StudioConsoleService
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.proxy.CommonProxy
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StudioCleanTask internal constructor(
    project: Project?,
    title: String?,
    canBeCancelled: Boolean,
    backgroundOption: PerformInBackgroundOption?
) : Backgroundable(project, title!!, canBeCancelled, backgroundOption) {
    private val timeFormat = SimpleDateFormat("hh:mm:ss")

    override fun run(indicator: ProgressIndicator) {
        val configurationProvider = project.service<StudioConfigurationProvider>()
        val consoleView = project.service<StudioConsoleService>().consoleView
        val cartridgeRoots = configurationProvider.cartridgeRoots
        if (configurationProvider.cartridgeRoots.size < 1) {
            StudioServerNotifier.notify("No Cartridges Found")
            return
        }

        val webDavClient = WebDavClient(
            configurationProvider.hostname,
            configurationProvider.username,
            configurationProvider.password,
            proxySelector = CommonProxy.getInstance()
        )

        val version = configurationProvider.version
        val serverVersionPath = "${TopLevelDavFolders.CARTRIDGES}/${version}"
        val serverZipPath = "${TopLevelDavFolders.CARTRIDGES}/${version}.zip"

        indicator.text = "Preparing archive..."
        val zipFile = CodeManager.zipVersion(version, cartridgeRoots.map { File(it) })
        indicator.fraction = .166

        indicator.text = "Uploading archive..."
        try {
            webDavClient.put(serverZipPath, zipFile, "application/octet-stream")
            indicator.fraction = .332
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.text = "Removing previous version..."
        try {
            webDavClient.delete(serverVersionPath)
            indicator.fraction = .498
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.text = "Unzipping archive..."
        try {
            webDavClient.unzip(serverZipPath)
            indicator.fraction = .664
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.text = "Removing temporary files..."
        try {
            webDavClient.delete(serverZipPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        indicator.fraction = .83

        consoleView.print(
            "[${timeFormat.format(Date())}] Cleaned ${webDavClient.baseURI}${TopLevelDavFolders.CARTRIDGES}/${version}\n",
            ConsoleViewContentType.NORMAL_OUTPUT
        )

        FileUtil.delete(zipFile)
        indicator.fraction = 1.0
    }
}
