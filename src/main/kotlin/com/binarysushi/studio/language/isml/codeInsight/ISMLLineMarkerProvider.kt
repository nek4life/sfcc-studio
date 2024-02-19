package com.binarysushi.studio.language.isml.codeInsight

import com.binarysushi.studio.StudioIcons
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.nextLeafs
import com.intellij.psi.util.prevLeafs
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

class ISMLLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (element !is XmlAttribute) {
            return
        }
        val attributeValue = element.value
        if (attributeValue != null) {
            if (attributeValue.isEmpty() || attributeValue.contains("\${")) {
                return
            }
            if (element.name == "template" && ((element.parent as XmlTag).name == "isinclude" || (element.parent as XmlTag).name == "isdecorate")) {
                val project = element.getProject()
                val manager = PsiManager.getInstance(project)
                val files = FilenameIndex.getAllFilesByExt(project, "isml")
                val templateMatches = ArrayList<PsiFile?>()
                for (file in files) {
                    // Remove .isml so it is not doubled up if the extension was typed into the attribute value
                    val cleanedAttributeValue = attributeValue.replace(".isml", "")
                    if (file.path.endsWith("$cleanedAttributeValue.isml")) {
                        templateMatches.add(manager.findFile(file))
                    }
                }

                // TODO make the template completion text path more compact when choosing from multiple templates
                val builder = NavigationGutterIconBuilder.create(StudioIcons.STUDIO_ISML_ICON)
                    .setTooltipText("Goto included file")
                    .setTargets(templateMatches)

                if (templateMatches.size > 0) {
                    result.add(builder.createLineMarkerInfo(element.nextLeafs.first()))
                }
            }
        }
    }
}