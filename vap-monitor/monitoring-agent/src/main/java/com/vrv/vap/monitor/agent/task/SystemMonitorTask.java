package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.*;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class SystemMonitorTask extends MonitorBaseTask {
    BaseProperties baseProperties;
    ServerManager serverManager;
    private Result collectorResult;

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        serverManager = applicationContext.getBean(ServerManager.class);
        baseProperties = applicationContext.getBean(BaseProperties.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String, MonitorConfig> monitorConfigMap = (Map<String, MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig monitorConfig = monitorConfigMap.get("system");
        //获取组件名
        String name = monitorConfig.getName();
        log.debug("开始监控组件任务：{}",name);
        //获取数据库系统信息预警值
        Result result = getSystemInfoRate();
        if((result==null || !"0".equals(result.getCode()))&& collectorResult!=null){
            log.warn("使用缓存配置数据");
            result = collectorResult;
        }
        if (result.getCode().equals("0") && result.getData() != null) {
            collectorResult = result;
            Map<String, String> systemConfig = (Map<String, String>) result.getData();
            String cpu_rate = systemConfig.get("cpu_rate");
            String ram_rate = systemConfig.get("ram_rate");
            String disk_rate = systemConfig.get("disk_rate");
            oshi.SystemInfo si = new oshi.SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            OperatingSystem os = si.getOperatingSystem();
            Timestamp t = FormatUtil.getNowTime();
            String localIp = baseProperties.getLocalIp();
            MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
            metricInfo.setStatus(1);
            try {
                BeatInfo.CpuInfo cpu = OshiUtil.cpu(hal.getProcessor());
                BeatInfo.MemInfo memory = OshiUtil.memory(hal.getMemory());
//                List<BeatInfo.DiskInfo> file = OshiUtil.file(t,si.getOperatingSystem().getFileSystem());
                List<BeatInfo.DiskInfo> file = OshiUtil.getDiskInfos();
                LocalSystemInfo localSystemInfo = new LocalSystemInfo();
                localSystemInfo.setCpuRate(Double.parseDouble(String.valueOf(cpu.getSys())) / 100);
                localSystemInfo.setRamRate(memory.getUsePer() / 100);
                long diskTotal = 0l;
                long diskUsed = 0l;
                for (BeatInfo.DiskInfo diskInfoVO : file) {
                    diskTotal += diskInfoVO.getDiskTotal();
                    diskUsed += diskInfoVO.getDiskUsed();
                }
                if (diskTotal>0){
                    localSystemInfo.setDiskRate(new BigDecimal(1d * diskUsed / diskTotal).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }
                localSystemInfo.setCreateTime(new Date());
                metricInfo.setExtendContent(JsonUtil.objToJson(localSystemInfo));
                //保存
                try {
                    saveSystemInfo(localSystemInfo);
                } catch (Exception e) {
                    log.error("saveSystemInfo error");
                    e.printStackTrace();
                }
                log.info("localSystemInfo:{}" + localSystemInfo);
                if (localSystemInfo.getCpuRate() >= Double.parseDouble(cpu_rate) / 100) {
                    metricInfo.setStatus(0);
                    if (monitorConfig.getAlarm()) {
                        pushAlarm(AlarmTypeEnum.ALARM_CPU.getCode(), String.format(AlarmTypeEnum.ALARM_CPU.getDesc(), cpu_rate + "%"), localIp, monitorConfig.getName());
                    }
                }
                if (localSystemInfo.getRamRate() >= Double.parseDouble(ram_rate) / 100) {
                    metricInfo.setStatus(0);
                    if (monitorConfig.getAlarm()) {
                        pushAlarm(AlarmTypeEnum.ALARM_MEMORY.getCode(), String.format(AlarmTypeEnum.ALARM_MEMORY.getDesc(), ram_rate + "%"), localIp, monitorConfig.getName());
                    }
                }
                if (localSystemInfo.getDiskRate() >= Double.parseDouble(disk_rate) / 100) {
                    metricInfo.setStatus(0);
                    if (monitorConfig.getAlarm()) {
                        pushAlarm(AlarmTypeEnum.ALARM_DISK.getCode(), String.format(AlarmTypeEnum.ALARM_DISK.getDesc(), disk_rate + "%"), localIp, monitorConfig.getName());
                    }

                }
                log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");

                pushMetric(metricInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            log.error("无法获取服务端数据-cpu、内存、磁盘阈值信息");
        }
    }

    @Override
    public void run(String jobName) {

    }
    private List<BeatInfo.DiskInfo> getDiskInfos() {
        List<BeatInfo.DiskInfo> result = new ArrayList<BeatInfo.DiskInfo>();

        long allSize = 0;

        String executeCmd = CmdExecute.executeCmd("df -k");
        String[] rows = executeCmd.split(System.lineSeparator());
        for(int i=1;i<rows.length;i++) {
            String row=rows[i];
            String[] cols = row.split(" {1,50}");
            if(cols[0].equals("tmpfs")) {
                continue;
            }
            Long total = Long.parseLong(cols[1])*1024;
            Long used= Long.parseLong(cols[2])*1024;
            Long free= Long.parseLong(cols[3])*1024;

            BeatInfo.DiskInfo vo = new BeatInfo.DiskInfo();


            vo.setDiskName(cols[5]);

            vo.setDiskTotal(total);
            vo.setDiskTotalCount(oshi.util.FormatUtil.formatBytes(total));

            vo.setDiskType(cols[0]);

            vo.setDiskUsed(used);
            vo.setDiskUsedCount(oshi.util.FormatUtil.formatBytes(used));

            vo.setDiskFree(free);
            vo.setDiskFreeCount(oshi.util.FormatUtil.formatBytes(free));


            vo.setDiskUsedRate(Math.round(100d*used/(used+free)));
            vo.setDiskFreeRate(100- Math.round(100d*used/(used+free)));

            result.add(vo);
        }
        return result;
    }


    public Result restartService(MonitorConfig config){
        return Result.builder().code("-1").msg("系统模块不能重启").build();
    }

    @Override
    public Boolean restart(MonitorConfig config) {
        return true;
    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {
        return true;
    }

}
