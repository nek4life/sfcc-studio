package com.binarysushi.studio.debugger

import com.binarysushi.studio.cartridges.CartridgePathUtil
import com.binarysushi.studio.debugger.client.ObjectMember
import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.XDebuggerBundle
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XValueChildrenList
import java.nio.file.Paths

class StudioStackFrame(
    private val process: StudioDebugProcess,
    private val thread: ScriptThread,
    private val stackFrame: StackFrame
) : XStackFrame() {

    override fun customizePresentation(component: ColoredTextContainer) {
        val position = sourcePosition
        if (position != null) {
            component.append(stackFrame.location.functionName, SimpleTextAttributes.REGULAR_ATTRIBUTES)
            component.append(" – " + position.file.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
            component.append(":" + (position.line + 1), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            component.setIcon(AllIcons.Debugger.Frame)
        } else {
            component.append(XDebuggerBundle.message("invalid.frame"), SimpleTextAttributes.ERROR_ATTRIBUTES)
        }
    }

    override fun getEvaluator(): XDebuggerEvaluator? {
        return StudioEvaluator(process, thread, stackFrame)
    }

    override fun getSourcePosition(): XSourcePosition? {
        // TODO look up cartridge path and get match based on that to remove hardcoded cartridges

        val path = CartridgePathUtil.getAbsolutFilePathFromCartridgeRelativePath(process.session.project, stackFrame.location.scriptPath)

//        val basePath = process.session.project.basePath
//        val filePath = "${basePath}/cartridges${stackFrame.location.scriptPath}"
        val virtualFile = LocalFileSystem.getInstance().findFileByNioFile(Paths.get(path).normalize())
        return XDebuggerUtil.getInstance().createPosition(virtualFile, stackFrame.location.lineNumber - 1);
    }

    override fun computeChildren(node: XCompositeNode) {
        process.debuggerClient.getVariables(thread.id, stackFrame.index, onSuccess = { response ->
            val scopes = HashMap<String, MutableList<ObjectMember>>()
            val scopeChildrenList = XValueChildrenList()

            // Create map of scopes and store child object members for each scope
            for (member in response.objectMembers) {
                if (scopes.containsKey(member.scope)) {
                    scopes[member.scope]?.add(member)
                } else {
                    scopes[member.scope] = mutableListOf()
                }
            }

            // Add new value group for each scope available
            for (scope in scopes.toSortedMap().keys.reversed()) {
                val children = XValueChildrenList()

                for (member in scopes[scope]!!) {
                    children.add(StudioNamedValue(process, thread, stackFrame, member))
                }

                scopeChildrenList.addBottomGroup(StudioValueGroup(scope, children))
            }

            node.addChildren(scopeChildrenList, true)
        })

        super.computeChildren(node)
    }
}
