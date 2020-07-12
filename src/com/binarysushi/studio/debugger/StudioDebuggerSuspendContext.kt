package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext

class StudioDebuggerSuspendContext(private val process: StudioDebuggerProcess, private val thread: ScriptThread) : XSuspendContext() {
    private var activeExecutionStack: StudioDebuggerExecutionStack? = null;

    init {
        activeExecutionStack = StudioDebuggerExecutionStack(process, thread)
    }

    override fun getActiveExecutionStack(): XExecutionStack? {
        return activeExecutionStack
    }

    override fun getExecutionStacks(): Array<XExecutionStack> {
        return Array(1) { StudioDebuggerExecutionStack(process, thread)}
    }
}
