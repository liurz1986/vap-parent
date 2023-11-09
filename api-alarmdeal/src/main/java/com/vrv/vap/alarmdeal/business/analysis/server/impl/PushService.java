package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.frameworks.feign.PushClient;
import com.vrv.vap.common.vo.Result;

/**
 * @author yangwenxin E-mail:
 * @version 创建时间 2018/8/30 11:41
 * @description 类说明 :
 */
@Service
public class PushService {
    private Logger logger = LoggerFactory.getLogger(PushService.class);
    @Autowired
    private PushClient  pushFeign;

    /**
     * Description: vap-事件消息推送-推向用户组
     * param:
     * Date:2018/8/30
     * User: yangwenxin
     * Time: 11:42
     */
    
    public void pushMessage(String title,String content,String url,String roleId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("title",title);
        map.put("content",content);
        map.put("url",url);
        map.put("roleId",roleId);
        Result result = pushFeign.pushMessage(map);
        logger.info("code: "+result.getCode() +";" +"message: "+result.getMessage());
    }

    /**
     * Description: vap-事件消息推送-推向用户
     * param:
     * Date:2018/8/31
     * User: yangwenxin
     * Time: 16:54
     */
    
    public void pushMessageToUser(String title, String content, String url, String userId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("title",title);
        map.put("content",content);
        map.put("url",url);
        map.put("userId",userId);
        Result result = pushFeign.pushMessageToUser(map);
        logger.info("code: "+result.getCode() +";" +"message: "+result.getMessage());
    }
}
