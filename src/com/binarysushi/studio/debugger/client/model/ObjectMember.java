package com.binarysushi.studio.debugger.client.model;

/*
@Serializable
data class ObjectMember(
    val name: String,
    val parent: String,
    val scope: String,
    val type: String,
    val value: String
)
 */
public class ObjectMember {
    private String name;
    private String parent;
    private String scope;
    private String type;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
