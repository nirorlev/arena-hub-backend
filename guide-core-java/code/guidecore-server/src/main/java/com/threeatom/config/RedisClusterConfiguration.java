package com.threeatom.config;

import java.time.Duration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisClusterConfiguration extends CachingConfigurerSupport{
	@Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionfactory){
           RedisTemplate<String,Object> redisTemplate=new RedisTemplate<String,Object>();
           redisTemplate.setConnectionFactory(redisConnectionfactory);
           
           Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
           
           ObjectMapper mapper = new ObjectMapper();
           mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
           mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
           serializer.setObjectMapper(mapper);

           
           redisTemplate.setKeySerializer(new StringRedisSerializer());
           redisTemplate.setValueSerializer(serializer);
           redisTemplate.afterPropertiesSet();
           
           //清空当前缓存
           this.flushallRedis(redisTemplate);
           return redisTemplate;
    }
	
	private void flushallRedis(RedisTemplate<String,Object> redisTemplate) {
		
		redisTemplate.delete(redisTemplate.keys("*"));
		
		System.out.println("Redis_____flush");
	}
	
	@Bean
    //如使用注解的话需要配置cacheManager
	RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        //设置默认超过期时间是5小时
        defaultCacheConfig.entryTtl(Duration.ofHours(5));
        //初始化RedisCacheManager
        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
        return cacheManager;
    }

}
