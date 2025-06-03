package com.paohaijiao.javelin.test;

import com.paohaijiao.javelin.bean.JBeanDefinition;
import com.paohaijiao.javelin.context.service.JCglibBeanProvider;

public class CglibContainerExample {
    public static void main(String[] args) {
        JCglibBeanProvider container = new JCglibBeanProvider();
        JBeanDefinition serviceDef = new JBeanDefinition(ProviderUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        container.registerInterceptor("myService",
                (proxyObj, method, args1, methodProxy, targetBean) -> {
                    System.out.println("[CGLIB] Before: " + method.getName());
                    long start = System.currentTimeMillis();
                    try {
                        Object result = methodProxy.invoke(targetBean, args1);
                        System.out.printf("[CGLIB] After: %s (cost: %dms)\n",
                                method.getName(),
                                System.currentTimeMillis() - start);
                        return result;
                    } catch (Exception e) {
                        System.err.println("[CGLIB] Error: " + e.getMessage());
                        throw e;
                    }
                });
        ProviderUserServiceImpl service = container.getBean("myService", ProviderUserServiceImpl.class);
        service.sayHello("helloqsw");
    }
}
