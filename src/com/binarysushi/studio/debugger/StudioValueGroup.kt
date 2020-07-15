package com.binarysushi.studio.debugger

import com.intellij.icons.AllIcons
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueGroup
import javax.swing.Icon

class StudioValueGroup(val groupName: String, private val children: XValueChildrenList) : XValueGroup(groupName) {
    override fun isAutoExpand(): Boolean {
        return true
    }

    override fun getComment(): String? {
        return when(groupName.toLowerCase()) {
            "local" -> "Local Variable Scope"
            "global" -> "Global Variable Scope"
            "closure" -> "Closure Variable Scope"
            else -> "Unknown Variable Scope"
        }
    }

    override fun getName(): String {
        return super.getName().capitalize()
    }

    override fun computeChildren(node: XCompositeNode) {
        node.addChildren(children, true)
    }
}
