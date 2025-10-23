package com.github.paohaijiao.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 国际化工具类，支持UTF-8编码，提供灵活的消息获取方式
 *
 * @author Martin
 * @version 2.0.0
 * @since 2025/10/23
 */
public class I18nUtils {

    private static final String DEFAULT_BASE_NAME = "i18n/messages";

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final ThreadLocal<Locale> currentLocale = ThreadLocal.withInitial(() -> DEFAULT_LOCALE);

    private static final int CACHE_MAX_SIZE = 1000;
    private static final long CACHE_EXPIRE_MILLIS = 3600000; // 1小时
    private static final Map<String, CacheEntry> messageCache = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

    private static final ResourceBundle.Control UTF8_CONTROL = new ResourceBundle.Control() {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (InputStream stream = getInputStream(loader, resourceName, reload)) {
                if (stream != null) {
                    try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                        return new PropertyResourceBundle(reader);
                    }
                }
            } catch (IOException e) {
                 e.printStackTrace();
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }

        private InputStream getInputStream(ClassLoader loader, String resourceName, boolean reload) throws IOException {
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        return connection.getInputStream();
                    }
                }
            }
            return loader.getResourceAsStream(resourceName);
        }

        @Override
        public long getTimeToLive(String baseName, Locale locale) {
            return CACHE_EXPIRE_MILLIS;
        }

        @Override
        public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
            return (System.currentTimeMillis() - loadTime) >= getTimeToLive(baseName, locale);
        }
    };

    /**
     * 设置当前线程的语言环境
     */
    public static void setLocale(Locale locale) {
        if (locale == null) {
            currentLocale.set(DEFAULT_LOCALE);
        } else {
            currentLocale.set(locale);
        }
    }

    /**
     * 设置当前线程的语言环境
     */
    public static void setLocale(String language, String country) {
        setLocale(new Locale(language, country));
    }

    /**
     * 获取当前线程的语言环境
     */
    public static Locale getCurrentLocale() {
        return currentLocale.get();
    }

    /**
     * 清除当前线程的语言环境设置
     */
    public static void clearThreadLocale() {
        currentLocale.remove();
    }

    /**
     * 获取资源包 - 支持UTF-8编码
     */
    private static ResourceBundle getBundle(String baseName) {
        return getBundle(baseName, currentLocale.get());
    }

    /**
     * 获取指定区域的资源包
     */
    private static ResourceBundle getBundle(String baseName, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, UTF8_CONTROL);
            Locale actualLocale = bundle.getLocale();
            if (!actualLocale.equals(locale) && !actualLocale.equals(Locale.ROOT)) {
                System.out.printf("警告: 请求的locale %s 但实际加载的是 %s%n", locale, actualLocale);
            }
            return bundle;
        } catch (MissingResourceException e) {
            System.out.printf("无法找到资源: %s for %s%n", baseName, locale);
            try {
                System.out.printf("尝试回退到默认locale: %s%n", DEFAULT_LOCALE);
                return ResourceBundle.getBundle(baseName, DEFAULT_LOCALE, UTF8_CONTROL);
            } catch (MissingResourceException e2) {
                System.out.println("连默认资源也找不到!");
                throw e2;
            }
        }
    }

    /**
     * 获取国际化消息
     */
    public static String getMessage(String key) {
        return getMessage(DEFAULT_BASE_NAME, key);
    }

    /**
     * 获取国际化消息
     */
    public static String getMessage(String baseName, String key) {
        String cacheKey = baseName + "_" + currentLocale.get() + "_" + key;
        cacheLock.readLock().lock();
        try {
            CacheEntry entry = messageCache.get(cacheKey);
            if (entry != null && !entry.isExpired()) {
                return entry.value;
            }
        } finally {
            cacheLock.readLock().unlock();
        }
        // 缓存未命中或已过期，重新加载
        String value;
        try {
            ResourceBundle bundle = getBundle(baseName);
            value = bundle.getString(key);
            // 更新缓存
            cacheLock.writeLock().lock();
            try {
                // 缓存满时清理过期条目
                if (messageCache.size() >= CACHE_MAX_SIZE) {
                    cleanupExpiredCache();
                }
                messageCache.put(cacheKey, new CacheEntry(value));
            } finally {
                cacheLock.writeLock().unlock();
            }

            return value;
        } catch (MissingResourceException e) {
            System.out.println("键不存在: " + key);
            return "???" + key + "???";
        }
    }

    /**
     * 获取带参数的国际化消息
     */
    public static String getMessage(String key, Object... params) {
        return getMessage(DEFAULT_BASE_NAME, key, params);
    }

    /**
     * 获取带参数的国际化消息
     */
    public static String getMessage(String baseName, String key, Object... params) {
        try {
            String pattern = getMessage(baseName, key);
            return MessageFormat.format(pattern, params);
        } catch (Exception e) {
            return "???" + key + "???";
        }
    }

    /**
     * 检查指定的key是否存在
     */
    public static boolean containsKey(String key) {
        return containsKey(DEFAULT_BASE_NAME, key);
    }

    /**
     * 检查指定的key是否存在
     */
    public static boolean containsKey(String baseName, String key) {
        try {
            ResourceBundle bundle = getBundle(baseName);
            return bundle.containsKey(key);
        } catch (MissingResourceException e) {
            return false;
        }
    }

    /**
     * 获取所有可用的key
     */
    public static Set<String> getKeys() {
        return getKeys(DEFAULT_BASE_NAME);
    }

    /**
     * 获取所有可用的key
     */
    public static Set<String> getKeys(String baseName) {
        try {
            ResourceBundle bundle = getBundle(baseName);
            return new HashSet<>(bundle.keySet());
        } catch (MissingResourceException e) {
            return Collections.emptySet();
        }
    }

    /**
     * 清除所有缓存
     */
    public static void clearCache() {
        cacheLock.writeLock().lock();
        try {
            messageCache.clear();
            ResourceBundle.clearCache();
            System.out.println("缓存已清除");
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * 清理过期的缓存条目
     */
    private static void cleanupExpiredCache() {
        long now = System.currentTimeMillis();
        messageCache.entrySet().removeIf(entry ->
                now - entry.getValue().timestamp > CACHE_EXPIRE_MILLIS);
    }

    /**
     * 获取支持的语言列表
     */
    public static List<Locale> getSupportedLocales() {
        return Arrays.asList(Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE, Locale.JAPANESE, Locale.FRENCH, Locale.GERMAN);
    }

    /**
     * 检查是否支持指定的语言
     */
    public static boolean isSupportedLocale(Locale locale) {
        return getSupportedLocales().contains(locale);
    }

    /**
     * 获取指定基础名称下所有可用的区域设置
     */
    public static List<Locale> getAvailableLocales(String baseName) {
        List<Locale> available = new ArrayList<>();
        for (Locale locale : getSupportedLocales()) {
            try {
                ResourceBundle.getBundle(baseName, locale, UTF8_CONTROL);
                available.add(locale);
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
        }
        return available;
    }

    /**
     * 缓存条目类
     */
    private static class CacheEntry {
        String value;
        long timestamp;
        CacheEntry(String value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRE_MILLIS;
        }
    }
}