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
package console;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.console.JConsoleConfig;
import com.github.paohaijiao.console.JConsoleConfigLoader;
import com.github.paohaijiao.enums.JLogLevel;
import org.junit.jupiter.api.Test;

/**
 * packageName console
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/1
 */
public class JConsoleTest {
    @Test
    public void test(){
        JConsoleConfig config = JConsoleConfigLoader.load();
        JConsole.init(config);
        JConsole console = JConsole.getInstance();
        console.debug("This is a debug message");
        console.info("This is an info message");
        console.warn("This is a warning message");
        console.error("This is an error message");
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            console.error("An error occurred", e);
        }
        console.setLevel(JLogLevel.DEBUG);
        console.debug("Now debug messages are visible");
        console.shutdown();
    }
    @Test
    public void test1(){
        JConsole console = new JConsole();
        console.info("This is a debug message");

    }
    @Test
    public void test2(){
        JConsole console = new JConsole();
        String userName = "张三";
        int age = 25;
        console.info("用户 {} 的年龄是 {} 岁", userName, age);
        String orderId = "ORD-12345";
        double amount = 99.99;
        String status = "SUCCESS";
        console.info("订单 {} 金额 {} 状态 {}", orderId, amount, status);
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            console.error("计算失败，用户: {}, 操作: {}", e, userName, "除法运算");
        }
        console.debug("调试信息: 变量值 = {}", "debug value");
        console.warn("配置项 {} 未设置，使用默认值 {}", "timeout", 5000);
        String nullValue = null;
        console.info("空值测试: {}", nullValue);
        User user = new User("李四", 30);
        console.info("用户信息: {}", user);

    }
    static class User {
        String name;
        int age;
        User(String name, int age) { this.name = name; this.age = age; }
        @Override
        public String toString() { return "User{name='" + name + "', age=" + age + "}"; }
    }
}
