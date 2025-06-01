package com.paohaijiao.javelin.i18n.factory;

import com.paohaijiao.javelin.i18n.I18nService;
import com.paohaijiao.javelin.i18n.conf.ResourceBundleConfig;
import com.paohaijiao.javelin.i18n.impl.AdvancedI18nService;

import java.io.IOException;
import java.util.Locale;

public class I18nServiceFactory {
    private static volatile I18nService instance;

    public static I18nService getDefaultInstance() {
        if (instance == null) {
            synchronized (I18nServiceFactory.class) {
                if (instance == null) {
                    instance = createDefaultService();
                }
            }
        }
        return instance;
    }

    private static I18nService createDefaultService() {
        AdvancedI18nService service = new AdvancedI18nService();

        // 添加默认资源文件
        service.addResourceBundle(new ResourceBundleConfig("i18n/messages"));
        service.addResourceBundle(new ResourceBundleConfig("i18n/validation"));

        // 设置回退语言
        service.setFallbackLocale(Locale.ENGLISH);

        // 启动文件监听（开发环境）
        if (isDevelopmentMode()) {
            try {
                service.startFileWatching();
            } catch (IOException e) {
                System.err.println("Failed to start file watching: " + e.getMessage());
            }
        }

        return service;
    }

    private static boolean isDevelopmentMode() {
        // 简化实现，实际应根据环境配置判断
        return true;
    }
}
