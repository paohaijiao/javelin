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
package com.github.paohaijiao.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JDateUtil {
    public static SimpleDateFormat getSimpleDateFormat(String format, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        return sdf;
    }

    public static SimpleDateFormat getSimpleDateFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf;
    }

    public static String format(SimpleDateFormat sdf, Date date) {
        String dateString = sdf.format(date);
        return dateString;
    }

    public static Date parse(SimpleDateFormat sdf, String dateStr) {
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static Date toDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof java.sql.Date) {
            return new Date(((java.sql.Date) obj).getTime());
        }
        if (obj instanceof java.util.Calendar) {
            return ((java.util.Calendar) obj).getTime();
        }

        if (obj instanceof Number) {
            long timestamp = ((Number) obj).longValue();// 判断是秒（10位）还是毫秒（13位）
            String tsStr = String.valueOf(timestamp);
            if (tsStr.length() == 10) { // 秒级时间戳
                return new Date(timestamp * 1000);
            } else if (tsStr.length() == 13) {
                return new Date(timestamp);
            } else {
                return new Date(timestamp);
            }
        }
        if (obj instanceof String) {
            String dateStr = (String) obj;
            if (dateStr.trim().isEmpty()) {
                return null;
            }
            try{
                return parseDateString(dateStr);
            }catch(Exception e){
            }
            try {
                long timestamp = Long.parseLong(dateStr.trim());
                String tsStr = String.valueOf(timestamp);
                if (tsStr.length() == 10) {
                    return new Date(timestamp * 1000);
                } else {
                    return new Date(timestamp);
                }
            } catch (NumberFormatException e) {
                // 不是纯数字，继续尝试日期格式
            }
        }
        return null;
    }
    /**
     * 解析字符串日期，尝试多种常见格式
     */
    private static Date parseDateString(String dateStr) {
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd",
                "yyyy年MM月dd日 HH:mm:ss",
                "yyyy年MM月dd日",
                "MM/dd/yyyy HH:mm:ss",
                "MM/dd/yyyy",
                "dd/MM/yyyy HH:mm:ss",
                "dd/MM/yyyy",
                "yyyyMMddHHmmss",
                "yyyyMMdd"
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
                sdf.setLenient(false);
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    return date;
                }
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
