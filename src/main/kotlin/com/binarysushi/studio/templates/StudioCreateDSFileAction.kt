package com.binarysushi.studio.templates

import com.binarysushi.studio.StudioIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class StudioCreateDSFileAction : CreateFileFromTemplateAction("DS File", "Creates a DS file", StudioIcons.STUDIO_DS_ICON), DumbAware {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("DS File")
            .addKind("DS File", StudioIcons.STUDIO_DS_ICON, "DS File.ds")
            .addKind("DS Script Node File", StudioIcons.STUDIO_DS_ICON, "DS Script Node File.ds")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String {
        return "StudioCreateDSFile"
    }
}
