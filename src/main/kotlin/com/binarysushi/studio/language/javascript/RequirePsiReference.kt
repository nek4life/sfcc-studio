package com.binarysushi.studio.language.javascript

import com.binarysushi.studio.cartridges.*
import com.intellij.json.*
import com.intellij.lang.javascript.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.search.*


class RequirePsiReference(private val element: PsiElement) : PsiPolyVariantReference {
    override fun getElement(): PsiElement {
        return element
    }

    override fun getRangeInElement(): TextRange {
        return TextRange(1, element.text.length - 1)
    }

    private fun cleanElementPath(element: PsiElement) : CharSequence {
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

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        // Lookup JavaScript and JSON files by FileType
        val fileTypeIndex =
            FileTypeIndex.getFiles(
                JavaScriptFileType.INSTANCE,
                GlobalSearchScope.projectScope(element.project)
            ) + FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(element.project))

        val cleanedElementPath = cleanElementPath(element)
        // Regex that adds file extensions so that elementPath can match files in the fileTypeIndices
        val extensionRegex = "$cleanedElementPath\\.[js|ds|json]".toRegex()

        val matches = fileTypeIndex.filter {
            val cartridgeRootPath = CartridgePathUtil.getCartridgeRootPathForFile(element.project, it.path)

            // If file is not part of active cartridge path do not provide match
            if (cartridgeRootPath != null) {
                val rootRelativePath = CartridgePathUtil.getCartridgeRelativeFilePath(cartridgeRootPath, it.path)

                // If cleanedElementPath has extension use that to match against file in index
                if (".+\\.\\w+$".toRegex().matches(cleanedElementPath)) {
                    rootRelativePath.contains(cleanedElementPath)
                } else {
                    rootRelativePath.contains(extensionRegex)
                }
            } else {
                false
            }
        }

        return matches.map {
            PsiElementResolveResult(
                PsiManager.getInstance(element.project).findFile(it)!!.originalElement
            )
        }.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getCanonicalText(): String {
        return element.text
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        TODO("Not yet implemented")
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        TODO("Not yet implemented")
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return this.element == element
    }

    override fun isSoft(): Boolean {
        return false
    }
}