package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext
import java.util.concurrent.ConcurrentHashMap

class StudioSuspendContext(
    process: StudioDebugProcess,
    currentThread: ScriptThread,
    currentThreads: ConcurrentHashMap<Int, ScriptThread>
) : XSuspendContext() {
    private var activeExecutionStack: StudioExecutionStack?
    private var stackFrames: MutableList<StudioExecutionStack>

    init {
        activeExecutionStack = null
        stackFrames = mutableListOf()

        currentThreads.forEach {
            stackFrames.add(StudioExecutionStack(process, this, it.value))
        }

        setActiveExecutionStack(currentThread.id)
    }

    override fun getActiveExecutionStack(): XExecutionStack? {
        return activeExecutionStack
    }

    override fun getExecutionStacks(): Array<XExecutionStack> {
        return stackFrames.toTypedArray()
    }

    fun setActiveExecutionStack(threadId: Int) {
        stackFrames.forEach {
            if (it.scriptThread.id == threadId) {
                activeExecutionStack = it
            }
        }
    }
}
