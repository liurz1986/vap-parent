package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BeatInfo {

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 1正常，2下线
     */
    private Integer status;

    /**
     * 是否已接收配置
     */
    private Boolean configStatus;

    /**
     * 时间
     */
    private Date time;

    /**
     * CPU使用率
     */
    private Double cpuRate;

    /**
     * 内存使用率
     */
    private Double ramRate;

    /**
     * 磁盘使用率
     */
    private Double diskRate;

    /**
     * CPU信息
     */
    private CpuInfo cpuInfo;

    /**
     * 内存信息
     */
    private MemInfo memInfo;


    /**
     * 系统信息
     */
    private SysInfo sysInfo;

    /**
     * 磁盘信息
     */
    private List<DiskInfo> diskInfos;

    /**
     * 磁盘空间
     */
    private DiskSpace diskSpace;


    @Data
    public static class CpuInfo{

        /**
         * host名称
         */
        private String hostname;

        /**
         * 用户态的CPU时间（%）废弃
         */
        private String user;

        /**
         * cpu使用率
         */
        private Double sys;

        /**
         * 当前空闲率
         */
        private Double idle;

        /**
         * cpu当前等待率
         */
        private Double iowait;

        /**
         * 硬中断时间（%） 废弃
         */
        private String irq;

        /**
         * 软中断时间（%） 废弃
         */
        private String soft;

        /**
         * 添加时间
         * MM-dd hh:mm:ss
         */
        private String dateStr;

        /**
         * 创建时间
         */
        private Date createTime;
    }

    @Data
    public static class MemInfo{
        /**
         *
         */
        private static final long serialVersionUID = -1412473355088780549L;


        /**
         * host名称
         */
        private String hostname;

        /**
         * 总计内存，M
         */
        private String total;

        /**
         * 已使用多少，M
         */
        private String used;

        /**
         * 未使用，M
         */
        private String free;

        /**
         * 已使用百分比%
         */
        private Double usePer;

        /**
         * 添加时间
         * yyyy-MM-dd hh:mm:ss
         */
        private String dateStr;

        /**
         * 创建时间
         */
        private Date createTime;
    }

    @Data
    public static class DiskInfo{
        // 磁盘描述
        private String diskName;
        // 磁盘类型
        private String diskType;
        // 逻辑磁盘总容量
        private long diskTotal;
        // 逻辑磁盘总容量
        private String diskTotalCount;

        // 逻辑磁盘已使用容量
        private long diskUsed;
        private String diskUsedCount;

        // 逻辑磁盘已使用容量
        private long diskFree;
        private String diskFreeCount;

        // 逻辑磁盘利用率
        private double diskUsedRate;

        // 逻辑磁盘利用率
        private double diskFreeRate;
    }

    @Data
    public static class DiskSpace {
        // 磁盘空间总量
        private long diskSpaceTotal;
        private String diskSpaceCount;
        // 磁盘空间使用总量
        private long diskSpaceUsedTotal;
        private String diskSpaceUsedCount;
        // 磁盘空间剩余总量
        private long diskSpaceFreeTotal;
        private String diskSpaceFreeCount;

        // 磁盘空间使用率
        private double diskUsedRate;

        // 磁盘空间空闲率
        private double diskFreeRate;
    }

    @Data
    public static class SysInfo{
        /**
         * host名称
         */
        private String hostname;

        /**
         * 系统版本信息
         */
        private String version;

        /**
         * 系统版本详细信息
         */
        private String versionDetail;

        /**
         * 内存使用率
         */
        private Double memPer;

        /**
         * core的个数(即核数)
         */
        private String cpuCoreNum;

        /**
         * 物理个数
         */
        private String cpuPhNum;

        /**
         * cpu使用率
         */
        private Double cpuPer;

        /**
         * CPU型号信息
         */
        private String cpuXh;


        /**
         * 主机状态，1正常，2下线
         */
        private String state;


        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 系统运行时间
         */
        private String upTime;

        /**
         * 系统运行时间(毫秒)
         */
        private Long upTime2;

    }
}
