package com.binarysushi.studio.language.isml.codeInsight.tags

import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor

class ISMLXmlAttributeDescriptor(private val tagName: String, attributeName: String?) :
    AnyXmlAttributeDescriptor(attributeName) {

    private val attrMap: MutableMap<String, Map<String, Array<String?>>> = HashMap()
    override fun getEnumeratedValues(): Array<String?> {
        var values = arrayOfNulls<String>(0)
        val valueMap = attrMap[tagName]
        if (valueMap != null && valueMap[name] != null) {
            values = valueMap[name]!!
        }
        return values
    }

    init {
        val iscacheMap: MutableMap<String, Array<String?>> = HashMap()
        iscacheMap["status"] = arrayOf("on", "off")
        iscacheMap["type"] = arrayOf("relative", "daily")
        iscacheMap["hour"] = arrayOf("1", "12", "24")
        iscacheMap["minute"] = arrayOf("5", "10", "15", "30", "45", "60")
        iscacheMap["varyby"] = arrayOf("price_promotion")
        attrMap["iscache"] = iscacheMap

        val iscontentMap: MutableMap<String, Array<String?>> = HashMap()
        iscontentMap["type"] = arrayOf("text/html", "application/xml", "application/json")
        iscontentMap["encoding"] = arrayOf("on", "off", "html", "xml", "wml")
        iscontentMap["compact"] = arrayOf("true", "false")
        attrMap["iscontent"] = iscontentMap

        val iscookieMap: MutableMap<String, Array<String?>> = HashMap()
        iscookieMap["secure"] = arrayOf("on", "off")
        attrMap["iscookie"] = iscookieMap

        val isprintMap: MutableMap<String, Array<String?>> = HashMap()
        isprintMap["timezone"] = arrayOf("SITE", "INSTANCE", "utc")
        isprintMap["encoding"] = arrayOf("on", "off")
        attrMap["isprint"] = isprintMap

        val isredirectMap: MutableMap<String, Array<String?>> = HashMap()
        isredirectMap["permanent"] = arrayOf("true", "false")
        attrMap["isredirect"] = isredirectMap

        val isremoveMap: MutableMap<String, Array<String?>> = HashMap()
        isremoveMap["scope"] = arrayOf("session", "pdict", "request", "page")
        attrMap["isremove"] = isremoveMap

        val isselectMap: MutableMap<String, Array<String?>> = HashMap()
        isselectMap["condition"] = arrayOf("true", "false")
        isselectMap["encoding"] = arrayOf("on", "off")
        attrMap["isselect"] = isselectMap

        val issetMap: MutableMap<String, Array<String?>> = HashMap()
        issetMap["scope"] = arrayOf("session", "request", "page")
        attrMap["isset"] = issetMap

        val isslot: MutableMap<String, Array<String?>> = HashMap()
        isslot["context"] = arrayOf("global", "category")
        attrMap["isslot"] = isslot
    }
}