package com.binarysushi.studio.instance.clients

import com.binarysushi.studio.instance.StudioServerAuthenticator
import com.github.sardine.DavResource
import com.github.sardine.SardineFactory
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.net.ProxySelector
import kotlin.jvm.Throws

/**
 * Client to interact with Commerce Cloud's Webdav Server
 */
class WebDavClient(
    hostname: String,
    username: String,
    password: String,
    proxySelector: ProxySelector? = null
) {
    private val sardine = SardineFactory.begin(username, password, proxySelector)
    private val httpClient = OkHttpClient.Builder()
        .proxySelector(proxySelector!!)
        .authenticator(StudioServerAuthenticator(username, password))
        .build()

    val baseURI = "https://${hostname}/on/demandware.servlet/webdav/Sites"

    @Throws(IOException::class)
    fun list(url: String): MutableList<DavResource>? {
        return sardine.list("${baseURI}${url}")
    }

    @Throws(IOException::class)
    fun put(url: String, localFile: File, contentType: String? = null) {
        sardine.put("${baseURI}${url}", localFile, contentType)
    }

    @Throws(IOException::class)
    fun unzip(url: String) {
        val formBody = FormBody.Builder()
            .add("method", "UNZIP")
            .build()

        val unzipRequest = Request.Builder()
            .url("${baseURI}${url}")
            .post(formBody)
            .build()

        httpClient.newCall(unzipRequest).execute()
    }

    fun createDirectory(url: String) {
        sardine.createDirectory("${baseURI}${url}")
    }

    @Throws(IOException::class)
    fun delete(url: String) {
        sardine.delete("${baseURI}${url}")
    }
}

object TopLevelDavFolders {
    const val IMPEX = "/Impex"
    const val CARTRIDGES = "/Cartridges"
    const val LOGS = "/Logs"
}