package com.binarysushi.studio.lang.isml.codeInsight.tags;

import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor;

import java.util.HashMap;
import java.util.Map;

public class ISMLXmlAttributeDescriptor extends AnyXmlAttributeDescriptor {
    public final String tagName;
    public final Map<String, Map<String, String[]>> attrMap = new HashMap<>();

    public ISMLXmlAttributeDescriptor(String tagName, String attibuteName) {
        super(attibuteName);
        this.tagName = tagName;

        Map<String, String[]> iscacheMap = new HashMap<>();
        iscacheMap.put("status", new String[]{"on", "off"});
        iscacheMap.put("type", new String[]{"relative", "daily"});
        iscacheMap.put("hour", new String[]{"1", "12", "24"});
        iscacheMap.put("minute", new String[]{"5", "10", "15", "30", "45", "60"});
        iscacheMap.put("varyby", new String[]{"price_promotion"});
        attrMap.put("iscache", iscacheMap);

        Map<String, String[]> iscontentMap = new HashMap<>();
        iscontentMap.put("type", new String[]{"text/html", "application/xml", "application/json"});
        iscontentMap.put("encoding", new String[]{"on", "off", "html", "xml", "wml"});
        iscontentMap.put("compact", new String[]{"true", "false"});
        attrMap.put("iscontent", iscontentMap);

        Map<String, String[]> iscookieMap = new HashMap<>();
        iscookieMap.put("secure", new String[]{"on", "off"});
        attrMap.put("iscookie", iscookieMap);

        Map<String, String[]> isprintMap = new HashMap<>();
        isprintMap.put("timezone", new String[]{"SITE", "INSTANCE", "utc"});
        isprintMap.put("encoding", new String[]{"on", "off"});
        attrMap.put("isprint", isprintMap);

        Map<String, String[]> isredirectMap = new HashMap<>();
        isredirectMap.put("permanent", new String[]{"true", "false"});
        attrMap.put("isredirect", isredirectMap);

        Map<String, String[]> isremoveMap = new HashMap<>();
        isremoveMap.put("scope", new String[]{"session", "pdict", "request", "page"});
        attrMap.put("isremove", isremoveMap);

        Map<String, String[]> isselectMap = new HashMap<>();
        isselectMap.put("condition", new String[]{"true", "false"});
        isselectMap.put("encoding", new String[]{"on", "off"});
        attrMap.put("isselect", isselectMap);

        Map<String, String[]> issetMap = new HashMap<>();
        issetMap.put("scope", new String[]{"session", "request", "page"});
        attrMap.put("isset", issetMap);

        Map<String, String[]> isslot = new HashMap<>();
        isslot.put("context", new String[]{"global", "category"});
        attrMap.put("isslot", isslot);
    }

    @Override
    public String[] getEnumeratedValues() {
        String[] values = new String[0];
        Map<String, String[]> valueMap = attrMap.get(tagName);

        if (valueMap != null && valueMap.get(getName()) != null) {
            values = valueMap.get(getName());
        }

        return values;
    }
}
