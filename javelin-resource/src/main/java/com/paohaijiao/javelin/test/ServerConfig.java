package com.paohaijiao.javelin.test;

import lombok.Data;

public class ServerConfig {
    private int port;
    private String name;
    private boolean enabled;

    public ServerConfig() {}

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // 其他属性的 getter 和 setter...
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
