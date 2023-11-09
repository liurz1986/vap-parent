package com.vrv.vap.admin.common.util;

import com.sun.management.OperatingSystemMXBean;
import com.vrv.vap.admin.util.CleanUtil;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SystemStatusUtils {
    public static String getDiskInfo() {
       /* // 空闲空间
        long freeSpace = 0l;
        // 总空间
        long totalSpace = 0l;
        // 获取磁盘分区列表
        File[] roots = File.listRoots();
        for (File file : roots) {
            freeSpace += file.getFreeSpace();
            totalSpace += file.getTotalSpace();
        }
        double diskUsage = (1 - freeSpace/totalSpace) * 100;
        return new BigDecimal(diskUsage).setScale(1, RoundingMode.HALF_UP).floatValue() + "%";*/

        float diskUsage = 0.0f;
        Process pro = null;
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        try {
            pro = r.exec(CleanUtil.cleanString("iostat -d -x"));
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
                if (++count >= 4) {
                    String[] temp = line.split("\\s+");
                    if (temp.length > 1) {
                        float util = Float.parseFloat(temp[temp.length - 1]);
                        diskUsage = (diskUsage > util) ? diskUsage : util;
                    }
                }
            }
            if (diskUsage > 0) {
                diskUsage /= 100;
            }
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pro != null) {
                pro.destroy();
            }
        }
        return new BigDecimal(diskUsage*100).setScale(1, RoundingMode.HALF_UP).floatValue() + "%";

    }

    public static String getMemInfo() {
        /*OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize();
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        double memUsage = (1 - freePhysicalMemorySize/totalMemorySize) * 100;
        return new BigDecimal(memUsage).setScale(1, RoundingMode.HALF_UP).floatValue() + "%";*/
        double memUsage = 0.0;
        Process pro = null;
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        try {
            pro = r.exec(CleanUtil.cleanString("cat /proc/meminfo"));
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            int count = 0;
            long totalMem = 0, freeMem = 0;
            while ((line = in.readLine()) != null) {
                String[] memInfo = line.split("\\s+");
                if (memInfo[0].startsWith("MemTotal")) {
                    totalMem = Long.parseLong(memInfo[1]);
                }
                if (memInfo[0].startsWith("MemFree")) {
                    freeMem = Long.parseLong(memInfo[1]);
                }
                memUsage = 1 - (double) freeMem / totalMem;
                if (++count == 2) {
                    break;
                }
            }
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pro != null) {
                pro.destroy();
            }
        }
        return new BigDecimal(memUsage*100).setScale(1, RoundingMode.HALF_UP).floatValue() + "%";

    }


    public static String getCpuInfo() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osmxb.getSystemCpuLoad() * 100;
        return new BigDecimal(cpuUsage).setScale(1, RoundingMode.HALF_UP).floatValue() + "%";
    }
}
