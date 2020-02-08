package com.binarysushi.studio.webdav

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.configuration.projectSettings.StudioServerConfigurable
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.util.proxy.CommonProxy
import okhttp3.*
import java.io.IOException
import java.net.UnknownHostException
import kotlin.properties.Delegates

class StudioWebDavClient(val project: Project) {
    private var basePath: String by Delegates.notNull()
    private var client: OkHttpClient by Delegates.notNull()

    init {
        val config = project.service<StudioConfigurationProvider>();
        val authenticator = object : Authenticator {
            @Throws(IOException::class)
            override fun authenticate(route: Route?, response: Response): Request? {
                if (response.request.header("Authorization") != null) {
                    return null // Give up, we've already attempted to authenticate.
                }

                val credential = Credentials.basic(config.username, config.password)
                return response.request.newBuilder().header("Authorization", credential).build()
            }
        }
        this.client = OkHttpClient.Builder()
            .proxySelector(CommonProxy.getInstance())
            .authenticator(authenticator)
            .build()

        this.basePath = "https:/${config.hostname}/on/demandware.servlet/webdav/Sites/Cartridges/${config.version}"
    }

    private fun notifyServerSettings(reason: String) {
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

    fun testRemoteFileExistence(path: String): Boolean {
        val request = Request.Builder()
            .url("${this.basePath}/${path}")
            .head()
            .build()

        return try {
            val call = client.newCall(request).execute()
            if (call.code == 401) {
                notifyServerSettings("Unauthorized Request")
            }
            call.code == 200
        } catch (e: UnknownHostException) {
            notifyServerSettings("Unknown Host")
            false;
        }
    }

//    fun delete(path: String) {
//        val request = Request.Builder()
//            .url("${this.basePath}/${path}")
//            .delete()
//    }
}
