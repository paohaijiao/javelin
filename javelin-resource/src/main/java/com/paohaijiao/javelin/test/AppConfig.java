package com.paohaijiao.javelin.test;

import lombok.Data;

@Data
public class AppConfig {
    private ServerConfig server;

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }
}
