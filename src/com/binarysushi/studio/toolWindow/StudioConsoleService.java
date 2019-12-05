package com.binarysushi.studio.toolWindow;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;

public class StudioConsoleService {
    private final ConsoleView consoleView;

    public StudioConsoleService(Project project) {
        consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
    }

    public ConsoleView getConsoleView() {
        return consoleView;
    }
}
