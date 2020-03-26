package com.binarysushi.studio.debugger

import com.binarysushi.studio.StudioIcons
import com.binarysushi.studio.configuration.run.StudioDebuggerRunConfiguration
import com.binarysushi.studio.debugger.client.SDAPIClient
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager

class StudioScriptDebuggerRunner : ProgramRunner<RunnerSettings> {

    override fun getRunnerId(): String {
        return "StudioScriptDebuggerRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == DefaultDebugExecutor.EXECUTOR_ID && profile is StudioDebuggerRunConfiguration
    }

    @Throws(ExecutionException::class)
    override fun execute(environment: ExecutionEnvironment) {
        FileDocumentManager.getInstance().saveAllDocuments()

        val session = XDebuggerManager.getInstance(environment.project)
            .startSessionAndShowTab(
                "SFCC Debugger",
                StudioIcons.STUDIO_ICON,
                environment.contentToReuse,
                false,
                object : XDebugProcessStarter() {
                    @Throws(ExecutionException::class)
                    override fun start(session: XDebugSession): XDebugProcess {
                        val client = session.project.service<SDAPIClient>()
                        client.createSession()
                        return StudioDebuggerProcess(session)
                    }
                })

        environment.contentToReuse = session.runContentDescriptor
    }
}
