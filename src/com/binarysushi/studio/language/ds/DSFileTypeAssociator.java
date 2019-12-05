package com.binarysushi.studio.language.ds;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class DSFileTypeAssociator implements StartupActivity {
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();

    @Override
    public void runActivity(@NotNull Project project) {
        FileType javaScriptFileType = fileTypeManager.getFileTypeByExtension("js");
        if (!(javaScriptFileType instanceof UnknownFileType)) {
            fileTypeManager.associateExtension(javaScriptFileType, "ds");
        }
    }
}
