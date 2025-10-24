package spi.impl;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import spi.NotificationService;

@Priority(PriorityConstants.SYSTEM_HIGHEST)
public class SystemAlertService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("[系统告警] " + message);
    }

    @Override
    public boolean supports(String type) {
        return "SYSTEM_ALERT".equals(type);
    }
}
