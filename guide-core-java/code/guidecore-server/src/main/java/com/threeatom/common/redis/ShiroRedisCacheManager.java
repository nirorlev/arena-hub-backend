//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.redis;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

public class ShiroRedisCacheManager extends AbstractCacheManager {
    private RedisTemplate<String, Object> redisTemplate;

    public ShiroRedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected Cache createCache(String arg0) throws CacheException {
        return new ShiroRedisCache(this.redisTemplate, arg0);
    }
}
