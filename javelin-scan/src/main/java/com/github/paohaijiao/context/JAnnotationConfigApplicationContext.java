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

import com.github.paohaijiao.context.service.JProxyEnhancedBeanProvider;
import com.github.paohaijiao.factory.JBeanProviderFactory;
import com.github.paohaijiao.model.JBeanDefinitionModel;
import com.github.paohaijiao.scan.JClassPathScanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class JAnnotationConfigApplicationContext {
    protected final JBeanProvider beanProvider;
    protected final Set<String> beanNames = new HashSet<>();

    public JAnnotationConfigApplicationContext(String... basePackages) {
        this(new Properties(), basePackages);
    }

    public JAnnotationConfigApplicationContext(Properties config, String... basePackages) {
        this.beanProvider = JBeanProviderFactory.createProvider(config);
        scanAndRegisterBeans(basePackages);
        processConfigurationClasses();
    }

    private void scanAndRegisterBeans(String[] basePackages) {
        try {
            for (String basePackage : basePackages) {
                List<JBeanDefinitionModel> beanDefinitions = JClassPathScanner.scan(basePackage);
                for (JBeanDefinitionModel bd : beanDefinitions) {
                    String beanName = toLowerFirstCase(bd.getBeanClass().getSimpleName());
                    beanProvider.registerBeanDefinition(beanName, bd);
                    beanNames.add(beanName);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to scan base packages", e);
        }
    }

    protected void processConfigurationClasses() {
        // 这里可以扩展处理@Configuration类
        // 目前只处理@Component、@Service、@Repository注解的类
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return beanProvider.getBean(beanName, requiredType);
    }

    public void addBeanPostProcessor(JBeanPostProcessor processor) {
        beanProvider.addBeanPostProcessor(processor);
    }

    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        if (beanProvider instanceof JProxyEnhancedBeanProvider) {
            ((JProxyEnhancedBeanProvider) beanProvider).registerInterceptor(beanName, interceptor);
        }
        // 可以扩展支持CglibBeanContainer
    }

    private String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    protected Set<String> getBeanNames() {
        return new HashSet<>(beanNames);
    }
}
