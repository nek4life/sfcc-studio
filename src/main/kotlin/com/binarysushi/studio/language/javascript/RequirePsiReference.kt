package com.binarysushi.studio.language.javascript

import com.binarysushi.studio.cartridges.*
import com.intellij.json.*
import com.intellij.lang.javascript.*
import com.intellij.openapi.util.*
import com.intellij.openapi.util.io.*
import com.intellij.openapi.vfs.*
import com.intellij.psi.*
import com.intellij.psi.search.*


class RequirePsiReference(element: PsiElement) :
    PsiPolyVariantReferenceBase<PsiElement>(element, TextRange(1, element.text.length - 1)) {

    private fun cleanElementPath(element: PsiElement): CharSequence {
        val elementPath = element.text

        // Drop the quotes from element text
        var cleanedElementPath = elementPath.drop(1).dropLast(1)

        // Remove / charter from beginning of path
        if (cleanedElementPath.startsWith("/")) {
            cleanedElementPath = cleanedElementPath.drop(1)
        }

        // "*" throws an java.util.regex.PatternSyntaxException: Dangling meta character '*' near index 0
        if (cleanedElementPath.startsWith("*")) {
            cleanedElementPath = cleanedElementPath.drop(1)
        }

        // Remove ~ charter from beginning of path
        if (cleanedElementPath.startsWith("~")) {
            cleanedElementPath = cleanedElementPath.drop(1)
        }

        return cleanedElementPath
    }

    private fun findFileMatches(element: PsiElement): List<VirtualFile> {
        // Lookup JavaScript and JSON files by FileType
        val fileTypeIndex =
            FileTypeIndex.getFiles(
                JavaScriptFileType.INSTANCE,
                GlobalSearchScope.projectScope(element.project)
            ) + FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(element.project))


        // Remove extra characters and special syntax from psiElement which is this case is a
        // require() module path or Salesforce B2C application path
        val cleanedElementPath = cleanElementPath(element)

        // Regex that adds file extensions so that elementPath can match files in the fileTypeIndex
        val extensionRegex = "$cleanedElementPath\\.[js|ds|json]".toRegex()
        val studioFileManager = StudioFileManager(element.project)

        var result = fileTypeIndex.filter {
            val studioFile = studioFileManager.getStudioFile(it)

            if (studioFile != null) {
                val modulePath = studioFile.getModulePath()

                // If cleanedElementPath has file extension use that to match against file in index otherwise
                // use the extension regex that fuzzy matches the files with all possible extensions
                if (".+\\.\\w+$".toRegex().matches(cleanedElementPath)) {
                    modulePath.contains(cleanedElementPath)
                } else {
                    modulePath.contains(extensionRegex)
                }
            } else {
                false
            }
        }

        // Scope reference to current file's cartridge path
        if (element.text.drop(1).startsWith("~/")) {
            result = result.filter {
                val studioFile = studioFileManager.getStudioFile(it)
                studioFile !== null && FileUtil.toSystemIndependentName(element.containingFile.originalFile.virtualFile.presentableUrl).contains(
                    "/${studioFile.getCartridgeName()}"
                )
            }
        }

        return result
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return findFileMatches(element).map {
            PsiElementResolveResult(
                PsiManager.getInstance(element.project).findFile(it)!!.originalElement
            )
        }.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }
}