package com.github.paohaijiao.enums;

public enum JSQLType {
    MYSQL("MySQL", "MySQL"),
    ORACLE("Oracle", "Oracle"),
    POSTGRESQL("PostgreSQL", "PostgreSQL"),
    SQLSERVER("SQL Server", "SQL Server"),
    H2("H2", "H2"),
    OTHER("Other", "Other");

    private final String code;
    private final String name;

    JSQLType(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
