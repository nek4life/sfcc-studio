package com.binarysushi.studio.debugger.client


import com.binarysushi.studio.webdav.StudioServerAuthenticator
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


    fun getThreads(onSuccess: (List<ScriptThread>?) -> Unit = {}, onFailure: (Any) -> Unit = {}) {
        val request = Request.Builder()
            .url("$baseURL/threads")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonBody = json.parse(ScriptThreadsResponse.serializer(), body)
                if (jsonBody.scriptThreads != null) {
                    onSuccess(jsonBody.scriptThreads)
                }
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
                println(e.message)
            }

            override fun onResponse(call: Call, response: Response) {}
        })
    }

    fun createBreakpoint(
        lineNumber: Int,
        scriptPath: String,
        onSuccess: (Breakpoint) -> Unit = {},
        onFailure: (Call) -> Unit = {}
    ) {
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

    fun getMembers(
        threadId: Int,
        frameIndex: Int,
        objectPath: String? = null,
        start: Int? = 0,
        count: Int? = 100,
        onSuccess: (ObjectMemberResponse) -> Unit = {},
        onFailure: (Call) -> Unit = {}
    ) {
        var url = "$baseURL/threads/${threadId}/frames/${frameIndex}/members?start=${start}&count=${count}"

        if (objectPath != null) {
            url = "${url}&object_path=${objectPath}"
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonResponse = json.parse(ObjectMemberResponse.serializer(), body)
                onSuccess(jsonResponse)
            }
        })
    }

    fun getVariables(
        threadId: Int,
        frameIndex: Int,
        start: Int? = 0,
        count: Int? = 100,
        onSuccess: (ObjectMemberResponse) -> Unit = {},
        onFailure: (Call) -> Unit = {}
    ) {
        val request = Request.Builder()
            .url("$baseURL/threads/${threadId}/frames/${frameIndex}/variables?start=${start}&count=${count}")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonResponse = json.parse(ObjectMemberResponse.serializer(), body)
                onSuccess(jsonResponse)
            }
        })
    }

    fun resume(threadId: Int) {
        val request = Request.Builder()
            .url("$baseURL/threads/${threadId}/resume")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("resume failed")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body!!.close()
            }

        })
    }
}
