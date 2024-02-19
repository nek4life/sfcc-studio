package com.binarysushi.studio.language.javascript.server

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

class ServerPsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(com.intellij.lang.javascript.JSTokenTypes.STRING_LITERAL),
            ServerPsiReferenceProvider())
    }
}