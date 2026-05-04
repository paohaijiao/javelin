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
package com.github.paohaijiao.spi;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JLogLevel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.spi
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/4
 */
public class JQuickServiceLoader extends ServiceLoader {

    public static <T> List<T> loadServicesConditional(Class<T> serviceClass, Predicate<T> condition) {
        return loadServices(serviceClass).stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    public static <T> List<T> loadAvailableServices(Class<T> serviceClass) {
        return loadServices(serviceClass).stream()
                .filter(service -> isServiceAvailable(service))
                .collect(Collectors.toList());
    }

    private static boolean isServiceAvailable(Object service) {
        try {
            service.getClass();
            return true;
        } catch (Exception e) {
            JConsole console=JConsole.initConsoleEnvironment();
            console.log(JLogLevel.WARN, "Service " + service.getClass().getName() + " is not available: " + e.getMessage());
            return false;
        }
    }
}
