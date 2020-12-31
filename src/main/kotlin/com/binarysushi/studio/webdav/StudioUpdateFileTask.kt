package com.binarysushi.studio.webdav

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.toolWindow.StudioConsoleService
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StudioUpdateFileTask(
    project: Project,
    title: String,
    canBeCancelled: Boolean,
    backgroundOption: PerformInBackgroundOption,
    sourceRootPath: String,
    private val eventFile: VirtualFile
) : Backgroundable(project, title, canBeCancelled, backgroundOption) {
    private val configurationProvider = project.service<StudioConfigurationProvider>();
    private val serverConnection = StudioServerConnection(configurationProvider);
    private val consoleView = project.service<StudioConsoleService>().consoleView
    private val remoteDirPaths = serverConnection.getRemoteDirPaths(sourceRootPath, eventFile.path)
    private val remoteFilePath = serverConnection.getRemoteFilePath(sourceRootPath, eventFile.path)
    private val timeFormat = SimpleDateFormat("hh:mm:ss")

    override fun run(indicator: ProgressIndicator) {
        val localFile = File(eventFile.path)
        var fileStatus = FileStatus.NEW;
        indicator.isIndeterminate = false
        indicator.fraction = .33
        val statusCode = serverConnection.testRemoteFileExistence(remoteFilePath)

        // If there is an error don't do anything on the server
        if (statusCode == 401 || statusCode == -1) {
            return
        }

        // If the status code is 200 a file exists
        if (statusCode == 200) {
            // Check the existence of the local virtual file to determine whether to update or delete.
            fileStatus = if (eventFile.exists()) {
                FileStatus.UPDATED
            } else {
                FileStatus.DELETED
            }
        }

        indicator.fraction = .5
        val request = Request.Builder()
            .url(remoteFilePath)
            .put(localFile.asRequestBody("application/octet-stream".toMediaType()))
            .build()

        when (fileStatus) {
            FileStatus.NEW -> createNewFile(request, localFile)
            FileStatus.UPDATED -> doHttpRequest(request, localFile, "Updated file")
            FileStatus.DELETED -> doHttpRequest(
                Request.Builder().url(remoteFilePath).delete().build(), localFile, "Deleted"
            )
        }
        indicator.fraction = 1.0
    }

    private fun createRemoteDirectories() {
        for (path in remoteDirPaths) {
            val request = Request.Builder().url("$path/").method("MKCOL", null).build()

            try {
                val response = serverConnection.client.newCall(request).execute()
                if (response.code == 201) {
                    consoleView.print(
                        "[${timeFormat.format(Date())}] [Created folder] ${request.url}\n",
                        ConsoleViewContentType.NORMAL_OUTPUT
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createNewFile(request: Request, localFile: File) {
        createRemoteDirectories()
        doHttpRequest(request, localFile, "Created file")
    }

    private fun doHttpRequest(request: Request, localFile: File, message: String) {
        try {
            serverConnection.client.newCall(request).execute().use { response ->
                response.close()

                consoleView.print(
                    "[" + timeFormat.format(Date()) + "] " + "[" + message + " (" + localFile.name + ")] " + request.url
                        .toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT
                )
            }
        } catch (e: FileNotFoundException) {
            // TODO handle file not found
        }

    }

    private enum class FileStatus {
        NEW, UPDATED, DELETED
    }
}
