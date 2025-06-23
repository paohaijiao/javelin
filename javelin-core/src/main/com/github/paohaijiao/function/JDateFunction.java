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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;

/**
 * packageName com.paohaijiao.javelin.function
 *
 * @author Martin
 * @version 1.0.0
 * @className JDateFunction
 * @date 2025/6/20
 * @description
 */
public class JDateFunction {
    public static final BiFunction<Date, String, String> DATEFORMAT = (date, format) -> {
        JAssert.notNull(date, "date  must not be null");
        JAssert.notNull(format, "format  must not be null");
        JAssert.isTrue(date instanceof Date, "parameter 1 must be a date");
        JAssert.isTrue(format instanceof String, "parameter 2 must be a date");
        Date d = (Date) date;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    };
    public static final BiFunction<String, String, Date> parseToDate = (dateStr, format) -> {
        JAssert.notNull(dateStr, "dateStr  must not be null");
        JAssert.notNull(format, "format  must not be null");
        JAssert.isTrue(dateStr instanceof String, "parameter 1 must be a String");
        JAssert.isTrue(format instanceof String, "parameter 2 must be a date");
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            JAssert.throwNewException("parse date failed");
            return null;
        }
    };
}
