package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-12-20
 */
@ApiModel(value="BaseLineSequence对象", description="")
public class BaseLineSequence implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String userIp;

    private String sysId;

    @ApiModelProperty(value = "访问总量")
    private Integer size;

    private Integer distinctSize;

    @ApiModelProperty(value = "总时间（秒）")
    private Integer timeTotal;

    @ApiModelProperty(value = "无效总数")
    private Integer invalidNum;

    @ApiModelProperty(value = "资源总数")
    private Integer resourceNum;

    @ApiModelProperty(value = "数据时间")
    private LocalDate dataTime;

    private String compress;

    private String startTime;

    private String endTime;

    private String org;

    private String role;

    @ApiModelProperty(value = "小时")
    private Integer hour;

    @ApiModelProperty(value = "包大小")
    private Float pck;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    public Integer getDistinctSize() {
        return distinctSize;
    }

    public void setDistinctSize(Integer distinctSize) {
        this.distinctSize = distinctSize;
    }
    public Integer getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(Integer timeTotal) {
        this.timeTotal = timeTotal;
    }
    public Integer getInvalidNum() {
        return invalidNum;
    }

    public void setInvalidNum(Integer invalidNum) {
        this.invalidNum = invalidNum;
    }
    public Integer getResourceNum() {
        return resourceNum;
    }

    public void setResourceNum(Integer resourceNum) {
        this.resourceNum = resourceNum;
    }
    public LocalDate getDataTime() {
        return dataTime;
    }

    public void setDataTime(LocalDate dataTime) {
        this.dataTime = dataTime;
    }
    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }
    public Float getPck() {
        return pck;
    }

    public void setPck(Float pck) {
        this.pck = pck;
    }
    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "BaseLineSequence{" +
            "id=" + id +
            ", userIp=" + userIp +
            ", sysId=" + sysId +
            ", size=" + size +
            ", distinctSize=" + distinctSize +
            ", timeTotal=" + timeTotal +
            ", invalidNum=" + invalidNum +
            ", resourceNum=" + resourceNum +
            ", dataTime=" + dataTime +
            ", compress=" + compress +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", org=" + org +
            ", role=" + role +
            ", hour=" + hour +
            ", pck=" + pck +
            ", insertTime=" + insertTime +
        "}";
    }
}
