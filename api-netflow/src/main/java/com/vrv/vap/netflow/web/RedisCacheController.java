package com.vrv.vap.netflow.web;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.netflow.service.NetflowBaseDataService;
import com.vrv.vap.netflow.service.impl.NetflowBaseDataServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * @author wh1107066
 */

@RestController("/redis")
@Api(value = "redis缓存接口")
public class RedisCacheController {
    private final Logger logger = LoggerFactory.getLogger(RedisCacheController.class);
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NetflowBaseDataService netflowBaseDataService;

    /**
     * 获取 BaseInfo 的key
     *
     * @return 获取redis中所有以_BASEINFO: 开头的key的值
     */
    @GetMapping("/redisBaseInfoKeys")
    @ApiOperation("获取所有redis的_BASEINFO开头key键")
    public String getRedisBaseInfoKeys() {
        Set<String> keys = stringRedisTemplate.keys("_BASEINFO:*");
        for (Object key : keys) {
            logger.error("redis中存在的key循环: {}", key);
        }
        return "success";
    }

    /**
     * 获取redis中的hash值
     *
     * @param key key  such as like this   --->  _BASEINFO:BASE_PERSON_ZJG:ALL
     * @return String
     */

    @GetMapping("/getStringLocalCacheByKey/{key}")
    @ApiOperation("获取redis的value为string值")
    public String getStringLocalCacheByKey(@PathVariable("key") String key) {
        Assert.notNull(key, "key key不能为空！");
        String value = stringRedisTemplate.opsForValue().get(key);
        logger.error("value的值为： {}", value);

        String s = stringRedisTemplate.opsForValue().get(NetflowBaseDataServiceImpl.CACHE_PERSON_ZJG_KEY);
        logger.error("s: " + s);

        return "success";
    }


    /**
     * 获取redis中的hash值
     *
     * @param key key     such as like this   --->   _BASEINFO:ASSET:IP
     * @return String
     */
    @GetMapping("/getLocalCacheByHashKey/{key}")
    @ApiOperation("获取redis的value为Hash值")
    public String getLocalCacheByHashKey(@PathVariable("key") String key) {
        String jsonString = "success";
        logger.info("getLocalCacheByHashKey参数key: {}", key);
        Assert.notNull(key, "key key不能为空！");
        RedisConnectionFactory connectionFactory = stringRedisTemplate.getConnectionFactory();
        LettuceConnectionFactory lettuceConnectionFactory = ((LettuceConnectionFactory) connectionFactory);
        String hostName = lettuceConnectionFactory.getHostName();
        logger.info("------hostName--------- " + hostName);
        // 从redis中获取数据
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        if (MapUtil.isEmpty(entries)) {
            logger.error("assetIpJson is null");
        } else {
            jsonString = JSONObject.toJSONString(entries);
            logger.error("assetIpJson的值为： {}", jsonString);

        }
        return "success";
    }


    @GetMapping("/getMapKeyValue")
    @ApiOperation("获取redis的静态map值,_BASEINFO:ASSET:IP")
    public String getMapKeyValue() {
        RedisSerializer keySerializer = stringRedisTemplate.getKeySerializer();
        RedisSerializer valueSerializer = stringRedisTemplate.getValueSerializer();
        Set<String> keys = stringRedisTemplate.keys("*");
        for (String key : keys) {
            logger.error("redis中存在的key循环: {}", key);
        }
        String cacheAssetIpKey1 = NetflowBaseDataServiceImpl.CACHE_ASSET_IP_KEY;
        Map<String, Object> localCache = netflowBaseDataService.getLocalCache(cacheAssetIpKey1);
        logger.info("获取缓存key:{} value:{}", cacheAssetIpKey1, JSONObject.toJSONString(localCache));
        return "success";
    }
}
