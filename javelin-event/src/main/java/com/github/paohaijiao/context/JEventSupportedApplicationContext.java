/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.context;

import com.github.paohaijiao.anno.JEventListener;
import com.github.paohaijiao.event.JApplicationEvent;
import com.github.paohaijiao.publisher.JEventPublisher;

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
