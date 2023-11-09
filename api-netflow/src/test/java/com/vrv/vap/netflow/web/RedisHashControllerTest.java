package com.vrv.vap.netflow.web;

import com.vrv.vap.netflow.service.impl.NetflowBaseDataServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wh1107066
 * @date 2023/9/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RedisHashControllerTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getRedisHashValueData() throws Exception {

        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        LettuceConnectionFactory lettuceConnectionFactory = ((LettuceConnectionFactory)connectionFactory);
        String hostName = lettuceConnectionFactory.getHostName();
        System.out.println("------hostName: " + hostName);

        Set keys = redisTemplate.opsForHash().keys("_BASEINFO:ASSET:IP");
        redisTemplate.opsForValue().get("aa");

        Map assetIpJson = redisTemplate.opsForHash().entries(NetflowBaseDataServiceImpl.CACHE_ASSET_IP_KEY);

        System.out.println(assetIpJson);
        TimeUnit.MILLISECONDS.sleep(6000000);
        System.out.println("------");
    }


}