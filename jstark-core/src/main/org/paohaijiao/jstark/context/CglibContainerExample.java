package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.service.CglibBeanContainer;
import org.paohaijiao.jstark.context.service.MyUserService;
import org.paohaijiao.jstark.context.service.MyUserServiceImpl;

public class CglibContainerExample {
    public static void main(String[] args) {
        CglibBeanContainer container = new CglibBeanContainer();
        BeanDefinition serviceDef =
                new BeanDefinition(MyUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        container.registerInterceptor("myService",
                (proxyObj, method, args1, methodProxy, targetBean) -> {

                    System.out.println("[CGLIB] Before: " + method.getName());
                    long start = System.currentTimeMillis();
                    try {
                        // 使用methodProxy调用原始方法
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
        MyUserService service = container.getBean("myService", MyUserService.class);
        service.sayHello("helloqsw");
    }
}
