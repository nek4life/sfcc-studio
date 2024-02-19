package com.binarysushi.studio.language.ds

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.UnknownFileType
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class DSFileTypeAssociator : StartupActivity, DumbAware {
    private val fileTypeManager: FileTypeManager = FileTypeManager.getInstance()

    override fun runActivity(project: Project) {
        ApplicationManager.getApplication().invokeLaterOnWriteThread {
            val javaScriptFileType = fileTypeManager.getFileTypeByExtension("js")
            if (javaScriptFileType !is UnknownFileType) {
                fileTypeManager.associateExtension(javaScriptFileType, "ds")
            }
        }
    }
}
