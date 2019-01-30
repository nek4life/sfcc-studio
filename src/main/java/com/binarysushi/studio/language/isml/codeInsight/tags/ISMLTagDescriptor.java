package com.binarysushi.studio.language.isml.codeInsight.tags;

import com.intellij.html.impl.RelaxedHtmlFromSchemaElementDescriptor;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.html.dtd.HtmlNSDescriptorImpl;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ISMLTagDescriptor implements XmlElementDescriptor {
    protected final String name;
    protected final PsiElement declaration;
    private final Map<String, String> attrMap = new HashMap<String, String>();

    public ISMLTagDescriptor(String myName, PsiElement declaration) {
        this.name = myName;
        this.declaration = declaration;

        attrMap.put("isactivedatacontext", "current_category");
        attrMap.put("iscache", "status,type,hour,minute,varyby");
        attrMap.put("iscomponent", "pipeline,locale");
        attrMap.put("iscontent", "type,charset,encoding,compact");
        attrMap.put("iscookie", "name,value,comment,domain,path,maxAge,version,secure");
        attrMap.put("isdecorate", "template");
        attrMap.put("isif", "condition");
        attrMap.put("iselseif", "condition");
        attrMap.put("isinclude", "template,url");
        attrMap.put("isloop", "items,var,alias,status,begin,end,step");
        attrMap.put("ismodule", "template,name");
        attrMap.put("isobject", "object,view");
        attrMap.put("isprint", "value,style,formatter,timezone,padding,encoding");
        attrMap.put("isredirect", "location,permanent");
        attrMap.put("isremove", "name,scope");
        attrMap.put("isselect", "name,iterator,description,value,condition,encoding");
        attrMap.put("isset", "name,value,scope");
        attrMap.put("isslot", "id,context,context-object,description,preview-url");
        attrMap.put("isstatus", "value");
    }

    @Override
    public String getQualifiedName() {
        return name;
    }

    @Override
    public String getDefaultName() {
        return name;
    }

    @Override
    public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        XmlDocumentImpl xmlDocument = PsiTreeUtil.getParentOfType(context, XmlDocumentImpl.class);
        if (xmlDocument == null) return EMPTY_ARRAY;
        return xmlDocument.getRootTagNSDescriptor().getRootElementsDescriptors(xmlDocument);
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        XmlTag parent = contextTag.getParentTag();
        if (parent == null) return null;
        final XmlNSDescriptor descriptor = parent.getNSDescriptor(childTag.getNamespace(), true);
        return descriptor == null ? null : descriptor.getElementDescriptor(childTag);
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
        if (context != null) {
            final String tagName = context.getName();
            if (attrMap.containsKey(tagName)) {
                final String[] attrs = attrMap.get(tagName).split(",");
                final XmlAttributeDescriptor[] result = new XmlAttributeDescriptor[attrs.length];
                for (int i = 0; i < attrs.length; i++) {
                    result[i] = new ISMLXmlAttributeDescriptor(tagName, attrs[i]);
                }
                return result;
            }
        }
        final XmlAttributeDescriptor[] commonAttributes = HtmlNSDescriptorImpl.getCommonAttributeDescriptors(context);
        return RelaxedHtmlFromSchemaElementDescriptor.addAttrDescriptorsForFacelets(context, commonAttributes);
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        return getAttributeDescriptor(attribute.getName(), attribute.getParent());
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(@NonNls final String attributeName, @Nullable XmlTag context) {
        return ContainerUtil.find(getAttributesDescriptors(context), new Condition<XmlAttributeDescriptor>() {
            @Override
            public boolean value(XmlAttributeDescriptor descriptor) {
                return attributeName.equals(descriptor.getName());
            }
        });
    }

    @Override
    public XmlNSDescriptor getNSDescriptor() {
        return null;
    }

    @Nullable
    @Override
    public XmlElementsGroup getTopGroup() {
        return null;
    }

    @Override
    public int getContentType() {
        return CONTENT_TYPE_ANY;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public PsiElement getDeclaration() {
        return declaration;
    }

    @Override
    public String getName(PsiElement context) {
        return getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(PsiElement element) {

    }

    @Override
    public Object[] getDependences() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
}
