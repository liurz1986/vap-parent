package com.vrv.vap.line.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
     * @param key
     * @param value
     * @param minutesNum 分钟
     */
    public void addExpireBySession(String key, String value, int minutesNum) {
        redisTemplate.opsForValue().set(key, value, minutesNum, TimeUnit.MINUTES);
    }

    /**
     * 添加缓存，并设置默认过期时间，未手动删除的情况自动清理
     *
     * @param key
     * @param value
     */
    public void addExpire(final String key, String value) {
        redisTemplate.opsForValue().set(key, value, EXPIRE_TIME, TimeUnit.SECONDS);
        LOG.info("add expire cache key:" + key);
    }

    /**
     * 添加缓存
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        LOG.info("add cache key:" + key);
    }

    /**
     * 查询
     *
     * @param key
     */
    public String get(String key) {
        LOG.info("get cache key:" + key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
        LOG.info("delete cache key:" + key);
    }

    /**
     * @param key
     * @param listJsonStr
     */
    public void addList(String key, List<String> listJsonStr) {
        redisTemplate.opsForList().leftPushAll(key, listJsonStr);


    }

    /**
     * @param key
     */
    public void getList(String key) {
        redisTemplate.opsForList().leftPop(key);


    }

    /**
     * @param key
     */
    public void saveOrUpdateHashMap(String key, Map<?, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * @param key
     */
    public Object getHashMapField(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public Set<Object> gethHashMapKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * @param key
     * @param field
     * @param value
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
     * @param key
     * @return
     */
    public Map<String, String> getHashMap(String key) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(key);
        Map<String, String> map = boundHashOperations.entries();
        return map;
    }

    public Map<String, String> getHashMapByKeyName(String keyName, String key) {
        Object obj = getHashMapField(keyName, key);
        if (obj != null) {
            ObjectMapper mapper = new ObjectMapper();
            String strCache = obj.toString();
            try {
                return mapper.readValue(strCache, Map.class);
            } catch (IOException e) {
                LOG.error("getHashMapByKeyName", e);
            }
        }
        return null;
    }


}
