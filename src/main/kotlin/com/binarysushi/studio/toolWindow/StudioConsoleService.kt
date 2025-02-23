package com.binarysushi.studio.toolWindow

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class StudioConsoleService(project: Project) {
    val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
}
