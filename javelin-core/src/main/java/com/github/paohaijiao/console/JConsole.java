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

    /**
     * 系统属性开关名：{@code -Djquick.color=true/false}，优先级高于环境变量与终端探测
     */
    private static final String COLOR_PROPERTY = "jquick.color";

    /**
     * 终端是否支持 ANSI 颜色码，JVM 生命周期内只探测一次。
     *
     * <p>容器内（docker logs）、CI 控制台、输出重定向等非交互式场景下为 false，
     * 避免 {@code ESC[32m} 被原样打印成 {@code [32m} 之类的乱码。</p>
     */
    private static final boolean TERMINAL_SUPPORTS_COLOR = detectTerminalColorSupport();

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

    public static JConsole initConsoleEnvironment (){
        JConsoleConfig config = JConsoleConfigLoader.load();
        JConsole.init(config);
        JConsole console = JConsole.getInstance();
        return console;
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
     * 判断是否真正输出 ANSI 颜色码。
     *
     * <p>{@link JConsoleConfig#isEnableColor()} 作为总开关的语义保持不变，需要同时满足两个条件：</p>
     * <ul>
     *     <li>配置层面允许着色，即 {@code config.isEnableColor()} 为 true；</li>
     *     <li>当前终端具备 ANSI 能力，即非容器 / 非 CI / 非重定向输出。</li>
     * </ul>
     *
     * @return true 表示可以向控制台写入颜色码
     */
    private boolean isColorEnabled() {
        return config.isEnableColor() && TERMINAL_SUPPORTS_COLOR;
    }

    /**
     * 探测当前终端是否支持 ANSI 颜色码，按优先级依次判断：
     *
     * <ol>
     *     <li>系统属性 {@code -Djquick.color=true/false}：显式指定，最高优先级；</li>
     *     <li>环境变量 {@code NO_COLOR}：存在且非空字符串即关闭，参见 https://no-color.org/；</li>
     *     <li>环境变量 {@code TERM} 为 {@code dumb}：明确表示不支持；</li>
     *     <li>Windows：老 cmd（conhost）默认不解析 VT 序列，仅认明确支持的终端；</li>
     *     <li>非 Windows 且 {@code TERM} 未设置：视为非交互式终端；</li>
     *     <li>{@link System#console()} 为 null：非交互式，例如 docker logs、Jenkins、重定向到文件。</li>
     * </ol>
     *
     * @return true 表示终端支持 ANSI 颜色码
     */
    private static boolean detectTerminalColorSupport() {
        String property = System.getProperty(COLOR_PROPERTY);
        if (property != null && !property.trim().isEmpty()) {
            return Boolean.parseBoolean(property.trim());
        }
        String noColor = System.getenv("NO_COLOR");
        if (noColor != null && !noColor.isEmpty()) {
            return false;
        }
        String term = System.getenv("TERM");
        if (term != null && "dumb".equalsIgnoreCase(term.trim())) {
            return false;
        }
        if (isWindows()) {
            return supportsColorOnWindows();
        }
        if (term == null) {
            return false;
        }
        return System.console() != null;
    }

    /**
     * 判断当前操作系统是否为 Windows
     *
     * @return true 表示 Windows
     */
    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    /**
     * 判断 Windows 是否运行在支持 ANSI 的终端中。
     *
     * <p>Windows Terminal、ConEmu、ANSICON 会设置各自的标识环境变量；
     * 原生老 cmd / conhost 默认不解析 VT 序列，因此返回 false。</p>
     *
     * @return true 表示当前 Windows 终端支持 ANSI 颜色码
     */
    private static boolean supportsColorOnWindows() {
        return System.getenv("WT_SESSION") != null
                || System.getenv("ConEmuANSI") != null
                || System.getenv("ANSICON") != null;
    }

    /**
     * 获取颜色代码，终端不支持颜色或配置关闭颜色时返回空字符串
     */
    private String getColorCode(JLogLevel level) {
        if (!isColorEnabled()) {
            return "";
        }
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
            // 颜色只在此处拼接，formattedMessage 保持纯净，保证写入文件的内容不含任何转义码
            String color = getColorCode(level);
            String reset = color.isEmpty() ? "" : ANSI_RESET;
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
            String color = getColorCode(level);
            String reset = color.isEmpty() ? "" : ANSI_RESET;
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

    /**
     * 格式化消息，将 {} 替换为对应的参数
     * @param message 包含 {} 占位符的消息模板
     * @param args 参数数组
     * @return 格式化后的消息
     */
    private String formatWithPlaceholders(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        int argIndex = 0;
        int placeholderIndex;
        while ((placeholderIndex = message.indexOf("{}", lastIndex)) != -1 && argIndex < args.length) {
            result.append(message, lastIndex, placeholderIndex);
            Object arg = args[argIndex];
            result.append(arg != null ? arg.toString() : "null");
            lastIndex = placeholderIndex + 2;
            argIndex++;
        }
        if (lastIndex < message.length()) {
            result.append(message.substring(lastIndex));
        }
        return result.toString();
    }
    public void log(JLogLevel level, String message, Object... args) {
        if (!isLevelEnabled(level)) return;
        String formattedMessage = formatWithPlaceholders(message, args);
        String finalMessage = formatMessage(level, formattedMessage);
        output(level, finalMessage);
    }

    public void log(JLogLevel level, String message, Throwable throwable, Object... args) {
        if (!isLevelEnabled(level)) return;
        String formattedMessage = formatWithPlaceholders(message, args);
        String finalMessage = formatMessage(level, formattedMessage);
        output(level, finalMessage);
        if (config.isConsoleOutput() && throwable != null) {
            String color = getColorCode(level);
            String reset = color.isEmpty() ? "" : ANSI_RESET;
            System.err.println(color + formatStackTrace(throwable) + reset);
        }
        if (fileWriter != null && !fileWriterError && throwable != null) {
            throwable.printStackTrace(fileWriter);
            fileWriter.flush();
        }
    }
    public void debug(String message, Object... args) {
        log(JLogLevel.DEBUG, message, args);
    }

    public void debug(String message, Throwable throwable, Object... args) {
        log(JLogLevel.DEBUG, message, throwable, args);
    }

    public void info(String message, Object... args) {
        log(JLogLevel.INFO, message, args);
    }

    public void info(String message, Throwable throwable, Object... args) {
        log(JLogLevel.INFO, message, throwable, args);
    }

    public void warn(String message, Object... args) {
        log(JLogLevel.WARN, message, args);
    }

    public void warn(String message, Throwable throwable, Object... args) {
        log(JLogLevel.WARN, message, throwable, args);
    }

    public void error(String message, Object... args) {
        log(JLogLevel.ERROR, message, args);
    }

    public void error(String message, Throwable throwable, Object... args) {
        log(JLogLevel.ERROR, message, throwable, args);
    }
}