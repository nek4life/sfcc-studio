package com.binarysushi.studio.language.javascript.require

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

class RequirePsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
            registrar.registerReferenceProvider(
                PlatformPatterns.instanceOf(JSLiteralExpression::class.java),
                RequirePsiReferenceProvider()
            )
    }
}