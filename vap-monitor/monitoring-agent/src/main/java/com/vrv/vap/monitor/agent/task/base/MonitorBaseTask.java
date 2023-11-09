package com.vrv.vap.monitor.agent.task.base;


import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.common.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

/**
 * 基础任务
 */
@Slf4j
public abstract class MonitorBaseTask extends BaseTask {
    private ServerManager serverManager;

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public void pushMetric(MetricInfo metricInfo) {
        serverManager.sendMetric(metricInfo);
    }

    public void collectorInfoUpdate(NetworkMonitor networkMonitor) {
        serverManager.collectorUpdate(networkMonitor);
    }

    public Result collectorInfoGet() {
        return serverManager.collectorSel();
    }

    public Result getSystemInfoRate() {
        return serverManager.getSystemInfoRate();
    }

    public void saveSystemInfo(LocalSystemInfo localSystemInfo) {
        serverManager.saveSystemInfo(localSystemInfo);
    }

    public Result flumeCidGet(String cid) {
        return serverManager.flumeCidGet(cid);
    }

    public void pushAlarm(String code, String desc, String ip, String monitorName) {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setDesc(desc);
        alarmInfo.setTime(new Date());
        alarmInfo.setType(code);
        alarmInfo.setIp(ip);
        alarmInfo.setMonitorName(monitorName);
        alarmInfo.setLevel(1);
        alarmInfo.setStatus(0);
        serverManager.sendAlarm(alarmInfo);
    }

    public void pushHandler(String code, String desc, String ip, String monitorName, Integer status) {
        HandlerInfo handlerInfo = new HandlerInfo();
        handlerInfo.setDesc(desc);
        handlerInfo.setTime(new Date());
        handlerInfo.setType(code);
        handlerInfo.setIp(ip);
        handlerInfo.setMonitorName(monitorName);
        handlerInfo.setLevel(1);
        handlerInfo.setStatus(status);
        serverManager.sendHandle(handlerInfo);
    }

    public Result pushLog(LogSendInfo logSendInfo) throws IOException {
        return serverManager.sendServerFile(logSendInfo);
    }

    public void restartService(MonitorConfig config, RestartInfo restartInfo) {
        if (!config.getRestart()) {
            restartInfo.setTime(new Date());
            restartInfo.setStatus(0);
            restartInfo.setMsg("组件不能重启");
            serverManager.sendRestartBack(restartInfo);
            return;
        }
        Boolean r = restart(config);
        if (!r) {
            restartInfo.setTime(new Date());
            restartInfo.setStatus(2);
            restartInfo.setMsg("命令错误");
            serverManager.sendRestartBack(restartInfo);
            return;
        }
        r = checkRestartStatus(config);
        if (!r) {
            restartInfo.setTime(new Date());
            restartInfo.setStatus(2);
            restartInfo.setMsg("检查状态错误");
            serverManager.sendRestartBack(restartInfo);
            return;
        }
        restartInfo.setTime(new Date());
        restartInfo.setStatus(3);
        serverManager.sendRestartBack(restartInfo);
    }

    public abstract Boolean restart(MonitorConfig config);


    public abstract Boolean checkRestartStatus(MonitorConfig config);


    public MetricInfo buildBaseMetric(MonitorConfig config, String ip) {
        MetricInfo metricInfo = new MetricInfo();
        metricInfo.setIp(ip);
        metricInfo.setTime(new Date());
        metricInfo.setMonitorName(config.getName());
        metricInfo.setLog(config.getLog());
        metricInfo.setRestart(config.getRestart());
        metricInfo.setGroup(config.getGroup());
        return metricInfo;
    }

}
