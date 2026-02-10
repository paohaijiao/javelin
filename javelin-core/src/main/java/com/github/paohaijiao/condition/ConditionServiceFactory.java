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
package com.github.paohaijiao.condition;

import com.github.paohaijiao.condition.impl.AsyncConditionService;

/**
 * packageName com.github.paohaijiao.condition
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/2/10
 */
public class ConditionServiceFactory {
    @SuppressWarnings("unchecked")
    public static <T, R> ConditionService<T, R> createService(ServiceType type) {
        switch (type) {
            case SIMPLE:
                return new SimpleConditionService<>();
            case ASYNC:
                return new AsyncConditionService<>();
            default:
                throw new IllegalArgumentException("Unknown service type: " + type);
        }
    }

    public static ConditionService<String, String> createStringService(ServiceType type) {
        return createService(type);
    }

    public static ConditionService<Integer, Boolean> createIntegerService(ServiceType type) {
        return createService(type);
    }

    public enum ServiceType {
        SIMPLE,
        ASYNC
    }
}
