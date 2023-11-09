package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryIn;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
@ApiModel("地区IP段查询条件")
public class BaseAreaIpSegmentVo extends Query{

    @QueryLike
    @ApiModelProperty("区域编码")
    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "start_ip")
    @ApiModelProperty("开始IP地址段")
    private String startIp;

    @Column(name = "end_ip")
    @ApiModelProperty("结束IP地址段")
    private String endIp;

    @Column(name = "start_ip_num")
    @ApiModelProperty("结束IP数值，计算公式：ip[0]*256*256*256 + ip[1]*256*256 + ip[2]*256 + ip[3]")
    private Long startIpNum;

    @Column(name = "end_ip_num")
    @ApiModelProperty("开始IP数值，计算公式：ip[0]*256*256*256 + ip[1]*256*256 + ip[2]*256 + ip[3]")
    private Long endIpNum;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public Long getStartIpNum() {
        return startIpNum;
    }

    public void setStartIpNum(Long startIpNum) {
        this.startIpNum = startIpNum;
    }

    public Long getEndIpNum() {
        return endIpNum;
    }

    public void setEndIpNum(Long endIpNum) {
        this.endIpNum = endIpNum;
    }
}
