package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.reader.impl.JResourcePropertiesReader;
import org.paohaijiao.jstark.reader.impl.JResourceYamlReader;

import java.io.IOException;

public class JResourceMain {
    public static void main(String[] args) {
        try {
            JResourceYamlReader<AppConfig> yamlParser = new JResourceYamlReader<AppConfig>();
            AppConfig yamlConfig = yamlParser.parse("application.yml", AppConfig.class);

            JResourceYamlReader<ServerConfig> yamlSub= new JResourceYamlReader<ServerConfig>();
            ServerConfig serverConfig = yamlSub.parse("application.yml", ServerConfig.class,"server");

            System.out.println("Server Port: " + serverConfig.getPort());
            System.out.println("YAML config: " + yamlConfig);
            JResourcePropertiesReader<AppConfig> propsParser = new JResourcePropertiesReader<>();
            AppConfig propsConfig = propsParser.parse("application.properties", AppConfig.class);

            JResourcePropertiesReader<ServerConfig> subpropsParser = new JResourcePropertiesReader<>();
            ServerConfig serverConfig1 = subpropsParser.parse("application.properties", ServerConfig.class,"server");
            System.out.println("Properties config: " + propsConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
