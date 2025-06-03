package com.paohaijiao.javelin.factory;

import com.paohaijiao.javelin.context.JBeanProvider;
import com.paohaijiao.javelin.context.service.JProxyEnhancedBeanProvider;
import com.paohaijiao.javelin.context.service.JSimpleBeanProvider;

import java.util.Properties;

public class JBeanProviderFactory {
    /**
     * create container instance based on configuration
     */
    public static JBeanProvider createProvider(Properties config) {
        String mode = config.getProperty("bean.container.mode", "simple");
        switch (mode.toLowerCase()) {
            case "proxy":
                return new JProxyEnhancedBeanProvider();
            case "simple":
            default:
                return new JSimpleBeanProvider();
        }
    }

    /**
     * create default container
     */
    public static JBeanProvider createDefaultContainer() {
        return createProvider(new Properties());
    }
}
