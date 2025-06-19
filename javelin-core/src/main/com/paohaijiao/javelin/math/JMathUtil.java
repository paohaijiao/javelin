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
package com.paohaijiao.javelin.math;

import com.paohaijiao.javelin.exception.JAssert;

import java.math.BigDecimal;

public class JMathUtil {

    public static BigDecimal op(Object obj1, Object obj2,String op) {
        JAssert.notNull(obj1,"obj1 is null");
        JAssert.notNull(obj2,"obj2 is null");
        BigDecimal b1 = new BigDecimal(obj1.toString());
        BigDecimal b2 = new BigDecimal(obj2.toString());
        if(op.equals("+")) {
            return b1.add(b2);
        } else if (op.equals("-")) {
            return b1.subtract(b2);
        } else if(op.equals("*")) {
            return b1.multiply(b2);
        }else if(op.equals("/")) {
            return b1.divide(b2);
        }
        JAssert.throwNewException("invalid operator");
        return null;
    }
}
