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
 * 项目边界通道使用情况
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyProjectPlatChannel对象", description="项目边界通道使用情况")
public class CompanyProjectPlatChannelQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "涉及边界通道名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String channelName;

    @ApiModelProperty(value = "业务名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String businessName;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "数据网络流向")
    private String dataFlowTo;

    @ApiModelProperty(value = "前置机IP/端口")
    private String ip;

    @ApiModelProperty(value = "业务描述")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String description;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    public String getDataFlowTo() {
        return dataFlowTo;
    }

    public void setDataFlowTo(String dataFlowTo) {
        this.dataFlowTo = dataFlowTo;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CompanyProjectPlatChannel{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", companyId=" + companyId +
            ", channelName=" + channelName +
            ", businessName=" + businessName +
            ", businessType=" + businessType +
            ", dataFlowTo=" + dataFlowTo +
            ", ip=" + ip +
            ", description=" + description +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
