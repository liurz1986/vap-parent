package com.vrv.vap.alarmdeal.business.evaluation.job;

import com.vrv.vap.alarmdeal.business.evaluation.service.EventDataService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EventQueUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.EventResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 自查自评数据来结果生成任务
 *
 * 事件处置完后信息，自查自评数据来源
 *
 * @Date 2023-09
 * @author liurz
 */
@Component
@Order(value = 20)
public class EventDataJob implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(EventDataJob.class);
    @Autowired
    private EventDataService eventDataService;
    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                excEventDataSync();
            }
        }).start();
    }

    private void excEventDataSync() {
        //  死循环，一直处理
        while (true) {
            try {
                //获取队列数据
                EventResultVO data = EventQueUtil.poll();
                if(null == data){
                    continue;
                }
                eventDataService.process(data);
            } catch (Exception e) {
                logger.error("自查自评数据来结果生成任务处理异常", e);
            }
        }
    }
}
