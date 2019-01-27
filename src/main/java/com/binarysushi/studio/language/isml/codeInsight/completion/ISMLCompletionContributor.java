package com.binarysushi.studio.language.isml.codeInsight.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class ISMLCompletionContributor extends CompletionContributor {

    ISMLCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN),
                new ISMLTemplateAttributeProvider());
    }

    private class ISMLTemplateAttributeProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            XmlTag xmlTag = PsiTreeUtil.getParentOfType(parameters.getPosition(), XmlTag.class, false);
            XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(parameters.getPosition(), XmlAttribute.class, false);

            if (xmlTag == null || xmlAttribute == null) {
                return;
            }

            if (xmlAttribute.getName().equals("template") && (xmlTag.getName().equals("isinclude") || xmlTag.getName().equals("isdecorate"))) {
                Project project = xmlTag.getProject();
                Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, "isml");

                for (VirtualFile file : files) {
                    String filePath = file.getPath();

                    if (filePath.contains("templates")) {
                        String cartridges = "cartridges";
                        Path resultPath = Paths.get(filePath.substring(filePath.indexOf(cartridges) + cartridges.length()).replace(".isml", ""));
                        result.addElement(LookupElementBuilder.create(resultPath.toString())
                                .withPresentableText(resultPath.subpath(4, resultPath.getNameCount()).toString())
                                .withTailText("  " + resultPath.subpath(0, 1) + "/" + resultPath.subpath(3, 4))
                                .withInsertHandler(new ISMLTemplateFileInsertHandler())
                        );
                    }
                }
            }
        }
    }

    /**
     * Inserts the proper path that the application expects rather than the full
     * file system path that is used for the index lookup.
     */
    private class ISMLTemplateFileInsertHandler implements InsertHandler<LookupElement> {

        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
            String resultText = item.getLookupString();
            Path resultPath = Paths.get(resultText);
            Editor editor = context.getEditor();
            Document document = context.getDocument();
            final int caretOffset = editor.getCaretModel().getOffset();
            document.insertString(caretOffset, resultPath.subpath(4, resultPath.getNameCount()).toString());
            document.deleteString(caretOffset - resultText.length(), caretOffset);

        }
    }
}
