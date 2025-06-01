package com.paohaijiao.javelin.console;
import com.paohaijiao.javelin.enums.JStarkLogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Console {
    // ANSI color Code
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_DEBUG = "\u001B[36m"; // light green
    private static final String ANSI_INFO = "\u001B[32m";  // green
    private static final String ANSI_WARN = "\u001B[33m";  // yellow
    private static final String ANSI_ERROR = "\u001B[31m"; // red

    private boolean showTimestamp;
    private DateTimeFormatter timestampFormatter;

    public Console(boolean showTimestamp) {
        this.showTimestamp = showTimestamp;
        this.timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public Console() {
        this(true);
    }

    private String getTimestamp() {
        return LocalDateTime.now().format(timestampFormatter);
    }

    public void log(JStarkLogLevel level, String message) {
        String timestamp = showTimestamp ? "[" + getTimestamp() + "] " : "";
        String color = getColorCode(level);
        String levelName = level.name();

        System.out.println(timestamp + color + "[" + levelName + "] " + message + ANSI_RESET);
    }

    public void log(JStarkLogLevel level, String message, Throwable throwable) {
        log(level, message);
        throwable.printStackTrace(System.out);
    }

    private String getColorCode(JStarkLogLevel level) {
        switch (level) {
            case DEBUG: return ANSI_DEBUG;
            case INFO: return ANSI_INFO;
            case WARN: return ANSI_WARN;
            case ERROR: return ANSI_ERROR;
            default: return ANSI_RESET;
        }
    }

    public void debug(String message) {
        log(JStarkLogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(JStarkLogLevel.INFO, message);
    }

    public void warn(String message) {
        log(JStarkLogLevel.WARN, message);
    }

    public void error(String message) {
        log(JStarkLogLevel.ERROR, message);
    }

    public void error(String message, Throwable throwable) {
        log(JStarkLogLevel.ERROR, message, throwable);
    }

    public static void main(String[] args) {
        Console console = new Console();
        console.debug("hello");
        console.info("hello");
        console.warn("hello");
        console.error("hello");

        try {
            int result = 10 / 0;
        } catch (Exception e) {
            console.error("error", e);
        }
    }
}
