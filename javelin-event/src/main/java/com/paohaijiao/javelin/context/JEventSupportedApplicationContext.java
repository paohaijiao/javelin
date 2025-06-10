package com.paohaijiao.javelin.context;

import com.paohaijiao.javelin.anno.JEventListener;
import com.paohaijiao.javelin.event.JApplicationEvent;
import com.paohaijiao.javelin.publisher.JEventPublisher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class JEventSupportedApplicationContext extends JAnnotationConfigApplicationContext  implements JEventPublisher {
    protected static final Map<Class<?>, List<EventListenerMethod>> eventListeners = new ConcurrentHashMap<>();

    public JEventSupportedApplicationContext(String... basePackages) {
        super(basePackages);
    }

    public JEventSupportedApplicationContext(Properties config, String... basePackages) {
        super(config, basePackages);
    }

    @Override
    protected void processConfigurationClasses() {
        super.processConfigurationClasses();
        // Scan for event listeners after beans are registered
        for (String beanName : getBeanNames()) {
            Object bean = getBean(beanName, Object.class);
            registerEventListeners(bean);
        }
    }

    private void registerEventListeners(Object bean) {
        Class<?> beanClass = bean.getClass();
        for (Method method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(JEventListener.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1 || !JApplicationEvent.class.isAssignableFrom(parameterTypes[0])) {
                    throw new IllegalArgumentException(
                            "Event listener method must have exactly one parameter of type JApplicationEvent: " +
                                    beanClass.getName() + "." + method.getName());
                }

                Class<?> eventType = parameterTypes[0];
                eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                        .add(new EventListenerMethod(bean, method));
            }
        }
    }

    @Override
    public void publishEvent(JApplicationEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }

        Class<?> eventType = event.getClass();
        List<EventListenerMethod> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            for (EventListenerMethod listener : listeners) {
                try {
                    listener.method.invoke(listener.bean, event);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke event listener", e);
                }
            }
        }
    }

    private static class EventListenerMethod {
        final Object bean;
        final Method method;

        EventListenerMethod(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }
}
