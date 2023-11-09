package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

@Table(name = "base_area_ip_segment")
public class BaseAreaIpSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 区域编码
     */
    @Column(name = "area_code")
    @ApiModelProperty(value = "区域编码")
    private String areaCode;

    /**
     * 开始IP地址段
     */
    @Column(name = "start_ip")
    @ApiModelProperty(value = "开始IP地址段")
    private String startIp;

    /**
     * 结束IP地址段
     */
    @Column(name = "end_ip")
    @ApiModelProperty(value = "结束IP地址段")
    private String endIp;

    /**
     * 开始IP地址段转换成整型
     */
    @Column(name = "start_ip_num")
    @ApiModelProperty(value = "开始IP地址段转换成整型")
    @Ignore
    private Long startIpNum;

    /**
     * 结束IP地址段转换成整型
     */
    @Column(name = "end_ip_num")
    @ApiModelProperty(value = "结束IP地址段转换成整型")
    @Ignore
    private Long endIpNum;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取区域编码
     *
     * @return area_code - 区域编码
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * 设置区域编码
     *
     * @param areaCode 区域编码
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * 获取开始IP地址段
     *
     * @return start_ip - 开始IP地址段
     */
    public String getStartIp() {
        return startIp;
    }

    /**
     * 设置开始IP地址段
     *
     * @param startIp 开始IP地址段
     */
    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    /**
     * 获取结束IP地址段
     *
     * @return end_ip - 结束IP地址段
     */
    public String getEndIp() {
        return endIp;
    }

    /**
     * 设置结束IP地址段
     *
     * @param endIp 结束IP地址段
     */
    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    /**
     * 获取开始IP地址段转换成整型
     *
     * @return start_ip_num - 开始IP地址段转换成整型
     */
    public Long getStartIpNum() {
        return startIpNum;
    }

    /**
     * 设置开始IP地址段转换成整型
     *
     * @param startIpNum 开始IP地址段转换成整型
     */
    public void setStartIpNum(Long startIpNum) {
        this.startIpNum = startIpNum;
    }

    /**
     * 获取结束IP地址段转换成整型
     *
     * @return end_ip_num - 结束IP地址段转换成整型
     */
    public Long getEndIpNum() {
        return endIpNum;
    }

    /**
     * 设置结束IP地址段转换成整型
     *
     * @param endIpNum 结束IP地址段转换成整型
     */
    public void setEndIpNum(Long endIpNum) {
        this.endIpNum = endIpNum;
    }
}