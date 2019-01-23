package com.binarysushi.studio.webdav.clean;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class StudioCleanAction extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        ProgressManager.getInstance().run(
                new StudioCleanTask(project, "Cleaning cartridges...", true, PerformInBackgroundOption.ALWAYS_BACKGROUND)
        );
    }
}
