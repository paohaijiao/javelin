package org.paohaijiao.jstark.factory;

import org.paohaijiao.jstark.context.JBeanProvider;
import org.paohaijiao.jstark.context.service.JProxyEnhancedBeanContainer;
import org.paohaijiao.jstark.context.service.JSimpleBeanContainer;

import java.util.Properties;

public class JBeanProviderFactory {
    /**
     * create container instance based on configuration
     */
    public static JBeanProvider createProvider(Properties config) {
        String mode = config.getProperty("bean.container.mode", "simple");
        switch (mode.toLowerCase()) {
            case "proxy":
                return new JProxyEnhancedBeanContainer();
            case "simple":
            default:
                return new JSimpleBeanContainer();
        }
    }

    /**
     * create default container
     */
    public static JBeanProvider createDefaultContainer() {
        return createProvider(new Properties());
    }
}
