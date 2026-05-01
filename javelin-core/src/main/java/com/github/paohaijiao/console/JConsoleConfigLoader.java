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
package com.github.paohaijiao.console;

import com.github.paohaijiao.enums.JLogLevel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JConsoleConfigLoader {

    private static final String DEFAULT_CONFIG_FILE = "jconsole.properties";

    private static JConsole console = new JConsole();

    public static JConsoleConfig load() {
        return load(DEFAULT_CONFIG_FILE);
    }

    public static JConsoleConfig load(String configFile) {
        JConsoleConfig config = new JConsoleConfig();
        Properties props = new Properties();
        try (InputStream input = JConsoleConfigLoader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                props.load(input);
                applyProperties(config, props);
                System.out.println("[JConsole] Loaded configuration from classpath: " + configFile);
            } else {
                try (InputStream fileInput = new FileInputStream(configFile)) {
                    props.load(fileInput);
                    applyProperties(config, props);
                    System.out.println("[JConsole] Loaded configuration from file: " + configFile);
                } catch (FileNotFoundException e) {
                    System.out.println("[JConsole] No config file found, using defaults");
                }
            }
        } catch (IOException e) {
            System.out.println("[JConsole] Failed to load config: " + e.getMessage());
        }
        return config;
    }

    private static void applyProperties(JConsoleConfig config, Properties props) {
        config.setEnabled(Boolean.parseBoolean(props.getProperty("jconsole.enabled", "true")));
        String levelStr = props.getProperty("jconsole.level", "INFO").toUpperCase();
        try {
            config.setLevel(JLogLevel.valueOf(levelStr));
        } catch (IllegalArgumentException e) {
            console.error("[JConsole] Invalid log level: " + levelStr + ", using INFO");
            config.setLevel(JLogLevel.INFO);
        }
        config.setShowTimestamp(Boolean.parseBoolean(props.getProperty("jconsole.showTimestamp", "true")));
        config.setTimestampFormat(props.getProperty("jconsole.timestampFormat", "yyyy-MM-dd HH:mm:ss.SSS"));
        config.setConsoleOutput(Boolean.parseBoolean(props.getProperty("jconsole.consoleOutput", "true")));
        config.setEnableColor(Boolean.parseBoolean(props.getProperty("jconsole.enableColor", "true")));
        config.setLogFilePath(props.getProperty("jconsole.logFilePath", null));
    }
}
