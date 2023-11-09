package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.custom.DataCleanMapper;
import com.vrv.vap.xc.service.CommonService;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据备份清理转储定时任务
 * Created by lizj on 2021/1/7
 */
public class DataDumpTask extends BaseTask {

    private CommonService commonService = VapXcApplication.getApplicationContext().getBean(CommonService.class);

    private DataCleanMapper dataCleanDao = VapXcApplication.getApplicationContext().getBean(DataCleanMapper.class);

    @Override
    void run(String jobName) {
        // 磁盘使用率默认80
        double esDiskUsedThreshold = 80d;
        String esDiskUsedThresholdStr = dataCleanDao.getConfById("es_disk_used_threshold");
        if (StringUtils.isNotEmpty(esDiskUsedThresholdStr)) {
            esDiskUsedThreshold = Double.parseDouble(esDiskUsedThresholdStr);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("diskUsedPercentThreshold", esDiskUsedThreshold);
        commonService.dataBackupAndCLean(param);
    }
}
