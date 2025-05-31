package org.paohaijiao.jstark.datasource.impl;

import org.paohaijiao.jstark.bean.JDataSourceBean;
import org.paohaijiao.jstark.datasource.JBaseDataSource;

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
