//package org.paohaijiao.jstark.cache;
//
//import org.paohaijiao.jstark.cache.impl.ConcurrentMapCacheManager;
//
//public class CacheConfig {
//    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager("users", "orders", "products");
//    }
//
//    @Bean
//    public CacheDemoService cacheDemoService() {
//        return new CacheDemoService();
//    }
//
//    @Bean
//    public CacheInterceptor cacheInterceptor() {
//        CacheInterceptor interceptor = new CacheInterceptor();
//        interceptor.setCacheManager(cacheManager());
//        return interceptor;
//    }
//}
