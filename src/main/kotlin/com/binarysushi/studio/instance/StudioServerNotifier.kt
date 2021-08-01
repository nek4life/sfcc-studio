package com.binarysushi.studio.instance

import com.binarysushi.studio.configuration.projectSettings.StudioServerConfigurable
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.options.ShowSettingsUtil

object StudioServerNotifier {
    fun notify(reason: String) {
        // Action to the server configurations
        val notificationAction = object : NotificationAction("Configuration") {
            override fun actionPerformed(anActionEvent: AnActionEvent, notification: Notification) {
                val dataContext = anActionEvent.dataContext
                val project = PlatformDataKeys.PROJECT.getData(dataContext)
                ShowSettingsUtil.getInstance()
                    .showSettingsDialog(project, StudioServerConfigurable::class.java)
            }
        }

        val notification = Notification(
            "Salesforce",
            reason,
            "Please check your server configurations.",
            NotificationType.ERROR
        )

        notification.addAction(notificationAction)
        Notifications.Bus.notify(notification)
    }
}
