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

import com.github.paohaijiao.enums.JMethodEnums;
import com.github.paohaijiao.evalue.JEvaluator;
import com.github.paohaijiao.string.PlaceholderResolver;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

/**
 * packageName PACKAGE_NAME
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/3/21
 */
public class JPlaceholderTest {
    @Test
    public void result1() throws IOException {
        String result1 = PlaceholderResolver.resolve("Resolving entity: publicId={}, systemId={}","{}", "public-123", "system-456");
        System.out.println(result1);
    }
    @Test
    public void result2() throws IOException {
        String result2 = PlaceholderResolver.resolve("User: #{}, Age: #{}, City: #{}",  "#{}",  "张三", 25, "北京" );
        System.out.println(result2);
    }
    @Test
    public void result3() throws IOException {
        String result3 = PlaceholderResolver.resolve(
                "SELECT * FROM users WHERE id = ${} AND name = ${}",
                "${}",
                1001, "张三"
        );
        System.out.println(result3);
    }
    @Test
    public void result4() throws IOException {
        String result4 = PlaceholderResolver.resolve(
                "Error: %{}, Code: %{}",
                "%{}",
                "File not found", 404
        );
        System.out.println(result4);
    }
    @Test
    public void result5() throws IOException {
        String result5 = PlaceholderResolver.use("User: {}, Age: {}")
                .add("李四")
                .add(30)
                .resolve();
        System.out.println(result5);
    }
    @Test
    public void result6() throws IOException {
        String result6 = PlaceholderResolver.use("Product: #{}, Price: #{}", "#{}")
                .add("笔记本电脑")
                .add(5999.99)
                .resolve();
        System.out.println(result6);
    }
    @Test
    public void result7() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "王五");
        params.put("age", 28);
        params.put("city", "深圳");
        String result7 = PlaceholderResolver.resolveNamed("User: ${name}, Age: ${age}, City: ${city}", params);
        System.out.println(result7);
    }
    @Test
    public void result8() throws IOException {
        String result8 = PlaceholderResolver.useNamed("User: ${name:匿名}, Age: ${age:18}, City: ${city:未知}"
                ).set("name", "赵六")
                .resolve();
        System.out.println(result8);
    }
    @Test
    public void result9() throws IOException {
        String result9 = PlaceholderResolver.useNamed("User: ${name}, Age: ${age}, VIP: ${vip}")
                .set("name", "孙七")
                .set("age", 35)
                .set("vip", true)
                .resolve();
        System.out.println(result9);
    }
    @Test
    public void result10() throws IOException {
        try {
            String result10 = PlaceholderResolver.use("User: {}, Age: {}")
                    .strict()
                    .add("张三")
                    .resolve();
        } catch (IllegalArgumentException e) {
            System.out.println("严格模式异常: " + e.getMessage());
        }
    }
    @Test
    public void result11() throws IOException {
        String result11 = PlaceholderResolver.use("Name: {}, Age: {}")
                .nullStrategy(PlaceholderResolver.NullStrategy.KEEP_PLACEHOLDER)
                .add("张三", null)
                .resolve();
        System.out.println(result11);
    }
    @Test
    public void result12() throws IOException {
        String result12 = PlaceholderResolver.useNamed("Amount: ${amount}")
                .setNumber("amount", 1234567.89, "%,.2f")
                .resolve();
        System.out.println(result12);
    }
    @Test
    public void result13() throws IOException {
        Date now = new Date();
        String result13 = PlaceholderResolver.useNamed("Current Time: ${time}")
                .setDate("time", now, "yyyy-MM-dd")
                .resolve();
        System.out.println(result13);
    }
    @Test
    public void result14() throws IOException {
        boolean isVIP = true;
        String result14 = PlaceholderResolver.useNamed("User: ${name}, Level: ${level}")
                .set("name", "周八")
                .condition(isVIP, "level", "VIP", "Normal")
                .resolve();
        System.out.println(result14);
    }
    @Test
    public void result15() throws IOException {
        String result15 = PlaceholderResolver.useNamed("User: ${name}, Score: ${score}")
                .set("name", "吴九")
                .set("score", 85.5, v -> String.format("%.1f分", v))
                .resolve();
        System.out.println(result15);
    }
    @Test
    public void result16() throws IOException {
        List<Object> args = Arrays.asList("张三", 25, "北京");
        String result16 = PlaceholderResolver.resolve("User: {}, Age: {}, City: {}", args);
        System.out.println(result16);
    }
    @Test
    public void result17() throws IOException {
        List<Object> args = Arrays.asList("张三", 25, "北京");
        String result17 = PlaceholderResolver.resolve("User: #{}, Age: #{}","#{}",  args);
        System.out.println(result17);
    }
    @Test
    public void result18() throws IOException {
        Map<String, Object> nestedParams = new HashMap<>();
        nestedParams.put("user", "张三");
        nestedParams.put("greeting", "Hello, ${user}");
        String result18 = PlaceholderResolver.resolveNamed("Message: ${greeting}", nestedParams);
        System.out.println(result18);
    }
    @Test
    public void result19() throws IOException {
        PlaceholderResolver resolver = PlaceholderResolver.use("User: {}, Age: {}")
                .add("张三")
                .add(25);

        PlaceholderResolver copy = resolver.copy();
        String result19 = copy.resolve();
        System.out.println(result19);
    }

    @Test
    public void result20() throws IOException {
        String resolver = PlaceholderResolver.resolve("User: {}, Age: {}","{}","张三",23);
        System.out.println(resolver);
    }




}
