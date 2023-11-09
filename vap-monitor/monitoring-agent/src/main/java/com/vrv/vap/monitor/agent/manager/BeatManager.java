package com.vrv.vap.monitor.agent.manager;

import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.utils.FormatUtil;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.agent.utils.OshiUtil;
import com.vrv.vap.monitor.common.model.BeatInfo;
import com.vrv.vap.monitor.common.model.Result;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class BeatManager {
    @Value("${server.port:9910}")
    private Integer port;

    @Resource
    BaseProperties baseProperties;

    @Resource
    ServerManager serverManager;

    @Resource
    MonitorManager monitorManager ;

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("com.vrv.vap.monitor.agent.beat");
            return thread;
        }
    });

    /**
     * 计算磁盘空间总量和使用率
     *
     * @return
     */
    public BeatInfo.DiskSpace calcDiskSpace( List<BeatInfo.DiskInfo> diskInfos){
        // 磁盘空间总量
        long diskTotal = 0;
        // 磁盘空间使用总量
        long diskUsed = 0;
        // 磁盘空间剩余总量
        long diskFree = 0;
        for (BeatInfo.DiskInfo diskInfo : diskInfos) {
            diskTotal += diskInfo.getDiskTotal();
            diskUsed += diskInfo.getDiskUsed();
            diskFree += diskInfo.getDiskFree();
        }
        BeatInfo.DiskSpace diskSpace = new BeatInfo.DiskSpace();
        diskSpace.setDiskSpaceTotal(diskTotal);
        diskSpace.setDiskSpaceCount(oshi.util.FormatUtil.formatBytes(diskTotal));
        diskSpace.setDiskSpaceUsedTotal(diskUsed);
        diskSpace.setDiskSpaceUsedCount(oshi.util.FormatUtil.formatBytes(diskUsed));
        diskSpace.setDiskSpaceFreeTotal(diskFree);
        diskSpace.setDiskSpaceFreeCount(oshi.util.FormatUtil.formatBytes(diskFree));
        long round = Math.round(100d * diskUsed / (diskUsed + diskFree));
        diskSpace.setDiskUsedRate(round);
        diskSpace.setDiskFreeRate(100 - Math.round(round));
        return diskSpace;
    }

    public BeatInfo getBeatInfo() {
        BeatInfo beatInfo = new BeatInfo();
        beatInfo.setIp(baseProperties.getLocalIp());
        beatInfo.setPort(port);
        beatInfo.setStatus(1);
        beatInfo.setTime(new Date());
        beatInfo.setConfigStatus(monitorManager.getConfigStatus());
        try {
            oshi.SystemInfo si = new oshi.SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            OperatingSystem os = si.getOperatingSystem();
            Timestamp t = FormatUtil.getNowTime();
            beatInfo.setCpuInfo(OshiUtil.cpu(hal.getProcessor()));
            beatInfo.setMemInfo(OshiUtil.memory(hal.getMemory()));
            beatInfo.setSysInfo(OshiUtil.os(hal.getProcessor(), os));
//            beatInfo.setDiskInfos(OshiUtil.file(t,si.getOperatingSystem().getFileSystem()));
            beatInfo.setDiskInfos(OshiUtil.getDiskInfos());
            beatInfo.setDiskSpace(calcDiskSpace(beatInfo.getDiskInfos()));
            //计算cpuRate,diskRate,ramRate
            long diskTotal = 0l;
            long diskUsed = 0l;
            for (BeatInfo.DiskInfo diskInfoVO : beatInfo.getDiskInfos()) {
                diskTotal += diskInfoVO.getDiskTotal();
                diskUsed += diskInfoVO.getDiskUsed();
            }
            if (diskTotal>0){
                beatInfo.setDiskRate(new BigDecimal(1d * diskUsed / diskTotal).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
            if (beatInfo.getCpuInfo()!=null&&beatInfo.getCpuInfo().getSys()!=null){
                beatInfo.setCpuRate(                        beatInfo.getCpuInfo().getSys() / 100                );
            }
            if (beatInfo.getMemInfo()!=null&&beatInfo.getMemInfo().getUsePer()!=null){
                beatInfo.setRamRate(beatInfo.getMemInfo().getUsePer() / 100);
            }

        } catch (Exception e) {
            log.error("获取系统状态信息失败：{}", e);
        }
        return beatInfo;

    }


    public void startBeat() {
        log.info("[AGENT-BEAT] start beat ,beat interval : {} ms", baseProperties.getBeatInterval());
        executorService.schedule(new BeatTask(), 1000, TimeUnit.MILLISECONDS);
    }


    class BeatTask implements Runnable {

        public BeatTask() {

        }

        @Override
        public void run() {
            BeatInfo beatInfo = getBeatInfo();
            log.debug("[AGENT-BEAT] beat info :{}", beatInfo);
            try {
                Result result = serverManager.sendBeat(beatInfo); // 发送心跳包
                if (!"0".equals(result.getCode())) {
                    log.error("[AGENT-BEAT] failed to send beat: {}, msg: {}",
                            JsonUtil.objToJson(beatInfo), "send to server error");
                    //心跳发送
                }
            } catch (Exception ex) {
                log.error("[AGENT-BEAT] failed to send beat: {}, msg: {}",
                        JsonUtil.objToJson(beatInfo), ex.getMessage());

            }
            executorService.schedule(new BeatTask(), baseProperties.getBeatInterval(), TimeUnit.MILLISECONDS); // 设置下一次执行  nextTime(period)毫秒后执行1次 默认5s
        }

    }



}
