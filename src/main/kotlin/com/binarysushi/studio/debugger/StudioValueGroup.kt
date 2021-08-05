package com.binarysushi.studio.debugger

import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueGroup
import java.util.*

/**
 * Used to display groups variables in the variables panel. These groups are being used to
 * group the variables into scopes. Display can be customized by overriding the methods found here.
 */
class StudioValueGroup(private val groupName: String, private val children: XValueChildrenList) :
    XValueGroup(groupName) {
    override fun isAutoExpand(): Boolean {
        return true
    }

    override fun getComment(): String? {
        return when (groupName.lowercase()) {
            "local" -> "Local Variable Scope"
            "global" -> "Global Variable Scope"
            "closure" -> "Closure Variable Scope"
            else -> "Unknown Variable Scope"
        }
    }

    override fun getName(): String {
        return super.getName()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun computeChildren(node: XCompositeNode) {
        node.addChildren(children, true)
    }
}
