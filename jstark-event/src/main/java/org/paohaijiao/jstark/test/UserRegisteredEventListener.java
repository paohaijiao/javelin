package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.anno.JComponent;
import org.paohaijiao.jstark.function.JApplicationListener;
@JComponent
public class UserRegisteredEventListener  implements JApplicationListener<SystemNotificationEvent> {
    private boolean eventReceived = false;
    private String receivedUsername;

    @Override
    public void onApplicationEvent(SystemNotificationEvent event) {
        this.eventReceived = true;
        System.out.println("UserRegisteredEventListener 收到事件: " );
    }

    public boolean isEventReceived() {
        return eventReceived;
    }

    public String getReceivedUsername() {
        return receivedUsername;
    }
}
