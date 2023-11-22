package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.custom.DataCleanMapper;
import com.vrv.vap.xc.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据备份清理转储定时任务
 * Created by lizj on 2021/1/7
 */
public class DataDumpTask extends BaseTask {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpTask.class);

    private CommonService commonService = VapXcApplication.getApplicationContext().getBean(CommonService.class);

    private DataCleanMapper dataCleanDao = VapXcApplication.getApplicationContext().getBean(DataCleanMapper.class);

    @Override
    void run(String jobName) {
        // 磁盘使用率默认80
        logger.warn(String.format("Elasticsearch开始执行数据备份定时任务, jobName: %s, 时间：%s", jobName,
                DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")));
        double esDiskUsedThreshold = 80d;
        String esDiskUsedThresholdStr = dataCleanDao.getConfById("es_disk_used_threshold");
        if (StringUtils.isNotEmpty(esDiskUsedThresholdStr)) {
            esDiskUsedThreshold = Double.parseDouble(esDiskUsedThresholdStr);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("diskUsedPercentThreshold", esDiskUsedThreshold);
        commonService.dataBackupAndCLean(param);
        logger.warn(String.format("Elasticsearch结束执行数据备份清理转储, jobName: %s, 时间：%s", jobName,
                DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")));
    }
}
