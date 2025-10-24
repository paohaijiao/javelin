package spi.impl;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import spi.NotificationService;

@Priority(PriorityConstants.BUSINESS_MEDIUM)
public class SmsNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("[短信通知] " + message);
    }

    @Override
    public boolean supports(String type) {
        return "SMS".equals(type);
    }
}
