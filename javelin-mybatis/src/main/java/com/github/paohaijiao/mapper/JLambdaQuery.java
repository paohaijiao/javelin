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
package com.github.paohaijiao.mapper;

import com.github.paohaijiao.function.JSFunction;

import java.util.Collection;
import java.util.List;

public interface JLambdaQuery<T>{
    JLambdaQuery<T> eq(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> ne(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> gt(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> ge(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> lt(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> le(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> like(JSFunction<T, ?> column, String value);
    JLambdaQuery<T> in(JSFunction<T, ?> column, Collection<?> values);
    JLambdaQuery<T> orderByAsc(JSFunction<T, ?> column);
    JLambdaQuery<T> orderByDesc(JSFunction<T, ?> column);

    List<T> list();
    T one();
    long count();
}
