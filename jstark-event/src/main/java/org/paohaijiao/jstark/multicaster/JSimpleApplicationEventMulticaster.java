package org.paohaijiao.jstark.multicaster;

import org.paohaijiao.jstark.anno.JComponent;
import org.paohaijiao.jstark.context.JBeanPostProcessor;
import org.paohaijiao.jstark.event.JApplicationEvent;
import org.paohaijiao.jstark.function.JApplicationListener;
import org.paohaijiao.jstark.publish.JApplicationEventPublisher;
import org.paohaijiao.jstark.context.JBeanProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@JComponent
public class JSimpleApplicationEventMulticaster extends  JApplicationEventPublisher implements JBeanPostProcessor {
    private final Map<Class<?>, List<JApplicationListener<?>>> listenerCache = new ConcurrentHashMap<>();
    private final JBeanProvider beanContainer;

    public JSimpleApplicationEventMulticaster(JBeanProvider beanContainer) {
        this.beanContainer = beanContainer;
    }

    @Override
    public void publishEvent(JApplicationEvent event) {
        Class<?> eventType = event.getClass();
        List<JApplicationListener<?>> listeners = listenerCache.get(eventType);
        if (listeners != null) {
            for (JApplicationListener<?> listener : listeners) {
                invokeListener(listener, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void invokeListener(JApplicationListener<?> listener, JApplicationEvent event) {
        ((JApplicationListener<JApplicationEvent>) listener).onApplicationEvent(event);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 检查bean是否是ApplicationListener
        if (bean instanceof JApplicationListener) {
            addApplicationListener((JApplicationListener<?>) bean);
        }
        return bean;
    }

    private void addApplicationListener(JApplicationListener<?> listener) {
        Class<?> eventType = resolveEventType(listener);
        listenerCache.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(listener);
    }

    private Class<?> resolveEventType(JApplicationListener<?> listener) {
        Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == JApplicationListener.class) {
                    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }
        throw new IllegalArgumentException("can not parse listener type: " + listener.getClass());
    }

    public void addApplicationListeners(Collection<JApplicationListener<?>> listeners) {
        listeners.forEach(this::addApplicationListener);
    }
}
