package com.paohaijiao.javelin.publisher;

import com.paohaijiao.javelin.event.JApplicationEvent;

public interface JEventPublisher {
    void publishEvent(JApplicationEvent event);

}
