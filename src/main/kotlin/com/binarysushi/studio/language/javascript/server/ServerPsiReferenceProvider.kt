package com.binarysushi.studio.language.javascript.server

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

class ServerPsiReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(psiElement: PsiElement, processingContext: ProcessingContext): Array<PsiReference> {
        TODO("Not yet implemented")
    }

}
