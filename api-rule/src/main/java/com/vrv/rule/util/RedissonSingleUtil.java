package com.vrv.rule.util;

import com.vrv.rule.ruleInfo.exchangeType.dimension.FilterDimensionTable;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author wudi
 * @date 2022/11/21 18:46
 */
public class RedissonSingleUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedissonSingleUtil.class);
    private static  RedissonClient redissonClient;

    private volatile  static RedissonSingleUtil redissonSingleUtil;

    private RedissonSingleUtil(){

    }

    public static RedissonSingleUtil   getInstance(){
        if(redissonClient==null){
            synchronized (RedissonSingleUtil.class){
                if(redissonClient==null){
                    redissonSingleUtil = new RedissonSingleUtil();
                    initRedisson();
                }
            }
        }
        return redissonSingleUtil;
    }


    private static void initRedisson(){
        String redisPwd = JasyptUtil.decryptPassword("REDIS_PWD");
        String redisUrl =  YmlUtil.getValue("application.yml", "REDIS_HOST").toString();
        String redisPort =  YmlUtil.getValue("application.yml", "REDIS_PORT").toString();
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisUrl+":"+redisPort).setPassword(redisPwd).setDatabase(1);
        redissonClient = Redisson.create(config);
    }


    public static void set(String key,String value){
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value);
    }

    public static String get(String key){
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        String value = bucket.get();
        return value;
    }

    public static boolean exists(String key){
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        boolean booleanRFuture = bucket.isExists();
        return booleanRFuture;
    }

    public static boolean delete(String key){
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        boolean booleanRFuture = bucket.delete();
        return booleanRFuture;
    }
    public void deleteByPrex(String key){
        RKeys keys = redissonClient.getKeys();
        System.out.println("---------"+keys);
        long count = keys.deleteByPattern(key+"*");
    }
}
