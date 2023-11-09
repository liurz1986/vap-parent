package com.vrv.vap.netflow.common.task;

import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.model.NetworkMonitorCurrentStatus;
import com.vrv.vap.netflow.service.NetworkMonitorAuditedService;
import com.vrv.vap.netflow.service.NetworkMonitorCurrentStatusService;
import com.vrv.vap.netflow.service.NetworkMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sj
 * @version 1.0
 * @date 2023/10/8 17:07
 * @program: api-netflow
 * @description: 周期检测更新监测器状态
 */

@Component
@Slf4j
public class NetworkMonitorCurrentStatusTask {
    private static final Logger logger = LoggerFactory.getLogger(NetworkMonitorCurrentStatusTask.class);
    @Resource
    private NetworkMonitorCurrentStatusService networkMonitorCurrentStatusService;

    @Resource
    private NetworkMonitorAuditedService networkMonitorAuditedService;

    @Resource
    private NetworkMonitorService networkMonitorService;

static  Boolean isRun=false;



    /**
     * 每1分钟执行一次检测,并更新运行状态值
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkSync() {
        logger.error("并更新运行状态值开始执行");
        if (isRun) {
            return;
        }
        try {


            isRun = true;
            List<NetworkMonitorAudited> reglist = networkMonitorAuditedService.findAll();
            if (reglist == null || reglist.isEmpty()) {
                isRun = false;
                return;
            }
            reglist.forEach(reg -> {

                List<NetworkMonitorCurrentStatus> list = networkMonitorCurrentStatusService.findByids(reg.getDeviceId());

                NetworkMonitorCurrentStatus regStatus = null;
                if (list != null && !list.isEmpty()) {
                    regStatus = list.get(0);

                    //监测时间  如果是30分钟之前的
                    if (regStatus.getUpdate_time() == null || regStatus.getUpdate_time().before(DateUtils.addMinutes(new Date(), -30))) {
                        regStatus.setDeviceStatus(0);
                        regStatus.setDeviceStatusDescription("设备状态异常：设备已断开30分钟");
                    }else{
                        regStatus.setDeviceStatus(1);
                        regStatus.setDeviceStatusDescription("设备连接正常");
                    }
                } else {
                    regStatus = new NetworkMonitorCurrentStatus();
                    regStatus.setDeviceStatus(0);
                    regStatus.setDeviceStatusDescription("设备状态异常：暂无运行状态信息");
                }

                regStatus.setIp(networkMonitorService.getValueFromMapStringByKey("ip", reg.getInterfaceInfo()));
                regStatus.setDeviceBelong(reg.getDeviceBelong());
                regStatus.setDeviceId(reg.getDeviceId());
                regStatus.setDeviceLocation(reg.getDeviceLocation());
                regStatus.setDeviceSoftVersion(reg.getDeviceSoftVersion());

                if (list != null && !list.isEmpty()) {
                    networkMonitorCurrentStatusService.update(regStatus);
                } else {
                    networkMonitorCurrentStatusService.save(regStatus);
                }

            });
        }catch (Exception ex ){
            logger.error("状态更新异常",ex);
        }
        finally {
            isRun = false;
        }

        isRun = false;

    }

}
