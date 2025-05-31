package org.paohaijiao.jstark.context.service;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.paohaijiao.jstark.context.CglibMethodInterceptor;
import org.paohaijiao.jstark.context.bean.BeanDefinition;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class CglibBeanContainer extends SimpleBeanContainer{
    // 存储Bean拦截器
    private final Map<String, CglibMethodInterceptor> interceptors = new ConcurrentHashMap<>();

    /**
     * 注册方法拦截器
     */
    public void registerInterceptor(String beanName, CglibMethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }

    @Override
    protected Object createBean(String beanName, BeanDefinition bd) {
        Object rawBean = super.createBean(beanName, bd);
        CglibMethodInterceptor interceptor = interceptors.get(beanName);
        if (interceptor != null) {
            return createCglibProxy(bd.getBeanClass(), rawBean, interceptor);
        }
        return rawBean;
    }

    /**
     * 创建CGLIB代理对象
     */
    protected Object createCglibProxy(Class<?> targetClass, Object targetBean,
                                      CglibMethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new InternalInterceptor(targetBean, interceptor));
        return enhancer.create();
    }
}
