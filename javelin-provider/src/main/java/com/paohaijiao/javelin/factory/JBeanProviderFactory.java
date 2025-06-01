package com.paohaijiao.javelin.factory;

import com.paohaijiao.javelin.context.JBeanProvider;
import com.paohaijiao.javelin.context.service.JProxyEnhancedBeanContainer;
import com.paohaijiao.javelin.context.service.JSimpleBeanContainer;

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
