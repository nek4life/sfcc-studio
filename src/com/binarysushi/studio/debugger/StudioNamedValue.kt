package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ObjectMember
import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation

class StudioNamedValue(
    private val process: StudioDebugProcess,
    private val thread: ScriptThread,
    private val stackFrame: StackFrame,
    private val member: ObjectMember,
    private val parentObjectPath: String? = null
) : XNamedValue(member.name) {

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        // TODO Figure out which values have children
        // TODO Update presentation based on value type
        var hasChildren = false

        if (member.value.toLowerCase().contains("[object")) {
            hasChildren = true
        }

        if (member.value.toLowerCase().contains("[javaclass")) {
            hasChildren = true
        }

        if (member.value.toLowerCase().contains("[map")) {
            hasChildren = true
        }

        node.setPresentation(
            AllIcons.Debugger.Value,
            XStringValuePresentation(member.value),
            hasChildren
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
                    children.add(StudioNamedValue(process, thread, stackFrame, childMember, objectPath))
                }

                node.addChildren(children, true)
            })
    }
}
