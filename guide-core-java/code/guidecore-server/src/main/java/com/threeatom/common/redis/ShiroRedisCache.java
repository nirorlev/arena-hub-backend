//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

public class ShiroRedisCache implements Cache<Object, Object> {
    private RedisTemplate<String, Object> redisTemplate;
    private String prefix;

    public ShiroRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.prefix = "SHIRO";
        this.redisTemplate = redisTemplate;
    }

    public ShiroRedisCache(RedisTemplate<String, Object> redisTemplate, String prefix) {
        this(redisTemplate);
        if (!StringUtils.isEmpty(prefix)) {
            this.prefix = prefix;
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void clear() throws CacheException {
        this.redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    public Object get(Object arg0) throws CacheException {
        return arg0 == null ? null : this.redisTemplate.opsForValue().get(this.getPrefix() + arg0);
    }

    public Set<Object> keys() {
        Set<String> set = this.redisTemplate.keys(this.getPrefix() + "*");
        Set<Object> objSet = new HashSet();
        Iterator var3 = set.iterator();

        while (var3.hasNext()) {
            String k = (String) var3.next();
            objSet.add(k);
        }

        return objSet;
    }

    public Object put(Object k, Object v) throws CacheException {
        if (k != null && v != null) {
            this.redisTemplate.opsForValue().set(this.getPrefix() + k, v);
            return v;
        } else {
            return null;
        }
    }

    public Object remove(Object k) throws CacheException {
        if (k == null) {
            return null;
        } else {
            Object v = this.redisTemplate.opsForValue().get(this.getPrefix() + k);
            this.redisTemplate.delete(this.getPrefix() + k);
            return v;
        }
    }

    public int size() {
        return this.redisTemplate.getConnectionFactory().getConnection().dbSize().intValue();
    }

    public Collection<Object> values() {
        Set<Object> keys = this.keys();
        List<Object> values = new ArrayList();
        Iterator var3 = keys.iterator();

        while (var3.hasNext()) {
            Object k = var3.next();
            values.add(this.get(k));
        }

        return values;
    }
}
