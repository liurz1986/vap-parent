package com.vrv.vap.alarmdeal.business.model.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateUtil {
    private static Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

    private RestTemplate restTemplate = new RestTemplate();

    public  <T> T post(String url,String jsonParams,Class<T> responseType){
        logger.info("URL:"+url+"=====jsonParams:"+jsonParams);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonParams, headers);
        return restTemplate.postForObject(url, formEntity, responseType);
    }

    public String post(String url,HttpHeaders headers,String jsonParams){
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonParams, headers);
        String result = restTemplate.postForObject(url, formEntity, String.class);
        return result;
    }
}
