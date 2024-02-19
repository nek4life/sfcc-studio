package com.binarysushi.studio.language.isml

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.javascript.JSInjectionBracesUtil
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.html.HtmlTagImpl
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl
import com.intellij.psi.impl.source.xml.XmlTextImpl
import java.util.*

class ISMLScriptInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context.containingFile.originalFile.virtualFile.fileType !== ISMLFileType) {
            return
        }
        if (context.parent != null && context.parent is HtmlTagImpl) {
            if ((context.parent as HtmlTagImpl).name == "isscript") {
                registrar.startInjecting(JavascriptLanguage.INSTANCE)
                    .addPlace(
                        null,
                        null,
                        (context as PsiLanguageInjectionHost),
                        ElementManipulators.getValueTextRange(context)
                    )
                    .doneInjecting()
            }
        }
        if (context is XmlTextImpl || context is XmlAttributeValueImpl) {
            JSInjectionBracesUtil.injectInXmlTextByDelimiters(
                registrar,
                context,
                JavascriptLanguage.INSTANCE,
                "\${",
                "}"
            )
        }
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>?> {
        return Arrays.asList(XmlTextImpl::class.java, XmlAttributeValueImpl::class.java)
    }
}