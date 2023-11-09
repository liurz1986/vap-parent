package com.vrv.vap.line.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Component;

@Component("springSessionDefaultRedisSerializer")
public class CustomSessionDefaultRedisSerializer extends JdkSerializationRedisSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSessionDefaultRedisSerializer.class);

    @Override
    public Object deserialize(byte[] bytes) {
        Object deserialObj = null;
        try {
            deserialObj = super.deserialize(bytes);
        } catch (Exception e) {
            LOG.warn("deserialize session Object error!", e.getMessage());
        }
        return deserialObj;
    }

}

