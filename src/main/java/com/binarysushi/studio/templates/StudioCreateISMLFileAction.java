package com.binarysushi.studio.templates;

import com.binarysushi.studio.StudioIcons;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;

public class StudioCreateISMLFileAction extends CreateFileFromTemplateAction implements DumbAware {
    public StudioCreateISMLFileAction() {
        super("ISML File", "Creates a new ISML file",  StudioIcons.STUDIO_ISML_ICON);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("ISML")
                .addKind("ISML File", StudioIcons.STUDIO_ISML_ICON, "ISML File.isml");
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "StudioCreateISMLFile";
    }
}
