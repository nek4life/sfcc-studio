package com.binarysushi.studio.lang.ds;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UnknownFileType;
import org.jetbrains.annotations.NotNull;

public class DSFileTypeAssociator implements ApplicationComponent {
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();

    @Override
    public void initComponent() {
        FileType javaScriptFileType = fileTypeManager.getFileTypeByExtension("js");
        if (!(javaScriptFileType instanceof UnknownFileType)) {
            fileTypeManager.associateExtension(javaScriptFileType, "ds");
        }
    }

    @Override
    public void disposeComponent() {}

    @NotNull
    @Override
    public String getComponentName() {
        return "DSFileTypeAssociator";
    }
}
