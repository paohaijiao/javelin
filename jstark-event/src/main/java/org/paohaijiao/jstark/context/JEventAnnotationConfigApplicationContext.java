package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.event.JApplicationEvent;
import org.paohaijiao.jstark.multicaster.JSimpleApplicationEventMulticaster;


public class JEventAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext implements BeanContainer {
    private JSimpleApplicationEventMulticaster eventMulticaster;

    protected void initEventMulticaster() {
        this.eventMulticaster = new JSimpleApplicationEventMulticaster(this);
        this.registerBeanDefinition("applicationEventMulticaster",
                new BeanDefinition(JSimpleApplicationEventMulticaster.class));
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
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanContainer.registerBeanDefinition(beanName, new BeanDefinition(JSimpleApplicationEventMulticaster.class));
    }
}
