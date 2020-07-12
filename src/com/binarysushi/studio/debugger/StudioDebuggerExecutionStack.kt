package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame

class StudioDebuggerExecutionStack(private val process: StudioDebuggerProcess, val scriptThread: ScriptThread) :
    XExecutionStack(scriptThread.id.toString()) {
    val stackFrames: MutableList<StudioDebuggerStackFrame> = mutableListOf();

    init {
        for (call in scriptThread.callStack) {
            stackFrames.add(StudioDebuggerStackFrame(process, scriptThread, call))
        }
    }

    override fun getTopFrame(): XStackFrame? {
        return stackFrames.first()
    }

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer?) {
        container!!.addStackFrames(stackFrames, true)
    }
}
