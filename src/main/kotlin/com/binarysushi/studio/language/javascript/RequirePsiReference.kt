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

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val jsFileIndex =
            FileTypeIndex.getFiles(
                JavaScriptFileType.INSTANCE,
                GlobalSearchScope.projectScope(element.project)
            ) + FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(element.project))

        val elementPath = element.text
        val cleanedElementPath = elementPath.drop(2).dropLast(1)
        val regex = "$cleanedElementPath\\.[js|ds|json]".toRegex()

        val matches = jsFileIndex.filter {
            val cartridgeRootPath = CartridgePathUtil.getCartridgeRootPathForFile(element.project, it.path)
            if (cartridgeRootPath != null) {
                val rootRelativePath = CartridgePathUtil.getCartridgeRelativeFilePath(cartridgeRootPath, it.path)
                rootRelativePath.contains(regex)
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