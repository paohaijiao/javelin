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
package com.github.paohaijiao.function;

import java.util.List;
import java.util.function.Function;

/**
 * packageName com.paohaijiao.javelin.function
 *
 * @author Martin
 * @version 1.0.0
 * @className JNumberFunction
 * @date 2025/6/20
 * @description
 */
public class JNumberFunction {
    public static final Function<List<Object>, Object> SUM = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .sum();
    };
    public static final Function<List<Object>, Object> MAX = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .max().getAsDouble();
    };
    public static final Function<List<Object>, Object> MIN = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .min().getAsDouble();
    };
    public static final Function<List<Object>, Object> AVG = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .average().getAsDouble();
    };
}
