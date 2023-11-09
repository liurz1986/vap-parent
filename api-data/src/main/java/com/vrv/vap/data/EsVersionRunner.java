package com.vrv.vap.data;

import com.vrv.vap.data.component.ESManager;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/11/25
 * @description
 */
@Component
public class EsVersionRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EsVersionRunner.class);

    @Autowired
    private StringRedisTemplate redisTpl;

    @Override
    public void run(String... args) {
        String endpoint = "";
        try {
            Response resp = ESManager.sendGet(endpoint);
            if (resp != null) {
                String responseStr = EntityUtils.toString(resp.getEntity());
                if (StringUtils.isNotEmpty(responseStr)) {
                    Map result = JsonUtil.jsonToMap(responseStr);
                    Map version = (Map) result.get("version");
                    String number = (String) version.get("number");
                    redisTpl.opsForValue().set(SYSTEM.ES_VERSION,number);
                }
            }
        } catch (IOException e) {
            log.error("",e);
        }
    }
}
