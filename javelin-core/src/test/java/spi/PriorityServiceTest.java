package spi;

import com.github.paohaijiao.spi.ServiceLoader;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;


public class PriorityServiceTest {

    @Test
    void testDefaultLocale() {

        ServiceLoader.printServicePriorities(NotificationService.class);

        // 按优先级获取所有服务
        List<NotificationService> allServices = ServiceLoader.loadServicesByPriority(NotificationService.class);
        System.out.println("按优先级顺序执行:");
        for (NotificationService service : allServices) {
            service.send("测试消息");
        }

        // 获取最高优先级的服务
        Optional<NotificationService> highestPriority = ServiceLoader.getHighestPriorityService(NotificationService.class);
        highestPriority.ifPresent(service -> {
            System.out.println("\n最高优先级服务: " + service.getClass().getSimpleName());
        });

        // 根据条件获取最高优先级服务
        Optional<NotificationService> emailService = ServiceLoader.getHighestPriorityService(
                NotificationService.class,
                service -> service.supports("EMAIL")
        );
        emailService.ifPresent(service -> {
            System.out.println("邮件服务: " + service.getClass().getSimpleName());
        });

        // 获取特定级别的服务
        List<NotificationService> systemServices = ServiceLoader.getSystemLevelServices(NotificationService.class);
        System.out.println("\n系统级服务数量: " + systemServices.size());
    }
}

