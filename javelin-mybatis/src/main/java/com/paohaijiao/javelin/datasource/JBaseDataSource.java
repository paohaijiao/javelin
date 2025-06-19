/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.paohaijiao.javelin.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.paohaijiao.javelin.model.JDataSourceBean;
import com.paohaijiao.javelin.model.JDruid;

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
