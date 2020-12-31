package com.binarysushi.studio.language.ds

import com.binarysushi.studio.StudioIcons
import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import javax.swing.Icon

class DSIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val containingFile = element.containingFile
        if (containingFile != null) {
            containingFile.name
            if (containingFile.name.endsWith(".ds")) {
                return StudioIcons.STUDIO_DS_ICON
            }
        }
        return null
    }
}