package com.binarysushi.studio.webdav.clean

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressManager

class StudioCleanAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        ProgressManager.getInstance().run(
            StudioCleanTask(project, "Cleaning cartridges...", true, PerformInBackgroundOption.ALWAYS_BACKGROUND)
        )
    }
}
