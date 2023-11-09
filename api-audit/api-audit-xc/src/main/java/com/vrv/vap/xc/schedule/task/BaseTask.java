package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.schedule.TaskLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTask implements Job {
    public static final Logger log = Logger.getLogger(BaseTask.class);
    /**
     * 保存正在执行的任务,防止重复运行
     */
    private static final Map<String, String> TASKS = new HashMap<>();

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        String jobClassName = arg0.getJobDetail().getJobClass().getName();
        JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();
        if (null != jobDataMap.get(TaskLoader.ID_KEY)) {
            jobClassName = jobClassName + "_" + jobDataMap.get(TaskLoader.ID_KEY);
        }

        Date nowTime = TimeTools.getNow();
        Date startTime = StringUtils.isNotEmpty(TASKS.get(jobClassName)) ? TimeTools.parseDate2(TASKS.get(jobClassName)) : nowTime;
        if (startTime.before(nowTime)) {
            String val = TASKS.remove(jobClassName);
            log.info(new StringBuffer().append("before task fail force remove job:").append(jobClassName).append(" start at:").append(val)
                    .append(" remove at:").append(TimeTools.format(TimeTools.getNow())));
        }
        if (!TASKS.containsKey(jobClassName)) {
            log.info("start " + jobClassName);

            TASKS.put(jobClassName, TimeTools.format2(TimeTools.getNow()));
            if (jobDataMap.size() != 0) {
                this.run(jobClassName, jobDataMap);
            } else {
                this.run(jobClassName);
            }

            String val = TASKS.remove(jobClassName);

            log.info("remove job:" + jobClassName + " start at:" + val + " remove at:" + TimeTools.format(TimeTools.getNow()));
        } else {
            log.info("job " + jobClassName + " already running,start at:" + TASKS.get(jobClassName));

        }
    }

    abstract void run(String jobName);

    void run(String jobName, JobDataMap jobDataMap) {
        this.run(jobName);
    }
}
