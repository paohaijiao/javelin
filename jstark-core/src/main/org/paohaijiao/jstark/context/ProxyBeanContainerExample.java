package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.service.MyUserService;
import org.paohaijiao.jstark.context.service.MyUserServiceImpl;
import org.paohaijiao.jstark.context.service.UserService;

public class ProxyBeanContainerExample {
    public static void main(String[] args) {
        // 1. 创建容器
        ProxyBeanContainer container = new ProxyBeanContainer();
        // 2. 注册 Bean 定义
        BeanDefinition userServiceDef =
                new BeanDefinition(MyUserServiceImpl.class);
        container.registerBeanDefinition("userService", userServiceDef);
        // 3. 注册方法拦截器 (AOP 功能)
        container.registerInterceptor("userService", invocation -> {
            System.out.println("===> 拦截方法: " + invocation.getMethod().getName());
            long start = System.currentTimeMillis();
            try {
                return invocation.proceed();
            } finally {
                System.out.println("<=== 方法执行耗时: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        // 4. 添加后处理器
        container.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                System.out.println("后处理器: 初始化后的Bean - " + beanName);
                return bean;
            }
        });

        // 5. 获取代理后的 Bean
        MyUserService userService = container.getBean("userService", MyUserService.class);
        userService.sayHello("World");
        userService.getUserInfo(123);
    }
}
