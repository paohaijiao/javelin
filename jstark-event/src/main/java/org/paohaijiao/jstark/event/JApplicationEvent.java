package org.paohaijiao.jstark.event;

public abstract class JApplicationEvent {
    private final Object source;
    private final long timestamp;

    public JApplicationEvent(Object source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public Object getSource() {
        return source;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
