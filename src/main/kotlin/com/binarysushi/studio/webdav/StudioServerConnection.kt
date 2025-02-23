package com.binarysushi.studio.webdav

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.instance.StudioServerAuthenticator
import com.binarysushi.studio.instance.StudioServerNotifier
import com.intellij.util.net.JdkProxyProvider
import com.intellij.util.proxy.CommonProxy
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.UnknownHostException
import java.nio.file.Paths

class StudioServerConnection(config: StudioConfigurationProvider) {
    val basePath = "https://${config.hostname}/on/demandware.servlet/webdav/Sites/Cartridges/${config.version}"
    val client = OkHttpClient.Builder()
        .proxySelector(JdkProxyProvider.getInstance().proxySelector)
        .authenticator(StudioServerAuthenticator(config.username, config.password))
        .build()

    private fun getCartridgeName(rootPath: String): String {
        return Paths.get(rootPath).fileName.toString()
    }

    fun getRemoteFilePath(rootPath: String, filePath: String): String {
        val relPath = filePath.substring(rootPath.length)
        val cartridgeName = getCartridgeName(rootPath)
        return "$basePath/$cartridgeName$relPath"
    }

    fun getRemoteDirPaths(rootPath: String, filePath: String?): ArrayList<String> {
        val serverPaths = ArrayList<String>()
        // There may be no parent path in root directory
        val relPath =
            Paths.get(rootPath).relativize(Paths.get(filePath)).parent
        val cartridgeName = getCartridgeName(rootPath)
        var dirPath = ""
        if (relPath != null) {
            for (subPath in relPath) {
                dirPath = dirPath + "/" + subPath.fileName
                serverPaths.add("$basePath/$cartridgeName$dirPath")
            }
        }
        return serverPaths
    }

    fun testRemoteFileExistence(path: String): Int {
        val request = Request.Builder()
            .url(path)
            .head()
            .build()

        var code = -1

        try {
            client.newCall(request).execute().use {
                if (it.code == 401) {
                    StudioServerNotifier.notify("Unauthorized Request")
                }
                code = it.code
            }
        } catch (e: UnknownHostException) {
            StudioServerNotifier.notify("Unknown Host")
        }

        return code
    }
}
