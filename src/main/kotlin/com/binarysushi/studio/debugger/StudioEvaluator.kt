package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.util.PlatformIcons
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XNamedValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation

class StudioEvaluator(
    private val process: StudioDebugProcess,
    private val thread: ScriptThread,
    private val stackFrame: StackFrame
) : XDebuggerEvaluator() {
    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        process.debuggerClient.evaluate(thread.id, stackFrame.index, expression, onSuccess = { response ->
            callback.evaluated(object : XNamedValue(response.expression) {
                override fun computePresentation(node: XValueNode, place: XValuePlace) {
                    node.setPresentation(PlatformIcons.VARIABLE_ICON, XStringValuePresentation(response.result), false)
                }
            })
        })
    }
}
