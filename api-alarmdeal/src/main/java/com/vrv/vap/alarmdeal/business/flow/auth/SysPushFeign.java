package com.vrv.vap.alarmdeal.business.flow.auth;

import com.vrv.vap.alarmdeal.business.flow.processdef.vo.Message;
import com.vrv.vap.alarmdeal.frameworks.feign.FeignHttpsConfig;
import com.vrv.vap.common.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name ="server-push",url="${server_push_ip:https://127.0.0.1:8780}",configuration = FeignHttpsConfig.class)
public interface SysPushFeign {

    /**
     * 消息推送
     * @param message
     * @return
     */
    @RequestMapping(value="/push/user",method = RequestMethod.PUT,consumes= MediaType.APPLICATION_JSON_VALUE)
    public Result pushToUser(@RequestBody Message message);

}
