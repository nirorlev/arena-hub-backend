//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.userdata;

import com.threeatom.common.redis.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisUserInfo<T> {
    private static Logger LOGGER = LoggerFactory.getLogger(RedisUserInfo.class);
    private static final String KEY = "UserInfo";
    @Autowired RedisOperator redisOperator;

    public RedisUserInfo() {}

    public boolean putData(Class<T> t, Integer uid, String key, Object value) {
        String fullKey = this.getFullKey(t) + uid.toString();
        return this.redisOperator.hset(fullKey, key, value);
    }

    public String getDataString(Class<T> clazz, Integer uid, String key) {
        return (String) this.getData(clazz, uid, key);
    }

    private String getFullKey(Class<T> clazz) {
        return "UserInfo:" + clazz.getName() + ":";
    }

    public Object getData(Class<T> clazz, Integer uid, String key) {
        String fullKey = this.getFullKey(clazz) + uid.toString();
        return this.redisOperator.hget(fullKey, key);
    }
}
