package com.binarysushi.studio.toolWindow

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project

class StudioConsoleService(project: Project) {
    val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
}
