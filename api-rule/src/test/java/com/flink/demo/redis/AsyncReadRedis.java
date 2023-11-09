package com.flink.demo.redis;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class AsyncReadRedis extends RichAsyncFunction<String,Set<String>> {

	//获取连接池的配置对象
    private JedisPoolConfig config = null;
    //获取连接池
    JedisPool jedisPool = null;
    //获取核心对象
    Jedis jedis = null;
    //Redis服务器IP
    private static String ADDR = "192.168.89.131";
    //Redis的端口号
    private static int PORT = 6379;
    //访问密码
    private static String AUTH = "XXXXXX";
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(AsyncReadRedis.class);

    
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        config = new JedisPoolConfig();
        jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, null, 0);
        jedis = jedisPool.getResource();
 
    }
	
	@Override
	public void asyncInvoke(String input, ResultFuture<Set<String>> resultFuture) throws Exception {
		CompletableFuture.supplyAsync(new Supplier<Set<String>>() {
            @Override
            public Set<String> get() {
            	
            	  Set<String> hkeys = jedis.hkeys("asdw");
//                String[] split = input.split(",");
//                String name = split[1];
//                logger.info("名称:{}", name);
//                String s = jedis.hget("asdw", name);
                logger.info("redis值:{}", hkeys);
                return hkeys;
            }
        }).thenAccept((Set<String> dbResult) -> {
            // 设置请求完成时的回调: 将结果传递给 collector
            resultFuture.complete(Collections.singleton(dbResult));
        });		
	}
	
	@Override
    public void timeout(String input, ResultFuture resultFuture) throws Exception {
    }
    @Override
    public void close() throws Exception {
        super.close();
    }

}
