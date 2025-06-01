package com.paohaijiao.javelin.datasource.impl;

import com.paohaijiao.javelin.bean.JDataSourceBean;
import com.paohaijiao.javelin.datasource.JBaseDataSource;

public class JMySqlBaseDataSource extends JBaseDataSource {
    public JMySqlBaseDataSource(String driver, String url,
                                String username, String password) {
        JDataSourceBean dataSourceBean = new JDataSourceBean();
        dataSourceBean.setUsername(username);
        dataSourceBean.setPassword(password);
        dataSourceBean.setDriver(driver);
        dataSourceBean.setUrl(url);
    }

}
