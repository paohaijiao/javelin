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
package result;
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
import cn.hutool.json.JSONObject;
import com.github.paohaijiao.result.JResult;
import com.github.paohaijiao.result.JResultConverter;
import com.github.paohaijiao.result.factory.JResultFactory;
import com.github.paohaijiao.type.JTypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JResultFactory 单元测试
 *
 * @author Martin
 * @version 1.0.0
 * @date 2026/3/22
 */
class JResultFactoryTest {

    private static final String TEST_JSON = "{\"name\":\"test\",\"value\":123}";
    private static final String TEST_STRING = "Hello World";
    private static final byte[] TEST_BYTES = TEST_STRING.getBytes();
    private static final String TEST_NUMBER = "12345";
    private static final String TEST_BOOLEAN_TRUE = "true";
    private static final String TEST_BOOLEAN_FALSE = "false";

    private JResult jsonResult;
    private JResult stringResult;
    private JResult bytesResult;
    private JResult numberResult;
    private JResult booleanResult;

    @BeforeEach
    void setUp() {
        jsonResult = JResult.builder().json(TEST_JSON).build();
        stringResult = JResult.builder().text(TEST_STRING).build();
        bytesResult = JResult.builder().bytes(TEST_BYTES).build();
        numberResult = JResult.builder().text(TEST_NUMBER).build();
        booleanResult = JResult.builder().text(TEST_BOOLEAN_TRUE).build();
    }
    @Test
    void testCreateResult_String() throws IOException {
        String result = JResultFactory.createResult(stringResult, String.class);
        assertEquals(TEST_STRING, result);
    }

    @Test
    void testCreateResult_JResult() throws IOException {
        JResult result = JResultFactory.createResult(jsonResult, JResult.class);
        assertSame(jsonResult, result);
    }

    @Test
    void testCreateResult_Map() throws IOException {
        Map result = JResultFactory.createResult(jsonResult, Map.class);
        assertNotNull(result);
        assertEquals("test", result.get("name"));
        assertEquals(123.0, result.get("value"));
    }

    @Test
    void testCreateResult_JSONObject() throws IOException {
        JSONObject result = JResultFactory.createResult(jsonResult, JSONObject.class);
        assertNotNull(result);
        assertEquals("test", result.getStr("name"));
        assertEquals(123, result.getInt("value"));
    }

    @Test
    void testCreateResult_Object() throws IOException {
        Object result = JResultFactory.createResult(jsonResult, Object.class);
        assertNotNull(result);
        assertTrue(result instanceof Map);
    }

