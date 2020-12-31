package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame

class StudioExecutionStack(private val process: StudioDebugProcess, private val suspendContext: StudioSuspendContext, val scriptThread: ScriptThread) :
    XExecutionStack(scriptThread.id.toString()) {

    private val stackFrames: List<StudioStackFrame> by lazy {
        scriptThread.callStack.map { call ->
            StudioStackFrame(process, scriptThread, call)
        }
    }

    override fun getTopFrame(): XStackFrame? {
        return stackFrames.firstOrNull()
    }

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer?) {
        suspendContext.setActiveExecutionStack(scriptThread.id)

        if (firstFrameIndex == 0) {
            container!!.addStackFrames(stackFrames, true)
        }
    }
}
