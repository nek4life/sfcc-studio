package com.binarysushi.studio.language.isml

import com.binarysushi.studio.StudioIcons
import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class ISMLFileType : XmlLikeFileType {
    private constructor() : super(HTMLLanguage.INSTANCE) {}
    internal constructor(language: Language?) : super(language!!) {}

    override fun getName(): String {
        return "ISML"
    }

    override fun getDescription(): String {
        return "ISML File"
    }

    override fun getDefaultExtension(): String {
        return "isml"
    }

    override fun getIcon(): Icon? {
        return StudioIcons.STUDIO_ISML_ICON
    }

    companion object {
        val DOT_DEFAULT_EXTENSION: @NonNls String = ".isml"
        val INSTANCE = ISMLFileType()
    }
}