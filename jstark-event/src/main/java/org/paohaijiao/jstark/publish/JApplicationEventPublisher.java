package org.paohaijiao.jstark.publish;

import org.paohaijiao.jstark.event.JApplicationEvent;
import org.paohaijiao.jstark.event.JPayloadApplicationEvent;

public abstract class JApplicationEventPublisher {

    public abstract void publishEvent(JApplicationEvent event);

    public void publishEvent(Object event) {
        if (event instanceof JApplicationEvent) {
            publishEvent((JApplicationEvent) event);
        } else {
            publishEvent(new JPayloadApplicationEvent<>(this, event));
        }
    }
}
