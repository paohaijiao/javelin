package com.github.paohaijiao.spi;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 增强的 ServiceLoader 工具类
 * 支持 @Priority 注解和 PriorityConstants 优先级管理
 */
public final class ServiceLoader {

    private static final Map<Class<?>, java.util.ServiceLoader<?>> SERVICE_LOADER_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, List<?>> SERVICE_INSTANCE_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, List<?>> PRIORITY_SERVICE_CACHE = new ConcurrentHashMap<>();

    private static final JConsole console = new JConsole();

    private ServiceLoader() {
        throw new AssertionError("工具类不允许实例化");
    }

    public static <T> List<T> loadServices(Class<T> serviceClass) {
        return loadServices(serviceClass, Thread.currentThread().getContextClassLoader());
    }


    @SuppressWarnings("unchecked")
    public static <T> List<T> loadServices(Class<T> serviceClass, ClassLoader classLoader) {
        return (List<T>) SERVICE_INSTANCE_CACHE.computeIfAbsent(serviceClass, key -> {
            java.util.ServiceLoader<T> serviceLoader = getServiceLoader(serviceClass, classLoader);
            List<T> instances = new ArrayList<>();
            serviceLoader.forEach(instances::add);
            return Collections.unmodifiableList(instances);
        });
    }


    @SuppressWarnings("unchecked")
    public static <T> List<T> loadServicesByPriority(Class<T> serviceClass) {
        return (List<T>) PRIORITY_SERVICE_CACHE.computeIfAbsent(serviceClass, key -> {
            List<T> services = new ArrayList<>(loadServices(serviceClass));
            services.sort(Comparator.comparingInt(ServiceLoader::getServicePriority));
            return Collections.unmodifiableList(services);
        });
    }

    private static int getServicePriority(Object service) {
        Class<?> serviceClass = service.getClass();
        Priority priorityAnnotation = serviceClass.getAnnotation(Priority.class);
        if (priorityAnnotation != null) {
            return priorityAnnotation.value();
        }
        Integer reflectivePriority = getReflectivePriority(service);
        if (reflectivePriority != null) {
            return reflectivePriority;
        }
        return PriorityConstants.DEFAULT;
    }

    private static Integer getReflectivePriority(Object service) {
        try {
            java.lang.reflect.Method method = service.getClass().getMethod("getPriority");
            if (method.getReturnType() == int.class || method.getReturnType() == Integer.class) {
                Object result = method.invoke(service);
                return (Integer) result;
            }
        } catch (NoSuchMethodException e) {
            console.log(JLogLevel.INFO, "No service priority found: " + service.getClass().getName());
        } catch (Exception e) {
            console.log(JLogLevel.INFO, "some error found in : " + e.getMessage());
        }
        try {
            java.lang.reflect.Field field = service.getClass().getField("priority");
            if (field.getType() == int.class || field.getType() == Integer.class) {
                Object result = field.get(service);
                return (Integer) result;
            }
        } catch (Exception e) {
            console.log(JLogLevel.INFO, "some error found in priority: " + e.getMessage());
        }
        return null;
    }

    public static <T> Optional<T> getHighestPriorityService(Class<T> serviceClass) {
        List<T> services = loadServicesByPriority(serviceClass);
        return services.isEmpty() ? Optional.empty() : Optional.of(services.get(0));
    }

    public static <T> Optional<T> getHighestPriorityService(Class<T> serviceClass, Predicate<T> filter) {
        return loadServicesByPriority(serviceClass).stream().filter(filter).findFirst();
    }

    public static <T> List<T> getServicesInPriorityRange(Class<T> serviceClass, int minPriority, int maxPriority) {
        return loadServicesByPriority(serviceClass).stream().filter(service -> {
            int priority = getServicePriority(service);
            return priority >= minPriority && priority <= maxPriority;
        }).collect(Collectors.toList());
    }

    public static <T> List<T> getSystemLevelServices(Class<T> serviceClass) {
        return getServicesInPriorityRange(serviceClass, PriorityConstants.SYSTEM_HIGHEST, PriorityConstants.SYSTEM_LOW);
    }

    public static <T> List<T> getApplicationLevelServices(Class<T> serviceClass) {
        return getServicesInPriorityRange(serviceClass, PriorityConstants.APPLICATION_HIGHEST, PriorityConstants.APPLICATION_LOW);
    }

    public static <T> List<T> getBusinessLevelServices(Class<T> serviceClass) {
        return getServicesInPriorityRange(serviceClass, PriorityConstants.BUSINESS_HIGHEST, PriorityConstants.BUSINESS_LOW);
    }

    public static <T> void printServicePriorities(Class<T> serviceClass) {
        List<T> services = loadServicesByPriority(serviceClass);
        console.log(JLogLevel.INFO, "=== " + serviceClass.getSimpleName() + " 服务加载信息 ===");
        console.log(JLogLevel.INFO, "总服务数: " + services.size());
        for (int i = 0; i < services.size(); i++) {
            T service = services.get(i);
            int priority = getServicePriority(service);
            String level = PriorityConstants.getPriorityName(priority);
            String info = String.format("%2d. %-40s | Priority: %-5d | Level: %-12s | Class: %s", i + 1, service.getClass().getSimpleName(), priority, level, service.getClass().getName());
            console.log(JLogLevel.INFO, info);
        }
    }

    public static <T> void reload(Class<T> serviceClass) {
        SERVICE_LOADER_CACHE.remove(serviceClass);
        SERVICE_INSTANCE_CACHE.remove(serviceClass);
        PRIORITY_SERVICE_CACHE.remove(serviceClass);
    }

    @SuppressWarnings("unchecked")
    private static <T> java.util.ServiceLoader<T> getServiceLoader(Class<T> serviceClass, ClassLoader classLoader) {
        return (java.util.ServiceLoader<T>) SERVICE_LOADER_CACHE.computeIfAbsent(serviceClass, key -> java.util.ServiceLoader.load(serviceClass, classLoader));
    }
}