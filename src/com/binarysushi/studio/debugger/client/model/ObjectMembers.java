package com.binarysushi.studio.debugger.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/*
    val count: Int,
    @SerialName("object_members") val objectMembers: ObjectMember,
    val start: Int,
    val total: Int
 */
public class ObjectMembers {
    private int count;

    @JsonProperty("object_members")
    private List<ObjectMember> objectMembers;

    private int start;
    private int total;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ObjectMember> getObjectMembers() {
        return objectMembers;
    }

    public void setObjectMembers(List<ObjectMember> objectMembers) {
        this.objectMembers = objectMembers;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
