package org.paohaijiao.jstark.cache.impl;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.paohaijiao.jstark.cache.Cache;
import org.paohaijiao.jstark.cache.CacheManager;
import org.paohaijiao.jstark.cache.bean.CacheOperation;
import org.paohaijiao.jstark.cache.bean.CacheableOperation;

import java.lang.reflect.Method;
import java.util.Collection;

public abstract  class CacheAspectSupport {
    private CacheManager cacheManager;
    private CacheOperationInvoker errorHandler;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setErrorHandler(CacheOperationInvoker errorHandler) {
        this.errorHandler = errorHandler;
    }

    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) throws Exception {
        // 1. 解析缓存注解
        Collection<CacheOperation> operations = parseCacheAnnotations(method);

        if (operations.isEmpty()) {
            return invoker.invoke();
        }

        // 2. 处理缓存逻辑
        try {
            return execute(invoker, method, args, target, operations);
        } catch (Exception ex) {
            if (errorHandler != null) {
                return errorHandler.handle(ex);
            }
            throw ex;
        }
    }

    private Object execute(CacheOperationInvoker invoker, Method method, Object[] args,
                           Object target, Collection<CacheOperation> operations) throws Exception {
        CacheOperation operation = operations.iterator().next();
        if (operation instanceof CacheableOperation) {
            CacheableOperation cacheableOp = (CacheableOperation) operation;
            Cache cache = cacheManager.getCache(cacheableOp.getCacheName());
            if (cache == null) {
                return invoker.invoke();
            }
            Object key = generateKey(method, args, cacheableOp.getKey());
            Cache.ValueWrapper cachedValue = cache.get(key);
            if (cachedValue != null) {
                return cachedValue.get();
            }
            // NOT CACHE RETRIVE
            Object result = invoker.invoke();
            if (result != null || cacheableOp.isAllowNullValues()) {
                cache.put(key, result);
            }
            return result;
        }


        return invoker.invoke();
    }

    protected abstract Collection<CacheOperation> parseCacheAnnotations(Method method);

    protected Object generateKey(Method method, Object[] args, String keyExpression) {
        if (StringUtils.isNotEmpty(keyExpression)) {
            return "customKey";
        }
        return  "fixKey";
        //return new SimpleKey(method.getName(), args);
    }

    public interface CacheOperationInvoker {
        Object invoke() throws Exception;
        Object handle(Exception ex);
    }


}
