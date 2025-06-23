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

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.Function;

/**
 * packageName com.paohaijiao.javelin.function
 *
 * @author Martin
 * @version 1.0.0
 * @className JListArrayFunction
 * @date 2025/6/20
 * @description
 */
public class JListArrayFunction {

    public static final Function<Object, Object> LENGTH = obj -> {
        if (obj instanceof String) {
            return ((String) obj).length();
        } else if (obj instanceof List) {
            return ((List<?>) obj).size();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        throw new IllegalArgumentException("Unsupported type for length(): " + obj.getClass());
    };



}
