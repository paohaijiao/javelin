package org.paohaijiao.jstark.context;
import org.paohaijiao.jstark.bean.JBeanDefinition;
import org.paohaijiao.jstark.context.service.JProxyEnhancedBeanContainer;
import org.paohaijiao.jstark.factory.JBeanProviderFactory;
import org.paohaijiao.jstark.scan.JClassPathScanner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
public class JAnnotationConfigApplicationContext {
    protected final JBeanProvider beanContainer;

    public JAnnotationConfigApplicationContext(String... basePackages) {
        this(new Properties(), basePackages);
    }

    public JAnnotationConfigApplicationContext(Properties config, String... basePackages) {
        this.beanContainer = JBeanProviderFactory.createProvider(config);
        scanAndRegisterBeans(basePackages);
        processConfigurationClasses();
    }

    private void scanAndRegisterBeans(String[] basePackages) {
        try {
            for (String basePackage : basePackages) {
                List<JBeanDefinition> beanDefinitions = JClassPathScanner.scan(basePackage);
                for (JBeanDefinition bd : beanDefinitions) {
                    String beanName = toLowerFirstCase(bd.getBeanClass().getSimpleName());
                    beanContainer.registerBeanDefinition(beanName, bd);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to scan base packages", e);
        }
    }

    private void processConfigurationClasses() {
        // 这里可以扩展处理@Configuration类
        // 目前只处理@Component、@Service、@Repository注解的类
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return beanContainer.getBean(beanName, requiredType);
    }

    public void addBeanPostProcessor(JBeanPostProcessor processor) {
        beanContainer.addBeanPostProcessor(processor);
    }

    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        if (beanContainer instanceof JProxyEnhancedBeanContainer) {
            ((JProxyEnhancedBeanContainer) beanContainer).registerInterceptor(beanName, interceptor);
        }
        // 可以扩展支持CglibBeanContainer
    }

    private String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
