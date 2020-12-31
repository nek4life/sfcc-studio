package com.binarysushi.studio.language.javascript

import com.intellij.lang.javascript.psi.*
import com.intellij.psi.*
import com.intellij.util.*

class RequirePsiReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        return if (isRequire(element)) arrayOf(RequirePsiReference(element)) else arrayOf()
    }

    private fun isRequire(element: PsiElement): Boolean {
        val targetElement = element.parent?.parent ?: return false
        val regex = "\\/cartridge\\/".toRegex()

        if (targetElement is JSCallExpression) {
            val targetElementText = targetElement.children[0].text
            if (targetElementText.contains("require") && element.text.contains(regex)) {
                return true
            }
        }

        return false
    }
}