package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.context.AnnotationConfigApplicationContext;
import org.paohaijiao.jstark.context.JEventAnnotationConfigApplicationContext;

public class EventPublishingMain {

    public static void main(String[] args) {
        System.out.println("===== 事件监听机制演示 =====");
        JEventAnnotationConfigApplicationContext context = new JEventAnnotationConfigApplicationContext("org.paohaijiao.jstark.test");
        UserService userService = context.getBean("userService", UserService.class);

        System.out.println("\n=== 测试1: 用户注册事件 ===");
        userService.registerUser("张三");
        context.publishEvent(new SystemNotificationEvent( "系统将于今晚23:00进行维护"));


    }
}
