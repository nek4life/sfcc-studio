package com.binarysushi.studio.language.javascript

import com.intellij.lang.javascript.psi.*
import com.intellij.patterns.*
import com.intellij.psi.*

class RequirePsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        
            registrar.registerReferenceProvider(
                PlatformPatterns.instanceOf(JSLiteralExpression::class.java),
                RequirePsiReferenceProvider()
            )
    }
}