package com.vrv.vap.monitor.server.common.controller;

import com.vrv.vap.monitor.common.model.*;
import com.vrv.vap.monitor.server.common.util.FileUtils;
import com.vrv.vap.monitor.server.common.util.SpringContextUtil;
import com.vrv.vap.monitor.server.config.MonitorProperties;
import com.vrv.vap.monitor.server.manager.AgentManager;
import com.vrv.vap.monitor.server.model.SystemConfig;
import com.vrv.vap.monitor.server.service.*;
import com.vrv.vap.monitor.server.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/server")
@Api(value = "代理Server接收命令", tags = "代理Server接收命令")
@Slf4j
public class ServerController {
    @Resource
    AgentManager agentManager;
    @Resource
    private MonitorProperties monitorProperties;

    //接收指令信息
    @PostMapping("/cmd")
    public Result agentCommand(@RequestBody CommandInfo commandInfo) {
        log.debug("[SERVER-COMMAND] command info :{}", JsonUtil.objToJson(commandInfo));
        ApplicationContext ctx = SpringContextUtil.getApplicationContext();
        NetworkMonitorService networkMonitorService = ctx.getBean(NetworkMonitorService.class);
        SystemConfigService systemConfigService = ctx.getBean(SystemConfigService.class);
        LocalSystemInfoService localSystemInfoService = ctx.getBean(LocalSystemInfoService.class);
        AlarmCollectionService alarmCollectionService = SpringContextUtil.getApplicationContext().getBean(AlarmCollectionService.class);
        switch (commandInfo.getCommandType()) {
            case BEAT:
                //agent 发送心跳
                BeatInfo beatInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), BeatInfo.class);
                agentManager.updateBeat(beatInfo);
                break;
            case CONFIG:
                //主动获取agent的CONFIG

                break;
            case METRIC:
                //上报指标数据
                MetricInfo metricInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), MetricInfo.class);
                agentManager.updateMetric(metricInfo);
                break;
            case ALARM:
                //上报告警数据
                AlarmInfo alarmInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), AlarmInfo.class);
                //判断mysql服务是否正常
                log.info("====agentManager.getMysqlStatus()====" + agentManager.getMysqlStatus());
                if (!agentManager.getMysqlStatus()) {
                    return Result.builder().code("500").build();
                }
                try {
                    agentManager.updateAlarm(alarmInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.builder().code("500").build();
                }
                break;
            case HANDLER:
                //上报处理结果数据
                HandlerInfo handlerInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), HandlerInfo.class);
                if (!agentManager.getMysqlStatus()) {
                    return Result.builder().code("500").build();
                }
                try {
                    agentManager.updateHandler(handlerInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.builder().code("500").build();
                }
                break;
            case OFFLINE:
                List<AlarmInfo> alarmInfos = JsonUtil.jsonToEntityList(commandInfo.getCommandBody(), AlarmInfo.class);
                if (!agentManager.getMysqlStatus()) {
                    return Result.builder().code("500").build();
                }
                for (AlarmInfo alarmInfo1 : alarmInfos) {
                    try {
                        agentManager.updateAlarm(alarmInfo1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Result.builder().code("500").build();
                    }
                }
                log.info("OFFLINE===save====");
                break;
            case COllECTORSEL:
                //采集器信息查询
                List<com.vrv.vap.monitor.server.model.NetworkMonitor> monitorList = networkMonitorService.findAll();
                log.info("networkMonitorService.findAll");
                return Result.builder().code("0").data(monitorList).build();
            case COllECTORUPDATE:
                //采集器信息查询
                networkMonitorService.update(JsonUtil.jsonToEntity(commandInfo.getCommandBody(), com.vrv.vap.monitor.server.model.NetworkMonitor.class));
                log.info("networkMonitorService.update");
                break;
            case FLUMECID:
                //接收器cid查询
                CollectorDataAccessService bean = ctx.getBean(CollectorDataAccessService.class);
                Example example = new Example(com.vrv.vap.monitor.server.model.CollectorDataAccess.class);
                example.createCriteria().andEqualTo("cid", commandInfo.getCommandBody());
                log.info("commandInfo.getCommandBody()" + commandInfo.getCommandBody());
                List<com.vrv.vap.monitor.server.model.CollectorDataAccess> byExample = bean.findByExample(example);
                log.info("CollectorDataAccessService.sel" + byExample);
                if (byExample.size() > 0) {
                    return Result.builder().code("0").data(byExample.get(0)).build();
                }
            case SYSTEMRATE:
                SystemConfig systemConfigCpu = systemConfigService.findByConfId("cpu_rate");
                SystemConfig systemConfigRam = systemConfigService.findByConfId("ram_rate");
                SystemConfig systemConfigDisk = systemConfigService.findByConfId("disk_rate");
                Map<String, String> systemConfig = new HashMap<>();
                systemConfig.put("cpu_rate", systemConfigCpu.getConfValue());
                systemConfig.put("ram_rate", systemConfigRam.getConfValue());
                systemConfig.put("disk_rate", systemConfigDisk.getConfValue());
                log.info("SystemConfig.findAll");
                return Result.builder().code("0").data(systemConfig).build();
            case SYSTEMSAVE:
                localSystemInfoService.save(JsonUtil.jsonToEntity(commandInfo.getCommandBody(), com.vrv.vap.monitor.server.model.LocalSystemInfo.class));
                log.info("SystemConfig.save");
                break;
            case RESTART:
                //重启服务，异步执行，无返回结果
                log.info("commandBody打印输出: {}", commandInfo.getCommandBody());
                RestartInfo restartInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), RestartInfo.class);
                agentManager.updateRestart(restartInfo);
            default:
                log.error("未知命令类型");
                return Result.builder().code("404").msg("未知命令类型").build();
        }

        return Result.builder().code("0").build();

    }


    //接收指令信息
    @PostMapping("/getConfig")
    public Map<String, Object> getConfig() {
        System.out.println(monitorProperties);
        Map<String, Object> elasticsearch = monitorProperties.getComponents().get("elasticsearch").getConnectConfig();
        System.out.println(elasticsearch);
        Map<String, Object> elk = (Map<String, Object>) elasticsearch.get("elk");
        Map<String, Object> cluster = (Map<String, Object>) elk.get("cluster");
        String name = (String) cluster.get("name");
        System.out.println(name);
        return elasticsearch;

    }

    //获取采集器信息
    @PostMapping("/collect")
    public List<com.vrv.vap.monitor.server.model.NetworkMonitor> collect() {
        ApplicationContext ctx = SpringContextUtil.getApplicationContext();
        NetworkMonitorService networkMonitorService = ctx.getBean(NetworkMonitorService.class);
        List<com.vrv.vap.monitor.server.model.NetworkMonitor> monitorList = networkMonitorService.findAll();
        return monitorList;
    }


    @ApiOperation("上传文件")
    @PostMapping(path = "/upload")
    public Result uploadFile(@ApiParam(value = "上传的文件") @RequestParam MultipartFile file,
                             @RequestParam("sendInfo") String sendInfo) {
        LogSendInfo logSendInfo = JsonUtil.jsonToEntity(sendInfo, LogSendInfo.class);
        if (logSendInfo == null) {
            log.info("上传信息不完整");
            return Result.builder().code("-1").msg("上传信息不完整").build();
        }
        String filePath = Paths.get(logSendInfo.getTempDir(),logSendInfo.getLogFileName()).toString();
        FileUtils.uploadFileStream(file,  filePath );
        return Result.builder().code("0").build();
    }

}
