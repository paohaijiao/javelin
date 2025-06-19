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
package com.paohaijiao.javelin.date;
import com.paohaijiao.javelin.console.JConsole;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JDateUtil {
    public static SimpleDateFormat getSimpleDateFormat(String format, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(format,locale);
        return sdf;
    }
    public static SimpleDateFormat getSimpleDateFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);
        return sdf;
    }
    public static String format(SimpleDateFormat sdf, Date date) {
        String dateString= sdf.format(date);
        return dateString;
    }
    public static Date parse(SimpleDateFormat sdf, String dateStr) {
        Date date= null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
