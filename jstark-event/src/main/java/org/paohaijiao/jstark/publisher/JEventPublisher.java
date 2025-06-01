package org.paohaijiao.jstark.publisher;

import org.paohaijiao.jstark.event.JApplicationEvent;

public interface JEventPublisher {
    void publishEvent(JApplicationEvent event);

}
