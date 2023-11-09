package com.vrv.vap.netflow.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.netflow.common.util.LogSendUtil;
import com.vrv.vap.netflow.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/V1/alarm")
public class MonitorAlarmController {
    private static final Logger log = LoggerFactory.getLogger(MonitorAlarmController.class);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Value("${vap.alarm-log.address}")
    private String udpAddress;

    /**
     * 监测器将生成的告警信息通过此接口向管理系统上传,一次上传一条或多条告警信息
     *
     * @param logs 攻击检测告警数据
     * @return <code>true</code> if
     */
    @PostMapping("/trojan/inner_policy")
    @ApiOperation("木马活动告警信息上传")
    public Result trojanInfo(@RequestBody List<Map<String, Object>> logs) {
        String trojanInfo = "木马活动告警信息";
        return sendFlume(trojanInfo, logs);
    }

    /**
     * 监测器上传渗透行为告警事件到管理系统
     *
     * @param logs 攻击检测告警数据
     * @return <code>true</code> if
     */
    @PostMapping("/attack/inner_policy")
    @ApiOperation("渗透行为告警信息上传")
    public Result attackInfo(@RequestBody List<Map<String, Object>> logs) {
        String attackInfo = "渗透行为告警信息";
        return sendFlume(attackInfo, logs);
    }

    /**
     * 监测器上传渗透行为到管理系统
     *
     * @param logs 攻击检测告警数据
     * @return <code>true</code> if
     */
    @PostMapping("/malware/inner_policy")
    @ApiOperation("恶意文件告警信息上传")
    public Result malware(@RequestBody List<Map<String, Object>> logs) {
        String malwareInfo = "恶意文件告警信息";
        return sendFlume(malwareInfo, logs);
    }

    /**
     * 监测器异常告警信息上传管理系统
     *
     * @param logs 攻击检测告警数据
     * @return <code>true</code> if
     */
    @PostMapping("/abnormal/inner_policy")
    @ApiOperation("异常告警信息上传")
    public Result abnormal(@RequestBody List<Map<String, Object>> logs) {
        String abnormalInfo = "异常告警信息";
        return sendFlume(abnormalInfo, logs);
    }


    /**
     * 数据发送flume
     *
     * @param eventInfo 事件数据分类
     * @param logs      攻击检测告警数据
     * @return <code>true</code> if
     */
    public Result sendFlume(String eventInfo, List<Map<String, Object>> logs) {
        log.info("接收监测器" + eventInfo + "上传开始");
        log.info(eventInfo + "上传：" + gson.toJson(logs));
        try {
            if (CollectionUtils.isNotEmpty(logs)) {
                for (Map<String, Object> log : logs) {
                    LogSendUtil.sendLogByUdp(gson.toJson(log), udpAddress);
                }
            }
        } catch (Exception e) {
            log.error("接收监测器" + eventInfo + "上传出现错误", e);
        }
        log.info("接收监测器" + eventInfo + "上传结束");
        return new Result("0", "Success");
    }
}
