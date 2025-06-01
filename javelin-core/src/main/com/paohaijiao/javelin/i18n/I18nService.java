package com.paohaijiao.javelin.i18n;

import com.paohaijiao.javelin.i18n.conf.ResourceBundleConfig;

import java.util.Locale;
import java.util.Map;

public interface I18nService {
    /**
     *
     * @param code
     * @param locale
     * @param args
     * @return
     */
    String getMessage(String code, Locale locale, Object... args);

    /**
     *
     * @param code
     * @param args
     * @return
     */
    String getMessage(String code, Object... args);

    /**
     *
     * @param locale
     * @return
     */
    boolean supportsLocale(Locale locale);

    /**
     *
     */
    void reload();

    /**
     *
     * @param config
     */
    void addResourceBundle(ResourceBundleConfig config);

    /**
     *
     * @param fallbackLocale
     */
    void setFallbackLocale(Locale fallbackLocale);

    /**
     *
     * @return
     */
    Map<Locale, Long> getResourceBundleStats();
}
