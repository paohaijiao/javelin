//package org.paohaijiao.jstark.cache;
//
//import org.paohaijiao.jstark.cache.anno.CacheEvict;
//import org.paohaijiao.jstark.cache.anno.CachePut;
//import org.paohaijiao.jstark.cache.anno.Cacheable;
//import org.paohaijiao.jstark.cache.anno.Caching;
//
//public class CacheDemoService {
//    @Cacheable(value = "users", key = "#id")
//    public User getUserById(String id) {
//        // 模拟数据库查询
//        System.out.println("Querying database for user: " + id);
//        return new User(id, "User " + id);
//    }
//
//    @CachePut(value = "users", key = "#user.id")
//    public User updateUser(User user) {
//        // 模拟数据库更新
//        System.out.println("Updating user in database: " + user.getId());
//        return user;
//    }
//
//    @CacheEvict(value = "users", key = "#id")
//    public void deleteUser(String id) {
//        // 模拟数据库删除
//        System.out.println("Deleting user from database: " + id);
//    }
//
//    @Caching(
//            evict = {
//                    @CacheEvict(value = "users", allEntries = true),
//                    @CacheEvict(value = "orders", allEntries = true)
//            }
//    )
//    public void resetAllCaches() {
//        System.out.println("Resetting all caches");
//    }
//}
