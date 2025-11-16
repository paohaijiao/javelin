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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JConsole {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_DEBUG = "\u001B[36m"; // light green
    private static final String ANSI_INFO = "\u001B[32m";  // green
    private static final String ANSI_WARN = "\u001B[33m";  // yellow
    private static final String ANSI_ERROR = "\u001B[31m"; // red

    private boolean showTimestamp;

    private DateTimeFormatter timestampFormatter;

    public JConsole(boolean showTimestamp) {
        this.showTimestamp = showTimestamp;
        this.timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public JConsole() {
        this(true);
    }

    public static void main(String[] args) {
        JConsole console = new JConsole();
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

    private String getTimestamp() {
        return LocalDateTime.now().format(timestampFormatter);
    }

    public void log(JLogLevel level, String message) {
        String timestamp = showTimestamp ? "[" + getTimestamp() + "] " : "";
        String color = getColorCode(level);
        String levelName = level.name();
        System.out.println(timestamp + color + "[" + levelName + "] " + message + ANSI_RESET);
    }

    public void log(JLogLevel level, String message, Throwable throwable) {
        log(level, message);
        throwable.printStackTrace(System.out);
    }

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

    public void debug(String message) {
        log(JLogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(JLogLevel.INFO, message);
    }

    public void warn(String message) {
        log(JLogLevel.WARN, message);
    }

    public void error(String message) {
        log(JLogLevel.ERROR, message);
    }

    public void error(String message, Throwable throwable) {
        log(JLogLevel.ERROR, message, throwable);
    }
}
