package com.binarysushi.studio.language.javascript

import com.binarysushi.studio.*
import com.binarysushi.studio.cartridges.*
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
        val project = parameters.position.project

        when {
            query.startsWith("~") -> handleCompletion(project, result, false)
            query.startsWith("*") -> handleCompletion(project, result, false)
            query.startsWith("dw/") -> handleApiCompletion(result)
            else -> {
                handleCompletion(project, result)
            }
        }
    }

    private fun findFiles(project: @NotNull Project): List<VirtualFile> {
        val files = FileTypeIndex.getFiles(
            JavaScriptFileType.INSTANCE,
            GlobalSearchScope.projectScope(project)
        ) + FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(project))

        return files.filter { StudioFileManager(project).getStudioFile(it) != null }
    }

    /**
     * Tilde should only suggest files in the same cartridge path as the original file
     *
     */
    private fun handleCompletion(
        project: @NotNull Project,
        result: CompletionResultSet,
        insertCartridgeName: Boolean = true
    ) {
        var hasResults = false

        findFiles(project).forEach {
            val studioFile = StudioFileManager(project).getStudioFile(it)

            // TODO figure out why cartridge/cartridge_name/js/home.js isn't inserting the right path
            if (studioFile != null) {
                var lookupElementBuilder = LookupElementBuilder
                    .create(studioFile.getModulePath())
                    .withPresentableText(studioFile.getRelativeModulePath())
                    .withTypeText(studioFile.getCartridgeName(), StudioIcons.STUDIO_ICON, true)
                    .withTypeIconRightAligned(true)

                val insertText = if (insertCartridgeName) {
                    studioFile.getModulePath(false)
                } else {
                    studioFile.getRelativeModulePath(false)
                }

                lookupElementBuilder = lookupElementBuilder.withInsertHandler(
                    RequireInsertHandler(insertText)
                )

                result.addElement(lookupElementBuilder)
                hasResults = true
            }
        }

        if (hasResults) {
            result.stopHere()
        }
    }
}

/**
 * Star should complete files in any cartridge on the active cartridge path
 *
 */

private fun handleApiCompletion(
    result: CompletionResultSet
) {
    result.addElement(LookupElementBuilder.create("dw/api/result"))
}

private class RequireInsertHandler(private val insertText: String) : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val editor = context.editor
        val document = context.document
        val caretOffset = editor.caretModel.offset

        document.insertString(caretOffset, insertText)
        editor.caretModel.moveToOffset(
            caretOffset + insertText.length
        )
        document.deleteString(caretOffset - item.lookupString.length, caretOffset)
    }
}