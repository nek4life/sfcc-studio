package com.binarysushi.studio.language.dwjson

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.JsonLanguage
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class DWJsonCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(JsonLanguage.INSTANCE),
            DWJsonCompletionProvider()
        )
    }

    val properties = arrayOf(
        "cartridge",
        "hostname",
        "username",
        "password",
        "p12",
        "passphrase",
        "client-id",
        "client-secret",
        "code-version",
    )

    private inner class DWJsonCompletionProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet
        ) {

            if (parameters.originalFile.containingFile.fileType != DwJsonFileType) {
                return
            }

            val jsonProperty = PsiTreeUtil.getParentOfType(
                parameters.position,
                JsonStringLiteral::class.java
            ) ?: return

            if (jsonProperty.parent.firstChild == jsonProperty) {
                properties.forEach {
                    result.addElement(LookupElementBuilder.create(it))
                }
            }
        }
    }
}
