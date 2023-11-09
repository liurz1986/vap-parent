package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.ExecutorConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job.thread.AlarmThread;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataEntryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * @author 梁国露
 * @date 2021年11月01日 15:28
 */
@Component
@Order(value = 1)
public class AlarmDataSaveJob implements CommandLineRunner {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(AlarmDataSaveJob.class);

    /**
     * 开始时间
     */
    private Date startTime = new Date();

    @Autowired
    private AlarmDataEntryService alarmDataEntryService;

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

    private List<WarnResultLogTmpVO> saveList = new CopyOnWriteArrayList<>();

    private Executor executor = ExecutorConfig.alarmExtentServiceExecutor();

    /**
     * 实现run方法
     *
     * @param args 参数
     * @throws Exception 异常
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("AlarmDataSaveJob start");
        // 初始化数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAlarmData();
            }
        }).start();

    }

    /**
     * 获得时间
     *
     * @return
     */
    private boolean getTimeResult() {
        Date endTime = new Date();
        long timeSpan = (endTime.getTime() - startTime.getTime()) / 1000;
        if (timeSpan > 10) {
            startTime = new Date();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取数据方法
     */
    public void getAlarmData() {
        // 死循环，一直处理
        while (true) {
            // 获取队列告警数据
            try {
                WarnResultLogTmpVO warnResultLogTmpVO = QueueUtil.poll();
                if (warnResultLogTmpVO != null) {
                    saveList.add(warnResultLogTmpVO);
                }
                // 每500条处理一次或者10s处理一次
                boolean isFiveMin = getTimeResult();
                if (isFiveMin || saveList.size() > 500) {
                    logger.debug("saveList队列当中的数据为 :{}", saveList.size());
                    if (CollectionUtils.isNotEmpty(saveList)) {
                        List<WarnResultLogTmpVO> newList = new CopyOnWriteArrayList<>();
                        newList.addAll(saveList);
                        executor.execute(new AlarmThread(alarmDataEntryService,alarmDataHandleService,newList));
                        logger.warn("AlarmDataSaveJob handleAlarmData success");
                    }
                    saveList.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("保存数据失败，请检查！出错原因：{}", e.getMessage());
            }
        }
    }
}
