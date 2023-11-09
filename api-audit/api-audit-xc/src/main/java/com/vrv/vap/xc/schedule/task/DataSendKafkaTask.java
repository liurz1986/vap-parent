package com.vrv.vap.xc.schedule.task;


import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.service.XgsDataChannelService;

/**
 * 定时任务调用esclient 读取es数据发送kafka
 */
public class DataSendKafkaTask extends BaseTask {

    @Override
    void run(String jobName) {
        XgsDataChannelService channelService = VapXcApplication.getApplicationContext().getBean(XgsDataChannelService.class);
        channelService.esDataSendKafka();
    }
}
