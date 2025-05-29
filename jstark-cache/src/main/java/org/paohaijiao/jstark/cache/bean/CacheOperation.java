package org.paohaijiao.jstark.cache.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
public class CacheOperation {
    private  String cacheName;
    private  String key;
    private  String condition;
    private  String unless;

    protected CacheOperation(String cacheName, String key, String condition, String unless) {
        this.cacheName = cacheName;
        this.key = key;
        this.condition = condition;
        this.unless = unless;
    }
    public CacheOperation(){
    }
}
