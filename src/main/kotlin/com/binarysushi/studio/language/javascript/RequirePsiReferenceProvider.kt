package com.binarysushi.studio.language.javascript

import com.intellij.lang.javascript.psi.*
import com.intellij.psi.*
import com.intellij.util.*

class RequirePsiReferenceProvider : PsiReferenceProvider() {

    // TODO Find better way to ensure that only require contained in an SFCC project are matched without hurting performance
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        return if (isRequire(element) && element.text.contains("/cartridge/")) {
            arrayOf(RequirePsiReference(element))
        } else {
            arrayOf()
        }
    }

    /**
     * Find the outer PsiElement and make sure it is a JSCallExpression and that it is a require
     * function call.
     *
     * See https://jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_elements.html for
     * more details regarding PsiElements
     */
    private fun isRequire(element: PsiElement): Boolean {
        val targetElement = element.parent?.parent ?: return false

        if (targetElement is JSCallExpression) {
            val targetElementText = targetElement.children[0].text
            if (targetElementText.contains("require")) {
                return true
            }
        }

        return false
    }
}