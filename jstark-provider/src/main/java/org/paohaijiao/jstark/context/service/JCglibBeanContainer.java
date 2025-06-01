package org.paohaijiao.jstark.context.service;
import net.sf.cglib.proxy.Enhancer;
import org.paohaijiao.jstark.bean.JBeanDefinition;
import org.paohaijiao.jstark.context.JCglibMethodInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class JCglibBeanContainer extends JSimpleBeanContainer {
    // storage bean interceptor
    private final Map<String, JCglibMethodInterceptor> interceptors = new ConcurrentHashMap<>();

    /**
     * registration method interceptor
     */
    public void registerInterceptor(String beanName, JCglibMethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }

    @Override
    protected Object createBean(String beanName, JBeanDefinition bd) {
        Object rawBean = super.createBean(beanName, bd);
        JCglibMethodInterceptor interceptor = interceptors.get(beanName);
        if (interceptor != null) {
            return createCglibProxy(bd.getBeanClass(), rawBean, interceptor);
        }
        return rawBean;
    }

    /**
     * create CGLIB proxy object
     */
    protected Object createCglibProxy(Class<?> targetClass, Object targetBean,
                                      JCglibMethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new JInternalInterceptor(targetBean, interceptor));
        return enhancer.create();
    }
}
