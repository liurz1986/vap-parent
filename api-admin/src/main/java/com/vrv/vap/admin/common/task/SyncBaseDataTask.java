package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.*;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lilang
 * @date 2022/4/21
 * @description
 */
public class SyncBaseDataTask extends BaseTask {

    private static final Logger logger = LoggerFactory.getLogger(SyncBaseDataTask.class);

    SyncBaseDataService syncBaseDataService = SpringContextUtil.getApplicationContext().getBean(SyncBaseDataService.class);

    @Autowired
    private ThreeInOneAssetProducerService threeInOneAssetProducerService = SpringContextUtil.getApplicationContext().getBean(ThreeInOneAssetProducerService.class);
    @Autowired
    private PermitAssetProducerService permitAssetProducerService = SpringContextUtil.getApplicationContext().getBean(PermitAssetProducerService.class);
    @Autowired
    private MachineAuditAssetProducerService machineAuditAssetProducerService = SpringContextUtil.getApplicationContext().getBean(MachineAuditAssetProducerService.class);

    private PersonProducerService personProducerService = SpringContextUtil.getApplicationContext().getBean(PersonProducerService.class);

    private FileProducerService fileProducerService = SpringContextUtil.getApplicationContext().getBean(FileProducerService.class);

    private OperationManageAssetProducerService operationManageAssetProducerService = SpringContextUtil.getApplicationContext().getBean(OperationManageAssetProducerService.class);

    private OperationManageOrgProducerService operationManageOrgProducerService = SpringContextUtil.getApplicationContext().getBean(OperationManageOrgProducerService.class);

    private ServerAuditProducerService serverAuditProducerService = SpringContextUtil.getApplicationContext().getBean(ServerAuditProducerService.class);

    private SafeKitAssetProducerService safeKitAssetProducerService = SpringContextUtil.getApplicationContext().getBean(SafeKitAssetProducerService.class);

    private SafeKitAppProducerService safeKitAppProducerService = SpringContextUtil.getApplicationContext().getBean(SafeKitAppProducerService.class);

    private SafeKitEventLogService safeKitEventLogService = SpringContextUtil.getApplicationContext().getBean(SafeKitEventLogService.class);

    private static final String TYPE_PERSON = "person";

    private static final String TYPE_ASSET = "asset";

    private static final String TYPE_APP = "app";

    private static final String TYPE_FILE = "file";

    private static final String TYPE_LOG = "log";

    @Override
    void run(String jobName, JobDataMap jobDataMap) {
        if (null == jobDataMap) {
            logger.error("参数异常");
            return;
        }
        String taskId = jobDataMap.getString("id");
        SyncBaseData syncBaseData = syncBaseDataService.findById(Integer.valueOf(taskId));
        String type = syncBaseData.getType();
        String source = syncBaseData.getSource();
        String[] sources = source.split("-");
        if (sources.length < 2) {
            logger.error("数据来源参数配置异常");
            return;
        }
        if (TYPE_ASSET.equals(type)) {
            if (SyncSourceConstants.RY.equals(sources[1])) {
                threeInOneAssetProducerService.produce(syncBaseData);
            }
            if (SyncSourceConstants.ZR.equals(sources[1])) {
                permitAssetProducerService.produce(syncBaseData);
            }
            if (SyncSourceConstants.ZS.equals(sources[1])) {
                machineAuditAssetProducerService.produce(syncBaseData);
            }
            if (SyncSourceConstants.YG.equals(sources[1])) {
                operationManageAssetProducerService.produce(syncBaseData);
            }
            if (SyncSourceConstants.FS.equals(sources[1])) {
                serverAuditProducerService.produce(syncBaseData);
            }
            if (SyncSourceConstants.TJ.equals(sources[1])) {
                safeKitAssetProducerService.produce(syncBaseData);
            }
        }
        if (TYPE_PERSON.equals(type)) {
            if (SyncSourceConstants.GMP.equals(sources[1]) || SyncSourceConstants.TJ.equals(sources[1])) {
                personProducerService.produce(syncBaseData);
            }
            if (SyncSourceConstants.YG.equals(sources[1])) {
                operationManageOrgProducerService.produce(syncBaseData);
            }
        }
        if (TYPE_APP.equals(type)) {
            safeKitAppProducerService.produce(syncBaseData);
        }
        if (TYPE_FILE.equals(type)) {
            if (SyncSourceConstants.MB.equals(sources[1])) {
                fileProducerService.produce(syncBaseData);
            }
        }
        if (TYPE_LOG.equals(type)) {
            safeKitEventLogService.produce(syncBaseData);
        }
    }

    @Override
    void run(String jobName) {
        this.run(jobName, null);
    }
}
