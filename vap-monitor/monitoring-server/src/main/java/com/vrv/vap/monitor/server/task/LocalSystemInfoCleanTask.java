package com.vrv.vap.monitor.server.task;

import com.vrv.vap.monitor.server.common.util.SpringContextUtil;
import com.vrv.vap.monitor.server.mapper.LocalSystemInfoMapper;
import com.vrv.vap.monitor.server.model.LocalSystemInfo;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Calendar;

@Component
public class LocalSystemInfoCleanTask extends BaseTask {

    @Override
    void run(String jobName, JobDataMap jobDataMap) {
        LocalSystemInfoMapper localSystenInfoMapper = SpringContextUtil.getBean(LocalSystemInfoMapper.class);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -7);
        //simpleDateFormat.format(date.getTime());
        Example example = new Example(LocalSystemInfo.class);
        example.createCriteria().andLessThan("createTime", date.getTime());
        localSystenInfoMapper.deleteByExample(example);

    }

    @Override
    public void run(String jobName) {
        this.run(jobName, null);
    }
}
