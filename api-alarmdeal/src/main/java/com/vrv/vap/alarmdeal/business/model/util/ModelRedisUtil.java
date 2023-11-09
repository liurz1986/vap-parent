package com.vrv.vap.alarmdeal.business.model.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ModelRedisUtil {
    static String NAME = "modelManage";
    @Autowired
    private RedisTemplate<String, String[]> assetTemplate;

    public void saveModelTestStatus(String name,int code ,String status) {
        Map<String,Object> value = new HashMap<String,Object>();
        value.put("code",code);
        value.put("status",status);
        save(name,value);
    }

    public Map<String,Object> getModelTestStatus(String name) {
        Object object = assetTemplate.opsForHash().get(NAME, name);
        return  (Map<String,Object>)object;
    }


    public void save(String name,Object value) {
        Boolean hasKey = assetTemplate.opsForHash().hasKey(NAME, name);
        if(Boolean.TRUE.equals(hasKey)) {
            delete(name);
        }
        assetTemplate.opsForHash().put(NAME, name, value);
    }



    public Object get(String name) {
        Object object = assetTemplate.opsForHash().get(NAME, name);
        return  object;
    }


    public void delete(String name) {
        Boolean hasKey = assetTemplate.opsForHash().hasKey(NAME, name);
        if(Boolean.TRUE.equals(hasKey)) {
            assetTemplate.opsForHash().delete(NAME, name);
        }
    }
}
