package com.vrv.vap.netflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.netflow.model.NetworkMonitor;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.model.NetworkMonitorCurrentStatus;
import com.vrv.vap.netflow.model.NetworkMonitorRegAuditLog;
import com.vrv.vap.netflow.service.*;
import com.vrv.vap.netflow.utils.CompareFiledUtil;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

    private static final Logger log = LoggerFactory.getLogger(MonitorLogServiceImpl.class);

    private ExecutorService exec = Executors.newFixedThreadPool(10);

    @Autowired
    NetworkMonitorService networkMonitorService;

    @Autowired
    NetworkMonitorAuditedService networkMonitorAuditedService;

    @Autowired
    NetworkMonitorRegAuditLogService networkMonitorRegAuditLogService;
    @Autowired
    NetworkMonitorCurrentStatusService networkMonitorCurrentStatusService;
    @Autowired
    NetworkMonitorStatusService networkMonitorStatusService;

    @Autowired
    NetworkMonitorReportService networkMonitorReportService;

    private static String[] parsePatterns = {
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd",
            "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy-MM-dd", "yyyy.MM"};

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static Type cpuListType = new TypeToken<List<RegStatusCpuDeatil>>() {
    }.getType();

    private static Type diskListType = new TypeToken<List<DiskInfo>>() {
    }.getType();

    @Data
    public class RegStatusCpuDeatil {
        private Integer physical_id;
        private Float cpu_usage;
    }
    @Data
    public  class  DiskInfo{
        //[{"size":4000,"serial":"V1HD4VNG"}]

        Double size;
        String serial;
    }


    // 在线注册
    private static final Integer REGTYPE_ONLINE = 1;
    // 在线
    private static final Integer NETWORK_STATUS_ONLINE = 1;
    // 离线
    private static final Integer NETWORK_STATUS_OFFLINE = 0;
    // 正常
    private static final Integer STATUS_NORMAL = 0;


    @Override
    public Integer updateStatusNew(Map statusMap) {
        if (statusMap.get("device_id") != null) {
            Date time = new Date();
            try {
                if (statusMap.containsKey("time")) {
                    time = DateUtils.parseDate(statusMap.get("time").toString(), parsePatterns);
                } else {
                    log.error("状态信息没有time字段");
                    return 0;
                }
            } catch (Exception ex) {
                log.error("时间转换出现异常", ex);
            }


            String deviceId = statusMap.get("device_id").toString();
            NetworkMonitorAudited regAudited = networkMonitorAuditedService.getItem(deviceId);
            if (regAudited != null) {
                List<NetworkMonitorCurrentStatus> list = networkMonitorCurrentStatusService.findByids(regAudited.getDeviceId());

                NetworkMonitorCurrentStatus regStatus = null;
                if (list != null && !list.isEmpty()) {
                    regStatus = list.get(0);

                    if(regStatus.getUpdate_time()==null){
                        regStatus.setUpdate_time(DateUtils.addYears(new Date(),-100));
                    }

                    //如果推送的是历史数据 不变更状态 跳过后续步骤
                    if (time.before(regStatus.getUpdate_time())) {
                        return 1;
                    }
                } else {
                    regStatus = new NetworkMonitorCurrentStatus();

                    //如果日志是30分钟之前的  则状态为异常
                    if(time.before(DateUtils.addMinutes(new Date(),-30))) {
                        regStatus.setDeviceStatus(0);
                        regStatus.setDeviceStatusDescription("设备状态异常：设备已断开");
                    }
                }

                regStatus.setIp(networkMonitorService.getValueFromMapStringByKey("ip", regAudited.getInterfaceInfo()));
                regStatus.setDeviceBelong(regAudited.getDeviceBelong());
                regStatus.setDeviceId(regAudited.getDeviceId());
                regStatus.setDeviceLocation(regAudited.getDeviceLocation());
                regStatus.setDeviceSoftVersion(regAudited.getDeviceSoftVersion());
                regStatus.setUpdate_time(time);

                try {
                    DecimalFormat df = new DecimalFormat("#.00");

                    regStatus.setDeviceStatus(1);
                    regStatus.setDeviceStatusDescription("状态正常");

                    List<RegStatusCpuDeatil> cpus = gson.fromJson(statusMap.get("cpu").toString(), cpuListType);
                    double cpuUsage = cpus.stream().mapToDouble(RegStatusCpuDeatil::getCpu_usage).average().orElse(0d);

                    String cpuUsageStr=df.format(cpuUsage);

                    regStatus.setDeviceCpuUsage(Double.parseDouble(cpuUsageStr));

                    List<DiskInfo> disks = gson.fromJson(regAudited.getDiskInfo(), diskListType);

                    double totalDisk=0;
                    for (DiskInfo disk : disks){
                        totalDisk+=disk.getSize();
                    }

                    String diskUsage=df.format(Double.parseDouble(statusMap.get("disk").toString())*100/totalDisk);
                    regStatus.setDeviceDiskUsage(Double.parseDouble(diskUsage));
                    regStatus.setDeviceMemUsage(Double.parseDouble(statusMap.get("mem").toString()));

                } catch (Exception ex) {
                    log.error("获取设备最新状态出现异常", ex);
                }

                if (list != null && !list.isEmpty()) {
                    networkMonitorCurrentStatusService.update(regStatus);
                } else {
                    networkMonitorCurrentStatusService.save(regStatus);
                }
                return 1;
            } else {
                log.error("设备未注册");
                return 2;
            }
        } else {
            log.error("状态信息没有device_id字段");
            return 0;
        }
    }

    @Override
    public Integer updateStatus(Map map){
        if (map.get("device_id") != null) {
            String deviceId = map.get("device_id").toString();
            List<NetworkMonitor> networkMonitorList = networkMonitorService.findByProperty(NetworkMonitor.class,"deviceId",deviceId);
            if (CollectionUtils.isNotEmpty(networkMonitorList)) {
                NetworkMonitor networkMonitor = networkMonitorList.get(0);
                if (map.get("device_sys_version") != null) {
                    networkMonitor.setDeviceSysVersion(map.get("device_sys_version").toString());
                }
                if (map.get("device_soft_version") != null) {
                    networkMonitor.setDeviceSoftVersion(map.get("device_soft_version").toString());
                }
                if (map.get("device_bios_version") != null) {
                    networkMonitor.setDeviceBiosVersion(map.get("device_bios_version").toString());
                }
                if (map.get("device_cpu_core") != null) {
                    networkMonitor.setDeviceCpuCore(Integer.valueOf(map.get("device_cpu_core").toString()));
                }
                if (map.get("device_cpu_usage") != null) {
                    networkMonitor.setDeviceCpuUsage(Integer.valueOf(map.get("device_cpu_usage").toString()));
                }
                if (map.get("device_mem_size") != null) {
                    networkMonitor.setDeviceMemSize(Integer.valueOf(map.get("device_mem_size").toString()));
                }
                if (map.get("device_hdisk_size") != null) {
                    networkMonitor.setDeviceHdiskSize(Integer.valueOf(map.get("device_hdisk_size").toString()));
                }
                if (map.get("device_hdisk_num") != null) {
                    networkMonitor.setDeviceHdiskNum(JSON.toJSONString(("device_hdisk_num")));
                }
                if (map.get("device_mem_usage") != null) {
                    networkMonitor.setDeviceMemUsage(Integer.valueOf(map.get("device_mem_usage").toString()));
                }
                if (map.get("device_hdisk_usage") != null) {
                    networkMonitor.setDeviceHdiskUsage(Integer.valueOf(map.get("device_hdisk_usage").toString()));
                }
                networkMonitor.setReportTime(new Date());
                networkMonitor.setNetworkMonitorStatus(NETWORK_STATUS_ONLINE);
                return networkMonitorService.updateSelective(networkMonitor);
            } else {
                log.error("设备未注册");
                return 2;
            }
        } else {
            log.error("状态信息没有device_id字段");
            return 0;
        }
    }
    /**
     * 检测器注册
     * @param map
     */
    @Override
    public Integer register(Map map) {
        NetworkMonitor networkMonitor = new NetworkMonitor();
        if (map.get("device_id") != null) {
            String deviceId = map.get("device_id").toString();
            networkMonitor.setDeviceId(deviceId);
            //networkMonitorService.deleteByDeviceId(deviceId);
        }
        if (map.get("device_soft_version") != null) {
            networkMonitor.setDeviceSoftVersion(map.get("device_soft_version").toString());
        }
        if (map.get("interface") != null) {
            networkMonitor.setInterfaceInfo(JSON.toJSONString(map.get("interface")));
        }
        if (map.get("mem_total") != null) {
            networkMonitor.setMemTotal(Integer.valueOf(map.get("mem_total").toString()));
        }
        if (map.get("cpu_info") != null) {
            networkMonitor.setCpuInfo(JSON.toJSONString(map.get("cpu_info")));
        }
        if (map.get("disk_info") != null) {
            networkMonitor.setDiskInfo(JSON.toJSONString(map.get("disk_info")));
        }
        if (map.get("device_belong") != null) {
            networkMonitor.setDeviceBelong(map.get("device_belong").toString());
        }
        if (map.get("device_location") != null) {
            networkMonitor.setDeviceLocation(map.get("device_location").toString());
        }
        if (map.get("address_code") != null) {
            networkMonitor.setAddressCode(map.get("address_code").toString());
        }
        if (map.get("contact") != null) {
            networkMonitor.setContact(JSON.toJSONString(map.get("contact")));
        }
        if (map.get("memo") != null) {
            networkMonitor.setMemo(map.get("memo").toString());
        }

        //获取历史的注册信息
        NetworkMonitor  networkMonitorHistory= networkMonitorService.getLastItem(networkMonitor.getDeviceId());

        //转注册信息
        if(networkMonitorHistory!=null) {
            NetworkMonitorAudited auditItem = new NetworkMonitorAudited();
            NetworkMonitorAudited auditItemHistory = new NetworkMonitorAudited();
            BeanUtils.copyProperties(networkMonitor, auditItem, "id");
            BeanUtils.copyProperties(networkMonitorHistory, auditItemHistory, "id");

            List<String> ignoreList = new ArrayList<>();
            Map<String, List<Object>> fields = CompareFiledUtil.compareFields(auditItem, auditItemHistory, ignoreList);

            //表示多次提交的结果相同
            if (fields == null || fields.isEmpty()) {

                //检查审核记录
                NetworkMonitorRegAuditLog regAuditLog = networkMonitorRegAuditLogService.getLastItem(networkMonitor.getDeviceId());
                if (regAuditLog != null) {
                    if (regAuditLog.getRegId().intValue() == networkMonitorHistory.getId().intValue()) {
                        //说明历史审核记录 是有效的  未过期
                        //1 通过 0不通过
                        //0 成功 1 失败
                        return 1-regAuditLog.getAuditResult();
                    }
                }
                return -1;//未审核
            }
        }


        networkMonitor.setReportTime(new Date());
        networkMonitor.setRegType(REGTYPE_ONLINE);
        networkMonitor.setStatus(STATUS_NORMAL);
        networkMonitor.setNetworkMonitorStatus(NETWORK_STATUS_OFFLINE);
        networkMonitorService.saveSelective(networkMonitor);



        return -1;  //未审核
    }

}

