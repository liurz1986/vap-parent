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
@ApiModel(value="ReportSecurityAbnormalGeneral对象", description="")
public class ReportSecurityAbnormalGeneralQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "身份证")
    private String userId;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String userName;

    @ApiModelProperty(value = "区域")
    private String areaCode;

    @ApiModelProperty(value = "异常维度code")
    private String dimension;

    @ApiModelProperty(value = "异常维度名称")
    private String dimensionName;

    @ApiModelProperty(value = "详情json")
    private String reportDetail;

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
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }
    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
    public String getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(String reportDetail) {
        this.reportDetail = reportDetail;
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

}
