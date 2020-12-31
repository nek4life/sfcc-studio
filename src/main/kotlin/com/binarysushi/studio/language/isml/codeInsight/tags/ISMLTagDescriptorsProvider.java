package com.binarysushi.studio.language.isml.codeInsight.tags;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.XmlTagNameProvider;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ISMLTagDescriptorsProvider implements XmlElementDescriptorProvider, XmlTagNameProvider {
    private final String[] ismlTagNames = {
        "isactivedatacontext",
        "isactivedatahead",
        "isanalyticsoff",
        "isbreak",
        "iscache",
        "iscomment",
        "iscomponent",
        "iscontent",
        "iscontinue",
        "iscookie",
        "isdecorate",
        "iselse",
        "iselseif",
        "isif",
        "isinclude",
        "isloop",
        "ismodule",
        "isnext",
        "isobject",
        "isprint",
        "isredirect",
        "isremove",
        "isreplace",
        "isscript",
        "isselect",
        "isset",
        "isslot",
        "isstatus",
    };


    @Nullable
    @Override
    public XmlElementDescriptor getDescriptor(XmlTag tag) {
        if (!(tag instanceof HtmlTag) || !Arrays.asList(ismlTagNames).contains(tag.getName())) {
            return null;
        }

        final XmlNSDescriptor nsDescriptor = tag.getNSDescriptor(tag.getNamespace(), false);
        final XmlElementDescriptor descriptor = nsDescriptor != null ? nsDescriptor.getElementDescriptor(tag) : null;
        if (descriptor != null && !(descriptor instanceof AnyXmlElementDescriptor)) {
            return null;
        }

        return new ISMLTagDescriptor(tag.getName(), tag);
    }

    @Override
    public void addTagNameVariants(List<LookupElement> elements, @NotNull XmlTag tag, String prefix) {
        if (!(tag instanceof HtmlTag || !Arrays.asList(ismlTagNames).contains(tag.getName()))) {
            return;
        }

        for (String tagName : ismlTagNames) {
            elements.add(LookupElementBuilder.create(tagName));
        }
    }
}
