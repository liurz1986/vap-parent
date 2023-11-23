package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.business.flow.processdef.vo.Message;
import com.vrv.vap.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name="server-push",url="${server_push_ip:https://127.0.0.1:8780}",configuration = FeignHttpsConfig.class)
public interface PushClient {


    @RequestMapping(value = "/push/user",method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result pushToUser(@RequestBody Map<String,String> param);

    @RequestMapping(value="/push/user",method = RequestMethod.PUT,consumes= MediaType.APPLICATION_JSON_VALUE)
    public Result pushToUser(@RequestBody Message message);

    /**
     * Description: vap-事件消息推送 推向用户组
     * param:
     * Date:2018/8/30
     * User: yangwenxin
     * Time: 11:30
     */
    @RequestMapping(value = "/push/group",method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result pushMessage(@RequestBody Map<String,String> param);
    
    
    /**
     * Description: vap-事件消息推送 推向用户组
     * param:
     * Date:2018/8/30
     * User: yangwenxin
     * Time: 11:30
     */
    @RequestMapping(value = "/push/user",method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Result pushMessageToUser(@RequestBody Map<String,String> param);
}
