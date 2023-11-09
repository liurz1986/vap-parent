package com.vrv.vap.monitor.server.common.controller;

import com.vrv.vap.monitor.common.model.AgentStateInfo;
import com.vrv.vap.monitor.common.model.BeatInfo;
import com.vrv.vap.monitor.common.model.MetricInfo;
import com.vrv.vap.monitor.common.model.Result;
import com.vrv.vap.monitor.server.common.util.SpringContextUtil;
import com.vrv.vap.monitor.server.config.MonitorProperties;
import com.vrv.vap.monitor.server.manager.AgentManager;
import com.vrv.vap.monitor.server.model.JobModel;
import com.vrv.vap.monitor.server.model.SystemConfig;
import com.vrv.vap.monitor.server.service.CollectorDataAccessService;
import com.vrv.vap.monitor.server.service.HardwareService;
import com.vrv.vap.monitor.server.service.SystemConfigService;
import com.vrv.vap.monitor.server.task.AlarmCleanTask;
import com.vrv.vap.monitor.server.task.TaskManager;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "监控对外接口")
@RequestMapping(value = "/")
@Slf4j
public class WebController {
    @Autowired
    SystemConfigService systemConfigService;
    @Autowired
    private AgentManager agentManager;
    @Autowired
    private HardwareService hardwareService;

    @Resource
    private MonitorProperties monitorProperties;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("confValue", "{\"0\":\"不开启\",\"1\":\"开启\"}");
    }

    @PatchMapping("system/config")
    //@SysRequestLog(description="告警阈值修改", actionType = ActionType.UPDATE)
    @ApiOperation(value = "告警阈值修改", notes = "")
    @SysRequestLog(description = "修改告警阈值",actionType = ActionType.UPDATE)
    public Result updateWarnValue(@RequestBody List<SystemConfig> systemConfigList){
        systemConfigList.forEach(p -> {
            SystemConfig systemConfigSec = systemConfigService.findByConfId(p.getConfId());
            int result = systemConfigService.updateSelective(p);
            if (result == 1 && (!StringUtils.equals(systemConfigSec.getConfValue(),p.getConfValue()) || systemConfigSec.getConfEnable() != p.getConfEnable())) {
                if ("data_clean_auto".equals(p.getConfId())) {
                    SyslogSenderUtils.sendUpdateAndTransferredField(systemConfigSec,p,"修改自动数据清理配置",transferMap);
                } else {
                    String description = this.getConfLogDescription(p.getConfId());
                    SyslogSenderUtils.sendUpdateSyslog(systemConfigSec,p,"修改" + description + "使用率阈值(%)");
                }
            }
            if ("data_clean_auto".equals(p.getConfId())) {
                JobModel jobModel = new JobModel();
                if ("1".equals(p.getConfValue())) {
                    jobModel.setJobName("alarmCleanTask");
                    jobModel.setCronTime("0 0 2 * * ?");
                    jobModel.setJobClazz(AlarmCleanTask.class);
                    TaskManager.addJob(jobModel);
                    TaskManager.resumeJob("alarmCleanTask");
                } else {
                    jobModel.setJobName("alarmCleanTask");
                    TaskManager.removeJob(jobModel);
                }
            }
        });
        return  Result.builder().code("0").msg("success").build();
    }

    private String getConfLogDescription(String confId) {
        String description = "";
        if ("cpu_rate".equals(confId)) {
            description = "CPU";
        }
        if ("ram_rate".equals(confId)) {
            description = "内存";
        }
        if ("disk_rate".equals(confId)) {
            description = "磁盘";
        }
        return description;
    }


    @GetMapping("/agent/status")
    public Result getAllAgent(){
        List<AgentStateInfo> stateInfos = new ArrayList<>();
        Map<String, AgentStateInfo> ipAgentStateInfoMap = agentManager.getIpAgentStateInfoMap();
        ipAgentStateInfoMap.values().forEach(info->{
            AgentStateInfo agentStateInfo = new AgentStateInfo();
            BeanUtils.copyProperties(info,agentStateInfo);
            agentStateInfo.setMonitorConfigs(null);
            stateInfos.add(agentStateInfo);
        });
        return Result.builder().code("0").data(stateInfos).build();
    }

    @GetMapping("/agent/status/beat")
    public Result getAllAgentBeat(){
        List<BeatInfo> stateInfos = new ArrayList<>();
        Map<String, AgentStateInfo> ipAgentStateInfoMap = agentManager.getIpAgentStateInfoMap();
        ipAgentStateInfoMap.values().forEach(info->{
            if(info.getBeatInfo()!=null) {
                stateInfos.add(info.getBeatInfo());
            }
        });
        return Result.builder().code("0").data(stateInfos).build();
    }

//    @GetMapping("/agent/status/module}")
//    public Result getAllAgentModule(){
//
//
//    }


    //获取所有的agent状态
    @GetMapping("agentStatus")
    public Result getAgentStatus(@RequestBody(required = false) Map<String, String> map){
        String s = map.get("ip");
        String monitorName = map.get("monitorName");
        System.out.println(s);
        System.out.println(monitorName);
        Map<String, AgentStateInfo> ipAgentStateInfoMap = agentManager.getIpAgentStateInfoMap();
        if (StringUtils.isNotBlank(s)){
            AgentStateInfo agentStateInfo = ipAgentStateInfoMap.get(s);
            System.out.println(agentStateInfo);
            Map<String, MetricInfo> metricInfoMap = agentStateInfo.getMetricInfoMap();
            if (metricInfoMap==null){
                return  Result.builder().code("0").msg("success").data(null).build();
            }
            if (StringUtils.isNotBlank(monitorName)){
                MetricInfo metricInfo = metricInfoMap.get(monitorName);
                if (metricInfo==null){
                    return  Result.builder().code("0").msg("success").data(null).build();
                }
                return Result.builder().code("0").msg("success").data(metricInfo).build();
            }
            return Result.builder().code("0").msg("success").data(metricInfoMap).build();
        }else {
            return Result.builder().code("0").msg("success").data(ipAgentStateInfoMap).build();
        }
    }
    //获取当前所有组件状态

    //根据ip查询主机状态及组件监控状态
    @GetMapping("test")
    public Object test(){
        ApplicationContext ctx = SpringContextUtil.getApplicationContext();
        CollectorDataAccessService bean = ctx.getBean(CollectorDataAccessService.class);
        Example example=new Example(com.vrv.vap.monitor.server.model.CollectorDataAccess.class);
        example.createCriteria().andEqualTo("cid","9a02c8847e0cb93a980d9c97da00b59c");
        List<com.vrv.vap.monitor.server.model.CollectorDataAccess> byExample = bean.findByExample(example);
        return  Result.builder().code("0").data(byExample.get(0)).build();
    }

    @GetMapping(value = "/restart/{serviceName}")
    @ApiOperation(value = "根据服务名称重启服务", notes = "")
    @SysRequestLog(description = "根据服务名称重启服务",actionType = ActionType.AUTO)
    public Result restartService(@PathVariable @ApiParam("服务名称") String serviceName) {
        return agentManager.restartService(serviceName);

    }

    @GetMapping(value = "/download/{serviceName}")
    @ApiOperation(value = "下载日志")
    @SysRequestLog(description = "下载日志",actionType = ActionType.DOWNLOAD)
    public Result downLoadLog(@PathVariable @ApiParam("服务名称") String serviceName, HttpServletResponse response) {
        SyslogSenderUtils.sendDownLosdSyslog();
        return agentManager.downloadLog(response, serviceName);
    }


}
