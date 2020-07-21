package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ObjectMember
import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XValueChildrenList
import java.nio.file.Paths

class StudioStackFrame(
    private val process: StudioDebugProcess,
    private val thread: ScriptThread,
    private val stackFrame: StackFrame
) : XStackFrame() {
    override fun getSourcePosition(): XSourcePosition? {
        // Maybe try to match a breakpoint and file from that instead?
        val basePath = process.session.project.basePath
        val filePath = "${basePath}/cartridges${stackFrame.location.scriptPath}"
        val virtualFile = LocalFileSystem.getInstance().findFileByNioFile(Paths.get(filePath).normalize())
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