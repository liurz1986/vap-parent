package com.vrv.vap.monitor.model.es;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * es集群状态
 *
 * @author xw
 * @date 2018年7月18日
 */
@ApiModel("ES服务器状态")
public class EsServerStatusModel {

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("host")
    private String host;

    @ApiModelProperty("是否master节点")
    private boolean isMaster;

    @ApiModelProperty("cpu使用率")
    private int cpuPercent;

    @ApiModelProperty("最大文件数")
    private int maxFiles;

    @ApiModelProperty("打开文件数")
    private int openFiles;

    @ApiModelProperty("jvm内存,单位GB")
    private double jvmMemAll;

    @ApiModelProperty("jvm已用内存,单位GB")
    private double jvmMemUsed;

    @ApiModelProperty("系统内存,单位GB")
    private double osMemAll;

    @ApiModelProperty("系统已用内存,单位GB")
    private double osMemUsed;

    @ApiModelProperty("磁盘容量,单位GB")
    private double diskAll;

    @ApiModelProperty("已用磁盘,单位GB")
    private double diskUsed;

    @ApiModelProperty("是否存活")
    private boolean isAlive;

    @ApiModelProperty("节点状态")
    private String status;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public int getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(int cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public double getJvmMemAll() {
        return jvmMemAll;
    }

    public void setJvmMemAll(double jvmMemAll) {
        this.jvmMemAll = jvmMemAll;
    }

    public double getJvmMemUsed() {
        return jvmMemUsed;
    }

    public void setJvmMemUsed(double jvmMemUsed) {
        this.jvmMemUsed = jvmMemUsed;
    }

    public double getOsMemAll() {
        return osMemAll;
    }

    public void setOsMemAll(double osMemAll) {
        this.osMemAll = osMemAll;
    }

    public double getOsMemUsed() {
        return osMemUsed;
    }

    public void setOsMemUsed(double osMemUsed) {
        this.osMemUsed = osMemUsed;
    }

    public double getDiskAll() {
        return diskAll;
    }

    public void setDiskAll(double diskAll) {
        this.diskAll = diskAll;
    }

    public double getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(double diskUsed) {
        this.diskUsed = diskUsed;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public int getOpenFiles() {
        return openFiles;
    }

    public void setOpenFiles(int openFiles) {
        this.openFiles = openFiles;
    }

}
