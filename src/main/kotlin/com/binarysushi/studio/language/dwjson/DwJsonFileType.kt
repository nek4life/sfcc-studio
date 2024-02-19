package com.binarysushi.studio.language.dwjson

import com.binarysushi.studio.StudioIcons
import com.intellij.json.JsonLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object DwJsonFileType : LanguageFileType(JsonLanguage.INSTANCE, true) {

    override fun getName(): String {
        return "DW_JSON"
    }

    override fun getDescription(): String {
        return "dw.json configuration file"
    }

    override fun getDefaultExtension(): String {
        return ".json"
    }

    override fun getIcon(): Icon? {
        return StudioIcons.STUDIO_ICON
    }
}

