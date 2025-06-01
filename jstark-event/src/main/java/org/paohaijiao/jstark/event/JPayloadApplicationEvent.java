package org.paohaijiao.jstark.event;

public class JPayloadApplicationEvent <T> extends JApplicationEvent {
    private final T payload;
    public JPayloadApplicationEvent(Object source, T payload) {
        super(source);
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }
}
