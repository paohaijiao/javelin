package org.paohaijiao.jstark.cache;

import java.util.Collection;

public interface CacheManager {

        Cache getCache(String name);

        Collection<String> getCacheNames();
}
