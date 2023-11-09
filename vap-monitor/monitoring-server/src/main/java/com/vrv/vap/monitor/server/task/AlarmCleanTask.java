package com.vrv.vap.monitor.server.task;

import com.vrv.vap.monitor.server.common.util.SpringContextUtil;
import com.vrv.vap.monitor.server.mapper.AlarmCollectionMapper;
import com.vrv.vap.monitor.server.model.AlarmItem;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Calendar;

@Component
public class AlarmCleanTask extends BaseTask {

    @Override
    void run(String jobName, JobDataMap jobDataMap) {
        log.info("Alarm Clean start");
        AlarmCollectionMapper alarmCollectionMapper = SpringContextUtil.getBean(AlarmCollectionMapper.class);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -180);
        Example example = new Example(AlarmItem.class);
        example.createCriteria().andLessThan("alarmTime", date.getTime());
        alarmCollectionMapper.deleteByExample(example);
    }

    @Override
    public void run(String jobName) {
        this.run(jobName, null);
    }
}
