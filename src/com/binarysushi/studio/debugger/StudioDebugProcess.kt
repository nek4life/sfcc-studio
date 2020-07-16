package com.binarysushi.studio.debugger

import com.binarysushi.studio.configuration.projectSettings.StudioConfigurationProvider
import com.binarysushi.studio.debugger.breakpoint.StudioDebuggerJSLineBreakpointHandler
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.intellij.javascript.debugger.breakpoints.JavaScriptLineBreakpointProperties
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.MessageType
import com.intellij.util.ArrayUtil
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext

class StudioDebugProcess(session: XDebugSession) : XDebugProcess(session) {
    private val config = session.project.service<StudioConfigurationProvider>()
    val debuggerClient = SDAPIClient(config.hostname, config.username, config.password)
    private val breakpointHandler = StudioDebuggerJSLineBreakpointHandler(this)
    private val debugger = SDAPIDebugger(session, this)

    override fun sessionInitialized() {
        debugger.connect()
    }

    override fun stop() {
        debugger.disconnect()
        session.reportMessage("Debug session stopped", MessageType.INFO)
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return StudioDebuggerEditorsProvider()
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>?> {
        val breakpointHandlers = super.getBreakpointHandlers()
        return ArrayUtil.append(breakpointHandlers, breakpointHandler)
    }

    fun addBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        if (debugger.connectionState === DebuggerConnectionState.CONNECTED) {
            debugger.addBreakpoint(xLineBreakpoint)
        } else {
            debugger.addAwaitingBreakpoint(xLineBreakpoint)
        }
    }

    fun removeBreakpoint(xLineBreakpoint: XLineBreakpoint<JavaScriptLineBreakpointProperties>) {
        if (debugger.connectionState === DebuggerConnectionState.CONNECTED) {
            debugger.removeBreakpoint(xLineBreakpoint)
        }
    }

    override fun resume(context: XSuspendContext?) {
        if (context != null) {
            val activeExecutionStack = context.activeExecutionStack as StudioExecutionStack
            debugger.resume(activeExecutionStack.scriptThread)
        }
    }

    override fun startStepInto(context: XSuspendContext?) {
        if (context != null) {
            val activeExecutionStack = context.activeExecutionStack as StudioExecutionStack
            debugger.stepInto(activeExecutionStack.scriptThread)
        }
    }

    override fun startStepOver(context: XSuspendContext?) {
        if (context != null) {
            val activeExecutionStack = context.activeExecutionStack as StudioExecutionStack
            debugger.stepOver(activeExecutionStack.scriptThread)
        }
    }

    override fun startStepOut(context: XSuspendContext?) {
        if (context != null) {
            val activeExecutionStack = context.activeExecutionStack as StudioExecutionStack
            debugger.stepOver(activeExecutionStack.scriptThread)
        }
    }
}
