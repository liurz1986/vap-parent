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
public class JredisSingleConnectionUtil {

	
	//获取连接池的配置对象
    private JedisPoolConfig config = null;
    //获取连接池
    JedisPool jedisPool = null;
    //获取核心对象
    Jedis jedis = null;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。
    private static int TIMEOUT = 10000;
    
    private volatile static JredisSingleConnectionUtil jredisSingleConnectionUtil;
    
    public JredisSingleConnectionUtil() {}
    
    public static JredisSingleConnectionUtil getInstance() {
    	if(jredisSingleConnectionUtil==null) {
    		synchronized (JredisSingleConnectionUtil.class) {
				if(jredisSingleConnectionUtil==null) {
					jredisSingleConnectionUtil = new JredisSingleConnectionUtil();
					jredisSingleConnectionUtil.initJedisInfo();
				}
			}
    	}
		return jredisSingleConnectionUtil;
    }

    /**
     * 初始化Jedis相关新下    
     */
	private synchronized void initJedisInfo() {
		if(jedisPool==null || jedis==null) {
			config = new JedisPoolConfig();
			Object maxTotal = YmlUtil.getValue("application.yml", "max-active");
			Object maxWaitMillis = YmlUtil.getValue("application.yml", "max-wait");
			Object maxIdle = YmlUtil.getValue("application.yml", "max-idle");
			Object minIdle = YmlUtil.getValue("application.yml", "min-idle");
			config.setMaxTotal(Integer.valueOf(maxTotal== null ? Integer.valueOf(String.valueOf(maxTotal)):50));
			config.setMaxWaitMillis(Integer.valueOf(maxWaitMillis== null ? Integer.valueOf(String.valueOf(maxWaitMillis)):10000));
			config.setMaxIdle(Integer.valueOf(maxIdle== null ? Integer.valueOf(String.valueOf(maxIdle)):50));
			config.setMinIdle(Integer.valueOf(minIdle== null ? Integer.valueOf(String.valueOf(minIdle)):0));
//			String redisPwd =  YmlUtil.getValue("application.yml", "REDIS_PWD").toString();
			String redisPwd = JasyptUtil.decryptPassword("REDIS_PWD");
			String redisUrl =  YmlUtil.getValue("application.yml", "REDIS_HOST").toString();
			String redisPort =  YmlUtil.getValue("application.yml", "REDIS_PORT").toString();
			Integer port = Integer.valueOf(redisPort);
			jedisPool = new JedisPool(config, redisUrl, port, TIMEOUT, redisPwd, 1);
			jedis = jedisPool.getResource();			
		}
	}
	
	
	/**
	 * 保存缓存
	 * @param key
	 * @param value
	 */
	public void set(String key,String value) {
		jedis.set(key, value);
	}
	
	/**
	 * 根据key获得对应的value
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String value = null;
		try {
			value = jedis.get(key);
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			if(jedis != null){
				jedis.close();
			}
		}

	    return value;
	}
	
	/**
	 * g根据key获得所有的Map
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(String key) {
		Map<String, String> hgetAll = jedis.hgetAll(key);
		return hgetAll;
	}
	
	/**
	 * 获得对应某一个key hashmap当中对应的hashkey的值
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public String hget(String key,String hashKey) {
		String value = jedis.hget(key, hashKey);
		return value;
	}
	
	/**
	 * 是否存在该key
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public boolean hasKey(String key,String hashKey) {
		Boolean hexists = jedis.hexists(key, hashKey);
		return hexists;
	}
	
	/**
	 * 获得List的全部缓存
	 * @param key
	 * @return
	 */
	public List<?> lRange(String key){
		if(!jedis.exists(key)) {
			return null;
		}
		byte[] data = jedis.getrange(key.getBytes(), 0, -1);
		//byte[] data = jedis.get(key.getBytes());
		return SerializeUtil.unserializeList(data);
	}
	
	/**
	 * 获得筛选条件的List全部缓存
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<?> lRange(String key,long start,long end){
		if(!jedis.exists(key)) {
			return null;
		}
		byte[] data = jedis.getrange(key.getBytes(), start, end);
		//byte[] data = jedis.get(key.getBytes());
		return SerializeUtil.unserializeList(data);
	}
	
	/**
	 * 左边插入对应的数据
	 * @param key
	 * @param list
	 */
	public void set(String key,List<?> list){
		try {
			jedis.set(key.getBytes(), SerializeUtil.serializeList(list));
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if(jedis != null){
				jedis.close();
			}
		}

		//jedis.lpush(key.getBytes(), SerializeUtil.serializeList(list));
	}

	public boolean exists(String key) {
		boolean flag = true;
		try {
			flag = jedis.exists(key).booleanValue();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
//		}finally {
//			if(jedis != null){
//				try {
//					jedis.close();
//				} catch (Exception e) {
//					System.out.println("释放jedis资源出错，将要关闭jedis，异常信息：" + e.getMessage());
//					if (jedis != null) {
//						try {
//							// 2. 客户端主动关闭连接
//							jedis.disconnect();
//						} catch (Exception e1) {
//							System.out.println("disconnect jedis connection fail: " + e.getMessage());
//						}
//					}
//				}
//			}
//		}
		return flag;
	}
}
