package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.service.OrderService;
import org.paohaijiao.jstark.context.service.UserService;

public class BeanContainerExample {
    public static void main(String[] args) {
        BeanContainer container = new BeanContainer();

        BeanDefinition userServiceDef = new BeanDefinition(UserService.class);
        userServiceDef.setInitMethodName("init");
        container.registerBeanDefinition("userService", userServiceDef);

        BeanDefinition orderServiceDef = new BeanDefinition(OrderService.class);
        container.registerBeanDefinition("orderService", orderServiceDef);

        // 3. 获取 Bean
        UserService userService = container.getBean("userService", UserService.class);
        userService.sayHello();

        OrderService orderService = container.getBean("orderService", OrderService.class);
        orderService.createOrder();
    }
}
