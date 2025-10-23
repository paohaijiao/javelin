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
package com.github.paohaijiao.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.paohaijiao.map.JMultiValuedMap;
import com.github.paohaijiao.model.JUserModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * packageName com.github.paohaijiao.type
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/3
 */
public class JPrimitiveType {
    @Test
    public void primitive() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();
        System.out.println(converter.convert("123", int.class));
        System.out.println(converter.convert("123", long.class));
        System.out.println(converter.convert("3.14", float.class));
        System.out.println(converter.convert("3.14", double.class));
        System.out.println(converter.convert("true", boolean.class));
        System.out.println(converter.convert("false", boolean.class));
    }
    @Test
    public void wrapper() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();
        System.out.println(converter.convert("123", Integer.class));
        System.out.println(converter.convert("123", Long.class));
        System.out.println(converter.convert("3.14", Float.class));
        System.out.println(converter.convert("3.14", Double.class));
        System.out.println(converter.convert("true", Boolean.class));
        System.out.println(converter.convert("false", Boolean .class));
        System.out.println(converter.convert("A", Character .class));
    }
    @Test
    public void string() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();
        System.out.println(converter.convert(123, String.class));
        System.out.println(converter.convert(3.14, String.class));
        System.out.println(converter.convert(true, String.class));
        System.out.println(converter.convert("hello", String.class));
    }
    @Test
    public void array() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();
        short[] shortArray=converter.convert("[1, 2, 3]", short[].class);
        Short[] ShortArray=converter.convert("[1, 2, 3]", Short[].class);
        int[] intArray=converter.convert("[1, 2, 3]", int[].class);
        Integer[] IntegerArray=converter.convert("[1, 2, 3]", Integer[].class);
        long[] longArray=converter.convert("[1, 2, 3]", long[].class);
        Long[] LongArray=converter.convert("[1, 2, 3]", Long[].class);
        float[] floatArray=converter.convert("[1, 2, 3]", float[].class);
        Float[] FloatArray=converter.convert("[1, 2, 3]", Float[].class);
        double[] doubleArray=converter.convert("[1, 2, 3]", double[].class);
        Double[] DoubleArray=converter.convert("[1, 2, 3]", Double[].class);
        String[] StringArray=converter.convert("[\"a\", \"b\", \"c\"]", String[].class);
        JUserModel[] JUserModelArray=converter.convert( "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Alice\", \"age\":25}]",
                JUserModel[].class);
        System.out.println(JUserModelArray);
    }
    @Test
    public void collection() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();
        String jsonArray = "[\"a\", \"b\", \"c\"]";
        List<String> stringList = converter.convert(
                "[\"a\", \"b\", \"c\"]",
                new TypeReference<List<String>>() {});
        Set<Integer> integerSet = converter.convert(
                "[1, 2, 3, 3]",
                new TypeReference<Set<Integer>>() {});

        List<List<Integer>> nestedList = converter.convert(
                "[[1, 2], [3, 4]]",
                new TypeReference<List<List<Integer>>>() {});
        System.out.println(stringList);
    }
    @Test
    public void map() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();
        Map<String, Integer> simpleMap = converter.convert(
                "{\"one\": 1, \"two\": 2}",
                new TypeReference<Map<String, Integer>>() {});
        Map<String, Map<String, Integer>> nestedMap = converter.convert(
                "{\"outer\": {\"inner\": 42}}",
                new TypeReference<Map<String, Map<String, Integer>>>() {});
        Map<String, List<String>> mapWithList = converter.convert(
                "{\"fruits\": [\"apple\", \"banana\"], \"colors\": [\"red\", \"blue\"]}",
                new TypeReference<Map<String, List<String>>>() {});
        System.out.println(mapWithList);
    }
    @Test
    public void custom() throws IOException {
        JGenericlTypeConverter converter = new JGenericlTypeConverter();

        JUserModel user = converter.convert(
                "{\"name\":\"John\", \"age\":30}",
                JUserModel.class);
        List<JUserModel> users = converter.convert(
                "[{\"name\":\"John\", \"age\":30}, {\"name\":\"Alice\", \"age\":25}]",
                new TypeReference<List<JUserModel>>() {});

        Map<String, JUserModel> userMap = converter.convert(
                "{\"user1\": {\"name\":\"John\", \"age\":30}, \"user2\": {\"name\":\"Alice\", \"age\":25}}",
                new TypeReference<Map<String, JUserModel>>() {});
        System.out.println(userMap);
    }
}
