package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.model.NetworkMonitor;
import com.vrv.vap.admin.model.NetworkMonitorReport;
import com.vrv.vap.admin.model.NetworkMonitorStatus;
import com.vrv.vap.admin.service.MonitorLogService;
import com.vrv.vap.admin.service.NetworkMonitorReportService;
import com.vrv.vap.admin.service.NetworkMonitorService;
import com.vrv.vap.admin.service.NetworkMonitorStatusService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lilang
 * @date 2021/8/12
 * @description
 */
@Service
@Transactional
public class MonitorLogServiceImpl implements MonitorLogService {


    private ExecutorService exec = Executors.newFixedThreadPool(10);

    @Autowired
    NetworkMonitorService networkMonitorService;

    @Autowired
    NetworkMonitorStatusService networkMonitorStatusService;

    @Autowired
    NetworkMonitorReportService networkMonitorReportService;

    @Override
    public void saveBaseInfo(Map map) {
        exec.execute(() -> {
            this.updateStatus(map);
        });
    }



    private void  updateStatus(Map map){
        if (map.get("device_id") != null) {
            String deviceId = map.get("device_id").toString();
            List<NetworkMonitor> networkMonitorList = networkMonitorService.findByProperty(NetworkMonitor.class,"deviceId",deviceId);
            if (CollectionUtils.isNotEmpty(networkMonitorList)) {
                NetworkMonitor monitorReport = networkMonitorList.get(0);
                if (map.get("device-belong") != null) {
                    monitorReport.setDeviceBelong(map.get("device-belong").toString());
                }
                if (map.get("device_location") != null) {
                    monitorReport.setDeviceLocation(map.get("device_location").toString());
                }
                if (map.get("device_soft_version") != null) {
                    monitorReport.setDeviceSoftVersion(map.get("device_soft_version").toString());
                }
                if (map.get("device_port_id") != null) {
                    monitorReport.setDevicePortId(Integer.valueOf(map.get("device_port_id").toString()));
                }
                if (map.get("interface_icon") != null) {
                    monitorReport.setInterfaceIcon(map.get("interface_icon").toString());
                }
                if (map.get("data_type") != null) {
                    monitorReport.setDataType(Integer.valueOf(map.get("data_type").toString()));
                }
                if (map.get("device_sys_version") != null) {
                    monitorReport.setDeviceSysVersion(map.get("device_sys_version").toString());
                }
                if (map.get("device_soft_version") != null) {
                    monitorReport.setDeviceSoftVersion(map.get("device_soft_version").toString());
                }
                if (map.get("device_bios_version") != null) {
                    monitorReport.setDeviceBiosVersion(map.get("device_bios_version").toString());
                }
                if (map.get("device_cpu_core") != null) {
                    monitorReport.setDeviceCpuCore(Integer.valueOf(map.get("device_cpu_core").toString()));
                }
                if (map.get("device_cpu_usage") != null) {
                    monitorReport.setDeviceCpuUsage(Integer.valueOf(map.get("device_cpu_usage").toString()));
                }
                if (map.get("device_mem_size") != null) {
                    monitorReport.setDeviceMemSize(Integer.valueOf(map.get("device_mem_size").toString()));
                }
                if (map.get("device_hdisk_size") != null) {
                    monitorReport.setDeviceHdiskSize(Integer.valueOf(map.get("device_hdisk_size").toString()));
                }
                if (map.get("device_hdisk_num") != null) {
                    monitorReport.setDeviceHdiskNum(map.get("device_hdisk_num").toString());
                }
                if (map.get("device_mem_usage") != null) {
                    monitorReport.setDeviceMemUsage(Integer.valueOf(map.get("device_mem_usage").toString()));
                }
                if (map.get("device_hdisk_usage") != null) {
                    monitorReport.setDeviceHdiskUsage(Integer.valueOf(map.get("device_hdisk_usage").toString()));
                }
                monitorReport.setReportTime(new Date());
                monitorReport.setNetworkMonitorStatus(1);
                networkMonitorService.update(monitorReport);
            }

        }

    }
    /**
     * 保存基础数据
     * @param map
     */
    public void saveMonitorInfo(Map map) {
        NetworkMonitorReport monitorReport = new NetworkMonitorReport();
        if (map.get("device_id") != null) {
            String deviceId = map.get("device_id").toString();
            monitorReport.setDeviceId(deviceId);
            networkMonitorReportService.deleteByDeviceId(deviceId);
        }
        if (map.get("device-belong") != null) {
            monitorReport.setDeviceBelong(map.get("device-belong").toString());
        }
        if (map.get("device_location") != null) {
            monitorReport.setDeviceLocation(map.get("device_location").toString());
        }
        if (map.get("device_soft_version") != null) {
            monitorReport.setDeviceSoftVersion(map.get("device_soft_version").toString());
        }
        if (map.get("device_port_id") != null) {
            monitorReport.setDevicePortId(Integer.valueOf(map.get("device_port_id").toString()));
        }
        if (map.get("interface_icon") != null) {
            monitorReport.setInterfaceIcon(map.get("interface_icon").toString());
        }
        if (map.get("data_type") != null) {
            monitorReport.setDataType(Integer.valueOf(map.get("data_type").toString()));
        }
        monitorReport.setReportTime(new Date());
        networkMonitorReportService.save(monitorReport);
    }

    public void saveMonitorStatusInfo(Map map) {
        NetworkMonitorStatus networkMonitorStatus = new NetworkMonitorStatus();
        if (map.get("device_id") != null) {
            networkMonitorStatus.setDeviceId(map.get("device_id").toString());
        }
        if (map.get("device_sys_version") != null) {
            networkMonitorStatus.setDeviceSysVersion(map.get("device_sys_version").toString());
        }
        if (map.get("device_soft_version") != null) {
            networkMonitorStatus.setDeviceSoftVersion(map.get("device_soft_version").toString());
        }
        if (map.get("device_bios_version") != null) {
            networkMonitorStatus.setDeviceBiosVerion(map.get("device_bios_version").toString());
        }
        if (map.get("device_cpu_core") != null) {
            networkMonitorStatus.setDeviceCpuCore(Integer.valueOf(map.get("device_cpu_core").toString()));
        }
        if (map.get("device_cpu_usage") != null) {
            networkMonitorStatus.setDeviceCpuUsage(Integer.valueOf(map.get("device_cpu_usage").toString()));
        }
        if (map.get("device_mem_size") != null) {
            networkMonitorStatus.setDeviceMemSize(Integer.valueOf(map.get("device_mem_size").toString()));
        }
        if (map.get("device_hdisk_size") != null) {
            networkMonitorStatus.setDeviceHdiskSize(Integer.valueOf(map.get("device_hdisk_size").toString()));
        }
        if (map.get("device_hdisk_num") != null) {
            networkMonitorStatus.setDeviceHdiskNum(map.get("device_hdisk_num").toString());
        }
        if (map.get("device_mem_usage") != null) {
            networkMonitorStatus.setDeviceMemUsage(Integer.valueOf(map.get("device_mem_usage").toString()));
        }
        if (map.get("device_hdisk_usage") != null) {
            networkMonitorStatus.setDeviceHdiskUsage(Integer.valueOf(map.get("device_hdisk_usage").toString()));
        }
        networkMonitorStatus.setReportTime(new Date());
        networkMonitorStatusService.save(networkMonitorStatus);
    }
}

