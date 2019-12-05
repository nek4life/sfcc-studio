package com.binarysushi.studio.debugger.client.model;

public class StackFrame {
    private int index;
    private Location location;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
