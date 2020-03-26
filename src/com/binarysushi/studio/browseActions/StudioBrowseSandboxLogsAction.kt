package com.binarysushi.studio.browseActions

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAware

class StudioBrowseSandboxLogsAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val configurationProvider = StudioConfigurationProvider.getInstance(project)
        val hostname = configurationProvider.hostname
        if (hostname != null && hostname.isNotEmpty()) {
            BrowserUtil.browse("https://$hostname/on/demandware.servlet/webdav/Sites/Logs/")
        } else {
            Notifications.Bus.notify(
                Notification(
                    "Salesforce",
                    "Unknown Host",
                    "Please check your server configuration in the project settings panel. (File | Settings | Tools | Commerce Cloud Server)",
                    NotificationType.ERROR
                )
            )
        }
    }
}
