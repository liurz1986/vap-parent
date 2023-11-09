package com.vrv.vap.netflow.common.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wh1107066
 * @date 2023/8/18
 */
@Configuration
public class TypeDictionaryProperties implements Serializable, InitializingBean {
    @Autowired
    private BatchQueueProperties batchQueueProperties;
    private Map<String, String> logTypeDic = null;
    private Map<String, String> devTypeDic = null;
    private Map<String, Integer> levelTypeDic = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        logTypeDic = batchQueueProperties.generateTypeDic(batchQueueProperties.getLogTypeDic());
        devTypeDic = batchQueueProperties.generateTypeDic(batchQueueProperties.getDevTypeDic());
        levelTypeDic = batchQueueProperties.generateIntTypeDic(batchQueueProperties.getLevelTypeDic());

    }

    public Map<String, String> getLogTypeDic() {
        return logTypeDic;
    }

    public void setLogTypeDic(Map<String, String> logTypeDic) {
        this.logTypeDic = logTypeDic;
    }

    public Map<String, String> getDevTypeDic() {
        return devTypeDic;
    }

    public void setDevTypeDic(Map<String, String> devTypeDic) {
        this.devTypeDic = devTypeDic;
    }

    public Map<String, Integer> getLevelTypeDic() {
        return levelTypeDic;
    }

    public void setLevelTypeDic(Map<String, Integer> levelTypeDic) {
        this.levelTypeDic = levelTypeDic;
    }
}
