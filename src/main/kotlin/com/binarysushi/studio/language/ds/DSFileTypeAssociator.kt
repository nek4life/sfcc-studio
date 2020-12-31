package com.binarysushi.studio.language.ds

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.UnknownFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class DSFileTypeAssociator : StartupActivity {
    private val fileTypeManager: FileTypeManager = FileTypeManager.getInstance()

    override fun runActivity(project: Project) {
        val javaScriptFileType = fileTypeManager.getFileTypeByExtension("js")
        if (javaScriptFileType !is UnknownFileType) {
            fileTypeManager.associateExtension(javaScriptFileType, "ds")
        }
    }
}
