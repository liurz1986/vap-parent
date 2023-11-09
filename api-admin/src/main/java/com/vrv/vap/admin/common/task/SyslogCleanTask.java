package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.model.SysLog;
import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.service.SysLogService;
import com.vrv.vap.admin.service.SystemConfigService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author lilang
 * @date 2022/11/21
 * @description 系统日志清理任务
 */
@Component
public class SyslogCleanTask {

    @Resource
    SystemConfigService systemConfigService;

    @Resource
    SysLogService sysLogService;

    private static final String SYS_LOG_CLEAN = "sys_log_clean";

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanSyslog() {
        SystemConfig systemConfig = systemConfigService.findByConfId(SYS_LOG_CLEAN);
        if (systemConfig != null && "1".equals(systemConfig.getConfEnable().toString())) {
            String confValue = systemConfig.getConfValue();
            if (Integer.valueOf(confValue) > 0) {
                sysLogService.cleanSyslog(Integer.valueOf(confValue));
            }
            // 保存清理日志
            SysLog syslog = this.getSysLog();
            sysLogService.save(syslog);
        }
    }

    private SysLog getSysLog() {
        SysLog sysLog = new SysLog();
        sysLog.setRequestTime(new Date());
        sysLog.setType(4);
        sysLog.setRequestMethod("DELETE");
        sysLog.setRequestIp("127.0.0.1");
        sysLog.setResponseResult(1);
        sysLog.setLoginType(0);
        sysLog.setUserId("000000000000000000");
        sysLog.setOrganizationName("");
        sysLog.setUserName("系统超级管理员");
        sysLog.setRoleName("平台维护员");
        sysLog.setId(java.util.UUID.randomUUID().toString());
        sysLog.setDescription("定时清理审计日志");
        return sysLog;
    }
}
