package com.paohaijiao.javelin.context;
import com.paohaijiao.javelin.bean.JBeanDefinition;
import com.paohaijiao.javelin.context.service.JProxyEnhancedBeanProvider;
import com.paohaijiao.javelin.factory.JBeanProviderFactory;
import com.paohaijiao.javelin.scan.JClassPathScanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class JAnnotationConfigApplicationContext {
    protected final JBeanProvider beanProvider;
    protected final Set<String> beanNames = new HashSet<>();

    public JAnnotationConfigApplicationContext(String... basePackages) {
        this(new Properties(), basePackages);
    }

    public JAnnotationConfigApplicationContext(Properties config, String... basePackages) {
        this.beanProvider = JBeanProviderFactory.createProvider(config);
        scanAndRegisterBeans(basePackages);
        processConfigurationClasses();
    }

    private void scanAndRegisterBeans(String[] basePackages) {
        try {
            for (String basePackage : basePackages) {
                List<JBeanDefinition> beanDefinitions = JClassPathScanner.scan(basePackage);
                for (JBeanDefinition bd : beanDefinitions) {
                    String beanName = toLowerFirstCase(bd.getBeanClass().getSimpleName());
                    beanProvider.registerBeanDefinition(beanName, bd);
                    beanNames.add(beanName);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to scan base packages", e);
        }
    }

    protected void processConfigurationClasses() {
        // 这里可以扩展处理@Configuration类
        // 目前只处理@Component、@Service、@Repository注解的类
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return beanProvider.getBean(beanName, requiredType);
    }

    public void addBeanPostProcessor(JBeanPostProcessor processor) {
        beanProvider.addBeanPostProcessor(processor);
    }

    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        if (beanProvider instanceof JProxyEnhancedBeanProvider) {
            ((JProxyEnhancedBeanProvider) beanProvider).registerInterceptor(beanName, interceptor);
        }
        // 可以扩展支持CglibBeanContainer
    }

    private String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
    protected Set<String> getBeanNames() {
        return new HashSet<>(beanNames);
    }
}
