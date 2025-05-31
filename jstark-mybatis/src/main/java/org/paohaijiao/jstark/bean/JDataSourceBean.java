package org.paohaijiao.jstark.bean;

import lombok.Data;

@Data
public class JDataSourceBean<T extends JDataSourceExtract> {
    /**
     * driver
     */
    private String driver;
    /**
     *
     */
    private String ip;
    /**
     *
     */
    private String port;

    private String schema;

    private String database;

    private String url;
    /**
     * username
     */
    private String username;
    /**
     * password
     */
    private String password;

    private T extract;

    private JDruid druid;


}
