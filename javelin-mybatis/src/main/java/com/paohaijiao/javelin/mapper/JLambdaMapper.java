package com.paohaijiao.javelin.mapper;

import java.io.Serializable;

public interface JLambdaMapper<T>{
    /**
     *
     * @param entity
     * @return
     */
    int insert(T entity);

    /**
     * update
     * @param entity
     * @return
     */
    int updateById(T entity);

    /**
     * deleteById
     * @param id id
     * @return
     */
    int deleteById(Serializable id);

    /**
     * selectById
     * @param id
     * @return
     */
    T selectById(Serializable id);

    /**
     * Lambda query
     * @return
     */
    JLambdaQuery<T> query();

    /**
     * lamdaupte update
     * @return
     */
    JLambdaUpdate<T> update();
}
