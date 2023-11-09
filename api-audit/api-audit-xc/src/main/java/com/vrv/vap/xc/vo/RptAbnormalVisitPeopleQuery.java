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
@ApiModel(value="RptAbnormalVisitPeople对象", description="")
public class RptAbnormalVisitPeopleQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "部门")
    private String organization;

    @ApiModelProperty(value = "月份")
    private String month;

    @ApiModelProperty(value = "部门平局值")
    private Double organizationAvg;

    @ApiModelProperty(value = "访问次数")
    private Integer count;

    @ApiModelProperty(value = "本部门访问值阀值")
    private String organizationConf;

    @ApiModelProperty(value = "个人历史访问阀值")
    private String historyConf;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "详情")
    private String content;

    @ApiModelProperty(value = "历史平均值")
    private Double historyAvg;

    @ApiModelProperty(value = "应用id")
    private String sysId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
    public Double getOrganizationAvg() {
        return organizationAvg;
    }

    public void setOrganizationAvg(Double organizationAvg) {
        this.organizationAvg = organizationAvg;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getOrganizationConf() {
        return organizationConf;
    }

    public void setOrganizationConf(String organizationConf) {
        this.organizationConf = organizationConf;
    }
    public String getHistoryConf() {
        return historyConf;
    }

    public void setHistoryConf(String historyConf) {
        this.historyConf = historyConf;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Double getHistoryAvg() {
        return historyAvg;
    }

    public void setHistoryAvg(Double historyAvg) {
        this.historyAvg = historyAvg;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    @Override
    public String toString() {
        return "RptAbnormalVisitPeopleQuery{" +
            "id=" + id +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", organization=" + organization +
            ", month=" + month +
            ", organizationAvg=" + organizationAvg +
            ", count=" + count +
            ", organizationConf=" + organizationConf +
            ", historyConf=" + historyConf +
            ", createTime=" + createTime +
            ", content=" + content +
            ", historyAvg=" + historyAvg +
            ", sysId=" + sysId +
        "}";
    }
}
