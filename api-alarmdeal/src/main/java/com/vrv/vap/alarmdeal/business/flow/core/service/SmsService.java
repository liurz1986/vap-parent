package com.vrv.vap.alarmdeal.business.flow.core.service;


import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.jpa.common.ArrayUtil;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SmsService {

    private static LoggerUtil logger = LoggerUtil.getLogger(SmsService.class);

    @Autowired
    private AuthService authService;

    /**
     * 发送短信
     */
    public  void sendMessage(Set<String> userSet, String smsModel, String args){
        Map<String,Object> reqMap=new HashMap<>();
        String userString="";
        userString= ArrayUtil.join(userSet.toArray(),",");
        if(userString.length()>0){
            reqMap.put("userId",userString);
            reqMap.put("tempNum",smsModel);
            reqMap.put("args",args);
            logger.warn("发送短信内容 :"+ JSON.toJSONString(reqMap));
            Map<String,Object> result= null;
            try {
               //  result = authService.shortMessage(reqMap);  // 目前自监管api-admin中没有这个接口 2023-01-09
                logger.info("调用短信接口返回的结果 :"+JSON.toJSONString(result));
            } catch (Exception e) {
                logger.error("短信接口调用异常",e);
            }

        }
    }


}
