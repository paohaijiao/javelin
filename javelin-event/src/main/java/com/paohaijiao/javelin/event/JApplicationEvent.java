package com.paohaijiao.javelin.event;

public class JApplicationEvent extends java.util.EventObject{
    private final long timestamp;

    public JApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public final long getTimestamp() {
        return timestamp;
    }
}
