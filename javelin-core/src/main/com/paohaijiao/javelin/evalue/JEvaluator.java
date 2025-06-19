package com.paohaijiao.javelin.evalue;

import com.paohaijiao.javelin.enums.JMethodEnums;
import com.paohaijiao.javelin.function.JFunction;

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
        // Single Parameter
        FUNCTION_REGISTRY.put(JMethodEnums.length.getMethod(), JFunction.LENGTH);
        FUNCTION_REGISTRY.put(JMethodEnums.toInteger.getMethod(), (Function<Object, Object>) obj -> Integer.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toString.getMethod(), (Function<Object, Object>) obj -> String.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toDouble.getMethod(), (Function<Object, Object>) obj -> Double.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toFloat.getMethod(), (Function<Object, Object>) obj -> Float.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.toLower.getMethod(), (Function<Object, Object>) obj -> obj.toString().toLowerCase());
        FUNCTION_REGISTRY.put(JMethodEnums.toUpper.getMethod(), (Function<Object, Object>) obj -> obj.toString().toUpperCase());
        FUNCTION_REGISTRY.put(JMethodEnums.ceil.getMethod(), (Function<Object, Object>) obj -> Math.ceil(((Number) obj).doubleValue()));
        FUNCTION_REGISTRY.put(JMethodEnums.floor.getMethod(), (Function<Object, Object>) obj -> Math.floor(((Number) obj).doubleValue()));
        // Double Parameter
        FUNCTION_REGISTRY.put(JMethodEnums.contains.getMethod(), JFunction.CONTAINS);
        FUNCTION_REGISTRY.put(JMethodEnums.join.getMethod(), JFunction.JOIN);
        FUNCTION_REGISTRY.put(JMethodEnums.split.getMethod(), JFunction.SPLIT);
        FUNCTION_REGISTRY.put(JMethodEnums.trans.getMethod(), JFunction.TRANS);
        FUNCTION_REGISTRY.put(JMethodEnums.startsWith.getMethod(), (BiFunction<Object, Object, Object>)
                (str, prefix) -> str.toString().startsWith(prefix.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.dateFormat.getMethod(), JFunction.DATEFORMAT);
        FUNCTION_REGISTRY.put(JMethodEnums.parseToDate.getMethod(), JFunction.DATEFORMAT);

        FUNCTION_REGISTRY.put(JMethodEnums.endsWith.getMethod(), (BiFunction<Object, Object, Object>)
                (str, prefix) -> str.toString().endsWith(prefix.toString()));
        FUNCTION_REGISTRY.put(JMethodEnums.round.getMethod(), (BiFunction<Object, Object, Object>)
                (num, precision) -> {
                    double value = ((Number) num).doubleValue();
                    int scale = ((Number) precision).intValue();
                    return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
                });
        // list Parameter Functions
        FUNCTION_REGISTRY.put(JMethodEnums.sum.getMethod(), JFunction.SUM);
        FUNCTION_REGISTRY.put(JMethodEnums.max.getMethod(), JFunction.MAX);
        FUNCTION_REGISTRY.put(JMethodEnums.min.getMethod(), JFunction.MIN);
        FUNCTION_REGISTRY.put(JMethodEnums.avg.getMethod(), JFunction.AVG);
        FUNCTION_REGISTRY.put(JMethodEnums.substring.getMethod(), JFunction.SUBSTRING);
        FUNCTION_REGISTRY.put(JMethodEnums.replace.getMethod(), JFunction.REPLACE);


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
