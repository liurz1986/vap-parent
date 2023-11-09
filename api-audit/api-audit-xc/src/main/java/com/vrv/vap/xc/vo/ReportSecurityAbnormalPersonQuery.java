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
@ApiModel(value="ReportSecurityAbnormalPerson对象", description="")
public class ReportSecurityAbnormalPersonQuery extends Query {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String userName;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "机构编码")
    private String orgCode;

    @ApiModelProperty(value = "报告月份")
    private String reportMonth;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "区域编码")
    private String areaCode;

    @ApiModelProperty(value = "数据类型:1=异常访问  0=违规访问")
    private Integer dataType;

    @ApiModelProperty(value = "违规类型: 11-13违规事件, 1-8异常访问应用;多项违规用逗号分割")
    private String abnormalType;

    @ApiModelProperty(value = "违规描述")
    private String abnormalDesc;

    @ApiModelProperty(value = "异常或违规的维度个数")
    private Integer dimensionCount;

    @ApiModelProperty(value = "删除状态:1=删除 0=正常")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(String reportMonth) {
        this.reportMonth = reportMonth;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }
    public String getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(String abnormalType) {
        this.abnormalType = abnormalType;
    }
    public String getAbnormalDesc() {
        return abnormalDesc;
    }

    public void setAbnormalDesc(String abnormalDesc) {
        this.abnormalDesc = abnormalDesc;
    }
    public Integer getDimensionCount() {
        return dimensionCount;
    }

    public void setDimensionCount(Integer dimensionCount) {
        this.dimensionCount = dimensionCount;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportSecurityAbnormalPerson{" +
            "id=" + id +
            ", userName=" + userName +
            ", idCard=" + idCard +
            ", orgName=" + orgName +
            ", orgCode=" + orgCode +
            ", reportMonth=" + reportMonth +
            ", createTime=" + createTime +
            ", areaCode=" + areaCode +
            ", dataType=" + dataType +
            ", abnormalType=" + abnormalType +
            ", abnormalDesc=" + abnormalDesc +
            ", dimensionCount=" + dimensionCount +
            ", status=" + status +
        "}";
    }
}
