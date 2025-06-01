package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.bean.JBeanDefinition;
import org.paohaijiao.jstark.context.service.JCglibBeanContainer;

public class CglibContainerExample {
    public static void main(String[] args) {
        JCglibBeanContainer container = new JCglibBeanContainer();
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
