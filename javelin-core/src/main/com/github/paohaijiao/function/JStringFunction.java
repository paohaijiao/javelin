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

import com.github.paohaijiao.exception.JAssert;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * packageName com.paohaijiao.javelin.function
 *
 * @author Martin
 * @version 1.0.0
 * @className JStringFunction
 * @date 2025/6/20
 * @description
 */
public class JStringFunction {
    public static final BiFunction<Object, Object, Object> CONTAINS = (obj, substr) -> {
        if (obj instanceof String && substr instanceof String) {
            return ((String) obj).contains((String) substr);
        }
        throw new IllegalArgumentException("Invalid arguments for contains()");
    };
    public static final BiFunction<Object, Object, Object> JOIN = (list, delimiter) -> {
        JAssert.isTrue(list instanceof List, "parameter 1 must be a list");
        return ((List<?>) list).stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter.toString()));
    };

    public static final Function<List<Object>, Object> SUBSTRING = args -> {
        if (args.size() < 3) {
            throw new IllegalArgumentException("substring requires 3 arguments: (string, start, end)");
        }
        String str = args.get(0).toString();
        int start = ((Number) args.get(1)).intValue();
        int step = ((Number) args.get(2)).intValue();
        return str.substring(start, start + step);
    };
    public static final Function<List<Object>, Object> REPLACE = args -> {
        if (args.size() < 3) {
            throw new IllegalArgumentException("replace requires 3 arguments: (string, start, end)");
        }
        String str = args.get(0).toString();
        String searchString = args.get(1).toString();
        String replaceString = args.get(2).toString();
        return str.replace(searchString, replaceString);
    };
    public static final BiFunction<Object, Object, Object> SPLIT = (string, delimiter) -> {
        JAssert.isTrue(string instanceof String, "parameter 1 must be a string");
        String[] str = ((String) string).split(delimiter.toString());
        List<String> data = Arrays.asList(str);
        return data;
    };
}
