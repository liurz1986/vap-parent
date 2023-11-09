package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
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
 * @since 2021-05-19
 */
@ApiModel(value="ReportSecurityAbnormalDetail对象", description="")
public class ReportSecurityAbnormalDetailQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "身份证")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "系统")
    private String sysId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "区域")
    private String areaCode;

    @ApiModelProperty(value = "操作条件")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String operateCondition;

    @ApiModelProperty(value = "操作时间")
    private String timeStr;

    @ApiModelProperty(value = "报表月份")
    private String reportMonth;

    @ApiModelProperty(value = "入库时间")
    private Date insertTime;

    @ApiModelProperty(value = "机构编号")
    private String orgCode;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "警种编号")
    private String policeType;

    @ApiModelProperty(value = "维度")
    private String dimension;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
    public String getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(String reportMonth) {
        this.reportMonth = reportMonth;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "ReportSecurityAbnormalDetail{" +
            "id=" + id +
            ", userId=" + userId +
            ", userName=" + userName +
            ", sysId=" + sysId +
            ", ip=" + ip +
            ", areaCode=" + areaCode +
            ", operateCondition=" + operateCondition +
            ", timeStr=" + timeStr +
            ", reportMonth=" + reportMonth +
            ", insertTime=" + insertTime +
            ", orgCode=" + orgCode +
            ", orgName=" + orgName +
            ", policeType=" + policeType +
            ", dimension=" + dimension +
        "}";
    }
}
