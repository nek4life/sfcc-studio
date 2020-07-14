package com.binarysushi.studio.debugger

import com.binarysushi.studio.StudioIcons
import com.binarysushi.studio.debugger.client.ObjectMember
import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.frame.presentation.*
import javax.swing.Icon

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

        if (member.type == "Array") {
            hasChildren = true
        }

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
            getIcon(member.value, member.type),
            getValuePresentation(member.value, member.type),
            hasChildren
        )
    }

    private fun getIcon(value: String, type: String): Icon {
        if (type == "Function") {
            return AllIcons.Nodes.Function
        }

        if (type == "string") {
            return AllIcons.Nodes.Static
        }

        if (value.contains("JavaClass") || type.startsWith("dw.")) {
            return StudioIcons.STUDIO_ICON
        }

        return AllIcons.Debugger.Value
    }

    private fun getValuePresentation(value: String, type: String) : XValuePresentation {
        if (type == "Function") {
            XKeywordValuePresentation(value)
        }

        if (type == "string") {
            return XStringValuePresentation(value)
        }

        if (type == "number") {
            return XNumericValuePresentation(value)
        }

        return XRegularValuePresentation(value, type)
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
