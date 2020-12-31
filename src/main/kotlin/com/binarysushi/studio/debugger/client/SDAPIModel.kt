package com.binarysushi.studio.debugger.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Breakpoint(
    val id: Int? = null,
    val condition: String? = null,
    @SerialName("line_number") val lineNumber: Int,
    @SerialName("script_path") val scriptPath: String
)

@Serializable
data class Breakpoints(
    val breakpoints: List<Breakpoint>
)

@Serializable
data class BreakpointsResponse(
    @SerialName("_v") val version: String,
    val breakpoints: List<Breakpoint>
)

@Serializable
data class EvalResultResponse(
    @SerialName("_v") val version: String,
    val expression: String,
    val result: String
)

@Serializable
data class FaultResponse(
    @SerialName("_v") val version: String,
    val fault: Fault
)

@Serializable
data class Fault(
    val message: String,
    val type: String
)

@Serializable
data class Location(
    @SerialName("function_name") val functionName: String,
    @SerialName("line_number") val lineNumber: Int,
    @SerialName("script_path") val scriptPath: String
)

@Serializable
data class ObjectMemberResponse(
 @SerialName("_v") val version: String,
 @SerialName("object_members") val objectMembers: List<ObjectMember> = listOf(),
 val count: Int,
 val start: Int,
 val total: Int
)

@Serializable
data class ObjectMember(
    val name: String,
    val parent: String,
    val scope: String,
    val type: String,
    val value: String
)

@Serializable
data class ScriptThreadsResponse(
    @SerialName("_v") val version: String,
    @SerialName("script_threads") val scriptThreads: List<ScriptThread>? = listOf()
)

@Serializable
data class ScriptThread(
    @SerialName("_v") val version: String? = null,
    @SerialName("call_stack") val callStack: List<StackFrame> = listOf(),
    val id: Int,
    val status: String
)

@Serializable
data class StackFrame(
    val index: Int,
    val location: Location
)


