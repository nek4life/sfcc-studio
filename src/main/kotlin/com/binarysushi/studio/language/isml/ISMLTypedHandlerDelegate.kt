package com.binarysushi.studio.language.isml

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class ISMLTypedHandlerDelegate : TypedHandlerDelegate() {
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (!CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET) return Result.CONTINUE
        if (c != '{') return Result.CONTINUE
        if (!shouldProcess(file)) return Result.CONTINUE
        val offset = editor.caretModel.offset
        if (shouldCloseBrace(editor, offset, c)) {
            editor.document.insertString(offset, "}")
            return Result.STOP
        }
        return Result.CONTINUE
    }

}

private fun shouldCloseBrace(editor: Editor, offset: Int, c: Char): Boolean {
    val text = editor.document.charsSequence
    if (offset < 2) return false
    if (c != '{' || text[offset - 2] != '$') return false
    if (offset < text.length) {
        val next = text[offset]
        return if (next == '}') false else !Character.isLetterOrDigit(next)
    }
    return true
}

fun shouldProcess(file: PsiFile): Boolean {
    return ISMLFileType == file.viewProvider.fileType
}