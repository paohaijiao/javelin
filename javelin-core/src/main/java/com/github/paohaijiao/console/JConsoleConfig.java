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

/**
 * packageName com.github.paohaijiao.console
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/1
 */

import com.github.paohaijiao.enums.JLogLevel;

public class JConsoleConfig {

    private boolean enabled = true;

    private JLogLevel level = JLogLevel.INFO;

    private boolean showTimestamp = true;

    private String timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    private boolean consoleOutput = true;

    private boolean enableColor = true;

    private String logFilePath = null;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JLogLevel getLevel() {
        return level;
    }

    public void setLevel(JLogLevel level) {
        this.level = level;
    }

    public boolean isShowTimestamp() {
        return showTimestamp;
    }

    public void setShowTimestamp(boolean showTimestamp) {
        this.showTimestamp = showTimestamp;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public boolean isConsoleOutput() {
        return consoleOutput;
    }

    public void setConsoleOutput(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    public boolean isEnableColor() {
        return enableColor;
    }

    /**
     * 设置是否允许输出 ANSI 颜色码。
     *
     * <p>该开关表示"允许着色"：置为 false 时一定不输出颜色码；
     * 置为 true 时还需要终端具备 ANSI 能力，容器 / CI / 输出重定向等非交互式场景下不会着色，
     * 可通过 {@code -Djquick.color=true} 强制开启。</p>
     *
     * @param enableColor true 表示允许着色
     */
    public void setEnableColor(boolean enableColor) {
        this.enableColor = enableColor;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public String toString() {
        return "JConsoleConfig{" + "enabled=" + enabled + ", level=" + level + ", showTimestamp=" + showTimestamp + ", timestampFormat='" + timestampFormat + '\'' + ", consoleOutput=" + consoleOutput + ", enableColor=" + enableColor + ", logFilePath='" + logFilePath + '\'' + '}';
    }
}
