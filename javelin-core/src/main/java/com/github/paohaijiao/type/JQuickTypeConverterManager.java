package com.github.paohaijiao.type;

import com.github.paohaijiao.spi.ServiceLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** 按优先级加载并缓存目标类型对应的 {@link JQuickTypeConverter}。 */
public final class JQuickTypeConverterManager {

    private static final Map<Class<?>, Optional<JQuickTypeConverter>> CONVERTER_CACHE = new ConcurrentHashMap<>();

    private static volatile List<JQuickTypeConverter> converters;

    private JQuickTypeConverterManager() {
        throw new AssertionError("工具类不允许实例化");
    }

    public static JQuickTypeConverter getConverter(Class<?> targetType) {
        if (targetType == null) {
            return null;
        }
        return CONVERTER_CACHE.computeIfAbsent(targetType, JQuickTypeConverterManager::resolve).orElse(null);
    }

    public static void reload() {
        converters = null;
        CONVERTER_CACHE.clear();
        ServiceLoader.reload(JQuickTypeConverter.class);
    }

    private static Optional<JQuickTypeConverter> resolve(Class<?> targetType) {
        for (JQuickTypeConverter converter : loadConverters()) {
            try {
                if (converter.supports(targetType)) {
                    return Optional.of(converter);
                }
            } catch (Throwable ignore) {
                // 单个扩展点异常不影响主流程
            }
        }
        return Optional.empty();
    }

    private static List<JQuickTypeConverter> loadConverters() {
        List<JQuickTypeConverter> result = converters;
        if (result == null) {
            synchronized (JQuickTypeConverterManager.class) {
                result = converters;
                if (result == null) {
                    result = doLoad();
                    converters = result;
                }
            }
        }
        return result;
    }

    private static List<JQuickTypeConverter> doLoad() {
        try {
            return ServiceLoader.loadServicesByPriority(JQuickTypeConverter.class);
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }
}
