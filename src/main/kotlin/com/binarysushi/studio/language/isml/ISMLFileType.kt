package com.binarysushi.studio.language.isml

import com.binarysushi.studio.StudioIcons
import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.lang.html.HTMLLanguage
import javax.swing.Icon

object ISMLFileType : XmlLikeFileType(HTMLLanguage.INSTANCE) {

    override fun getName(): String {
        return "ISML"
    }

    override fun getDescription(): String {
        return "ISML File"
    }

    override fun getDefaultExtension(): String {
        return "isml"
    }

    override fun getIcon(): Icon {
        return StudioIcons.STUDIO_ISML_ICON
    }
}