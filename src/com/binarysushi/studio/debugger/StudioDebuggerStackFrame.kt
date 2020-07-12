package com.binarysushi.studio.debugger

import com.binarysushi.studio.debugger.client.ScriptThread
import com.binarysushi.studio.debugger.client.StackFrame
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XValueChildrenList
import java.nio.file.Paths

class StudioDebuggerStackFrame(
    private val process: StudioDebuggerProcess,
    private val thread: ScriptThread,
    private val stackFrame: StackFrame
) : XStackFrame() {
    override fun getSourcePosition(): XSourcePosition? {
        // Maybe try to match a breakpoint and file from that instead?
        val basePath = process.session.project.basePath
        val filePath = "${basePath}/cartridges${stackFrame.location.scriptPath}"
        val virtualFile = LocalFileSystem.getInstance().findFileByNioFile(Paths.get(filePath).normalize())
        return XDebuggerUtil.getInstance().createPosition(virtualFile, stackFrame.location.lineNumber);
    }

    override fun computeChildren(node: XCompositeNode) {
        process.debuggerClient.getMembers(thread.id, stackFrame.index, onSuccess = { response ->
            val children = XValueChildrenList()

            for (member in response.objectMembers) {
                children.add(StudioDebuggerValue(process, thread, stackFrame, member))
            }

            node.addChildren(children, true)
        })

        super.computeChildren(node)
    }
}
