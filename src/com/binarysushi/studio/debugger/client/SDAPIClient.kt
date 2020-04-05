package com.binarysushi.studio.debugger.client


import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.webdav.StudioServerAuthenticator
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.util.proxy.CommonProxy
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.nio.file.Paths

class SDAPIClient(private val project: Project) {
    private val config = project.service<StudioConfigurationProvider>()
    private val baseURL = "https://${config.hostname}/s/-/dw/debugger/v2_0"
    val idKey: Key<Int> = Key.create("STUDIO_BP_ID")

    @UnstableDefault
    private val json = Json(JsonConfiguration(encodeDefaults = false))

    val client = OkHttpClient.Builder()
        .proxySelector(CommonProxy.getInstance())
        .authenticator(StudioServerAuthenticator(config.username, config.password))
        .addInterceptor(Interceptor.invoke { chain ->
            val request = chain.request().newBuilder()
                .header("x-dw-client-id", "SFCCDebugger")
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(request)
        })
        .build()

    private fun getRelativeScriptPath(filePath: String): String {
        return filePath.substring(Paths.get(project.basePath.toString(), "cartridges").toString().length)
    }

    fun createSession() {
        val request = Request.Builder()
            .url("$baseURL/client")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {}
        })
    }

    fun deleteSession() {
        val request = Request.Builder()
            .url("$baseURL/client")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

            }
        })
    }

    fun createBreakpoint(xBreakPoint: XLineBreakpoint<XBreakpointProperties<*>?>) {
        val breakpoint = Breakpoint(
            lineNumber = xBreakPoint.line,
            scriptPath = xBreakPoint.presentableFilePath.substring(
                Paths.get(project.basePath.toString(), "cartridges").toString().length
            )
        )

        val breakpoints = Breakpoints(listOf(breakpoint))
        val jsonList = json.stringify(Breakpoints.serializer(), breakpoints)

        val request = Request.Builder()
            .url("$baseURL/breakpoints")
            .post(jsonList.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonResponse = json.parse(BreakpointsResponse.serializer(), body)
                xBreakPoint.putUserData(idKey, jsonResponse.breakpoints[0].id!!)
            }
        })
    }

    fun deleteBreakpoint(xBreakPoint: XLineBreakpoint<XBreakpointProperties<*>?>) {
        val request = Request.Builder()
            .url("$baseURL/breakpoints/${xBreakPoint.getUserData(idKey)}")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body!!.string())
            }
        })
    }
}
