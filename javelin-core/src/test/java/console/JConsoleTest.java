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

        // 带异常的输出
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            console.error("An error occurred", e);
        }

        // 动态修改日志级别
        console.setLevel(JLogLevel.DEBUG);
        console.debug("Now debug messages are visible");

        // 关闭日志系统
        console.shutdown();
    }
    @Test
    public void test1(){
        JConsole console = new JConsole();
        console.info("This is a debug message");

    }
}
