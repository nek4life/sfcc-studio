package com.binarysushi.studio.language.javascript

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import com.intellij.json.*
import com.intellij.lang.javascript.*
import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.search.*
import com.intellij.util.*
import org.jetbrains.annotations.*

class RequireCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val caretPositionOffset = parameters.offset - parameters.position.textOffset
        val query = parameters.position.text.substring(1, caretPositionOffset) // Drop the first quote

        when {
            query.startsWith("*") -> handleStarCompletion(parameters, context, result)
            query.startsWith("") -> handleTildeCompletion(parameters, context, result)
            query.startsWith("dw/") -> handleApiCompletion(parameters, context, result)
            else -> {
                result.addElement(LookupElementBuilder.create("dw/some/path"))
            }
        }
    }

    private fun findFiles(project: @NotNull Project): List<VirtualFile> {
        val files = FileTypeIndex.getFiles(
            JavaScriptFileType.INSTANCE,
            GlobalSearchScope.projectScope(project)
        ) + FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(project))

        return files.filter { it.path.contains("cartridge") || it.path.contains("module") }
    }

    /**
     * Tilde should only complete files that are relative to the cartridge path where the original
     * file is located
     *
     */
    private fun handleTildeCompletion(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        findFiles(parameters.position.project).forEach {
            result.addElement(LookupElementBuilder.create(it))
        }
    }

    /**
     * Star should complete files in any cartridge on the active cartridge path
     *
     */
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