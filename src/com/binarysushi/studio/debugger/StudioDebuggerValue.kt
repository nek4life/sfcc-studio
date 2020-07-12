package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ObjectMember
import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation

class StudioDebuggerValue(
    private val process: StudioDebuggerProcess,
    private val thread: ScriptThread,
    private val stackFrame: StackFrame,
    private val member: ObjectMember,
    private val parentObjectPath: String? = null
) : XNamedValue(member.name) {

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(
            AllIcons.Debugger.Value,
            XStringValuePresentation(member.value),
            member.value == "[object Object]"
        )
    }

    override fun computeChildren(node: XCompositeNode) {
        var objectPath = member.name

        if (parentObjectPath != null) {
            objectPath = "${parentObjectPath}.${member.name}"
        }

        process.debuggerClient.getMembers(
            thread.id,
            stackFrame.index,
            objectPath = objectPath,
            onSuccess = { response ->
                val children = XValueChildrenList()

                for (childMember in response.objectMembers) {
                    children.add(StudioDebuggerValue(process, thread, stackFrame, childMember, objectPath))
                }

                node.addChildren(children, true)
            })
    }
}
