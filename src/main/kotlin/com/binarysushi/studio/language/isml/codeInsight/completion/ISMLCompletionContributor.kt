package com.binarysushi.studio.language.isml.codeInsight.completion

import com.binarysushi.studio.language.isml.codeInsight.tags.ISMLXmlAttributeDescriptor
import com.binarysushi.studio.language.isml.codeInsight.tags.ISMLTagDescriptor
import com.binarysushi.studio.language.isml.codeInsight.completion.ISMLCompletionContributor.ISMLTemplateFileInsertHandler
import com.binarysushi.studio.language.isml.codeInsight.completion.ISMLCompletionContributor.ISMLTemplateAttributeProvider
import com.binarysushi.studio.StudioIcons
import com.binarysushi.studio.language.isml.ISMLFileType
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.JSInjectionBracesUtil
import com.binarysushi.studio.language.isml.ISMLTypedHandlerDelegate
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTokenType
import com.intellij.util.ProcessingContext
import java.nio.file.Paths

class ISMLCompletionContributor internal constructor() : CompletionContributor() {
    private inner class ISMLTemplateAttributeProvider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet
        ) {
            val xmlTag = PsiTreeUtil.getParentOfType(parameters.position, XmlTag::class.java, false)
            val xmlAttribute = PsiTreeUtil.getParentOfType(parameters.position, XmlAttribute::class.java, false)
            if (xmlTag == null || xmlAttribute == null) {
                return
            }
            if (xmlAttribute.name == "template" && (xmlTag.name == "isinclude" || xmlTag.name == "isdecorate")) {
                val project = xmlTag.project
                val files = FilenameIndex.getAllFilesByExt(project, "isml")
                for (file in files) {
                    val filePath = file.path
                    if (filePath.contains("templates")) {
                        val cartridges = "cartridges"
                        val resultPath = Paths.get(
                            filePath.substring(filePath.indexOf(cartridges) + cartridges.length).replace(".isml", "")
                        )
                        result.addElement(
                            LookupElementBuilder.create(resultPath.toString())
                                .withPresentableText(resultPath.subpath(4, resultPath.nameCount).toString() + ".isml")
                                .withTailText("  " + resultPath.subpath(0, 1) + ":" + resultPath.subpath(3, 4))
                                .withInsertHandler(ISMLTemplateFileInsertHandler())
                                .bold()
                        )
                    }
                }
            }
        }
    }

    /**
     * Inserts the proper path that the application expects rather than the full
     * file system path that is used for the index lookup.
     */
    private inner class ISMLTemplateFileInsertHandler : InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val resultText = item.lookupString
            val resultPath = Paths.get(resultText)
            val editor = context.editor
            val document = context.document
            val caretOffset = editor.caretModel.offset
            document.insertString(caretOffset, resultPath.subpath(4, resultPath.nameCount).toString())
            editor.caretModel.moveToOffset(caretOffset + resultPath.subpath(4, resultPath.nameCount).toString().length)
            document.deleteString(caretOffset - resultText.length, caretOffset)
        }
    }

    init {
        extend(
            CompletionType.BASIC, PlatformPatterns.psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN),
            ISMLTemplateAttributeProvider()
        )
    }
}