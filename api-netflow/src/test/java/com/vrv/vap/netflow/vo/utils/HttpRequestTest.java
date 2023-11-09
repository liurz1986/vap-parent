package com.vrv.vap.netflow.vo.utils;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.netflow.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1107066
 * @date 2023/9/8
 */
public class HttpRequestTest {
    private Logger logger = LoggerFactory.getLogger(HttpRequestTest.class);

    @Test
    public void httpPostRequestTest() {
        String url = "https://192.168.120.201:443/V1/register/reg_request";
        Map<String, Object> params = new HashMap<>();
//        params.put("updateTime", DateUtil.format(status.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
        params.put("device_id", "0805021");
        params.put("device_soft_version", "20170528001_9527");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
//        headers.put("Authorization", "Bearer " + accessToken);
        String post = null;
        try {
            String json = JSONObject.toJSONString(params);
            logger.info("json:{}", json);
            post = HttpUtil.POST(url, headers, json);
        } catch (Exception e) {
            throw new RuntimeException("调用post请求异常！", e);
        }
        if (StringUtils.isNotEmpty(post)) {
            logger.info("返回结果post请求值为:{}", post);
//            JSONObject jsonObject = JSONObject.parseObject(post);
//            Object code = jsonObject.get("code");
//            Object msg = jsonObject.get("msg");

        } else {
            throw new RuntimeException("post请求异常！获取的post的值为空！");
        }
    }
}
