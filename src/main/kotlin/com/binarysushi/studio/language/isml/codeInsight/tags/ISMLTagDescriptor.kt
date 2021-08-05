package com.binarysushi.studio.language.isml.codeInsight.tags

import com.intellij.html.impl.RelaxedHtmlFromSchemaElementDescriptor
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.html.dtd.HtmlNSDescriptorImpl
import com.intellij.psi.impl.source.xml.XmlDocumentImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.util.containers.ContainerUtil
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlElementsGroup
import com.intellij.xml.XmlNSDescriptor
import org.jetbrains.annotations.NonNls

class ISMLTagDescriptor(private val name: String, private val declaration: PsiElement) : XmlElementDescriptor {
    private val attrMap: MutableMap<String, String> = HashMap()
    override fun getQualifiedName(): String {
        return name
    }

    override fun getDefaultName(): String {
        return name
    }

    override fun getElementsDescriptors(context: XmlTag): Array<XmlElementDescriptor> {
        val xmlDocument = PsiTreeUtil.getParentOfType(
            context,
            XmlDocumentImpl::class.java
        )
            ?: return XmlElementDescriptor.EMPTY_ARRAY
        return xmlDocument.rootTagNSDescriptor.getRootElementsDescriptors(xmlDocument)
    }

    override fun getElementDescriptor(childTag: XmlTag, contextTag: XmlTag): XmlElementDescriptor? {
        val parent = contextTag.parentTag ?: return null
        val descriptor = parent.getNSDescriptor(childTag.namespace, true)
        return descriptor?.getElementDescriptor(childTag)
    }

    override fun getAttributesDescriptors(context: XmlTag?): Array<XmlAttributeDescriptor?> {
        if (context != null) {
            val tagName = context.name
            if (attrMap.containsKey(tagName)) {
                val attrs = attrMap[tagName]!!.split(",").toTypedArray()
                val result = arrayOfNulls<XmlAttributeDescriptor>(attrs.size)
                for (i in attrs.indices) {
                    result[i] = ISMLXmlAttributeDescriptor(tagName, attrs[i])
                }
                return result
            }
        }
        val commonAttributes = HtmlNSDescriptorImpl.getCommonAttributeDescriptors(context)
        return RelaxedHtmlFromSchemaElementDescriptor.addAttrDescriptorsForFacelets(context, commonAttributes)
    }

    override fun getAttributeDescriptor(attribute: XmlAttribute): XmlAttributeDescriptor? {
        return getAttributeDescriptor(attribute.name, attribute.parent)
    }

    override fun getAttributeDescriptor(attributeName: @NonNls String?, context: XmlTag?): XmlAttributeDescriptor? {
        return ContainerUtil.find(
            getAttributesDescriptors(context)
        ) { descriptor: XmlAttributeDescriptor? -> attributeName == descriptor?.name }
    }

    override fun getNSDescriptor(): XmlNSDescriptor? {
        return null
    }

    override fun getTopGroup(): XmlElementsGroup? {
        return null
    }

    override fun getContentType(): Int {
        return XmlElementDescriptor.CONTENT_TYPE_ANY
    }

    override fun getDefaultValue(): String? {
        return null
    }

    override fun getDeclaration(): PsiElement {
        return declaration
    }

    override fun getName(context: PsiElement): String {
        return getName()
    }

    override fun getName(): String {
        return name
    }

    override fun init(element: PsiElement) {}

    init {
        attrMap["isactivedatacontext"] = "current_category"
        attrMap["iscache"] = "status,type,hour,minute,varyby"
        attrMap["iscomponent"] = "pipeline,locale"
        attrMap["iscontent"] = "type,charset,encoding,compact"
        attrMap["iscookie"] = "name,value,comment,domain,path,maxAge,version,secure"
        attrMap["isdecorate"] = "template"
        attrMap["isif"] = "condition"
        attrMap["iselseif"] = "condition"
        attrMap["isinclude"] = "template,url"
        attrMap["isloop"] = "items,var,alias,status,begin,end,step"
        attrMap["ismodule"] = "template,name"
        attrMap["isobject"] = "object,view"
        attrMap["isprint"] = "value,style,formatter,timezone,padding,encoding"
        attrMap["isredirect"] = "location,permanent"
        attrMap["isremove"] = "name,scope"
        attrMap["isselect"] = "name,iterator,description,value,condition,encoding"
        attrMap["isset"] = "name,value,scope"
        attrMap["isslot"] = "id,context,context-object,description,preview-url"
        attrMap["isstatus"] = "value"
    }
}