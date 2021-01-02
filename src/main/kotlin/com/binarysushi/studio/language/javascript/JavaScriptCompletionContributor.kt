package com.binarysushi.studio.language.javascript

import com.intellij.codeInsight.completion.*
import com.intellij.lang.javascript.*
import com.intellij.lang.javascript.psi.*
import com.intellij.patterns.*
import com.intellij.patterns.PlatformPatterns.*

class JavaScriptCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            psiElement(JSTokenTypes.STRING_LITERAL)
                .withSuperParent(
                    3,
                    psiElement(JSCallExpression::class.java).withText(StandardPatterns.string().contains("require"))
                ),
            RequireCompletionProvider()
        )
    }
}