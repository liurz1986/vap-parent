package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.controller;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmCommonDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.ChangeRiskReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.FilterFieldReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.FilterSourceReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.res.FilterSourceRes;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月21日 14:19
 */
@RestController
@RequestMapping("/alarmcommon")
public class AlarmCommonDataController {
    private final Logger logger = LoggerFactory.getLogger(AlarmCommonDataController.class);

    @Autowired
    private AlarmCommonDataService alarmCommonDataService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisUtil redisUtil;
    @GetMapping("flinkRedisValue")
    public Result<Map<String,Object>> getFlinkRedisValue(){
           return ResultUtil.success(( Map)redisUtil.hmget("flinkTaskMap"));
    }

    @PostMapping(value = "/sendMsg")
    public Result<   Map<String,Object> > sendMsg(@RequestBody Map<String,String> map) {
        //下发数据模拟
        Map<String, Object> sendMsgMap = getSendMsgMap(map);

        kafkaTemplate.send("SuperviseAnnounce",new Gson().toJson(sendMsgMap));

        return ResultUtil.success(sendMsgMap);
    }

    public Map<String,Object>  getSendMsgMap(Map<String,String> map){
        Gson gson = new Gson();
        String type = map.get("type");
        String json="";
        switch (type){
            //督办
            case "1":
                json="{\"code\":200.0,\"msg\":\"成功\",\"push_time\":\"2023-10-17 14:39:28\",\"data\":[{\"event_id\":\"cheuhyuewhfiw93839ufe94ur84uadfadsf223445erue\",\"attachment\":[{\"file_name\":\"附件1\",\"file_bin\":\"test\"}],\"notice_id\":\"bc71f84457f3c2cb42bd3863d1ac392\",\"notice_desc\":\"test\"}],\"notice_type\":\"1\"}\n";
                break;
                //预警
            case "2":
                json="{\"code\":200.0,\"msg\":\"成功\",\"push_time\":\"2023-10-17 14:39:28\",\"data\":[{\"warnning_id\":\"133333333334\",\"warnning_description\":\"预警描述\",\"warn_file\":[{\"file_name\":\"附件1\",\"file_bin\":\"test\"}],\"warnning_conlusion\":\"预警结论\",\"notice_desc\":\"test\"}],\"notice_type\":\"2\"}\n";
                break;
                //协查
            case "4":
                json="{\"code\":200.0,\"msg\":\"成功\",\"push_time\":\"2023-10-17 14:39:28\",\"data\":[{\"confirmed_cause_type\":\"使用人发现了**应用系统的弱口令，并通过尝试\",\"assis_contact_dept\":\"综合部\",\"assis_unit\":\"单位1\",\"event_id\":\"ececc585-9690-4300-aa30-fb38e25d6aaf|unauthorized_unknown_address\",\"event_type\":\"4\",\"alert_detail\":\"**单位ip在时间端口\",\"assis_id\":\"c3d6511c3d4f4039beb63399fd150dcc\",\"event_name\":\"存在来自外部的异常访问端口\",\"assis_telephone\":\"13972932407\",\"event_disposal\":\"未履行审批手续\",\"assis_contact_name\":\"李四\",\"assis_type\":\"2\",\"assis_conclusion\":\"IP地址定位为北京海淀区**街道\"}],\"notice_type\":\"4\"}\n";
                break;
                //协办
            case "3":
                json="{\"code\":200.0,\"msg\":\"成功\",\"push_time\":\"2023-10-17 14:39:28\",\"data\":[{\"apply_unit\":\"单位1\",\"disposal_process\":\"*年*月*日，信息化部门进行了事件核实，填写了涉事人员信息\",\"verify_process\":\"*年*月*日，保密工作部门向信息化部门发起了事件核实任务\",\"verify_content\":\"**IP地址对应的责任人，部门，密级，设备信息\",\"apply_contact_dept\":\"综合部\",\"ip_source\":\"北京市海淀区\",\"cause\":\"网络管理员对防火墙配置错误\",\"recommend_disposal_measure\":\"询问**IP对应设备部门对该IP对应设备责任人是否违规操作\",\"apply_telephone\":\"13972932407\",\"apply_contact_name\":\"张三\",\"disposal_measure\":\"针对**事件中的涉事ip地址进行了临时限制措施\",\"app_name\":\"[\\u0027OA\\u0027,\\u0027邮件\\u0027]\",\"event_id\":\"ececc585-9690-4300-aa30-fb38e25d6aaf|unauthorized_unknown_address\",\"event_type\":\"4\",\"app_account\":\"[\\u0027test1\\u0027,\\u0027test@mails.com\\u0027]\",\"alert_detail\":\"**单位ip在时间端口\",\"extern_ip\":\"1.1.1.1\",\"connect_range\":\"[]\",\"assis_id\":\"b289d44b4e2a4172860c41067066744e\",\"event_name\":\"存在来自外部的异常访问端口\",\"assis_cause\":\"由于无法准确定位外部IP地址来源\"}],\"notice_type\":\"3\"}\n";
                break;
        }
        Map<String,Object> sendMap=gson.fromJson(json,Map.class);
        List<Map<String,Object>> mapList = (List)sendMap.get("data");
        sendMap.put("push_time", DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN));
        for (Map<String, Object> item : mapList) {
            if(map.containsKey("event_id")){
                item.put("event_id",map.get("event_id"));
            }
            if(map.containsKey("assis_id")){
                item.put("assis_id",map.get("assis_id"));
            }
            if(map.containsKey("warnning_id")){
                item.put("warnning_id",map.get("warnning_id"));
            }
        }
        sendMap.put("data",mapList);
        return sendMap;
    }


    @RequestMapping(value = "/filterColumnFresh", method = RequestMethod.POST)
    @ApiModelProperty("规则字段重排序")
    @SysRequestLog(description = "运维接口-规则字段重排序", actionType = ActionType.SELECT, manually = false)
    public Result<Boolean> filterColumnFresh(@RequestBody FilterFieldReq req) {
        boolean freshFlag = alarmCommonDataService.filterColumnFresh(req);
        return ResultUtil.success(freshFlag);
    }

    @RequestMapping(value = "/updateFilterColumn", method = RequestMethod.POST)
    @ApiModelProperty("规则字段更新")
    @SysRequestLog(description = "运维接口-规则字段更新", actionType = ActionType.SELECT, manually = false)
    public Result<Map<String, List<FilterSourceRes>>> updateFilterColumn(@RequestBody FilterSourceReq req) {
        Map<String, List<FilterSourceRes>> result = alarmCommonDataService.updateFilterColumn(req);
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/changeRiskList", method = RequestMethod.POST)
    @ApiModelProperty("查找两组策略的规则差集")
    @SysRequestLog(description = "运维接口-查找两组策略的规则差集", actionType = ActionType.SELECT, manually = false)
    public Result<List<String>> changeRiskList(@RequestBody ChangeRiskReq req) {
        List<String> result = alarmCommonDataService.changeRiskList(req);
        return ResultUtil.successList(result);
    }

    @RequestMapping(value = "/deleteRedisData/{name}", method = RequestMethod.GET)
    @ApiModelProperty("删除redis信息")
    @SysRequestLog(description = "运维接口-删除redis信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<String>> deleteRedisData(@PathVariable(value = "name") String name) {
        List<String> list = alarmCommonDataService.deleteRedisData(name);
        return ResultUtil.successList(list);
    }

    @RequestMapping(value = "/getRedisData/{name}", method = RequestMethod.GET)
    @ApiModelProperty("获取redis信息")
    @SysRequestLog(description = "运维接口-获取redis信息", actionType = ActionType.SELECT, manually = false)
    public Result<String> getRedisData(@PathVariable(value = "name") String name) {
        String value = alarmCommonDataService.getRedisData(name);
        return ResultUtil.success(value);
    }

    @RequestMapping(value = "/getRedisKeysData/{name}", method = RequestMethod.GET)
    @ApiModelProperty("获取redis key 信息")
    @SysRequestLog(description = "运维接口-获取redis key 信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<String>> getRedisKeysData(@PathVariable(value = "name") String name) {
        List<String> list = alarmCommonDataService.getRedisKeysData(name);
        return ResultUtil.successList(list);
    }

    @RequestMapping(value = "/getRedisKeysList/{name}", method = RequestMethod.GET)
    @ApiModelProperty("获取redis key 信息")
    @SysRequestLog(description = "运维接口-获取redis key 信息", actionType = ActionType.SELECT, manually = false)
    public Result<Set<String>> getRedisKeysList(@PathVariable(value = "name") String name) {
        Set<String> list = alarmCommonDataService.getRedisKeysList(name);
        return ResultUtil.successList(list);
    }

    @RequestMapping(value = "/getDataSource", method = RequestMethod.GET)
    @ApiModelProperty("变更数据源ID信息")
    @SysRequestLog(description = "变更数据源ID信息", actionType = ActionType.SELECT, manually = false)
    public Result<Boolean> getDataSource() {
        boolean result = alarmCommonDataService.getDataSource();
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/handleAlarmEsData", method = RequestMethod.GET)
    @ApiModelProperty("变更数据源ID信息")
    @SysRequestLog(description = "变更数据源ID信息", actionType = ActionType.SELECT, manually = false)
    public Result<Boolean> handleAlarmEsData() {
        boolean result = alarmCommonDataService.handleAlarmEsData();
        return ResultUtil.success(result);
    }

}
