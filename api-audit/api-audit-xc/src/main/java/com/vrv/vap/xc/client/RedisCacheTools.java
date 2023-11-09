package com.vrv.vap.xc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存服务.
 */
@Component
public class RedisCacheTools {

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 过期时间 60秒
     */
    private static final int EXPIRE_TIME = 60;

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheTools.class);

    /**
     * 增加缓存设置清理时间
     *
     * @String key
     * @String value
     * @int minutesNum 分钟
     */
    public void addExpireBySession(String key, String value, int minutesNum) {
        redisTemplate.opsForValue().set(key, value, minutesNum, TimeUnit.MINUTES);
    }

    /**
     * 添加缓存，并设置默认过期时间，未手动删除的情况自动清理
     *
     * @String key
     * @String value
     */
    public void addExpire(final String key, String value) {
        redisTemplate.opsForValue().set(key, value, EXPIRE_TIME, TimeUnit.SECONDS);
        LOG.info("add expire cache key:" + key);
    }

    /**
     * 添加缓存
     *
     * @String key
     * @String value
     */
    public void add(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        LOG.info("add cache key:" + key);
    }

    /**
     * 查询
     *
     * @String key
     */
    public String get(String key) {
        LOG.info("get cache key:" + key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除
     *
     * @String key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
        LOG.info("delete cache key:" + key);
    }

    /**
     * @String key
     * @List listJsonStr
     */
    public void addList(String key, List<String> listJsonStr) {
        redisTemplate.opsForList().leftPushAll(key, listJsonStr);


    }

    /**
     * @String key
     */
    public void getList(String key) {
        redisTemplate.opsForList().leftPop(key);


    }

    /**
     * @String key
     */
    public void saveOrUpdateHashMap(String key, Map<?, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * @String key
     */
    public Object getHashMapField(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public Set<Object> gethHashMapKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * @String key
     * @String field
     * @String value
     */
    public void setHashMapField(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public void deleteHashMapField(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 获取map缓存
     *
     * @String key
     * @return
     */
    public Map<String, String> getHashMap(String key) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(key);
        Map<String, String> map = boundHashOperations.entries();
        return map;
    }
}
