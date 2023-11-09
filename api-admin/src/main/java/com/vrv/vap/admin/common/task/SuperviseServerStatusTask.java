package com.vrv.vap.admin.common.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.common.enums.NoticeTypeEnum;
import com.vrv.vap.admin.common.enums.SuperviseDataTypeEnum;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.service.SuperviseDataReceiveService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.util.OauthUtil;
import com.vrv.vap.admin.vo.supervise.ServerInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 1.通过定时任务拉取 监管业务平台的数据接口
 * 2.定时任务上报 监管业务平台 状态上报接口
 */
@Component
@EnableScheduling
@EnableAsync
public class SuperviseServerStatusTask {
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static final Logger logger = LoggerFactory.getLogger(KafkaSenderService.class);
    @Resource
    private KafkaSenderService kafkaSenderService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private SuperviseDataReceiveService superviseDataReceiveService;

    /**
     * 上报上级业务监管平台数据
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    @Async
    public void reportMonitorPlatformStatusToKafka() {
        Map<String, Object> statusMap = new HashMap<>();
        ServerInfo server = getServerInfo();
        // TODO 0：运行故障,  1:运行正常。 需要综合审计监管系统的服务器状态信息。 只上报正常的心跳检测端口。 异常行为需要判断规则 or 服务中断无法上报
        statusMap.put("ssa_run_state", "1");
        statusMap.put("update_time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        statusMap.put("client_id", server.getClientId());
        statusMap.put("type", SuperviseDataTypeEnum.RUN_STATE.getCode());
        kafkaSenderService.send("SuperviseDataSubmit", null, gson.toJson(statusMap));
    }

    /**
     * 拉取接收监管业务平台的数据
     * 1：事件督办任务
     * 2: 风险预警任务
     * 3：事件协办任务
     * 4：协查结果信息
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    @Async
    public void receiveHigherLevelPlatformDataToKafka() {
        ServerInfo serverInfo = getServerInfo();
        // TODO 调用业务平台获取下发数据
        String uri = "/coor/api/routing/announce";

        // 每30分钟定时循环拉取业务平台notice_type的数据
        for (NoticeTypeEnum dataTypeEnum : NoticeTypeEnum.values()) {
            Map<String, String> noticeTypeParamsMap = new HashMap<>();
            String notice_type = dataTypeEnum.getType();
            logger.info(String.format("向监管业务系统拉取接口数据：notice_type:%s", notice_type));
            noticeTypeParamsMap.put("notice_type", notice_type);

            OauthUtil oauthUtil = new OauthUtil(noticeTypeParamsMap);
            Map<String,Object> data = new HashMap<>();
            try {
                data  = oauthUtil.oauth2Data(uri, serverInfo, notice_type);
                if (data != null && !data.isEmpty()) {
                    superviseDataReceiveService.saveAnnounce(data);
                } else {
                    logger.error(String.format("JsonUtil转化的参数异常! code: %s   data:%s", notice_type, data));
                }
            } catch (Exception e) {
                logger.error(String.format("NoticeTypeEnum, code: %s   data:%s", notice_type, data));
                throw new RuntimeException("业务平台下发数据本地存储异常！！", e);
            }
        }
    }

    private ServerInfo getServerInfo() {
        String conf_server_info = systemConfigService.findByConfId("ServerInfo").getConfValue();
        if (StringUtils.isEmpty(conf_server_info)) {
            logger.error("注册信息为空");
            throw new RuntimeException("获取的ServerInfo信息为空.");
        }
        ServerInfo serverInfo = gson.fromJson(conf_server_info, ServerInfo.class);
        if (!serverInfo.getIsRegister()) {
            logger.error("系统未注册");
            throw new RuntimeException("系统未注册,ServerInfo信息中的type为空.");
        }
        return serverInfo;
    }

}
