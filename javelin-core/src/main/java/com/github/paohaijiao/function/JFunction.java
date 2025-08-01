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
import com.github.paohaijiao.param.JContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class JFunction {

    public static final BiFunction<Object, Object, Object> TRANS = (context, key) -> {
        JAssert.notNull(key, "parameter 2 require not  null");
        //JAssert.isTrue(context instanceof HashMap, "parameter 1 must be initialized in the Context's map");
        Object object = ((Map) context).get(key.toString());
        return object;
    };





}
