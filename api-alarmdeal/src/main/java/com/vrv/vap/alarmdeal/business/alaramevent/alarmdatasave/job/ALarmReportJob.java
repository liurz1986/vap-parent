package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementService;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Order(value = 10)
public class ALarmReportJob implements CommandLineRunner {
    private static Logger logger= LoggerFactory.getLogger(ALarmReportJob.class);

    private static final String ALARM_REPORT_TIME_KEY = "ALARM_REPORT_TIME_KEY";

    @Resource
    AlarmEventManagementForESService alarmEventManagementForEsService;

    @Resource
    private AlarmDataHandleService alarmDataHandleService;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void run(String... args) throws Exception {
        new Thread(new ReportThread()).start();
    }


    public class ReportThread implements Runnable{
        @Override
        public void run() {
            while (true){
                try {
                    Object time = redisUtil.get(ALARM_REPORT_TIME_KEY);
                    if(time==null){
                        time = DateUtil.format(new Date());
                        redisUtil.set(ALARM_REPORT_TIME_KEY,time);
                    }
                   //TODO 考虑是不是用一个状态位来控制，如果正在执行，就不执行，如果没有执行，就执行
                    String begin  = time.toString();
                    Date endTime  = DateUtil.addMinutes(new Date(),30);
                    redisUtil.set(ALARM_REPORT_TIME_KEY,DateUtil.format(endTime));  //更新缓存单中的时间结束点数据
                    List<QueryCondition_ES> conditions =  new ArrayList<>();
                    String end = DateUtil.format(endTime);
                    conditions.add(QueryCondition_ES.between("eventCreattime",begin,end));
                    List<AlarmEventAttribute> alarmEventAttributes = alarmEventManagementForEsService.findAll(conditions);
                    alarmDataHandleService.pushSuperviseData(alarmEventAttributes);
                    logger.info("完成数据上报,上报数据总数：{}条",alarmEventAttributes.size());
                    Thread.sleep(1000*60*30);  //执行完成睡眠30min
                } catch (Exception e) {
                   logger.error("数据上报异常，异常信息：{},请检查！",e);
                }
            }
        }
    }
}
