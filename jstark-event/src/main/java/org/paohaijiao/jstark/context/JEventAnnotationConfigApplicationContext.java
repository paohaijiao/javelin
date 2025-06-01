package org.paohaijiao.jstark.context;


import org.paohaijiao.jstark.bean.JBeanDefinition;
import org.paohaijiao.jstark.event.JApplicationEvent;
import org.paohaijiao.jstark.multicaster.JSimpleApplicationEventMulticaster;


public class JEventAnnotationConfigApplicationContext extends JAnnotationConfigApplicationContext implements JBeanProvider {
    private JSimpleApplicationEventMulticaster eventMulticaster;

    protected void initEventMulticaster() {
        this.eventMulticaster = new JSimpleApplicationEventMulticaster(this);
        this.registerBeanDefinition("applicationEventMulticaster",
                new JBeanDefinition(JSimpleApplicationEventMulticaster.class));
    }

    public JEventAnnotationConfigApplicationContext(String... basePackages) {
        super(basePackages);
        initEventMulticaster();
    }


    public void publishEvent(JApplicationEvent event) {
        eventMulticaster.publishEvent(event);
    }

    public JSimpleApplicationEventMulticaster getEventMulticaster() {
        return eventMulticaster;
    }

    @Override
    public void registerBeanDefinition(String beanName, JBeanDefinition beanDefinition) {
        beanContainer.registerBeanDefinition(beanName, beanDefinition);
    }
}
