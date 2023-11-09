package com.vrv.vap.monitor.agent.utils;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.common.model.BeatInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OshiUtil {

    private static Logger logger = LoggerFactory.getLogger(OshiUtil.class);

    private static BaseProperties commonConfig = (BaseProperties) AgentApplication.getApplicationContext().getBean(BaseProperties.class);

    private static Runtime r = Runtime.getRuntime();


    /**
     * 获取内存使用信息
     *
     * @return
     */
    public static BeatInfo.MemInfo memory(GlobalMemory memory) throws Exception {
        BeatInfo.MemInfo memState = new BeatInfo.MemInfo();
        long total = memory.getTotal() / 1024L / 1024L;
        long free = memory.getAvailable() / 1024L / 1024L;
        double usePer = (double) (total - free) / (double) total;
        memState.setUsePer(FormatUtil.formatDouble(usePer * 100, 1));
        memState.setHostname(commonConfig.getLocalIp());
        memState.setTotal(total+"");
        memState.setFree(free+"");
        memState.setCreateTime(new Date());
        memState.setDateStr(new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date()));
        memState.setUsed(total - free+"");
        return memState;
    }


    /**
     * 获取cpu使用率，等待率，空闲率
     *
     * @return
     * @throws Exception
     */
    public static BeatInfo.CpuInfo cpu(CentralProcessor processor) throws Exception {
        long[] ticks = processor.getSystemCpuLoadTicks();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // Wait a second...
        Util.sleep(1000);

        BeatInfo.CpuInfo cpuState = new BeatInfo.CpuInfo();
        cpuState.setSys(FormatUtil.formatDouble(processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100, 1));
        cpuState.setHostname(commonConfig.getLocalIp());
        cpuState.setCreateTime(new Date());
        cpuState.setDateStr(new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date()));
        try {
            long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
            long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            cpuState.setIdle(Double.valueOf(idle));
            cpuState.setIowait(Double.valueOf(iowait));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuState;
    }
    /**
     * 获取操作系统信息
     *
     * @return
     * @throws Exception
     */
    public static BeatInfo.SysInfo os(CentralProcessor processor, OperatingSystem os) throws Exception {
        BeatInfo.SysInfo systemInfo = new BeatInfo.SysInfo();
        systemInfo.setHostname(commonConfig.getLocalIp());
        systemInfo.setCpuCoreNum(processor.getLogicalProcessorCount() + "");
        systemInfo.setCpuPhNum(processor.getPhysicalPackageCount()+"");
        String cpuInfo = processor.toString();
        if (cpuInfo.indexOf("\n") > 0) {
            cpuInfo = cpuInfo.substring(0, cpuInfo.indexOf("\n"));
        }
        systemInfo.setCpuXh(cpuInfo);
        systemInfo.setVersion(os.toString());
        systemInfo.setVersionDetail(os.toString());
        systemInfo.setState("1");
        systemInfo.setCreateTime(new Date());
        systemInfo.setUpTime2(getUptimeSecond2());
        systemInfo.setUpTime(formatSeconds(systemInfo.getUpTime2()));
        return systemInfo;
    }

    /**
     * 获取系统运行时长
     *
     * @return
     */
    public static String getUptime() {
        String executeCmd = CmdExecute.executeCmd("uptime");
        String[] rows = executeCmd.split(System.lineSeparator());
        String info = rows[0];
        String[] upTime = info.split(",");
        String day = upTime[0].replace(" ", "");
        String time = upTime[1].replace(" ", "");
        String[] hourAndMinUte = time.split(":");

        String hour = hourAndMinUte[0] + "小时";
        String minute = "";
        if(hourAndMinUte.length>1) {
             minute = hourAndMinUte[1] + "分";
        }
        String[] ups = day.split("up");
        String dayFormat = ups[1].substring(0, ups[1].length() - 4).trim() + "天";
        return dayFormat + hour + minute;
    }


    public static String formatSeconds(long seconds) {
        String timeStr = 0 + "分";
        if (seconds > 60) {
            long second = seconds % 60;
            long min = seconds / 60;
            timeStr = min + "分" ;
            if (min > 60) {
                min = (seconds / 60) % 60;
                long hour = (seconds / 60) / 60;
                timeStr = hour + "小时" + min + "分" ;
                if (hour > 24) {
                    hour = ((seconds / 60) / 60) % 24;
                    long day = (((seconds / 60) / 60) / 24);
                    timeStr = day + "天" + hour + "小时" + min + "分" ;
                }
            }
        }
        return timeStr;
    }

    public static Long getUptimeSecond2(){
        String str = CmdExecute.executeCmd("cat /proc/uptime");
        String result="0";

        if(StringUtils.isNotEmpty(str)) {
            if (str.contains(" ")) {
                String[] re = str.split(" ");

                if (re.length > 0) {
                    String first = re[0];
                    if (first.contains(".")) {
                        result = first.substring(0, first.indexOf("."));
                    }
                    else {
                        result = first;
                    }
                }
            }
            else{
                if (str.contains(".")) {
                    result = str.substring(0, str.indexOf("."));
                }
                else {
                    result = str;
                }
            }
        }
        Long l = 0L;
        try {
            l = Long.valueOf(result);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return l;
    }

    /**
     * 获取磁盘使用信息
     *
     * @throws Exception
     */
    public static List<BeatInfo.DiskInfo> file(Timestamp t, FileSystem fileSystem) throws Exception {

        List<BeatInfo.DiskInfo> list = new ArrayList<BeatInfo.DiskInfo>();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            if(fs.getMount()!=null && fs.getMount().endsWith("/tmp")){
                continue;
            }
            logger.debug("磁盘监控返回信息：{}",JsonUtil.objToJson(fs));
            BeatInfo.DiskInfo deskState = new BeatInfo.DiskInfo();
            deskState.setDiskName(fs.getMount());
            deskState.setDiskTotal(fs.getTotalSpace());
            deskState.setDiskTotalCount(oshi.util.FormatUtil.formatBytes(fs.getTotalSpace()));
            deskState.setDiskUsed(fs.getTotalSpace()-fs.getFreeSpace());
            deskState.setDiskUsedCount(oshi.util.FormatUtil.formatBytes(fs.getTotalSpace()-fs.getFreeSpace()));
            deskState.setDiskFree(fs.getFreeSpace());
            deskState.setDiskFreeCount(oshi.util.FormatUtil.formatBytes(fs.getFreeSpace()));
            deskState.setDiskType(fs.getName());
            deskState.setDiskFreeRate(Math.round(100d*fs.getFreeSpace()/fs.getTotalSpace()));
            deskState.setDiskUsedRate(100-deskState.getDiskFreeRate());
            list.add(deskState);
        }

        return list;
    }
    public static List<BeatInfo.DiskInfo> getDiskInfos() {
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



}
