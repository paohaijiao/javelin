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
package com.github.paohaijiao.util;

public class JStringUtils {

    public static String trim(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        String newStr = str.replaceAll("^['\"]|['\"]$", "");
        return newStr;
    }

    public static String replaceJsonFormat(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        String newStr = str.replaceAll(";type=application/json", "");
        return newStr;
    }

    public static String trimJsonFormat(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        String newStr = str.replaceAll("\\\\\"", "\"");
        return newStr;
    }


}
