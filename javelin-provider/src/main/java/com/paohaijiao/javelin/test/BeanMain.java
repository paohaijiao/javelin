package com.paohaijiao.javelin.test;

import com.paohaijiao.javelin.bean.JBeanDefinition;
import com.paohaijiao.javelin.context.JBeanProvider;
import com.paohaijiao.javelin.context.service.JProxyEnhancedBeanProvider;
import com.paohaijiao.javelin.factory.JBeanProviderFactory;

import java.util.Properties;

public class BeanMain {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.setProperty("bean.container.mode", "simple"); // 或 "simple"
        JBeanProvider container = JBeanProviderFactory.createProvider(config);
        JBeanDefinition serviceDef = new JBeanDefinition(ProviderUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        if (container instanceof JProxyEnhancedBeanProvider) {
            container.registerInterceptor("myService", invocation -> {
                System.out.println("拦截方法: " + invocation.getMethod().getName());
                long start = System.currentTimeMillis();
                try {
                    return invocation.proceed();
                } finally {
                    System.out.println("方法执行耗时: " + (System.currentTimeMillis() - start) + "ms");
                }
            });
        }
        ProviderUserService service = container.getBean("myService", ProviderUserService.class);
        service.sayHello("haha");
    }
}
