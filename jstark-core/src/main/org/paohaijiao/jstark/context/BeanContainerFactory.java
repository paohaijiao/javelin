package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.service.ProxyEnhancedBeanContainer;
import org.paohaijiao.jstark.context.service.SimpleBeanContainer;

import java.util.Properties;

public class BeanContainerFactory {
    /**
     * 根据配置创建容器实例
     */
    public static BeanContainer createContainer(Properties config) {
        String mode = config.getProperty("bean.container.mode", "simple");
        switch (mode.toLowerCase()) {
            case "proxy":
                return new ProxyEnhancedBeanContainer();
            case "simple":
            default:
                return new SimpleBeanContainer();
        }
    }

    /**
     * 创建默认容器
     */
    public static BeanContainer createDefaultContainer() {
        return createContainer(new Properties());
    }
}
