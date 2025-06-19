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
package com.paohaijiao.javelin.enums;

import com.paohaijiao.javelin.exception.JAssert;
import lombok.Getter;

@Getter
public enum JMethodEnums {
    length("length"),
    toInteger("toInteger"),
    toString("toString"),
    toDouble("toDouble"),
    toFloat("toFloat"),
    toLower("toLower"),
    toUpper("toUpper"),
    ceil("ceil"),
    floor("floor"),
    contains("contains"),
    join("join"),
    split("split"),
    trans("trans"),
    dateFormat("dateFormat"),
    parseToDate("parseToDate"),
    startsWith("startsWith"),
    endsWith("endsWith"),
    round("round"),
    sum("sum"),
    max("max"),
    min("min"),
    avg("avg"),
    substring("substring"),
    replace("replace");
    private String method;

    JMethodEnums(String method) {
        this.method = method;
    }

    /**
     * 根据传入的方法获取枚举类
     * @param method
     * @return
     */
    public static JMethodEnums methodOf(String method) {
        for (JMethodEnums jMethodEnums : JMethodEnums.values()) {
            if (jMethodEnums.method.equals(method)) {
                return jMethodEnums;
            }
        }
        JAssert.throwNewException("invalid method enum");
        return null;
    }

}
