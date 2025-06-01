package org.paohaijiao.jstark.context;
import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.service.ProxyEnhancedBeanContainer;
import org.paohaijiao.jstark.scan.ClassPathScanner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
public class AnnotationConfigApplicationContext {
    protected final BeanContainer beanContainer;

    public AnnotationConfigApplicationContext(String... basePackages) {
        this(new Properties(), basePackages);
    }

    public AnnotationConfigApplicationContext(Properties config, String... basePackages) {
        this.beanContainer = BeanContainerFactory.createContainer(config);
        scanAndRegisterBeans(basePackages);
        processConfigurationClasses();
    }

    private void scanAndRegisterBeans(String[] basePackages) {
        try {
            for (String basePackage : basePackages) {
                List<BeanDefinition> beanDefinitions = ClassPathScanner.scan(basePackage);
                for (BeanDefinition bd : beanDefinitions) {
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

    public void addBeanPostProcessor(BeanPostProcessor processor) {
        beanContainer.addBeanPostProcessor(processor);
    }

    public void registerInterceptor(String beanName, MethodInterceptor interceptor) {
        if (beanContainer instanceof ProxyEnhancedBeanContainer) {
            ((ProxyEnhancedBeanContainer) beanContainer).registerInterceptor(beanName, interceptor);
        }
        // 可以扩展支持CglibBeanContainer
    }

    private String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
