package com.paohaijiao.javelin.i18n.impl;

import org.apache.commons.lang3.StringUtils;
import com.paohaijiao.javelin.i18n.I18nService;
import com.paohaijiao.javelin.i18n.MessageFormatter;
import com.paohaijiao.javelin.i18n.conf.ResourceBundleConfig;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//@Slf4j
public class AdvancedI18nService implements I18nService {


    private final List<ResourceBundleConfig> bundleConfigs = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<Locale, ResourceBundleGroup> bundleCache = new ConcurrentHashMap<>();
    private final ThreadLocal<Locale> currentLocale = ThreadLocal.withInitial(() -> Locale.getDefault());
    private Locale fallbackLocale = Locale.ENGLISH;
    private volatile boolean watching = false;
    private WatchService watchService;
    private Thread watchThread;
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();

    private MessageFormatter messageFormatter = new DefaultMessageFormatter();
    private static class ResourceBundleGroup {
        final List<ResourceBundle> bundles = new ArrayList<>();
        final long loadTime = System.currentTimeMillis();

        String getString(String key) {
            for (ResourceBundle bundle : bundles) {
                try {
                    if (bundle.containsKey(key)) {
                        return bundle.getString(key);
                    }
                } catch (MissingResourceException e) {
                    continue;
                }
            }
            throw new MissingResourceException("Can't find resource for key: " + key,
                    ResourceBundle.class.getName(), key);
        }

        boolean containsKey(String key) {
            return bundles.stream().anyMatch(b -> b.containsKey(key));
        }
    }

    @Override
    public String getMessage(String code, Locale locale, Object... args) {
        if (StringUtils.isBlank(code)) {
            return code;
        }

        Locale targetLocale = locale != null ? locale : currentLocale.get();
        if (targetLocale == null) {
            targetLocale = fallbackLocale;
        }

        try {
            ResourceBundleGroup bundleGroup = getResourceBundle(targetLocale);
            String pattern = bundleGroup.getString(code);
            return messageFormatter.format(pattern, targetLocale, args);
        } catch (MissingResourceException e) {
            //log.info("Missing resource for key: {} in locale: {}", code, targetLocale);

            // 尝试回退语言
            if (!targetLocale.equals(fallbackLocale)) {
                return getMessage(code, fallbackLocale, args);
            }

            return code;
        } catch (Exception e) {
            //logger.error("Error formatting message for key: " + code, e);
            return code;
        }
    }

    @Override
    public String getMessage(String code, Object... args) {
        return getMessage(code, currentLocale.get(), args);
    }

    private ResourceBundleGroup getResourceBundle(Locale locale) {
        // 检查缓存
        ResourceBundleGroup bundleGroup = bundleCache.get(locale);
        if (bundleGroup != null) {
            cacheHits.incrementAndGet();
            return bundleGroup;
        }

        cacheMisses.incrementAndGet();
        synchronized (this) {
            // 双重检查
            bundleGroup = bundleCache.get(locale);
            if (bundleGroup != null) {
                return bundleGroup;
            }

            // 创建新的资源包组
            bundleGroup = new ResourceBundleGroup();
            for (ResourceBundleConfig config : bundleConfigs) {
                try {
                    ResourceBundle bundle = ResourceBundle.getBundle(
                            config.getBaseName(),
                            locale,
                            config.getControl());
                    bundleGroup.bundles.add(bundle);
                } catch (Exception e) {
//                    logger.warn("Failed to load resource bundle: {} for locale: {}",
//                            config.getBaseName(), locale, e);
                }
            }

            if (bundleGroup.bundles.isEmpty()) {
                throw new IllegalStateException("No resource bundles available for locale: " + locale);
            }

            bundleCache.put(locale, bundleGroup);
            return bundleGroup;
        }
    }

    @Override
    public boolean supportsLocale(Locale locale) {
        if (locale == null) return false;

        // 检查缓存中是否存在
        if (bundleCache.containsKey(locale)) {
            return true;
        }

        // 未缓存时尝试加载
        try {
            for (ResourceBundleConfig config : bundleConfigs) {
                ResourceBundle bundle = ResourceBundle.getBundle(
                        config.getBaseName(),
                        locale,
                        config.getControl());
                if (!bundle.getLocale().equals(Locale.ROOT)) {
                    return true;
                }
            }
        } catch (Exception e) {
      //      logger.debug("Locale not supported: " + locale, e);
        }

        return false;
    }

    @Override
    public void reload() {
        synchronized (this) {
            bundleCache.clear();
            cacheHits.set(0);
            cacheMisses.set(0);
     //       logger.info("Reloaded all i18n resource bundles");
        }
    }

    @Override
    public void addResourceBundle(ResourceBundleConfig config) {
        bundleConfigs.add(config);
        reload();
    }

    @Override
    public void setFallbackLocale(Locale fallbackLocale) {
        this.fallbackLocale = fallbackLocale != null ? fallbackLocale : Locale.ENGLISH;
    }

    public void setCurrentLocale(Locale locale) {
        currentLocale.set(locale);
    }

    public void setMessageFormatter(MessageFormatter formatter) {
        this.messageFormatter = Objects.requireNonNull(formatter);
    }

    public void startFileWatching() throws IOException {
        if (watching) {
            return;
        }

        watchService = FileSystems.getDefault().newWatchService();
        watchThread = new Thread(this::watchFileChanges, "I18n-File-Watcher");
        watchThread.setDaemon(true);
        watchThread.start();
        watching = true;
    }

    public void stopFileWatching() throws InterruptedException {
        if (!watching) {
            return;
        }

        watching = false;
        watchThread.interrupt();
        watchThread.join(1000);
        try {
            watchService.close();
        } catch (IOException e) {
         //   logger.warn("Error closing watch service", e);
        }
    }

    private void watchFileChanges() {
        try {
            // 注册所有资源文件目录
            Set<Path> watchedDirs = bundleConfigs.stream()
                    .map(config -> Paths.get("src/main/resources",
                            config.getBaseName().replace('.', '/')))
                    .filter(Files::isDirectory)
                    .collect(Collectors.toSet());

            for (Path dir : watchedDirs) {
                dir.register(watchService,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE);
            }

            while (watching) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (filename.toString().endsWith(".properties")) {
          //              logger.info("Detected change in i18n file: {}", filename);
                        reload();
                        break;
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            if (watching) {
         //       logger.error("Error in file watcher thread", e);
            }
        }
    }

    @Override
    public Map<Locale, Long> getResourceBundleStats() {
        return bundleCache.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().loadTime));
    }

    public long getCacheHits() {
        return cacheHits.get();
    }

    public long getCacheMisses() {
        return cacheMisses.get();
    }

    public double getCacheHitRate() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        return total > 0 ? (double) hits / total : 0.0;
    }
}
