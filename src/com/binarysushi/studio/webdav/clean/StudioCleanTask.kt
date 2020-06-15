package com.binarysushi.studio.webdav.clean

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.toolWindow.StudioConsoleService
import com.binarysushi.studio.webdav.StudioServerConnection
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.ZipUtil
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.apache.http.Consts
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.message.BasicNameValuePair
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipOutputStream

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
        val serverConnection = StudioServerConnection(configurationProvider);

        val cartridgeRoots = configurationProvider.cartridgeRoots

        if (configurationProvider.cartridgeRoots.size < 1) {
            return
        }

        val version = configurationProvider.version
        val tempDir = Paths.get(FileUtil.getTempDirectory(), project.name).toFile()
        val versionDir = Paths.get(tempDir.toString(), version).toFile()
        val zipFile = Paths.get(tempDir.toString(), "$version.zip").toFile()

        FileUtil.createDirectory(versionDir)

        indicator.text = "Preparing Archive"

        try {
            val fileOutputStream = FileOutputStream(zipFile)
            val zipOutputStream = ZipOutputStream(fileOutputStream)
            for (cartridgeRoot in cartridgeRoots) {
                val dir = File(cartridgeRoot)
                if (dir.exists()) {
                    FileUtil.copyDir(dir, Paths.get(versionDir.toString(), dir.name).toFile())
                }
            }
            ZipUtil.addDirToZipRecursively(zipOutputStream, null, versionDir, version, null, null)
            zipOutputStream.close()
            indicator.fraction = .166
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val uploadRequest: Request = Request.Builder()
            .url("${serverConnection.basePath}.zip")
            .put(zipFile.asRequestBody("application/octet-stream".toMediaType()))
            .build()

        val deleteVersionRequest = Request.Builder()
            .url(serverConnection.basePath)
            .delete()
            .build()

        val formBody = FormBody.Builder()
            .add("method", "UNZIP")
            .build()

        val unzipRequest = Request.Builder()
            .url("${serverConnection.basePath}.zip")
            .post(formBody)
            .build()

        val deleteZipRequest = Request.Builder()
            .url("${serverConnection.basePath}.zip")
            .delete()
            .build()

        indicator.text = "Uploading archive..."
        try {
            serverConnection.client.newCall(uploadRequest).execute().use { indicator.fraction = .332 }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.text = "Removing previous version..."
        try {
            serverConnection.client.newCall(deleteVersionRequest).execute().use { indicator.fraction = .498 }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.text = "Unzipping archive..."
        try {
            serverConnection.client.newCall(unzipRequest).execute().use { indicator.fraction = .664 }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indicator.text = "Removing temporary files..."
        try {
            serverConnection.client.newCall(deleteZipRequest).execute().use { indicator.fraction = .83 }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        consoleView.print(
            "[" + timeFormat.format(Date()) + "] " + "Cleaned " + serverConnection.basePath + "\n", ConsoleViewContentType.NORMAL_OUTPUT
        )

        FileUtil.delete(tempDir)
        indicator.fraction = 1.0
    }
}
