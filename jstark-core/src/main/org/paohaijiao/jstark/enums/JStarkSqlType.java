package org.paohaijiao.jstark.enums;

public enum JStarkSqlType {
    MYSQL("MySQL","MySQL"),
    ORACLE("Oracle","Oracle"),
    POSTGRESQL("PostgreSQL","PostgreSQL"),
    SQLSERVER("SQL Server","SQL Server"),
    H2("H2","H2"),
    OTHER("Other","Other");

    private final String code;
    private final String name;

    JStarkSqlType(String code,String name) {
        this.code = code;
        this.name = name;
    }

}
