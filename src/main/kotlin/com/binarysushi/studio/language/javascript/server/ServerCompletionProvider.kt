package com.binarysushi.studio.language.javascript.server

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext

class ServerCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val caretPositionOffset = parameters.offset - parameters.position.textOffset
        val query = parameters.position.text.substring(1, caretPositionOffset) // Drop the first quote
        val project = parameters.position.project

        println("ServerCompletionProvider: $query")
    }
}
