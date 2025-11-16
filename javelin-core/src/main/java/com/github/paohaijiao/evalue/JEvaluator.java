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
package com.github.paohaijiao.evalue;

import com.github.paohaijiao.enums.JMethodEnums;
import com.github.paohaijiao.function.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JEvaluator {

    private static final Map<String, Object> FUNCTION_REGISTRY = new HashMap<>();

    static {
        registerEnumMethods();
    }

    private static void registerEnumMethods() {
        registerFunction(JMethodEnums.toInteger.getMethod(), (Function<Object, Object>) obj -> Integer.valueOf(obj.toString()));
        registerFunction(JMethodEnums.toString.getMethod(), (Function<Object, Object>) obj -> String.valueOf(obj.toString()));
        registerFunction(JMethodEnums.toDouble.getMethod(), (Function<Object, Object>) obj -> Double.valueOf(obj.toString()));
        registerFunction(JMethodEnums.toFloat.getMethod(), (Function<Object, Object>) obj -> Float.valueOf(obj.toString()));
        registerFunction(JMethodEnums.toLower.getMethod(), (Function<Object, Object>) obj -> obj.toString().toLowerCase());
        registerFunction(JMethodEnums.toUpper.getMethod(), (Function<Object, Object>) obj -> obj.toString().toUpperCase());
        registerFunction(JMethodEnums.contains.getMethod(), JStringFunction.CONTAINS);
        registerFunction(JMethodEnums.join.getMethod(), JStringFunction.JOIN);
        registerFunction(JMethodEnums.split.getMethod(), JStringFunction.SPLIT);
        registerFunction(JMethodEnums.substring.getMethod(), JStringFunction.SUBSTRING);
        registerFunction(JMethodEnums.replace.getMethod(), JStringFunction.REPLACE);
        registerFunction(JMethodEnums.startsWith.getMethod(),
                (BiFunction<Object, Object, Object>) (str, prefix) -> str.toString().startsWith(prefix.toString()));
        registerFunction(JMethodEnums.endsWith.getMethod(),
                (BiFunction<Object, Object, Object>) (str, suffix) -> str.toString().endsWith(suffix.toString()));
        registerFunction(JMethodEnums.ceil.getMethod(), (Function<Object, Object>) obj -> Math.ceil(((Number) obj).doubleValue()));
        registerFunction(JMethodEnums.floor.getMethod(), (Function<Object, Object>) obj -> Math.floor(((Number) obj).doubleValue()));
        registerFunction(JMethodEnums.round.getMethod(),
                (BiFunction<Object, Object, Object>) (num, precision) -> {
                    double value = ((Number) num).doubleValue();
                    int scale = ((Number) precision).intValue();
                    return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
                });
        registerFunction(JMethodEnums.length.getMethod(), JListArrayFunction.LENGTH);
        registerFunction(JMethodEnums.sum.getMethod(), JNumberFunction.SUM);
        registerFunction(JMethodEnums.max.getMethod(), JNumberFunction.MAX);
        registerFunction(JMethodEnums.min.getMethod(), JNumberFunction.MIN);
        registerFunction(JMethodEnums.avg.getMethod(), JNumberFunction.AVG);
        registerFunction(JMethodEnums.dateFormat.getMethod(), JDateFunction.DATEFORMAT);
        registerFunction(JMethodEnums.parseToDate.getMethod(), JDateFunction.parseToDate);
        registerFunction(JMethodEnums.trans.getMethod(), JFunction.TRANS);
    }

    public static void registerFunction(String functionName, Object function) {
        if (functionName == null || functionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        if (function == null) {
            throw new IllegalArgumentException("Function implementation cannot be null");
        }
        FUNCTION_REGISTRY.put(functionName, function);
    }

    public static Object evaluateFunction(String functionName, List<Object> args) {
        Object function = FUNCTION_REGISTRY.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }
        try {
            if (function instanceof Function) {
                if (args.size() != 1) {
                    return ((Function<List<Object>, Object>) function).apply(args);
                }
                return ((Function<Object, Object>) function).apply(args.get(0));
            }
            if (function instanceof BiFunction) {
                if (args.size() != 2) {
                    throw new IllegalArgumentException("BiFunction requires exactly 2 arguments");
                }
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            if (isListAcceptingFunction(function)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }
            throw new UnsupportedOperationException("Unsupported function type for: " + functionName);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid argument types for function: " + functionName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error executing function: " + functionName, e);
        }
    }

    private static boolean isListAcceptingFunction(Object function) {
        if (!(function instanceof Function)) {
            return false;
        }
        try {
            ((Function<List<Object>, Object>) function).apply(Collections.emptyList());
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }


}
