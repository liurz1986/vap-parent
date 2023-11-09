package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 未注册和注册不规范设备访问预警
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-19
 */
@ApiModel(value="EarlyWarnUnregVisit对象", description="未注册和注册不规范设备访问预警")
public class EarlyWarnUnregVisitQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "姓名")
    private String username;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "地区")
    private String areaCode;

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    @ApiModelProperty(value = "警种")
    private String policeType;

    @ApiModelProperty(value = "机构")
    private String organization;

    @ApiModelProperty(value = "各系统访问详情")
    private String operateCondition;

    @ApiModelProperty(value = "访问总量")
    private Integer total;

    @ApiModelProperty(value = "类型，1-未注册访问预警，2-注册不规范访问预警")
    private Integer warnType;

    @ApiModelProperty(value = "预警时间")
    private Date warnTime;

    @ApiModelProperty(value = "数据时间")
    private Date eventTime;

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
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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
    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "EarlyWarnUnregVisit{" +
            "id=" + id +
            ", ip=" + ip +
            ", username=" + username +
            ", idCard=" + idCard +
            ", areaCode=" + areaCode +
            ", sysId=" + sysId +
            ", policeType=" + policeType +
            ", organization=" + organization +
            ", operateCondition=" + operateCondition +
            ", total=" + total +
            ", warnType=" + warnType +
            ", warnTime=" + warnTime +
            ", eventTime=" + eventTime +
        "}";
    }
}
