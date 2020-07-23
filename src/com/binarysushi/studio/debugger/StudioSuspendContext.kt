package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext

class StudioSuspendContext(private val process: StudioDebugProcess, private val thread: ScriptThread) : XSuspendContext() {
    private var activeExecutionStack: StudioExecutionStack? = null;

    init {
        activeExecutionStack = StudioExecutionStack(process, thread)
    }

    override fun getActiveExecutionStack(): XExecutionStack? {
        return activeExecutionStack
    }

    override fun getExecutionStacks(): Array<XExecutionStack> {
        return Array(1) { StudioExecutionStack(process, thread) }
    }
}
