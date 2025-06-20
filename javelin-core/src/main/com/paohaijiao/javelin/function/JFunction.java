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
package com.paohaijiao.javelin.function;

import com.paohaijiao.javelin.exception.JAssert;
import com.paohaijiao.javelin.param.JContext;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JFunction {

    public static final BiFunction<Object, Object, Object> TRANS = (context, key) -> {
        JAssert.notNull(key, "parameter 2 require not  null");
        JAssert.isTrue(context instanceof JContext, "parameter 1 must be initialized in the Context's map");
        Object object = ((JContext) context).get(key.toString());
        return object;
    };






}
