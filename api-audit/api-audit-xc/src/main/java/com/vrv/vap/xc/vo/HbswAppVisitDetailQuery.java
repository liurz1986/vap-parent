package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="HbswAppVisitDetail对象", description="")
public class HbswAppVisitDetailQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String ip;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "类型，1-上班访问预警，2-下班访问预警")
    private Integer warnType;

    @ApiModelProperty(value = "预警时间")
    private Date warnTime;

    @ApiModelProperty(value = "地区")
    private String areaCode;

    private String areaName;

    private String organ;

    private String sysId;

    private String operateCondition;

    private String operateTime;

    @ApiModelProperty(value = "数据时间")
    private String dataTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Integer getWarnType() {
        return warnType;
    }

    public void setWarnType(Integer warnType) {
        this.warnType = warnType;
    }
    public Date getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(Date warnTime) {
        this.warnTime = warnTime;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "HbswAppVisitDetail{" +
            "id=" + id +
            ", ip=" + ip +
            ", userName=" + userName +
            ", warnType=" + warnType +
            ", warnTime=" + warnTime +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", organ=" + organ +
            ", sysId=" + sysId +
            ", operateCondition=" + operateCondition +
            ", operateTime=" + operateTime +
            ", dataTime=" + dataTime +
        "}";
    }
}
