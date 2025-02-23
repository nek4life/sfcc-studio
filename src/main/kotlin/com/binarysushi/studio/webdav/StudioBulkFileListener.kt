package com.binarysushi.studio.webdav

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class StudioBulkFileListener : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        val projects = ProjectManager.getInstance().openProjects

        for (event in events) {
            if (event.file != null && !event.file!!.isDirectory) {
                for (project in projects) {
                    val configurationProvider = StudioConfigurationProvider.getInstance(project)

                    // Bail out if auto uploads are not enabled.
                    if (!StudioConfigurationProvider.getInstance(project).autoUploadEnabled) {
                        return
                    }

                    if (configurationProvider.cartridgeRoots.size < 1) {
                        return
                    }

                    for (cartridgeRoot in configurationProvider.cartridgeRoots) {
                        if (event.file!!.path.contains(cartridgeRoot)) {
                            val fileTask = StudioUpdateFileTask(
                                project,
                                "Syncing files to: " + StudioConfigurationProvider.getInstance(project).hostname,
                                true,
                                cartridgeRoot,
                                event.file!!
                            )

                            ProgressManager.getInstance().run(fileTask)
                        }
                    }
                }
            }
        }
    }
}
