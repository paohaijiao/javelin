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

public enum JOperatorEnums {
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    EQUAL("=="),
    NOT_EQUAL("!=");

    private final String symbol;

    JOperatorEnums(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
    public static JOperatorEnums symbolOf(String symbol) {
        for (JOperatorEnums jOperatorEnums : JOperatorEnums.values()) {
            if (jOperatorEnums.symbol.equals(symbol)) {
                return jOperatorEnums;
            }
        }
        JAssert.throwNewException("No JOperatorEnums found for symbol " + symbol);
        return null;
    }

}
