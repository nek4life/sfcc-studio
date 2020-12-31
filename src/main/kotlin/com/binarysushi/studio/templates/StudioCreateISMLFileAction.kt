package com.binarysushi.studio.templates

import com.binarysushi.studio.StudioIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class StudioCreateISMLFileAction :
    CreateFileFromTemplateAction("ISML File", "Creates a new ISML file", StudioIcons.STUDIO_ISML_ICON), DumbAware {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("ISML").addKind("ISML File", StudioIcons.STUDIO_ISML_ICON, "ISML File.isml")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String {
        return "StudioCreateISMLFile"
    }
}
