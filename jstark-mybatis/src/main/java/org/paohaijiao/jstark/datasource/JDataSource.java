package org.paohaijiao.jstark.datasource;

import org.paohaijiao.jstark.bean.JDataSourceBean;

import javax.sql.DataSource;

public interface JDataSource {
    /**
     *  get Database Driver Class
     * @return
     */
    String getDriver(JDataSourceBean ds);

    /**
     * get Database Url
     * @return
     */
    String getUrl(JDataSourceBean ds);

    /**
     *get Database UserName
     * @return
     */
    String getUserName(JDataSourceBean ds);

    /**
     *get Database Password
     * @return
     */
    String getPassword(JDataSourceBean ds);


    /**
     *  getDataSource
     * @return
     */
     DataSource getDataSource();

}
