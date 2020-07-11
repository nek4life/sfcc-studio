package com.binarysushi.studio.debugger.client


import com.binarysushi.studio.webdav.StudioServerAuthenticator
import com.intellij.openapi.util.Key
import com.intellij.util.proxy.CommonProxy
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializerByTypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.reflect.Type


@UnstableDefault
private val json = Json(JsonConfiguration(encodeDefaults = false))

@OptIn(UnstableDefault::class)
class JSONCallback(val type: Type, val then: (Any) -> Unit) : Callback {
    override fun onFailure(call: Call, e: IOException) {
        TODO("Not yet implemented")
    }

    override fun onResponse(call: Call, response: Response) {
        val body = response.body!!.string()
        then(json.parse(serializerByTypeToken(type), body))
    }
}

class SDAPIClient(private val hostname: String, private val username: String, private val password: String) {
    private val baseURL = "https://${hostname}/s/-/dw/debugger/v2_0"
    private val CLIENT_ID = "SFCCDebugger"
    private val idKey: Key<Int> = Key.create("STUDIO_BP_ID")


    private val client = OkHttpClient.Builder()
        .proxySelector(CommonProxy.getInstance())
        .authenticator(StudioServerAuthenticator(username, password))
        .addInterceptor(Interceptor.invoke { chain ->
            val request = chain.request().newBuilder()
                .header("x-dw-client-id", CLIENT_ID)
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(request)
        })
        .build()


    fun createSession(then: ((Response)) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/client")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Sheeeeeeit")
                println(call)
            }

            override fun onResponse(call: Call, response: Response) {
                then(response)
            }
        });
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


    fun getThreads(then: (List<ScriptThread>) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/threads")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonBody = json.parse(ScriptThreadsResponse.serializer(), body)
                jsonBody.scriptThreads?.let { then(it) }
            }
        })
    }

    fun resetThreads() {
        val request = Request.Builder()
            .url("$baseURL/threads/reset")
            .post("".toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                TODO("Not yet implemented")
            }
        })
    }

    fun createBreakpoint(lineNumber: Int, scriptPath: String, onSuccess: (Breakpoint) -> Unit = {}, onFailure: (Call) -> Unit = {}) {
        val breakpoint = Breakpoint(
            lineNumber = lineNumber,
            scriptPath = scriptPath
        )

        val breakpoints = Breakpoints(listOf(breakpoint))
        val jsonList = json.stringify(Breakpoints.serializer(), breakpoints)

        val request = Request.Builder()
            .url("$baseURL/breakpoints")
            .post(jsonList.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonResponse = json.parse(BreakpointsResponse.serializer(), body)
                onSuccess(jsonResponse.breakpoints[0])
            }
        })
    }

    fun deleteBreakpoint(breakpointId: Int, onSuccess: (Any) -> Unit = {}, onFailure: (Any) -> Unit = {}) {
        val request = Request.Builder()
            .url("$baseURL/breakpoints/${breakpointId}")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                onSuccess(response.body!!.string())
            }
        })
    }
}
