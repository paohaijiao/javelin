package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.service.MyUserService;
import org.paohaijiao.jstark.context.service.MyUserServiceImpl;
import org.paohaijiao.jstark.context.service.ProxyEnhancedBeanContainer;

import java.util.Properties;

public class BeanMain {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.setProperty("bean.container.mode", "simple"); // 或 "simple"
        BeanContainer container = BeanContainerFactory.createContainer(config);
        BeanDefinition serviceDef = new BeanDefinition(MyUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        if (container instanceof ProxyEnhancedBeanContainer) {
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
        MyUserService service = container.getBean("myService", MyUserService.class);
        service.sayHello("xssdcsd");
    }
}
