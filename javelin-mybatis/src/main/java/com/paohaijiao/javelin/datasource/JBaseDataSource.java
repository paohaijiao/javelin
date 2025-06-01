package com.paohaijiao.javelin.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.paohaijiao.javelin.bean.JDataSourceBean;
import com.paohaijiao.javelin.bean.JDruid;

import javax.sql.DataSource;

public abstract class JBaseDataSource implements JDataSource {
    protected JDataSourceBean ds;
    @Override
    public String getUrl(JDataSourceBean ds){
        return ds.getUrl();
    }
    @Override
    public String getDriver(JDataSourceBean ds) {
        return ds.getDriver();
    }

    @Override
    public String getUserName(JDataSourceBean ds) {
        return ds.getUsername();
    }

    @Override
    public String getPassword(JDataSourceBean ds) {
        return ds.getPassword();
    }
    @Override
    public DataSource getDataSource(){
        try{
            Class.forName(this.getDriver(ds));
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(this.getUrl(ds));
            dataSource.setDriverClassName(this.getDriver(ds));
            dataSource.setUsername(this.getUserName(ds));
            dataSource.setPassword(this.getPassword(ds));
            JDruid druid=ds.getDruid();
            if(null!=druid){
                dataSource.setInitialSize(druid.getInitialSize());
            }
            return dataSource;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

}
