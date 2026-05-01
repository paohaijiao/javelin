package com.github.paohaijiao.console;
import com.github.paohaijiao.enums.JLogLevel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class JConsole {

    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_DEBUG = "\u001B[36m"; // cyan

    private static final String ANSI_INFO = "\u001B[32m";  // green

    private static final String ANSI_WARN = "\u001B[33m";  // yellow

    private static final String ANSI_ERROR = "\u001B[31m"; // red

    private static final ReentrantLock lock = new ReentrantLock();

    private static final ConcurrentHashMap<JConsole, Boolean> allInstances = new ConcurrentHashMap<>();

    private static JConsole defaultInstance;

    private static volatile JConsoleConfig globalConfig;

    private static volatile boolean globalConfigLoaded = false;

    private JConsoleConfig config;

    private DateTimeFormatter formatter;

    private PrintWriter fileWriter;

    private boolean fileWriterError = false;

    private String instanceName;

    /**
     * 兼容老API：无参构造器
     * 使用全局配置创建一个新实例
     */
    public JConsole() {
        this(null, null);
    }

    /**
     * 兼容老API：带showTimestamp参数的构造器
     *
     * @param showTimestamp 是否显示时间戳
     */
    public JConsole(boolean showTimestamp) {
        this(null, null);
        if (this.config != null) {
            this.config.setShowTimestamp(showTimestamp);
        }
    }

    /**
     * 带实例名称的构造器
     *
     * @param instanceName 实例名称
     */
    public JConsole(String instanceName) {
        this(instanceName, null);
    }

    /**
     * 完整构造器
     *
     * @param instanceName 实例名称
     * @param config       配置（如果为null则使用全局配置）
     */
    private JConsole(String instanceName, JConsoleConfig config) {
        this.instanceName = instanceName;
        loadGlobalConfigIfNeeded();
        if (config != null) {
            this.config = config;
        } else {
            this.config = cloneConfig(globalConfig);
        }
        this.formatter = DateTimeFormatter.ofPattern(this.config.getTimestampFormat());
        if (this.config.getLogFilePath() != null && !this.config.getLogFilePath().isEmpty()) {
            try {
                this.fileWriter = new PrintWriter(new FileWriter(this.config.getLogFilePath(), true), true);
            } catch (IOException e) {
                System.err.println("[JConsole] Failed to create log file: " + e.getMessage());
                this.fileWriterError = true;
            }
        }
        allInstances.put(this, true);
    }

    /**
     * 加载全局配置
     */
    private static void loadGlobalConfigIfNeeded() {
        if (!globalConfigLoaded) {
            lock.lock();
            try {
                if (!globalConfigLoaded) {
                    globalConfig = JConsoleConfigLoader.load();
                    globalConfigLoaded = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 初始化全局配置（所有实例共享）
     *
     * @param config 全局配置
     */
    public static void init(JConsoleConfig config) {
        lock.lock();
        try {
            globalConfig = config;
            globalConfigLoaded = true;
            for (JConsole instance : allInstances.keySet()) {
                instance.updateConfig(config);
            }
            if (defaultInstance == null) {
                defaultInstance = new JConsole("default", config);
            } else {
                defaultInstance.updateConfig(config);
                defaultInstance.info("Global config updated");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取默认单例（推荐新代码使用）
     */
    public static JConsole getInstance() {
        if (defaultInstance == null) {
            lock.lock();
            try {
                if (defaultInstance == null) {
                    loadGlobalConfigIfNeeded();
                    defaultInstance = new JConsole("default", globalConfig);
                }
            } finally {
                lock.unlock();
            }
        }
        return defaultInstance;
    }

    /**
     * 获取指定名称的实例（如果不存在则创建）
     *
     * @param name 实例名称
     */
    public static JConsole getInstance(String name) {
        for (JConsole instance : allInstances.keySet()) {
            if (name.equals(instance.instanceName)) {
                return instance;
            }
        }
        return new JConsole(name, null);
    }

    /**
     * 重新加载全局配置
     */
    public static void reloadConfig() {
        lock.lock();
        try {
            globalConfig = JConsoleConfigLoader.load();
            globalConfigLoaded = true;
            for (JConsole instance : allInstances.keySet()) {
                instance.updateConfig(globalConfig);
            }
            if (defaultInstance != null) {
                defaultInstance.updateConfig(globalConfig);
                defaultInstance.info("Configuration reloaded");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 全局关闭所有日志输出
     */
    public static void globalDisable() {
        if (globalConfig != null) {
            globalConfig.setEnabled(false);
            for (JConsole instance : allInstances.keySet()) {
                instance.config.setEnabled(false);
            }
            if (defaultInstance != null) {
                defaultInstance.config.setEnabled(false);
            }
        }
    }

    /**
     * 全局开启所有日志输出
     */
    public static void globalEnable() {
        if (globalConfig != null) {
            globalConfig.setEnabled(true);
            for (JConsole instance : allInstances.keySet()) {
                instance.config.setEnabled(true);
            }
            if (defaultInstance != null) {
                defaultInstance.config.setEnabled(true);
            }
        }
    }

    /**
     * 克隆配置对象
     */
    private JConsoleConfig cloneConfig(JConsoleConfig source) {
        if (source == null) {
            return new JConsoleConfig();
        }
        JConsoleConfig target = new JConsoleConfig();
        target.setEnabled(source.isEnabled());
        target.setLevel(source.getLevel());
        target.setShowTimestamp(source.isShowTimestamp());
        target.setTimestampFormat(source.getTimestampFormat());
        target.setConsoleOutput(source.isConsoleOutput());
        target.setEnableColor(source.isEnableColor());
        target.setLogFilePath(source.getLogFilePath());
        return target;
    }

    /**
     * 更新实例配置
     */
    private void updateConfig(JConsoleConfig newConfig) {
        this.config = cloneConfig(newConfig);
        this.formatter = DateTimeFormatter.ofPattern(this.config.getTimestampFormat());
        if (this.fileWriter != null) {
            this.fileWriter.close();
            this.fileWriter = null;
        }
        if (this.config.getLogFilePath() != null && !this.config.getLogFilePath().isEmpty()) {
            try {
                this.fileWriter = new PrintWriter(new FileWriter(this.config.getLogFilePath(), true), true);
                this.fileWriterError = false;
            } catch (IOException e) {
                System.err.println("[JConsole] Failed to create log file: " + e.getMessage());
                this.fileWriterError = true;
            }
        }
    }

    /**
     * 获取实例名称
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * 判断日志级别是否启用
     */
    private boolean isLevelEnabled(JLogLevel level) {
        if (!config.isEnabled()) return false;
        return level.isEnabled(config.getLevel());
    }

    /**
     * 获取颜色代码
     */
    private String getColorCode(JLogLevel level) {
        switch (level) {
            case DEBUG:
                return ANSI_DEBUG;
            case INFO:
                return ANSI_INFO;
            case WARN:
                return ANSI_WARN;
            case ERROR:
                return ANSI_ERROR;
            default:
                return ANSI_RESET;
        }
    }

    /**
     * 格式化消息
     */
    private String formatMessage(JLogLevel level, String message) {
        StringBuilder sb = new StringBuilder();
        if (config.isShowTimestamp()) {
            sb.append("[").append(LocalDateTime.now().format(formatter)).append("] ");
        }
        if (instanceName != null && !"default".equals(instanceName) && !instanceName.isEmpty()) {
            sb.append("[").append(instanceName).append("] ");
        }
        sb.append("[").append(level.name()).append("] ");
        sb.append(message);
        return sb.toString();
    }

    /**
     * 输出日志
     */
    private void output(JLogLevel level, String formattedMessage) {
        if (config.isConsoleOutput()) {
            String color = config.isEnableColor() ? getColorCode(level) : "";
            String reset = config.isEnableColor() ? ANSI_RESET : "";
            System.out.println(color + formattedMessage + reset);
        }
        if (fileWriter != null && !fileWriterError) {
            try {
                fileWriter.println(formattedMessage);
                fileWriter.flush();
            } catch (Exception e) {
                if (!fileWriterError) {
                    System.err.println("[JConsole] Failed to write to log file: " + e.getMessage());
                    fileWriterError = true;
                }
            }
        }
    }

    /**
     * 日志输出方法
     */
    public void log(JLogLevel level, String message) {
        if (!isLevelEnabled(level)) return;
        String formattedMessage = formatMessage(level, message);
        output(level, formattedMessage);
    }
    public void debug(String message) {
        log(JLogLevel.DEBUG, message);
    }

    /**
     * 日志输出方法（带异常）
     */
    public void log(JLogLevel level, String message, Throwable throwable) {
        if (!isLevelEnabled(level)) return;
        String formattedMessage = formatMessage(level, message);
        output(level, formattedMessage);
        if (config.isConsoleOutput() && throwable != null) {
            String color = config.isEnableColor() ? getColorCode(level) : "";
            String reset = config.isEnableColor() ? ANSI_RESET : "";
            System.err.println(color + formatStackTrace(throwable) + reset);
        }
        if (fileWriter != null && !fileWriterError && throwable != null) {
            throwable.printStackTrace(fileWriter);
            fileWriter.flush();
        }
    }

    public void info(String message) {
        log(JLogLevel.INFO, message);
    }

    /**
     * 格式化堆栈信息
     */
    private String formatStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }

    public void warn(String message) {
        log(JLogLevel.WARN, message);
    }

    public void debug(String message, Throwable throwable) {
        log(JLogLevel.DEBUG, message, throwable);
    }

    public void error(String message) {
        log(JLogLevel.ERROR, message);
    }

    public void error(String message, Throwable throwable) {
        log(JLogLevel.ERROR, message, throwable);
    }

    public void info(String message, Throwable throwable) {
        log(JLogLevel.INFO, message, throwable);
    }

    public void warn(String message, Throwable throwable) {
        log(JLogLevel.WARN, message, throwable);
    }

    /**
     * 关闭日志系统
     */
    public void shutdown() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }

    /**
     * 获取当前实例的日志级别
     */
    public JLogLevel getLevel() {
        return config.getLevel();
    }

    /**
     * 设置当前实例的日志级别
     */
    public void setLevel(JLogLevel level) {
        this.config.setLevel(level);
        info("Log level changed to: " + level);
    }

    /**
     * 设置是否显示时间戳（当前实例）
     */
    public void setShowTimestamp(boolean showTimestamp) {
        this.config.setShowTimestamp(showTimestamp);
    }

    /**
     * 设置是否启用颜色（当前实例）
     */
    public void setEnableColor(boolean enableColor) {
        this.config.setEnableColor(enableColor);
    }
}