package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.bean.JBeanDefinition;
import org.paohaijiao.jstark.context.JBeanProvider;
import org.paohaijiao.jstark.context.service.JProxyEnhancedBeanContainer;
import org.paohaijiao.jstark.factory.JBeanProviderFactory;

import java.util.Properties;

public class BeanMain {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.setProperty("bean.container.mode", "simple"); // 或 "simple"
        JBeanProvider container = JBeanProviderFactory.createProvider(config);
        JBeanDefinition serviceDef = new JBeanDefinition(ProviderUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        if (container instanceof JProxyEnhancedBeanContainer) {
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
