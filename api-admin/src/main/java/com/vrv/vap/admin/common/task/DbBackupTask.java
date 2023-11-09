package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.DbOperationInfo;
import com.vrv.vap.admin.service.DbOperationService;
import org.slf4j.Logger;
import org.quartz.JobDataMap;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DbBackupTask extends BaseTask{
    @Value("${filePath:/opt/file/backup}")
    private String filePath;

    private static Logger log = LoggerFactory.getLogger(DbBackupTask.class);

    @Override
    void run(String jobName, JobDataMap jobDataMap) {
        DbOperationService dbOperationService = SpringContextUtil.getApplicationContext().getBean(DbOperationService.class);
        String dataTypes = jobDataMap.get("dataTypes").toString();
        DbOperationInfo dbOperationInfo = new DbOperationInfo();
        dbOperationInfo.setDataTypes(dataTypes);
        log.info("开始备份");
        dbOperationService.backup(dbOperationInfo);
        log.info("备份完成");

        Example example = new Example(DbOperationInfo.class);
        example.createCriteria().andIn("operationType", Arrays.asList(1,3)).andEqualTo("dataTypes", dataTypes).andEqualTo("operationStatus", 1);
        List<DbOperationInfo> dbOperationInfos = dbOperationService.findByExample(example);
        for (DbOperationInfo operationInfo : dbOperationInfos) {
            if ((int) (new Date().getTime() - operationInfo.getEndTime().getTime())/(1000*60*60*24L) > Integer.parseInt(jobDataMap.get("expireTime").toString())) {
                FileUtils.deleteFile(Paths.get(filePath, operationInfo.getFileName()).toString());
                operationInfo.setOperationStatus(4);
                dbOperationService.update(operationInfo);
            }
        }
    }

    @Override
    void run(String jobName) {
        this.run(jobName, null);
    }
}

