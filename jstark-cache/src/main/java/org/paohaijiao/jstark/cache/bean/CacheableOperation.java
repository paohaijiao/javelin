package org.paohaijiao.jstark.cache.bean;

import lombok.Data;

@Data
public class CacheableOperation  extends CacheOperation{
    private final boolean allowNullValues;

    public CacheableOperation(String cacheName, String key, String condition, String unless, boolean allowNullValues) {
        super(cacheName, key, condition, unless);
        this.allowNullValues = allowNullValues;
    }
}
