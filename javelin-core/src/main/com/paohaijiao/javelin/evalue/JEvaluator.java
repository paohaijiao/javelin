package com.paohaijiao.javelin.evalue;

import com.paohaijiao.javelin.function.JFunction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JEvaluator {

    private static final Map<String, Object> FUNCTION_REGISTRY = new HashMap<>();

    static {
        // Single Parameter
        FUNCTION_REGISTRY.put("length", JFunction.LENGTH);
        FUNCTION_REGISTRY.put("toInteger", (Function<Object, Object>) obj -> Integer.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put("toString", (Function<Object, Object>) obj -> String.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put("toDouble", (Function<Object, Object>) obj -> Double.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put("toFloat", (Function<Object, Object>) obj -> Float.valueOf(obj.toString()));
        FUNCTION_REGISTRY.put("toLower", (Function<Object, Object>) obj -> obj.toString().toLowerCase());
        FUNCTION_REGISTRY.put("toUpper", (Function<Object, Object>) obj -> obj.toString().toUpperCase());
        FUNCTION_REGISTRY.put("ceil", (Function<Object, Object>) obj -> Math.ceil(((Number) obj).doubleValue()));
        FUNCTION_REGISTRY.put("floor", (Function<Object, Object>) obj -> Math.floor(((Number) obj).doubleValue()));

        // Double Parameter
        FUNCTION_REGISTRY.put("contains", JFunction.CONTAINS);
        FUNCTION_REGISTRY.put("join", JFunction.JOIN);
        FUNCTION_REGISTRY.put("split", JFunction.SPLIT);

        FUNCTION_REGISTRY.put("startsWith", (BiFunction<Object, Object, Object>)
                (str, prefix) -> str.toString().startsWith(prefix.toString()));
        FUNCTION_REGISTRY.put("endsWith", (BiFunction<Object, Object, Object>)
                (str, prefix) -> str.toString().endsWith(prefix.toString()));
        FUNCTION_REGISTRY.put("round", (BiFunction<Object, Object, Object>)
                (num, precision) -> {
                    double value = ((Number) num).doubleValue();
                    int scale = ((Number) precision).intValue();
                    return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
                });
        // list Parameter Functions
        FUNCTION_REGISTRY.put("sum", JFunction.SUM);
        FUNCTION_REGISTRY.put("max", JFunction.MAX);
        FUNCTION_REGISTRY.put("min", JFunction.MIN);
        FUNCTION_REGISTRY.put("avg", JFunction.AVG);
        FUNCTION_REGISTRY.put("substring", JFunction.SUBSTRING);
        FUNCTION_REGISTRY.put("replace", JFunction.REPLACE);


    }
    public static Object evaluateFunction(String functionName, List<Object> args) {
        Object function = FUNCTION_REGISTRY.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }
        if (function instanceof Function) {
            if ("sum".equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }  else if ("max".equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }  else if ("min".equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            } else if ("avg".equals(functionName)) {
                return ((Function<List<Object>, Object>) function).apply(args);
            }else if("substring".equals(functionName)){
                return ((Function<List<Object>, Object>) function).apply(args);
            }else if("replace".equals(functionName)){
                return ((Function<List<Object>, Object>) function).apply(args);
            }
            else {
                return ((Function<Object, Object>) function).apply(args.get(0));
            }
        } else if (function instanceof BiFunction) {
            if("contains".equals(functionName)){
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            } else if ("join".equals(functionName)) {
               return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if ("split".equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if ("round".equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if ("startsWith".equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }
            else if ("endsWith".equals(functionName)) {
                return ((BiFunction<Object, Object, Object>) function).apply(args.get(0), args.get(1));
            }

        }
        throw new UnsupportedOperationException("Unsupported function type");
    }
}
