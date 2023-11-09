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
 * @since 2021-05-19
 */
@ApiModel(value="RptSecurityReportImportant对象", description="")
public class RptSecurityReportImportantQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "身份证号")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "机构编码")
    private String orgCode;

    @ApiModelProperty(value = "是否警员:1是0否")
    private Integer isPolice;

    @ApiModelProperty(value = "查询区域")
    private String queryArea;

    @ApiModelProperty(value = "查询应用名称")
    private String querySys;

    @ApiModelProperty(value = "查询人数")
    private Integer queryPeople;

    @ApiModelProperty(value = "入库时间")
    private Date insertTime;

    @ApiModelProperty(value = "数据时间")
    private String dataTime;

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
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public Integer getIsPolice() {
        return isPolice;
    }

    public void setIsPolice(Integer isPolice) {
        this.isPolice = isPolice;
    }
    public String getQueryArea() {
        return queryArea;
    }

    public void setQueryArea(String queryArea) {
        this.queryArea = queryArea;
    }
    public String getQuerySys() {
        return querySys;
    }

    public void setQuerySys(String querySys) {
        this.querySys = querySys;
    }
    public Integer getQueryPeople() {
        return queryPeople;
    }

    public void setQueryPeople(Integer queryPeople) {
        this.queryPeople = queryPeople;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "RptSecurityReportImportantQuery{" +
            "id=" + id +
            ", userId=" + userId +
            ", userName=" + userName +
            ", orgCode=" + orgCode +
            ", isPolice=" + isPolice +
            ", queryArea=" + queryArea +
            ", querySys=" + querySys +
            ", queryPeople=" + queryPeople +
            ", insertTime=" + insertTime +
            ", dataTime=" + dataTime +
        "}";
    }
}
