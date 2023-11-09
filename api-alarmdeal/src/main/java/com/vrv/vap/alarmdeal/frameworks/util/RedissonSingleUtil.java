package com.vrv.vap.alarmdeal.frameworks.util;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.ws.soap.Addressing;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wudi
 * @date 2022/11/21 18:46
 */
@Component
public class RedissonSingleUtil {


    @Autowired
    private RedissonClient redissonClient;


    public void set(String key,String value){
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value);
    }

    public String get(String key){
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        return bucket.get();
    }

    public boolean exists(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        Boolean booleanRFuture = bucket.isExists();
        return booleanRFuture;
    }

    public void deleteByPrex(String key){
        RKeys keys = redissonClient.getKeys();
        long count = keys.deleteByPattern(key+"*");
    }

    public List<String> findList(String name) {
        RKeys keys = redissonClient.getKeys();

        Iterable<String> keysByPattern = keys.getKeysByPattern(name+"*",100000);

        List<String> result = new ArrayList<>();
        for (String s : keysByPattern) {
            result.add(s);
        }

        return result;
    }

}
