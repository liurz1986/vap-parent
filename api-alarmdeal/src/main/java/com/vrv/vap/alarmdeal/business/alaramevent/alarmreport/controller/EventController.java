package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.EventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AbnormalEventVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AbnormalUserVo;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 16:44
 */
@RestController
@RequestMapping("/report/event")
@Api(description = "报表-监管事件分析")
public class EventController extends BaseController {
    private static Map<String, Integer> eventTypeMap = new ConcurrentHashMap<>();

    static {
        eventTypeMap.put("ConfigurationCompliance", 1); //配置合规信息 1
        eventTypeMap.put("NetworkSecurityException", 2); //网络安全异常 2
        eventTypeMap.put("AbnormalUserBehavior", 3); //用户行为异常 3
        eventTypeMap.put("OperationaBehavior", 4); //运维行为异常 4
        eventTypeMap.put("AbnormalWebLoginAuditApplicationBehavior", 5); // 应用异常 5
        eventTypeMap.put("ConnectivityAbnormal", 6); //互联互通异常  6
    }
    @Autowired
    AdminFeign adminFeign;
    @Autowired
    private EventService eventService;

    @PostMapping("/total")
    @ApiOperation(value = "监管事件统计信息", notes = "")
    @SysRequestLog(description = "报表-监管事件分析-监管事件统计信息", actionType = ActionType.SELECT, manually = false)
    public Result<EventTotalResponse> total(@RequestBody RequestBean item) {
        return ResultUtil.success(eventService.queryEventTotal(item));
    }

    @PostMapping("/trend")
    @ApiOperation(value = "监管事件趋势信息", notes = "")
    @SysRequestLog(description = "报表-监管事件分析-监管事件趋势信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTypeResponse>> trend(@RequestBody RequestBean item) {
        return ResultUtil.successList(eventService.queryEventTrend(item));
    }

    @PostMapping("/{type}")
    @ApiOperation(value = "监管事件趋势信息", notes = "")
    @SysRequestLog(description = "报表-监管事件分析-监管事件趋势信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTypeResponse>> type(@RequestBody RequestBean item, @PathVariable("type") String type) {
        return ResultUtil.successList(eventService.queryEventByType(item,type));
    }

    @PostMapping("/deal/list")
    @ApiOperation(value = "监管事件趋势信息", notes = "")
    @SysRequestLog(description = "报表-监管事件分析-监管事件趋势信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventListResponse>> list(@RequestBody RequestBean item) {
        return ResultUtil.successList(eventService.queryEventList(item));
    }
    @PostMapping("abnormal/{type}")
    @ApiOperation(value = "异常行为top10", notes = "")
    @SysRequestLog(description = "异常行为top10", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTypeResponse>> typeTop10(@RequestBody RequestBean item, @PathVariable("type") String type) {
        Integer integer = eventTypeMap.get(type);
        return ResultUtil.successList(eventService.typeTop10(item,integer));
    }
    @PostMapping("abnormalInfo/{type}")
    @ApiOperation(value = "异常行为列表top10", notes = "")
    @SysRequestLog(description = "异常行为列表top10", actionType = ActionType.SELECT, manually = false)
    public Result<List<AbnormalEventVo>> abnormalInfo10(@RequestBody RequestBean item, @PathVariable("type") String type) {
        Integer integer = eventTypeMap.get(type);
        return ResultUtil.successList(eventService.abnormalInfo10(item,integer));
    }
    @PostMapping("abnormalUserInfo/{type}")
    @ApiOperation(value = "异常人员信息列表", notes = "")
    @SysRequestLog(description = "异常人员信息列表", actionType = ActionType.SELECT, manually = false)
    public Result<List<AbnormalUserVo>> abnormalUserInfo(@RequestBody RequestBean item,@PathVariable("type") String type) {
        Integer integer = eventTypeMap.get(type);
        return ResultUtil.successList(eventService.abnormalUserInfo(item,integer));
    }
    @PostMapping("eventUser")
    @ApiOperation(value = "事件数量按人员统计top10列表", notes = "")
    @SysRequestLog(description = "事件数量按人员统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<AbnormalUserVo>> eventUserList(@RequestBody RequestBean item) {
        return ResultUtil.successList(eventService.eventUser(item));
    }
    @PostMapping("eventUserTop10")
    @ApiOperation(value = "事件数量按人员统计top10", notes = "")
    @SysRequestLog(description = "事件数量按人员统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<EventTypeResponse>> eventUser(@RequestBody RequestBean item) {
        return ResultUtil.successList(eventService.eventUserTop10(item));
    }
    @PostMapping("eventTypeInfo")
    @ApiOperation(value = "事件数量按人员统计top10", notes = "")
    @SysRequestLog(description = "事件数量按人员统计", actionType = ActionType.SELECT, manually = false)
    public Result<List<RuleTypeInfoResponse>> eventTypeInfo(@RequestBody RequestBean item) {
        return ResultUtil.successList(eventService.eventTypeInfo(item));
    }
    @PostMapping("person/{type}")
    @ApiOperation(value = "用户统计", notes = "")
    @SysRequestLog(description = "用户统计", actionType = ActionType.SELECT, manually = false)
    public Result<PersonTypeResponse> person(@PathVariable("type") Integer type) {
        PersonTypeResponse personTypeResponse=new PersonTypeResponse();
        VData<List<BasePersonZjg>> allPerson = adminFeign.getAllPerson();
        List<BasePersonZjg> data = allPerson.getData();
        if (data!=null){
            //普通用户
            List<String> list = new ArrayList<>();
            if (type==1){
               list.add("3");
               list.add("4");
            }
            //管理员
            else {
                list.add("1");
                list.add("2");
                list.add("5");
            }
            List<BasePersonZjg> collect = data.stream().filter(d -> list.contains(d.getPersonType())).collect(Collectors.toList());
            personTypeResponse.setTotal(collect.size());
            List<BasePersonZjg> collect1 = collect.stream().filter(m -> m.getSecretLevel().equals(4)).collect(Collectors.toList());
            personTypeResponse.setNosecret(collect1.size());
            personTypeResponse.setSecret(collect.size()-collect1.size());
        }
        return ResultUtil.success(personTypeResponse);
    }
}
