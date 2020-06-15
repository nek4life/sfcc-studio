package com.binarysushi.studio.language.isml.codeInsight;

import com.binarysushi.studio.StudioIcons;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ISMLLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof XmlAttribute)) {
            return;
        }

        String attributeValue = ((XmlAttribute) element).getValue();

        if (attributeValue != null) {

            if (attributeValue.isEmpty() || attributeValue.contains("${")) {
                return;
            }

            if (((XmlAttribute) element).getName().equals("template") && (((XmlTag) element.getParent()).getName().equals("isinclude") || ((XmlTag) element.getParent()).getName().equals("isdecorate"))) {
                Project project = element.getProject();
                PsiManager manager = PsiManager.getInstance(project);


                Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, "isml");
                ArrayList<PsiFile> templateMatches = new ArrayList<>();


                for (VirtualFile file : files) {
                    // Remove .isml so it is not doubled up if the extension was typed into the attribute value
                    String cleanedAttributeValue = attributeValue.replace(".isml", "");
                    if (file.getPath().endsWith(cleanedAttributeValue + ".isml")) {
                        templateMatches.add(manager.findFile(file));
                    }
                }

                // TODO make the template completion text path more compact when choosing from multiple templates
                NavigationGutterIconBuilder<PsiElement> builder =
                        NavigationGutterIconBuilder.create(StudioIcons.STUDIO_ISML_ICON)
                                .setTooltipText("Goto included file")
                                .setTargets(templateMatches);

                result.add(builder.createLineMarkerInfo(element));
            }
        }
    }
}
