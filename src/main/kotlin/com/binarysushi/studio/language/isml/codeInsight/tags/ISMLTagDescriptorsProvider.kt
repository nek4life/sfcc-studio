package com.binarysushi.studio.language.isml.codeInsight.tags

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlTagNameProvider
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor
import java.util.*

class ISMLTagDescriptorsProvider : XmlElementDescriptorProvider, XmlTagNameProvider {
    private val ismlTagNames = arrayOf(
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
        "isstatus"
    )

    override fun getDescriptor(tag: XmlTag): XmlElementDescriptor? {
        if (tag !is HtmlTag || !Arrays.asList(*ismlTagNames).contains(tag.getName())) {
            return null
        }
        val nsDescriptor = tag.getNSDescriptor(tag.getNamespace(), false)
        val descriptor = nsDescriptor?.getElementDescriptor(tag)
        return if (descriptor != null && descriptor !is AnyXmlElementDescriptor) {
            null
        } else ISMLTagDescriptor(tag.getName(), tag)
    }

    override fun addTagNameVariants(elements: MutableList<LookupElement>, tag: XmlTag, prefix: String) {
        if (!(tag is HtmlTag || !Arrays.asList(*ismlTagNames).contains(tag.name))) {
            return
        }
        for (tagName in ismlTagNames) {
            elements.add(LookupElementBuilder.create(tagName))
        }
    }
}