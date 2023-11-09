package com.vrv.rule.ruleInfo.assetRisk;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.vrv.rule.util.ArrayUtil;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.AssetRiskOutPutVO;
import com.vrv.rule.vo.AssetRiskVO;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class AssetGuidRiskByRedis extends RichAsyncFunction<Tuple2<Boolean,AssetRiskOutPutVO>,AssetRiskVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//获取连接池的配置对象
    private JedisPoolConfig config = null;
    private static final  String KEY = "assetIpAndIds";
    //获取连接池
    JedisPool jedisPool = null;
    //获取核心对象
    Jedis jedis = null;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(AssetGuidRiskByRedis.class);

    
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        config = new JedisPoolConfig();
        String redisPwd =  YmlUtil.getValue("application.yml", "REDIS_PWD").toString();
        String redisUrl =  YmlUtil.getValue("application.yml", "REDIS_HOST").toString();
        String redisPort =  YmlUtil.getValue("application.yml", "REDIS_PORT").toString();
        Integer port = Integer.valueOf(redisPort);
        jedisPool = new JedisPool(config, redisUrl, port, TIMEOUT, redisPwd, 1);
        jedis = jedisPool.getResource();
    }
	
	@Override
	public void asyncInvoke(Tuple2<Boolean,AssetRiskOutPutVO> input, ResultFuture<AssetRiskVO> resultFuture) throws Exception {
		CompletableFuture.supplyAsync(new Supplier<AssetRiskVO>() {
            @Override
            public AssetRiskVO get() {
            	  AssetRiskVO assetRiskVO = new AssetRiskVO();
            	  AssetRiskOutPutVO assetRiskOutPutVO = input.f1;
            	  Gson gson = new Gson();
            	  logger.info("assetRiskOutPutVO 序列化以后得出的数据：{}", gson.toJson(assetRiskOutPutVO));
            	  assetRiskVO.setIp(assetRiskOutPutVO.getIp());
            	  assetRiskVO.setWeight(assetRiskOutPutVO.getWeight());
            	  assetRiskVO.setNum(assetRiskOutPutVO.getNum());
            	  assetRiskVO.setStartTime(new Date(assetRiskOutPutVO.getStartTime().getTime()+8*60*60*1000));
            	  assetRiskVO.setEndTime(new Date());
            	  String assetGuidss = jedis.hget(KEY, assetRiskOutPutVO.getIp());
            	  if(assetGuidss!=null){
//           		  Gson gson = new Gson();
//               	  Object fromJson = gson.fromJson(assetGuidss, Object.class);
//            		  String[] arr = list.toArray(new String[list.size()]);
//            		  String assetGuids = ArrayUtil.join(arr, ",");
                      logger.info(assetRiskOutPutVO.getIp()+"资产guid:{}", assetGuidss);
                      assetRiskVO.setAssetguids(assetGuidss);
            	  }else {
            		  logger.info(assetRiskOutPutVO.getIp()+"没有关联对应的资产"); 
            	  }
                return assetRiskVO;
            }
        }).thenAccept((AssetRiskVO dbResult) -> {
            // 设置请求完成时的回调: 将结果传递给 collector
            resultFuture.complete(Collections.singleton(dbResult));
        });		
	}
	
	@Override
    public void timeout(Tuple2<Boolean,AssetRiskOutPutVO> input, ResultFuture<AssetRiskVO> resultFuture) throws Exception {
		
    }
	
    @Override
    public void close() throws Exception {
        super.close();
    }

}
