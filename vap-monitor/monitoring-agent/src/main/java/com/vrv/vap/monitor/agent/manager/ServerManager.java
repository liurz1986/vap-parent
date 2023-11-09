package com.vrv.vap.monitor.agent.manager;

import com.vrv.vap.monitor.agent.common.BatchQueue;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.common.enums.CommandType;
import com.vrv.vap.monitor.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
@Slf4j
@Component
public class ServerManager {
    @Resource
    BaseProperties baseProperties;

    @Resource
    RestTemplate restTemplate;
    @Resource
    MonitorManager monitorManager;
    @Resource
    BatchQueue<String> batchQueue;

    private ServerStateInfo serverStateInfo;

    public ServerStateInfo getServerStateInfo(){
        if(serverStateInfo == null ){
            serverStateInfo = new ServerStateInfo();
            serverStateInfo.setStatus(false);
            serverStateInfo.setTime(new Date());
        }

        return  serverStateInfo;
    }

    public void updateServerStateInfo(Boolean status){
        ServerStateInfo serverStateInfo = getServerStateInfo();
        serverStateInfo.setStatus(status);
        serverStateInfo.setTime(new Date());

    }



    public void sendMetric(MetricInfo metricInfo){
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.METRIC, JsonUtil.objToJson(metricInfo));
            Result result = sendServer(commandInfo);
        }
    }

    public void sendAlarm(AlarmInfo alarmInfo){
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.ALARM, JsonUtil.objToJson(alarmInfo));
            try {
                Result result = sendServer(commandInfo);
                if (!result.getCode().equals("0")){
                    batchQueue.add(JsonUtil.objToJson(alarmInfo));
                }
            } catch (Exception e) {
                log.error("sendAlarm error");
                batchQueue.add(JsonUtil.objToJson(alarmInfo));
                e.printStackTrace();
            }
        }else {
            batchQueue.add(JsonUtil.objToJson(alarmInfo));
        }
    }

    public void sendRestartBack(RestartInfo restartInfo) {
        CommandInfo commandInfo = buildCommandInfo(CommandType.RESTART, JsonUtil.objToJson(restartInfo));
        try {
            Result result = sendServer(commandInfo);
        } catch (Exception ex) {
            log.error("send restart back ：{}", ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void sendHandle(HandlerInfo handlerInfo){
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.HANDLER, JsonUtil.objToJson(handlerInfo));
            try {
                Result result = sendServer(commandInfo);
                if (!result.getCode().equals("0")){
                    batchQueue.add(JsonUtil.objToJson(handlerInfo));
                }
            } catch (Exception e) {
                log.error("sendHandle error");
                batchQueue.add(JsonUtil.objToJson(handlerInfo));
                e.printStackTrace();
            }
        }else {
            batchQueue.add(JsonUtil.objToJson(handlerInfo));
        }
    }
    public Boolean offonline(List<AlarmInfo> allFileInfo) {
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.OFFLINE, JsonUtil.objToJson(allFileInfo));
            try {
                Result result = sendServer(commandInfo);
                if (!result.getCode().equals("0")){
                    return false;
                }
            } catch (Exception e) {
                log.error("offonline error");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    public Result collectorSel(){
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.COllECTORSEL, null);
            Result result = sendServer(commandInfo);
            return result;
        }
        return null;
    }

    public Result getSystemInfoRate() {
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.SYSTEMRATE, null);
            Result result = sendServer(commandInfo);
            return result;
        }
        return null;
    }

    public void collectorUpdate(NetworkMonitor networkMonitor) {
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.COllECTORUPDATE, JsonUtil.objToJson(networkMonitor));
            Result result = sendServer(commandInfo);
        }
    }

    public void saveSystemInfo(LocalSystemInfo localSystemInfo) {
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.SYSTEMSAVE, JsonUtil.objToJson(localSystemInfo));
            Result result = sendServer(commandInfo);
        }
    }
    public Result flumeCidGet(String cid) {
        if(serverStateInfo.getStatus()) {
            CommandInfo commandInfo = buildCommandInfo(CommandType.FLUMECID, cid);
            Result result = sendServer(commandInfo);
            return result;
        }
        return null;
    }
    public Result sendBeat(BeatInfo beatInfo){
        CommandInfo commandInfo = buildCommandInfo(CommandType.BEAT, JsonUtil.objToJson(beatInfo));
        try {
            Result result = sendServer(commandInfo);
            if("0".equals(result.getCode())){
                updateServerStateInfo(true);
            }else {
                //修改server状态
                updateServerStateInfo(false);
                //修改configStatus状态为false,方便server启动后重新刷新任务
                monitorManager.setConfigStatus(false);
            }
            return  result;
        }catch (Exception exception){
            updateServerStateInfo(false);
            monitorManager.setConfigStatus(false);
            throw exception;
        }

    }

    private CommandInfo buildCommandInfo(CommandType commandType,String info){
        CommandInfo commandInfo = new CommandInfo();
        commandInfo.setCommandBody(info);
        commandInfo.setIp(baseProperties.getLocalIp());
        commandInfo.setToken(baseProperties.getToken());
        commandInfo.setTime(new Date());
        commandInfo.setCommandType(commandType);
        return commandInfo;
    }

    private Result sendServer(CommandInfo commandInfo){
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType type= MediaType.parseMediaType("application/json;charset=UTF-8");
        httpHeaders.setContentType(type);
        log.info("CommandInfo发起请求: {}", JsonUtil.objToJson(commandInfo));
        HttpEntity<CommandInfo> objectHttpEntity = new HttpEntity<>(commandInfo,httpHeaders);
        ResponseEntity<Result> responseResultResponseEntity = restTemplate.postForEntity(baseProperties.getUrl(), objectHttpEntity, Result.class);
        return responseResultResponseEntity.getBody();
    }




    public Result sendServerFile(LogSendInfo logSendInfo) throws IOException {

        log.debug(JsonUtil.objToJson(logSendInfo));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        FileSystemResource resource = new FileSystemResource(logSendInfo.getFilePath());
        if(!resource.exists()){
            log.info("日志地址不存在,{}",JsonUtil.objToJson(logSendInfo));
            return Result.builder().code("-1").msg("日志地址不存在").build();
        }
        if( resource.contentLength()>baseProperties.getSendFileMaxSize()){
            log.info("日志文件过大，不传输,{}",JsonUtil.objToJson(logSendInfo));
            return Result.builder().code("-1").msg("日志文件过大，不传输").build();
        }

        map.add("file", resource);
        map.add("sendInfo", JsonUtil.objToJson(logSendInfo));
        log.info("开始上传本地日志至服务端！！！！");
        // 组装请求体
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<Result> responseResultResponseEntity = restTemplate.postForEntity(baseProperties.getSendFileUrl(), requestEntity, Result.class);
        log.info("上传至服务端ok！！！！");
        return responseResultResponseEntity.getBody();
    }

}
