package com.binarysushi.studio.language.javascript

import com.binarysushi.studio.language.javascript.require.RequireCompletionProvider
import com.binarysushi.studio.language.javascript.server.ServerCompletionProvider
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement

class JavaScriptCompletionContributor : CompletionContributor() {
    private val serverMethods: PsiElementPattern.Capture<PsiElement> = psiElement(JSTokenTypes.STRING_LITERAL)
        .withSuperParent(
            3, psiElement(JSCallExpression::class.java)
                .andOr(
                    psiElement().withText(StandardPatterns.string().contains("server.post")),
                    psiElement().withText(StandardPatterns.string().contains("server.get")),
                )
        )

    private val requireCapturePattern: PsiElementPattern.Capture<PsiElement> = psiElement(JSTokenTypes.STRING_LITERAL)
        .withSuperParent(
            3,
            psiElement(JSCallExpression::class.java).withText(StandardPatterns.string().contains("require"))
        )

    init {
        extend(CompletionType.BASIC, requireCapturePattern, RequireCompletionProvider())
        extend(CompletionType.BASIC, serverMethods, ServerCompletionProvider())
    }
}