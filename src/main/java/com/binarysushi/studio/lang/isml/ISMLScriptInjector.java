package com.binarysushi.studio.lang.isml;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.javascript.JSInjectionBracesUtil;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.html.HtmlTagImpl;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.impl.source.xml.XmlPrologImpl;
import com.intellij.psi.impl.source.xml.XmlTextImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ISMLScriptInjector implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (context.getContainingFile().getOriginalFile().getVirtualFile().getFileType() != ISMLFileType.INSTANCE) { return; }

        if (context.getParent() != null && context.getParent() instanceof HtmlTagImpl) {
            if (((HtmlTagImpl) context.getParent()).getName().equals("isscript")) {
                registrar.startInjecting(JavascriptLanguage.INSTANCE)
                        .addPlace(null, null, (PsiLanguageInjectionHost) context, ElementManipulators.getValueTextRange(context))
                        .doneInjecting();
            }
        }

        if (context instanceof XmlTextImpl || context instanceof XmlAttributeValueImpl) {
            JSInjectionBracesUtil.injectInXmlTextByDelimiters(registrar, context, JavascriptLanguage.INSTANCE, "${", "}");
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Arrays.asList(XmlDocumentImpl.class, XmlPrologImpl.class, XmlTextImpl.class, XmlAttributeValueImpl.class);
    }
}
