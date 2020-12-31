package com.binarysushi.studio.language.dwjson

import com.binarysushi.studio.StudioIcons
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.json.psi.impl.JsonPropertyImpl
import com.intellij.json.psi.impl.JsonStringLiteralImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.findDescendantOfType
import com.intellij.refactoring.suggested.endOffset

class DwJsonLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.containingFile.fileType !== DwJsonFileType.INSTANCE) {
            return null
        }

        if (element is JsonPropertyImpl) {
            val stringLiteral = element.findDescendantOfType<JsonStringLiteralImpl>()
            if ((stringLiteral?.firstChild as LeafPsiElement).text == "\"hostname\"") {
                return LineMarkerInfo(
                    element,
                    TextRange(element.startOffsetInParent, element.endOffset),
                    StudioIcons.STUDIO_CARTRIDGE_ICON,
                    null,
                    null,
                    GutterIconRenderer.Alignment.CENTER
                )
            }
        }

        return null
    }
}
