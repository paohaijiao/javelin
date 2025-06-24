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

import com.github.paohaijiao.enums.JOperatorEnums;

import java.util.Date;
import java.util.Objects;


public class JDateCompareUtil {

    /**
     * 比较两个Date对象
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @param operator 比较操作符
     * @return 比较结果
     * @throws IllegalArgumentException 如果任一日期参数为null
     */
    public static boolean compare(Date date1, Date date2, JOperatorEnums operator) {
        Objects.requireNonNull(date1, "第一个日期参数不能为null");
        Objects.requireNonNull(date2, "第二个日期参数不能为null");
        Objects.requireNonNull(operator, "比较操作符不能为null");
        int comparison = date1.compareTo(date2);
        switch (operator) {
            case GREATER_THAN:
                return comparison > 0;
            case GREATER_THAN_OR_EQUAL:
                return comparison >= 0;
            case LESS_THAN:
                return comparison < 0;
            case LESS_THAN_OR_EQUAL:
                return comparison <= 0;
            case EQUAL:
                return comparison == 0;
            case NOT_EQUAL:
                return comparison != 0;
            default:
                throw new IllegalArgumentException("不支持的比较操作符: " + operator);
        }
    }
    /**
     * 重载方法，使用字符串指定比较操作符
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @param operator 比较操作符字符串(>, >=, <, <=, ==, !=)
     * @return 比较结果
     * @throws IllegalArgumentException 如果操作符无效
     */
    public static boolean compare(Date date1, Date date2, String operator) {
        JOperatorEnums op;
        switch (operator) {
            case ">":  op = JOperatorEnums.GREATER_THAN; break;
            case ">=": op = JOperatorEnums.GREATER_THAN_OR_EQUAL; break;
            case "<":  op = JOperatorEnums.LESS_THAN; break;
            case "<=": op = JOperatorEnums.LESS_THAN_OR_EQUAL; break;
            case "==": op = JOperatorEnums.EQUAL; break;
            case "!=": op = JOperatorEnums.NOT_EQUAL; break;
            default:
                throw new IllegalArgumentException("无效的比较操作符: " + operator +
                        "，支持的比较符有: >, >=, <, <=, ==, !=");
        }
        return compare(date1, date2, op);
    }
}
