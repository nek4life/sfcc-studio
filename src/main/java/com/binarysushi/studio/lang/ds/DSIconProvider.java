package com.binarysushi.studio.lang.ds;

import com.binarysushi.studio.StudioIcons;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DSIconProvider extends IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            if (containingFile.getName() != null && containingFile.getName().endsWith(".ds")) {
                return StudioIcons.STUDIO_DS_ICON;
            }
        }
        return null;
    }
}
