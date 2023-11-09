package com.vrv.vap.data.util;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.data.model.BaseReportInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class TargetInterfaceUtil {

    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        TargetInterfaceUtil.restTemplate = restTemplate;
    }

    private static final Logger log = LoggerFactory.getLogger(TargetInterfaceUtil.class);

    public static Map<String, Object> getDataFromInterface(BaseReportInterface model, Map<String, Object> paramMap) {
        Map<String, Object> data = new HashMap<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> formEntity = new HttpEntity<>(JSONObject.toJSONString(paramMap), headers);
            log.info("指标调用开始：url=" + model.getUrl());
            ResponseEntity responseEntity = restTemplate.postForEntity(model.getUrl(), formEntity, Map.class);
            log.info("指标调用结束：result=" + JSONObject.toJSONString(responseEntity));
            data = (Map<String, Object>) responseEntity.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return data;
    }
}
