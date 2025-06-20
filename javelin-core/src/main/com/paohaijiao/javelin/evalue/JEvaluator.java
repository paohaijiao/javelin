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
package com.paohaijiao.javelin.evalue;

import com.paohaijiao.javelin.enums.JMethodEnums;
import com.paohaijiao.javelin.function.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JEvaluator {

    private static final Map<String, Object> FUNCTION_REGISTRY = new HashMap<>();

    static {
        FUNCTION_REGISTRY.put(JMethodEnums.length.getMethod(), JListArrayFunction.LENGTH);
        FUNCTION_REGISTRY.put(JMethodEnums.toInteger.getMethod(), (Function<Object, Object>) obj -> Integer.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toString.getMethod(), (Function<Object, Object>) obj -> String.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toDouble.getMethod(), (Function<Object, Object>) obj -> Double.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toFloat.getMethod(), (Function<Object, Object>) obj -> Float.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toLower.getMethod(), (Function<Object, Object>) obj -> obj.toString().toLowerCase());
        FUNCTION_REGISTRY.put(JMethodEnums.toUpper.getMethod(), (Function<Object, Object>) obj -> obj.toString().toUpperCase());
        FUNCTION_REGISTRY.put(JMethodEnums.ceil.getMethod(), (Function<Object, Object>) obj -> Math.ceil(((Number) obj).doubleValue()));
        FUNCTION_REGISTRY.put(JMethodEnums.floor.getMethod(), (Function<Object, Object>) obj -> Math.floor(((Number) obj).doubleValue()));
        FUNCTION_REGISTRY.put(JMethodEnums.contains.getMethod(), JStringFunction.CONTAINS);
        FUNCTION_REGISTRY.put(JMethodEnums.join.getMethod(), JStringFunction.JOIN);
        FUNCTION_REGISTRY.put(JMethodEnums.split.getMethod(), JStringFunction.SPLIT);
        FUNCTION_REGISTRY.put(JMethodEnums.trans.getMethod(), JFunction.TRANS);
        FUNCTION_REGISTRY.put(JMethodEnums.startsWith.getMethod(), (BiFunction<Object, Object, Object>)
                (str, prefix) -> str.toString().startsWith(prefix.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.dateFormat.getMethod(), JDateFunction.DATEFORMAT);
        FUNCTION_REGISTRY.put(JMethodEnums.parseToDate.getMethod(), JDateFunction.parseToDate);

        FUNCTION_REGISTRY.put(JMethodEnums.endsWith.getMethod(), (BiFunction<Object, Object, Object>)
                (str, prefix) -> str.toString().endsWith(prefix.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.round.getMethod(), (BiFunction<Object, Object, Object>)
                (num, precision) -> {
                    double value = ((Number) num).doubleValue();
                    int scale = ((Number) precision).intValue();
                    return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
                });
        FUNCTION_REGISTRY.put(JMethodEnums.sum.getMethod(), JNumberFunction.SUM);
        FUNCTION_REGISTRY.put(JMethodEnums.max.getMethod(), JNumberFunction.MAX);
        FUNCTION_REGISTRY.put(JMethodEnums.min.getMethod(), JNumberFunction.MIN);
        FUNCTION_REGISTRY.put(JMethodEnums.avg.getMethod(), JNumberFunction.AVG);
        FUNCTION_REGISTRY.put(JMethodEnums.substring.getMethod(), JStringFunction.SUBSTRING);
        FUNCTION_REGISTRY.put(JMethodEnums.replace.getMethod(), JStringFunction.REPLACE);


    }
    public static Object evaluateFunction(String functionName, List<Object> args) {
        Object function = FUNCTION_REGISTRY.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }
        if (function instanceof Function) {
            if (JMethodEnums.sum.getMethod().equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }  else if (JMethodEnums.max.getMethod().equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }  else if (JMethodEnums.min.getMethod().equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            } else if (JMethodEnums.avg.getMethod().equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }else if(JMethodEnums.substring.getMethod().equals(functionName)){
                return ((Function<List<Object>, Object>) function).apply(args);
            }else if(JMethodEnums.replace.getMethod().equals(functionName)){
                return ((Function<List<Object>, Object>) function).apply(args);
            }
            else {
                return ((Function<Object, Object>) function).apply(args.get(0));
            }
        } else if (function instanceof BiFunction) {
            if(JMethodEnums.contains.getMethod().equals(functionName)){
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            } else if (JMethodEnums.join.getMethod().equals(functionName)) {
               return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if (JMethodEnums.split.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if (JMethodEnums.round.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if (JMethodEnums.startsWith.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if (JMethodEnums.endsWith.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if (JMethodEnums.trans.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }   else if (JMethodEnums.dateFormat.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }   else if (JMethodEnums.parseToDate.getMethod().equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }

        }
        throw new UnsupportedOperationException("Unsupported function type");
    }
}
