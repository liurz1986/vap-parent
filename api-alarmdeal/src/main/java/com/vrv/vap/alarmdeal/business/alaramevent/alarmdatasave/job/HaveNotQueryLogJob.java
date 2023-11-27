package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.ExecutorConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.HaveAlarmLogService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.jpa.web.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author 梁国露
 * @date 2021年11月02日 16:59
 */
@Component
@Order(value = 5)
public class HaveNotQueryLogJob implements CommandLineRunner {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(HaveNotQueryLogJob.class);

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

    @Autowired
    private HaveAlarmLogService haveAlarmLogService;

    @Autowired
    private EventTabelService eventTabelService;

    private Map<String, List<EventTable>> eventTableMap = new ConcurrentHashMap<>();

    /**
     * 开始时间
     */
    private Date startTime = new Date();

    List<SearchHit> hiss = new CopyOnWriteArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleData();
            }
        }).start();
    }

    /**
     * 初始化
     */
    public void init(){
        // 判断缓存
        if(CommomLocalCache.containsKey("eventTable")){
            eventTableMap = CommomLocalCache.get("eventTable");
        } else {
            // 缓存中不存在，查询数据
            Result<List<EventTable>> eventTableResult = eventTabelService.getAllEventTable();
            List<EventTable> eventTableList = eventTableResult.getList();
            if(CollectionUtils.isNotEmpty(eventTableList)){
                // 查询新数据后，更新缓存
                eventTableMap = eventTableList.parallelStream().collect(Collectors.groupingBy(EventTable::getName));
                CommomLocalCache.put("eventTable",eventTableMap,2, TimeUnit.HOURS);
            }
        }
        logger.debug("AlarmDataHandleService getEventTableMap result success");
    }

    /**
     * 处理数据
     */
    private void handleData(){
        init();
        logger.info("HaveNotQueryLogJob handleData start");
        while (true){
            // 1、查询es告警数据,筛选logs为空的数据
            List<SearchHit> list = haveAlarmLogService.queryAlarmDataForEs();

            if(CollectionUtils.isNotEmpty(list)){
                hiss.addAll(list);
            }
            // 每100条处理一次或者5s处理一次
            boolean isFiveMin = getTimeResult();
            try {
                if (isFiveMin || hiss.size() > 100) {
                    logger.info("HaveNotQueryLogJob handleData SearchHit size = {}",hiss.size());
                    if(CollectionUtils.isNotEmpty(hiss)){
                        // 2、匹配日志数据进行补充
                        List<AlarmEventAttribute> alarmEventAttributes = haveAlarmLogService.haveAlarmLogData(hiss,eventTableMap);
                        logger.info("HaveNotQueryLogJob handleData AlarmEventAttribute size = {}",alarmEventAttributes.size());
                        alarmEventAttributes = alarmEventAttributes.stream().filter(item->item.getFileInfos()!=null).collect(Collectors.toList());
                        // 3、推送到es
                        alarmDataHandleService.pushAlarmData(alarmEventAttributes);
                    }
                    hiss.clear();
                }
            }catch (Exception ex){
                logger.error("HaveNotQueryLogJob haveAlarmLogData error={}",ex);
            }
        }
    }

    /**
     * 获得时间
     *
     * @return
     */
    private boolean getTimeResult() {
        Date endTime = new Date();
        long timeSpan = (endTime.getTime() - startTime.getTime()) / 1000 / 60;
        if (timeSpan > 3) {
            startTime = new Date();
            return true;
        } else {
            return false;
        }
    }

}
