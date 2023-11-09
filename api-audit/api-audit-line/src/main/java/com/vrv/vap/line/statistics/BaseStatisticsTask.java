package com.vrv.vap.line.statistics;

import com.vrv.vap.line.schedule.task.BaseLineTask;
import com.vrv.vap.line.schedule.task.BaseTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.util.Map;

public abstract class BaseStatisticsTask {
    public static final Log log = LogFactory.getLog(BaseLineTask.class);

    abstract void execute(Map<String,Object> params);
}
