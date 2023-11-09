package com.vrv.vap.monitor.snmp;


import java.util.Map;

/**
 * 定义算法
 */
public interface SnmpAlgo {

    /**
     * 通用获取 - 直接获取oid对应的值
     * 比如进程数量, 系统信息
     *
     * @param oid
     * @return
     */
    Object common(String oid);

    /**
     * 通用计算 - 直接获取oid对应的值, 导入算式内计算结果
     * 要求: 通过odi获取的数据为数值类型
     *
     * @param oid
     * @return
     */
    Object cal(String oid, String ex);

    /**
     * 通用计算 - 直接获取oid对应的值, 导入算式内计算结果
     *
     * @param oid
     * @return
     */
    Object cal2(String oid, String ex);

    /**
     * 通用获取cpu使用率
     */
    Object cpuUseRate(String oid, String ex);

    /**
     * 通用获取列表类数据,例如进程列表
     */
    Object list(String oid);

    /**
     * 通用获取cpu核数
     */
    Object cpuCoreCount(String oid);

    /**
     * 通用获取磁盘使用量
     */
    Object disk(String oid);

    /**
     * 获取内存使用情况(从磁盘信息中获取)
     */
    Map<String, Object> memoryFromDisk(String oid);

    /**
     * USG400型号设备获取内存使用情况
     */
    Map<String, Object> memoryUSG400(String oid);
}
