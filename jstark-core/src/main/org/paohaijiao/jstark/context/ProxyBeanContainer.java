package org.paohaijiao.jstark.context;
import org.paohaijiao.jstark.anno.Autowired;
import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.bean.MethodInvocation;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class ProxyBeanContainer {
    // Bean 定义注册表
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    // 单例 Bean 缓存
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // Bean 后处理器
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    // 方法拦截器注册表
    private final Map<String, MethodInterceptor> interceptors = new ConcurrentHashMap<>();

    /**
     * 注册 Bean 定义
     */
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalStateException("Bean名称 '" + beanName + "' 已存在");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 添加 Bean 后处理器
     */
    public void addBeanPostProcessor(BeanPostProcessor processor) {
        this.beanPostProcessors.add(processor);
    }

    /**
     * 注册方法拦截器
     * @param beanName 要拦截的 Bean 名称
     * @param interceptor 方法拦截器
     */
    public void registerInterceptor(String beanName, MethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }

    /**
     * 获取 Bean 实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> requiredType) {
        Object bean = doGetBean(beanName);
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new IllegalArgumentException("Bean '" + beanName + "' 不是 " + requiredType.getName() + " 类型");
        }
        return (T) bean;
    }

    /**
     * 实际获取 Bean 的方法
     */
    protected Object doGetBean(String beanName) {
        // 检查单例缓存
        Object sharedInstance = singletonObjects.get(beanName);
        if (sharedInstance != null) {
            return sharedInstance;
        }

        // 检查 Bean 定义是否存在
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new IllegalArgumentException("未找到名为 '" + beanName + "' 的Bean定义");
        }

        // 创建 Bean 实例
        Object bean = createBean(beanName, bd);

        // 如果是单例，放入缓存
        if (bd.isSingleton()) {
            addSingleton(beanName, bean);
        }

        return bean;
    }

    /**
     * 创建 Bean 实例
     */
    protected Object createBean(String beanName, BeanDefinition bd) {
        try {
            // 实例化前处理
            Object bean = resolveBeforeInstantiation(beanName, bd);
            if (bean != null) {
                return bean;
            }

            // 实例化对象
            bean = instantiateBean(beanName, bd);

            // 应用 Bean 后处理器 (前置处理)
            bean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

            // 属性注入
            populateBean(beanName, bd, bean);

            // 初始化方法调用
            initializeBean(beanName, bd, bean);

            // 应用 Bean 后处理器 (后置处理)
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);

            // 如果需要代理，创建代理对象
            MethodInterceptor interceptor = interceptors.get(beanName);
            if (interceptor != null) {
                bean = createProxy(bean, interceptor);
            }

            return bean;
        } catch (Exception e) {
            throw new RuntimeException("创建Bean '" + beanName + "' 失败", e);
        }
    }

    /**
     * 实例化前处理 (给后处理器机会返回代理对象)
     */
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition bd) {
        for (BeanPostProcessor bp : beanPostProcessors) {
            Object result = bp.postProcessBeforeInstantiation(bd.getBeanClass(), beanName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * 实例化 Bean
     */
    protected Object instantiateBean(String beanName, BeanDefinition bd) throws Exception {
        return bd.getBeanClass().getDeclaredConstructor().newInstance();
    }

    /**
     * 属性注入
     */
    protected void populateBean(String beanName, BeanDefinition bd, Object bean) {
        // 简化版：只处理 @Autowired 标注的字段
        for (Field field : bd.getBeanClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getName(), field.getType());
                    field.set(bean, dependency);
                } catch (Exception e) {
                    throw new RuntimeException("注入依赖 '" + field.getName() + "' 到Bean '" + beanName + "' 失败", e);
                }
            }
        }
    }

    /**
     * 初始化 Bean
     */
    protected void initializeBean(String beanName, BeanDefinition bd, Object bean) {
        // 调用初始化方法
        if (bd.getInitMethodName() != null) {
            try {
                Method initMethod = bd.getBeanClass().getMethod(bd.getInitMethodName());
                initMethod.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException("调用初始化方法 '" + bd.getInitMethodName() + "' 失败", e);
            }
        }
    }

    /**
     * 创建代理对象
     */
    protected Object createProxy(Object target, MethodInterceptor interceptor) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    // 如果有拦截器，使用拦截器处理
                    return interceptor.intercept(new MethodInvocation() {
                        @Override
                        public Object proceed() throws Throwable {
                            return method.invoke(target, args);
                        }

                        @Override
                        public Method getMethod() {
                            return method;
                        }

                        @Override
                        public Object[] getArguments() {
                            return args;
                        }
                    });
                });
    }

    /**
     * 应用 Bean 后处理器 (初始化前)
     */
    protected Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        Object result = bean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 应用 Bean 后处理器 (初始化后)
     */
    protected Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        Object result = bean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 添加单例 Bean 到缓存
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }
}
