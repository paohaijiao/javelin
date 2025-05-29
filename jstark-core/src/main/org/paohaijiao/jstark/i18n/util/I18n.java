package org.paohaijiao.jstark.i18n.util;

import org.paohaijiao.jstark.i18n.I18nService;
import org.paohaijiao.jstark.i18n.factory.I18nServiceFactory;
import org.paohaijiao.jstark.i18n.impl.AdvancedI18nService;

import java.util.Locale;

public class I18n {
    private static final I18nService SERVICE = I18nServiceFactory.getDefaultInstance();

    private I18n() {}

    public static String print(String code) {
        return SERVICE.getMessage(code);
    }

    public static String print(String code, Object... args) {
        return SERVICE.getMessage(code, args);
    }

    public static String print(String code, Locale locale, Object... args) {
        return SERVICE.getMessage(code, locale, args);
    }

    public static void setLocale(Locale locale) {
        if (SERVICE instanceof AdvancedI18nService) {
            ((AdvancedI18nService) SERVICE).setCurrentLocale(locale);
        }
    }

    public static void reload() {
        SERVICE.reload();
    }
}
