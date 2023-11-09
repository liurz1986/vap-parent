package com.vrv.rule.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;

/**
 * redis单例相关的工具类
 * @author Administrator
 *
 */
public class JredisSingleConnectionNewUtil {


	//获取连接池的配置对象
    private JedisPoolConfig config = null;
    //获取连接池
    //获取核心对象
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。
    private static int TIMEOUT = 10000;

	private static JedisPool jedisPool;
	private static Jedis jedis;

	private static JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

	static {
		// 设置配置
		jedisPoolConfig.setMaxTotal(1024);
		jedisPoolConfig.setMaxIdle(100);
//		jedisPoolConfig.setMaxWaitMillis(100);
		jedisPoolConfig.setTestOnBorrow(true);//jedis 第一次启动时，会报错
		jedisPoolConfig.setTestOnReturn(true);
		// 初始化JedisPool
		String redisPwd = JasyptUtil.decryptPassword("REDIS_PWD");
		String redisUrl =  YmlUtil.getValue("application.yml", "REDIS_HOST").toString();
		String redisPort =  YmlUtil.getValue("application.yml", "REDIS_PORT").toString();
		Integer port = Integer.valueOf(redisPort);
		jedisPool = new JedisPool(jedisPoolConfig, redisUrl, port, TIMEOUT, redisPwd, 1);
		jedis = jedisPool.getResource();
	}

    /**
     * 初始化Jedis相关新下    
     */
//	private synchronized void initJedisInfo() {
//		if(jedisPool==null || jedis==null) {
//			config = new JedisPoolConfig();
//			Object maxTotal = YmlUtil.getValue("application.yml", "max-active");
//			Object maxWaitMillis = YmlUtil.getValue("application.yml", "max-wait");
//			Object maxIdle = YmlUtil.getValue("application.yml", "max-idle");
//			Object minIdle = YmlUtil.getValue("application.yml", "min-idle");
//			config.setMaxTotal(Integer.valueOf(maxTotal== null ? Integer.valueOf(String.valueOf(maxTotal)):50));
//			config.setMaxWaitMillis(Integer.valueOf(maxWaitMillis== null ? Integer.valueOf(String.valueOf(maxWaitMillis)):10000));
//			config.setMaxIdle(Integer.valueOf(maxIdle== null ? Integer.valueOf(String.valueOf(maxIdle)):50));
//			config.setMinIdle(Integer.valueOf(minIdle== null ? Integer.valueOf(String.valueOf(minIdle)):0));
////			String redisPwd =  YmlUtil.getValue("application.yml", "REDIS_PWD").toString();
//			String redisPwd = JasyptUtil.decryptPassword("REDIS_PWD");
//			String redisUrl =  YmlUtil.getValue("application.yml", "REDIS_HOST").toString();
//			String redisPort =  YmlUtil.getValue("application.yml", "REDIS_PORT").toString();
//			Integer port = Integer.valueOf(redisPort);
//			jedisPool = new JedisPool(config, redisUrl, port, TIMEOUT, redisPwd, 1);
//			jedis = jedisPool.getResource();
//		}
//	}

	public static String get(String key) {
//		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.get(key);
//		}
	}
	public static  String set(String key,String value) {
//		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.set(key,value);
//		}
	}

	public static String setex(String key ,int second,String value) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.setex(key,second,value);
		}
	}
	public static Long setnx(String key,String value) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.setnx(key,value);
		}
	}

	public static Long del(String... keys) {
		try(Jedis jedis = jedisPool.getResource()) {
			String[] newKeys = new String[keys.length];
			for(int i = 0 ; i < newKeys.length;i++) {
				newKeys[i] = keys[i];
			}
			return jedis.del(newKeys);
		}
	}

	public static boolean exists(String key) {
//		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.exists(key);
//		}
	}

	public static Long expire(String key,int seconds) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.expire(key,seconds);
		}
	}

	public static Long ttl(String key) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.ttl(key);
		}
	}

	public static Long lpush(String key,String... value) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.lpush(key,value);
		}
	}

	public static String rpop(String key) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.rpop(key);
		}
	}

	public static Long hset(String key,String field,String value){
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.hset(key,field,value);
		}
	}

	public static Long hdel(String key,String...  field) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.hdel(key,field);
		}
	}

	public static String hget(String key,String field) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.hget(key,field);
		}
	}

	public static Map<String,String> hgetAll(String key) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.hgetAll(key);
		}
	}

	public static Long incr(String key) {
		try(Jedis jedis = jedisPool.getResource()) {
			return jedis.incr(key);
		}
	}
	private static void shutDown() {
		try(Jedis jedis = jedisPool.getResource()) {
			try{
				jedis.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
