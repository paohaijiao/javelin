package com.paohaijiao.javelin.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface JPoolDataSource {

    public Connection getConnection() throws SQLException;

}
