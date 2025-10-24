package spi.impl;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import spi.NotificationService;

@Priority(PriorityConstants.APPLICATION_HIGH)
public class EmailNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("[邮件通知] " + message);
    }

    @Override
    public boolean supports(String type) {
        return "EMAIL".equals(type);
    }
}
