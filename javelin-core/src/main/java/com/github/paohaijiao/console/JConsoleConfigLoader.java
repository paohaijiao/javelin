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
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class JConsoleConfigLoader {

    private static final String DEFAULT_PROPERTIES_FILE = "jconsole.properties";

    private static final String DEFAULT_YAML_FILE = "jconsole.yml";

    private static final String DEFAULT_YAML_FILE_ALT = "jconsole.yaml";

    private static JConsole console = new JConsole();

    /**
     * 自动检测并加载配置（优先使用 YAML）
     */
    public static JConsoleConfig load() {
        JConsoleConfig config = loadYamlConfig();
        if (config != null) {
            return config;
        }
        return loadPropertiesConfig(DEFAULT_PROPERTIES_FILE);
    }

    /**
     * 根据文件名自动判断格式并加载
     */
    public static JConsoleConfig load(String configFile) {
        if (configFile == null || configFile.isEmpty()) {
            return load();
        }
        if (configFile.endsWith(".yml") || configFile.endsWith(".yaml")) {
            JConsoleConfig config = loadYamlConfig(configFile);
            if (config != null) {
                return config;
            }
        }
        return loadPropertiesConfig(configFile);
    }

    /**
     * 从 classpath 加载 YAML 配置
     */
    private static JConsoleConfig loadYamlConfig() {
        JConsoleConfig config = loadYamlFromClasspath("application.yml");
        if (config != null) return config;
        config = loadYamlFromClasspath("application.yaml");
        if (config != null) return config;
        config = loadYamlFromClasspath(DEFAULT_YAML_FILE);
        if (config != null) return config;
        config = loadYamlFromClasspath(DEFAULT_YAML_FILE_ALT);
        if (config != null) return config;
        return null;
    }

    /**
     * 从指定路径加载 YAML 配置
     */
    private static JConsoleConfig loadYamlConfig(String configFile) {
        JConsoleConfig config = loadYamlFromClasspath(configFile);
        if (config != null) return config;
        config = loadYamlFromFileSystem(configFile);
        if (config != null) return config;
        return null;
    }

    /**
     * 从 classpath 加载 YAML
     */
    private static JConsoleConfig loadYamlFromClasspath(String configFile) {
        try (InputStream input = JConsoleConfigLoader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                return parseYamlConfig(input, configFile);
            }
        } catch (IOException e) {
            System.out.println("[JConsole] Failed to load YAML from classpath: " + e.getMessage());
        }
        return null;
    }

    /**
     * 从文件系统加载 YAML
     */
    private static JConsoleConfig loadYamlFromFileSystem(String configFile) {
        Path path = Paths.get(configFile);
        if (Files.exists(path)) {
            try (InputStream input = Files.newInputStream(path)) {
                return parseYamlConfig(input, configFile);
            } catch (IOException e) {
                System.out.println("[JConsole] Failed to load YAML from file: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 解析 YAML 配置
     */
    private static JConsoleConfig parseYamlConfig(InputStream input, String configFile) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);
            Object jconsoleConfig = data.get("jconsole");
            if (jconsoleConfig instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> jconsoleMap = (Map<String, Object>) jconsoleConfig;
                return applyYamlProperties(jconsoleMap, configFile);
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> configMap = (Map<String, Object>) data;
                return applyYamlProperties(configMap, configFile);
            }
        } catch (Exception e) {
            System.out.println("[JConsole] Failed to parse YAML config: " + e.getMessage());
            return null;
        }
    }

    /**
     * 应用 YAML 属性到配置对象
     */
    private static JConsoleConfig applyYamlProperties(Map<String, Object> props, String configFile) {
        JConsoleConfig config = new JConsoleConfig();
        if (props.containsKey("enabled")) {
            config.setEnabled(Boolean.parseBoolean(String.valueOf(props.get("enabled"))));
        }
        if (props.containsKey("level")) {
            String levelStr = String.valueOf(props.get("level")).toUpperCase();
            try {
                config.setLevel(JLogLevel.valueOf(levelStr));
            } catch (IllegalArgumentException e) {
                console.error("[JConsole] Invalid log level: " + levelStr + ", using INFO");
                config.setLevel(JLogLevel.INFO);
            }
        }
        if (props.containsKey("showTimestamp")) {
            config.setShowTimestamp(Boolean.parseBoolean(String.valueOf(props.get("showTimestamp"))));
        }
        if (props.containsKey("timestampFormat")) {
            config.setTimestampFormat(String.valueOf(props.get("timestampFormat")));
        }
        if (props.containsKey("consoleOutput")) {
            config.setConsoleOutput(Boolean.parseBoolean(String.valueOf(props.get("consoleOutput"))));
        }
        if (props.containsKey("enableColor")) {
            config.setEnableColor(Boolean.parseBoolean(String.valueOf(props.get("enableColor"))));
        }
        if (props.containsKey("logFilePath")) {
            config.setLogFilePath(String.valueOf(props.get("logFilePath")));
        }

        System.out.println("[JConsole] Loaded configuration from YAML: " + configFile);
        return config;
    }

    /**
     * 加载 Properties 配置（原有逻辑）
     */
    private static JConsoleConfig loadPropertiesConfig(String configFile) {
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
