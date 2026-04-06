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
package ognl;

import com.github.paohaijiao.value.ValueResolver;
import ognl.model.Address;
import ognl.model.User;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * packageName ognl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/6
 */
public class ValueUtilsTest {
    private static Map<String, Object> contextMap=new HashMap<>();
    private static User testUser;
    static {
        contextMap.put("name", "张三");
        contextMap.put("age", 25);
        contextMap.put("score", 85.5);
        contextMap.put("active", true);
        contextMap.put("tags", Arrays.asList("java", "ognl", "test"));

        testUser = new User("李四", 30, "beijing@example.com");
    }
    @Test
    public void test1() throws OgnlException {
        Map<String, Object> context = new HashMap<>();
        context.put("name", "张三");
        context.put("age", 25);
        context.put("score", 85.5);
        Object result = ValueResolver.evaluate("#name + ' 的年龄是 ' + #age", context);
        System.out.println( result);
    }
    @Test
    public void test2() throws OgnlException {
        User user = new User("李四", 30,"beijing@example.com");
        Map<String, Object> vars = new HashMap<>();
        vars.put("multiplier", 2);
        Object ageResult = ValueResolver.evaluate("age * #multiplier", user, vars);
        System.out.println("" + ageResult);
    }
    @Test
    public void test3() throws OgnlException {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "admin");
        params.put("count", 10);
        params.put("status", "success");
        String template = "用户 ${username} 执行了操作，状态: #{#status}，影响记录数: ${count}";
        String rendered = ValueResolver.renderTemplate(template, params);
        System.out.println(" " + rendered);
    }
    @Test
    public void test4() throws OgnlException {
        Map<String, Object> incompleteParams = new HashMap<>();
        incompleteParams.put("name", "测试");
        String templateWithDefault = ValueResolver.renderTemplate("Hello ${name}, status: ${missing}", incompleteParams, "unknown");
        System.out.println(templateWithDefault);
    }
    @Test
    public void test5() throws OgnlException {
        String strValue = ValueResolver.convertValue(123, String.class);
        Integer intValue = ValueResolver.convertValue("456", Integer.class);
        System.out.println( strValue + ", " + intValue);
    }
    @Test
    public void test6() throws OgnlException {
        Object result = ValueResolver.evaluate("#name", contextMap);
        result = ValueResolver.evaluate("#age + 5", contextMap);
        result = ValueResolver.evaluate("#active", contextMap);
        System.out.println(result);
    }
    @Test
    public void test7() throws OgnlException {
        Object result = ValueResolver.evaluate("#name + ' 今年 ' + #age + ' 岁'", contextMap);
        System.out.println(result);
    }
    @Test
    public void test8() throws OgnlException {
        Object result = ValueResolver.evaluate("#age * 2", contextMap);
        result = ValueResolver.evaluate("#score + 10.5", contextMap);
        result = ValueResolver.evaluate("#age % 3", contextMap);
        System.out.println(result);
    }
    @Test
    public void test9() throws OgnlException {
        Object result = ValueResolver.evaluate("#age > 18 and #active", contextMap);
        result = ValueResolver.evaluate("#age < 18 or #score > 60", contextMap);
        result = ValueResolver.evaluate("not #active", contextMap);
        System.out.println(result);
    }
    @Test
    void testEvaluateTernary() throws OgnlException {
        Object result = ValueResolver.evaluate("#age >= 18 ? '成年' : '未成年'", contextMap);
        result = ValueResolver.evaluate("#score >= 60 ? '及格' : '不及格'", contextMap);
        System.out.println(result);
    }


    @Test
    void testEvaluateWithRoot() throws OgnlException {
        Object result = ValueResolver.evaluate("name", testUser, null);
        result = ValueResolver.evaluate("age", testUser, null);
        result = ValueResolver.evaluate("email", testUser, null);
        System.out.println(result);

    }
    @Test
    void testEvaluateMixed() throws OgnlException {
        Map<String, Object> vars = new HashMap<>();
        vars.put("multiplier", 2);
        Object result = ValueResolver.evaluate("age * #multiplier", testUser, vars);
        System.out.println(result);
    }
    @Test
    void testEvaluateNestedProperty() throws OgnlException {
        Address address = new Address("北京市", "朝阳区");
        testUser.setAddress(address);
        Object result = ValueResolver.evaluate("address.city", testUser, null);
        result = ValueResolver.evaluate("address.district", testUser, null);
        System.out.println(result);
    }
    @Test
    void testEvaluateNestedCollection() throws OgnlException {
        List<Address> addresses = Arrays.asList(
                new Address("北京", "朝阳"),
                new Address("上海", "浦东")
        );
        testUser.setAddresses(addresses);
        Object result = ValueResolver.evaluate("addresses[0].city", testUser, null);
        result = ValueResolver.evaluate("addresses[1].district", testUser, null);
        result = ValueResolver.evaluate("addresses.{city}", testUser, null);
        System.out.println(result);
    }
    @Test
    void testSetValue() throws OgnlException {
        User user = new User("张三", 20, "test@example.com");
        ValueResolver.setValue("name", user, null, "王五");
        assertEquals("王五", user.getName());
        ValueResolver.setValue("age", user, null, 35);
        assertEquals(35, user.getAge());
        ValueResolver.setValue("email", user, null, "wangwu@example.com");
        assertEquals("wangwu@example.com", user.getEmail());
    }
    @Test
    void testSetNestedValue() throws OgnlException {
        User user = new User("张三", 20, "test@example.com");
        Address address = new Address("北京", "朝阳");
        user.setAddress(address);
        ValueResolver.setValue("address.city", user, null, "上海");
        assertEquals("上海", address.getCity());
        ValueResolver.setValue("address.district", user, null, "浦东");
        assertEquals("浦东", address.getDistrict());
        System.out.println();
    }
    @Test
    void testRenderTemplateComplexOgnl() {
        Map<String, Object> params = new HashMap<>();
        params.put("numbers", Arrays.asList(1, 2, 3, 4, 5));
        params.put("threshold", 3);
        String template = "大于 ${threshold} 的数字: #{#numbers}";
        String result = ValueResolver.renderTemplate(template, params);
        assertEquals("大于 3 的数字: [4, 5]", result);
    }
    @Test
    void testRenderTemplateWithDefault() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "张三");
        String template = "Hello ${name}, status: ${missing}";
        String result = ValueResolver.renderTemplate(template, params, "unknown");
        assertEquals("Hello 张三, status: unknown", result);
    }
    @Test
    void testRenderTemplateBasic() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "张三");
        params.put("age", 25);
        String template = "用户 ${name} 年龄 ${age} 岁";
        String result = ValueResolver.renderTemplate(template, params);
        assertEquals("用户 张三 年龄 25 岁", result);
    }
    @Test
    void testRenderTemplateMixed() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "admin");
        params.put("count", 10);
        params.put("status", "success");
        String template = "用户 ${username} 执行了操作，状态: #{#status}，影响记录数: ${count}";
        String result = ValueResolver.renderTemplate(template, params);
        assertEquals("用户 admin 执行了操作，状态: success，影响记录数: 10", result);
    }
    @Test
    void testRenderTemplateWithOgnl() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "张三");
        params.put("age", 25);
        params.put("score", 80);
        String template = "用户 #{#name}，年龄 #{#age} 岁，成绩 #{#score >= 60 ? '及格' : '不及格'}";
        String result = ValueResolver.renderTemplate(template, params);
        assertEquals("用户 张三，年龄 25 岁，成绩 及格", result);
    }
    @Test
    void testEvaluateCollection() throws OgnlException {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("tags", Arrays.asList("java", "ognl", "test"));
        Object result1 = ValueResolver.evaluate("size()", contextMap);
        Object result3 = ValueResolver.evaluate("#tags.size()", contextMap);
        Object result = ValueResolver.evaluate("#tags.size()", contextMap);
        result = ValueResolver.evaluate("#tags[0]", contextMap);
        result = ValueResolver.evaluate("#tags.{#this.toUpperCase()}", contextMap);
        System.out.println(result);
    }
}
