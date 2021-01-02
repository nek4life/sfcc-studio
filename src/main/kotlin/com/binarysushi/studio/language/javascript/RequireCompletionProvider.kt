package com.binarysushi.studio.language.javascript

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import com.intellij.util.*

class RequireCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val caretPositionOffset = parameters.offset - parameters.position.textOffset
        val query = parameters.position.text.substring(1, caretPositionOffset)

        when {
            query.startsWith("*") -> handleStarCompletion(parameters, context, result)
            query.startsWith("") -> handleTildeCompletion(parameters, context, result)
            query.startsWith("dw/") -> handleApiCompletion(parameters, context, result)
            else -> {
                result.addElement(LookupElementBuilder.create("dw/some/path"))
            }
        }
    }

    private fun handleTildeCompletion(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val filePath = parameters.originalFile.virtualFile.path

        result.addElement(LookupElementBuilder.create("tilde"))
    }

    private fun handleStarCompletion(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addElement(LookupElementBuilder.create("star"))
    }

    private fun handleApiCompletion(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addElement(LookupElementBuilder.create("api"))
    }
}