    @Test
    void testCreateResult_PrimitiveInt() throws IOException {
        Integer result = JResultFactory.createResult(numberResult, Integer.class);
        assertEquals(12345, result);
        int primitiveResult = JResultFactory.createResult(numberResult, int.class);
        assertEquals(12345, primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveLong() throws IOException {
        Long result = JResultFactory.createResult(numberResult, Long.class);
        assertEquals(12345L, result);
        long primitiveResult = JResultFactory.createResult(numberResult, long.class);
        assertEquals(12345L, primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveDouble() throws IOException {
        Double result = JResultFactory.createResult(numberResult, Double.class);
        assertEquals(12345.0, result);
        double primitiveResult = JResultFactory.createResult(numberResult, double.class);
        assertEquals(12345.0, primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveFloat() throws IOException {
        Float result = JResultFactory.createResult(numberResult, Float.class);
        assertEquals(12345.0f, result);
        float primitiveResult = JResultFactory.createResult(numberResult, float.class);
        assertEquals(12345.0f, primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveBoolean() throws IOException {
        Boolean result = JResultFactory.createResult(booleanResult, Boolean.class);
        assertTrue(result);
        boolean primitiveResult = JResultFactory.createResult(booleanResult, boolean.class);
        assertTrue(primitiveResult);
        JResult falseResult = JResult.builder().text(TEST_BOOLEAN_FALSE).build();
        Boolean falseBoolean = JResultFactory.createResult(falseResult, Boolean.class);
        assertFalse(falseBoolean);
    }

    @Test
    void testCreateResult_PrimitiveByte() throws IOException {
        JResult byteResult = JResult.builder().text("127").build();
        Byte result = JResultFactory.createResult(byteResult, Byte.class);
        byte primitiveResult = JResultFactory.createResult(byteResult, byte.class);
        assertEquals(127, primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveShort() throws IOException {
        JResult shortResult = JResult.builder().text("32767").build();
        Short result = JResultFactory.createResult(shortResult, Short.class);
        short primitiveResult = JResultFactory.createResult(shortResult, short.class);
        assertEquals(32767, primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveCharacter() throws IOException {
        JResult charResult = JResult.builder().text("A").build();
        Character result = JResultFactory.createResult(charResult, Character.class);
        assertEquals('A', result);
        char primitiveResult = JResultFactory.createResult(charResult, char.class);
        assertEquals('A', primitiveResult);
    }

    @Test
    void testCreateResult_PrimitiveCharacterEmptyString() throws IOException {
        JResult emptyResult = JResult.builder().text("").build();
        Character result = JResultFactory.createResult(emptyResult, Character.class);
        assertNull(result);
    }

    @Test
    void testCreateResult_CustomPojo() throws IOException {
        JResult pojoResult = JResult.builder().json("{\"name\":\"test\",\"age\":25}").build();
        TestPojo result = JResultFactory.createResult(pojoResult, TestPojo.class);
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(25, result.getAge());
    }

    @Test
    void testCreateResult_List() throws IOException {
        JResult listResult = JResult.builder().json("[\"a\",\"b\",\"c\"]").build();
        List result = JResultFactory.createResult(listResult, List.class);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testCreateResult_NullResult() throws IOException {
        String result = JResultFactory.createResult(null, String.class);
        assertNull(result);
    }

    @Test
    void testCreateResult_NullResultWithPrimitive() throws IOException {
        // 当结果为 null 且目标类型是基本类型时，返回 null 会导致拆箱异常
        // 这里测试实际行为
        assertThrows(NullPointerException.class, () -> {
            JResultFactory.createResult(null, int.class);
        });
    }

    @Test
    void testCreateResult_WithCustomConverter() throws IOException {
        // 注册自定义转换器
        JResultConverter<TestPojo> customConverter = response -> {
            TestPojo pojo = new TestPojo();
            pojo.setName("custom");
            pojo.setAge(100);
            return pojo;
        };

        JResultFactory.registerConverter(TestPojo.class, customConverter);

        try {
            TestPojo result = JResultFactory.createResult(jsonResult, TestPojo.class);
            assertNotNull(result);
            assertEquals("custom", result.getName());
            assertEquals(100, result.getAge());
        } finally {
            // 清理：移除自定义转换器（通过重新注册默认行为或反射）
            // 注意：由于是静态 Map，测试结束后需要恢复
            JResultFactory.registerConverter(TestPojo.class, null);
        }
    }


    @Test
    void testConvertResponse_WithClassType() throws IOException {
        String result = JResultFactory.createResult(stringResult, new JTypeReference<String>() {});
        assertEquals(TEST_STRING, result);
    }

    @Test
    void testConvertResponse_WithPrimitiveType() throws IOException {
        Integer result = JResultFactory.createResult(numberResult, new JTypeReference<Integer>() {});
        assertEquals(12345, result);
    }

    @Test
    void testConvertResponse_WithParameterizedType_List() throws IOException {
        JResult listResult = JResult.builder().json("[\"a\",\"b\",\"c\"]").build();
        List<String> result = JResultFactory.createResult(listResult, new JTypeReference<List<String>>() {});
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
    }

    @Test
    void testConvertResponse_WithParameterizedType_Map() throws IOException {
        Map<String, Object> result = JResultFactory.createResult(jsonResult, new JTypeReference<Map<String, Object>>() {});
        assertNotNull(result);
        assertEquals("test", result.get("name"));
        assertEquals(123, result.get("value"));
    }

    @Test
    void testConvertResponse_WithComplexPojo() throws IOException {
        JResult pojoResult = JResult.builder().json("{\"name\":\"test\",\"age\":25,\"tags\":[\"java\",\"test\"]}").build();
        ComplexPojo result = JResultFactory.createResult(pojoResult, new JTypeReference<ComplexPojo>() {});
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(25, result.getAge());
        assertEquals(2, result.getTags().size());
        assertEquals("java", result.getTags().get(0));
        assertEquals("test", result.getTags().get(1));
    }

    @Test
    void testConvertResponse_WithNestedGeneric() throws IOException {
        JResult nestedResult = JResult.builder().json("{\"data\":{\"name\":\"test\"},\"code\":200}").build();
        ResultWrapper<TestPojo> result = JResultFactory.createResult(nestedResult,
                new JTypeReference<ResultWrapper<TestPojo>>() {});
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("test", result.getData().getName());
    }

    @Test
    void testConvertResponse_WithByteArrayType() throws IOException {
        byte[] result = JResultFactory.createResult(bytesResult, new JTypeReference<byte[]>() {});
        assertArrayEquals(TEST_BYTES, result);
    }

    @Test
    void testConvertResponse_NullResponse() {
        assertThrows(IllegalArgumentException.class, () -> {
            JResultFactory.createResult(null, new JTypeReference<String>() {});
        });
    }



    @Test
    void testCreateResult_WithEmptyString() throws IOException {
        JResult emptyResult = JResult.builder().text("").build();
        String result = JResultFactory.createResult(emptyResult, String.class);
        assertEquals("", result);
    }

    @Test
    void testCreateResult_WithWhitespaceString() throws IOException {
        JResult whitespaceResult = JResult.builder().text("   ").build();
        String result = JResultFactory.createResult(whitespaceResult, String.class);
        assertEquals("   ", result);
    }

    @Test
    void testCreateResult_WithInvalidJson() {
        JResult invalidJsonResult = JResult.builder().text("not a json").build();
        assertThrows(Exception.class, () -> {
            JResultFactory.createResult(invalidJsonResult, TestPojo.class);
        });
    }

    @Test
    void testCreateResult_WithBytesResultToString() throws IOException {
        String result = JResultFactory.createResult(bytesResult, String.class);
        assertEquals(TEST_STRING, result);
    }


    public static class TestPojo {
        private String name;
        private int age;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    public static class ComplexPojo {
        private String name;
        private int age;
        private List<String> tags;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    public static class ResultWrapper<T> {
        private T data;
        private int code;

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
    }
}
