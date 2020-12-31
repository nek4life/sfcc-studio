package com.binarysushi.studio.debugger.client

import com.binarysushi.studio.webdav.StudioServerAuthenticator
import com.intellij.util.proxy.CommonProxy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.reflect.Type

private val json = Json {
    encodeDefaults = false
}

class JSONCallback(val type: Type, val then: (Any) -> Unit) : Callback {
    override fun onFailure(call: Call, e: IOException) {
        TODO("Not yet implemented")
    }

    @ExperimentalSerializationApi
    override fun onResponse(call: Call, response: Response) {
        val body = response.body!!.string()
        then(json.decodeFromString(serializer(type), body))
    }
}

class SDAPIClient(private val hostname: String, private val username: String, private val password: String) {
    private val baseURL = "https://$hostname/s/-/dw/debugger/v2_0"
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
        })
    }

    fun deleteSession(onSuccess: () -> Unit, onFailure: () -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/client")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                onSuccess()
            }
        })
    }

    fun cancelRequests(requestTag: String) {
        for (call in client.dispatcher.queuedCalls()) {
            if (call.request().tag() != null && call.request().tag() == requestTag) {
                call.cancel()
            }
        }

        for (call in client.dispatcher.runningCalls()) {
            if (call.request().tag() != null && call.request().tag() == requestTag) {
                call.cancel()
            }
        }
    }

    fun getThreads(
        requestTag: String? = null,
        onSuccess: (List<ScriptThread>?) -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Any) -> Unit = {}
    ) {

        val request = Request.Builder()
            .url("$baseURL/threads")

        if (requestTag != null) {
            request.tag(requestTag)
        }

        client.newCall(request.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                if (response.isSuccessful) {
                    val jsonBody = json.decodeFromString(ScriptThreadsResponse.serializer(), body)
                    if (jsonBody.scriptThreads != null) {
                        onSuccess(jsonBody.scriptThreads)
                    }
                } else {
                    val jsonBody = json.decodeFromString(FaultResponse.serializer(), body)
                    onError(jsonBody.fault)
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

            override fun onResponse(call: Call, response: Response) {
                TODO("Not yet implemented")
            }
        })
    }

    fun createBreakpoint(
        lineNumber: Int,
        scriptPath: String,
        onSuccess: (Breakpoint) -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Call) -> Unit = {}
    ) {
        val breakpoint = Breakpoint(
            lineNumber = lineNumber,
            scriptPath = scriptPath
        )

        val breakpoints = Breakpoints(listOf(breakpoint))
        val jsonList = json.encodeToString(Breakpoints.serializer(), breakpoints)

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
                if (response.isSuccessful) {
                    val jsonBody = json.decodeFromString(BreakpointsResponse.serializer(), body)
                    onSuccess(jsonBody.breakpoints[0])
                } else {
                    val jsonBody = json.decodeFromString(FaultResponse.serializer(), body)
                    onError(jsonBody.fault)
                }
            }
        })
    }

    fun deleteBreakpoint(breakpointId: Int, onSuccess: (Any) -> Unit = {}, onFailure: (Any) -> Unit = {}) {
        val request = Request.Builder()
            .url("$baseURL/breakpoints/$breakpointId")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // not sure if we need to do anything when breakpoint is removed
                onSuccess(response.body!!.close())
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
        var url = "$baseURL/threads/$threadId/frames/$frameIndex/members?start=$start&count=$count"

        if (objectPath != null) {
            url = "$url&object_path=$objectPath"
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
                val jsonResponse = json.decodeFromString(ObjectMemberResponse.serializer(), body)
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
            .url("$baseURL/threads/$threadId/frames/$frameIndex/variables?start=$start&count=$count")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val jsonResponse = json.decodeFromString(ObjectMemberResponse.serializer(), body)
                onSuccess(jsonResponse)
            }
        })
    }

    fun resume(
        threadId: Int,
        onSuccess: () -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Any) -> Unit = {}
    ) {
        val request = Request.Builder()
            .url("$baseURL/threads/$threadId/resume")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("resume failed")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body!!.close()
                onSuccess()
            }
        })
    }

    fun stepInto(
        threadId: Int,
        onSuccess: (ScriptThread) -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Any) -> Unit = {}
    ) {
        val request = Request.Builder()
            .url("$baseURL/threads/$threadId/into")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("step into failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = json.decodeFromString(ScriptThread.serializer(), response.body!!.string())
                    onSuccess(jsonResponse)
                } else {
                    val jsonResponse = json.decodeFromString(FaultResponse.serializer(), response.body!!.string())
                    onError(jsonResponse.fault)
                }
            }
        })
    }

    fun stepOver(
        threadId: Int,
        onSuccess: (ScriptThread) -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Any) -> Unit = {}
    ) {
        val request = Request.Builder()
            .url("$baseURL/threads/$threadId/over")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("step over failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = json.decodeFromString(ScriptThread.serializer(), response.body!!.string())
                    onSuccess(jsonResponse)
                } else {
                    val jsonResponse = json.decodeFromString(FaultResponse.serializer(), response.body!!.string())
                    onError(jsonResponse.fault)
                }
            }
        })
    }

    fun stepOut(
        threadId: Int,
        onSuccess: (ScriptThread) -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Any) -> Unit = {}
    ) {
        val request = Request.Builder()
            .url("$baseURL/threads/$threadId/out")
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("step out failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = json.decodeFromString(ScriptThread.serializer(), response.body!!.string())
                    onSuccess(jsonResponse)
                } else {
                    val jsonResponse = json.decodeFromString(FaultResponse.serializer(), response.body!!.string())
                    onError(jsonResponse.fault)
                }
            }
        })
    }

    fun evaluate(
        threadId: Int,
        frameIndex: Int,
        expression: String,
        onSuccess: (EvalResultResponse) -> Unit = {},
        onError: (Fault) -> Unit = {},
        onFailure: (Any) -> Unit = {}
    ) {
        val request = Request.Builder()
            .url("$baseURL/threads/$threadId/frames/$frameIndex/eval?expr=$expression")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = json.decodeFromString(EvalResultResponse.serializer(), response.body!!.string())
                    onSuccess(jsonResponse)
                } else {
                    val jsonResponse = json.decodeFromString(FaultResponse.serializer(), response.body!!.string())
                    onError(jsonResponse.fault)
                }
            }
        })
    }
}
