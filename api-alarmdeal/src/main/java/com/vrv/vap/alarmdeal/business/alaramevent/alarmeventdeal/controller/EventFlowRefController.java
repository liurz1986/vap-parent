package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model.EventFlowRefConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.EventFlowRefConfigService;
import com.vrv.vap.common.utils.StringUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * 事件分类关联流程的配置信息
 * @author liurz
 * @date 202310
 */
@RestController
@RequestMapping("/eventFlowRef")
@Api(description="事件分类关联流程的配置信息")
public class EventFlowRefController {
    @Autowired
    private EventFlowRefConfigService ruleFlowRefConfigService;

    @PostMapping("/getFlowNameByEventType")
    @ApiOperation(value="通过事件分类获取流程名称",notes="")
    @SysRequestLog(description="通过事件分类获取流程名称", actionType = ActionType.SELECT,manually=false)
    public Result<String> getFlowNameByEventType(@RequestBody Map<String,String> param){
        String eventType = param.get("eventType");
        if(StringUtils.isEmpty(eventType)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"eventType不能为空");
        }
        EventFlowRefConfig bean = ruleFlowRefConfigService.getOne(eventType);
        if(null != bean){
            return ResultUtil.success(bean.getFlowName());
        }
        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"eventType对应的流程不存在："+eventType);
    }
}
