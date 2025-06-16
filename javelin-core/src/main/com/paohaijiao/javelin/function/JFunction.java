package com.paohaijiao.javelin.function;
import com.paohaijiao.javelin.exception.Assert;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JFunction {
    public static final Function<Object, Object> LENGTH = obj -> {
        if (obj instanceof String) {
            return ((String) obj).length();
        } else if (obj instanceof List) {
            return ((List<?>) obj).size();
        }else if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        throw new IllegalArgumentException("Unsupported type for length(): " + obj.getClass());
    };
    public static final BiFunction<Object, Object, Object> CONTAINS = (obj, substr) -> {
        if (obj instanceof String && substr instanceof String) {
            return ((String) obj).contains((String) substr);
        }
        throw new IllegalArgumentException("Invalid arguments for contains()");
    };
    public static final BiFunction<Object, Object, Object> JOIN = (list, delimiter) -> {
        Assert.isTrue(list instanceof List, "parameter 1 must be a list");
        return ((List<?>) list).stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter.toString()));
    };
    public static final BiFunction<Object, Object, Object> SPLIT = (string, delimiter) -> {
        Assert.isTrue(string instanceof String, "parameter 1 must be a string");
        String[] str=((String) string).split(delimiter.toString());
        List<String> data = Arrays.asList(str);
        return data;
    };


    public static final Function<List<Object>, Object> SUM = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .sum();
    };
    public static final Function<List<Object>, Object> MAX = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .max().getAsDouble();
    };
    public static final Function<List<Object>, Object> MIN = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .min().getAsDouble();
    };
    public static final Function<List<Object>, Object> AVG = args -> {
        return args.stream()
                .flatMap(arg -> {
                    if (arg instanceof List) {
                        return ((List<?>) arg).stream();
                    }
                    return java.util.stream.Stream.of(arg);
                })
                .filter(arg -> arg instanceof Number)
                .mapToDouble(arg -> ((Number) arg).doubleValue())
                .average().getAsDouble();
    };
    public static final Function<List<Object>, Object> SUBSTRING = args -> {
        if (args.size() < 3) {
            throw new IllegalArgumentException("substring requires 3 arguments: (string, start, end)");
        }
        String str = args.get(0).toString();
        int start = ((Number) args.get(1)).intValue();
        int step = ((Number) args.get(2)).intValue();
        return str.substring(start, start+step);
    };
    public static final Function<List<Object>, Object> REPLACE = args -> {
        if (args.size() < 3) {
            throw new IllegalArgumentException("replace requires 3 arguments: (string, start, end)");
        }
        String str = args.get(0).toString();
        String searchString = args.get(1).toString();
        String replaceString = args.get(2).toString();
        return str.replace(searchString, replaceString);
    };






}
