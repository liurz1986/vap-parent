package com.vrv.vap.admin.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.flume.cmd.FlumeTools;
import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.service.CollectorDataAccessService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
@EnableAsync
public class FlumeCollectorMetricTask {

    @Autowired
    private CollectorDataAccessService collectorDataAccessService;

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static Logger logger = LoggerFactory.getLogger(KafkaSenderService.class);

    @Scheduled(cron = "0 0/3 * * * ?")
    @Async
    public void updateCollectorDataAccessByFlumeMetric() {
        logger.info("updateCollectorDataAccessByFlumeMetric task started");
        List<CollectorDataAccess> collectorDataAllList = collectorDataAccessService.findAll();
        for (CollectorDataAccess collectorDataAccess : collectorDataAllList) {
            String cid = collectorDataAccess.getCid();
            // 上一次保存的接收的时间
            String latestReceiveMgsTime = collectorDataAccess.getLatestReceiveMgsTime();
            // 上一次保存的接收的数据量
            Double previousReceiveCount = collectorDataAccess.getLatestReceiveMaxCount();
            Double currentReceiveCount = 0.0;
            FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
            // TODO 获取flume监控数据 ， "{\"source\":[],\"channel\":[],\"sink\":[]}"
            String content = flumeTools.getMetricDetail(cid);
            if (StringUtils.isNotEmpty(content)) {
                currentReceiveCount = getCurrentReceiveCount(content);
                boolean needUpdateFlag = compareReceiveCount(currentReceiveCount, previousReceiveCount);
                logger.debug("currentReceiveCount:{}, previousReceiveCount: {}, needUpdateFlag:{}", currentReceiveCount, previousReceiveCount, needUpdateFlag);
                if (needUpdateFlag) {
                    // 更新采集器数据接入表数据信息，更新接收数据量和接收时间
                    collectorDataAccess.setLatestReceiveMaxCount(currentReceiveCount);
                    collectorDataAccess.setLatestReceiveMgsTime(DateUtils.getTime());
                    collectorDataAccessService.updateSelective(collectorDataAccess);
                } else {
                    logger.debug("updateCollectorDataAccessByFlumeMetric task not need update");
                    // TODO fix 重启flume后，source的计算值被重置了，变成0了。 需要更新一下接收的总数据量
                    collectorDataAccess.setLatestReceiveMaxCount(currentReceiveCount);
                    collectorDataAccessService.updateSelective(collectorDataAccess);
                }
            } else {
                logger.debug("实时获取flume监控数据失败，content为空");
            }
        }
    }

    /**
     * @param currentReceiveCount  当前获取flume的接收数据，实时接口
     * @param previousReceiveCount 当前获取之前存入mysql的接收数据，以前的最新数据
     */
    private boolean compareReceiveCount(Double currentReceiveCount, Double previousReceiveCount) {
        // 比较数据，如果当前接收数据量大于之前的数据量，则数据量有更新，并更新接收时间
        if (currentReceiveCount > previousReceiveCount) {
            // TODO 更新采集器数据接入表数据信息，更新接收数据量和接收时间
            return true;
        }
        return false;
    }

    /**
     * 解析并获取当前接收的数据量
     */
    private Double getCurrentReceiveCount(String content) {
        Map<String, Object> data = JSON.parseObject(content, new TypeReference<Map<String, Object>>() {
        });
        double eventReceivedCount = 0;
        List<Map<String, Object>> sources = (List<Map<String, Object>>) data.get("source");
        if (sources != null && sources.size() > 0) {
            Map<String, Object> source = sources.get(0);
            String o = String.valueOf(source.get("eventReceivedCount"));
            if (StringUtils.isNotEmpty(o)) {
                eventReceivedCount = Double.parseDouble(o);
            }
        }
        return eventReceivedCount;
    }

}
