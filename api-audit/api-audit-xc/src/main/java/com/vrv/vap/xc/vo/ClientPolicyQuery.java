package com.vrv.vap.xc.vo;

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
@ApiModel(value="ClientPolicy对象", description="")
public class ClientPolicyQuery extends Query {
    private String id;

    @ApiModelProperty(value = "策略类型 0：卸载，1：升级，2：采集，3、点对点")
    private String objType;

    private String name;

    private String content;

    private String crc;

    private String creatorId;

    private Date createTime;

    private String updaterId;

    private Date updateTime;

    private Integer isPublish;

    private Date publishTime;

    private String approvalId;

    private Integer approvalState;

    private Integer approvalLevel;

    private Integer state;

    private String organizationId;

    private String policyTemplateId;

    private Integer level;

    private String description;

    private Integer privateState;

    private Integer priority;

    @ApiModelProperty(value = "策略类型 0：卸载，1：升级，2：采集，3、点对点")
    private Integer policyType;

    private Integer targetState;

    private Integer targetCollectNum;

    private Integer targetCompletedNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(String updaterId) {
        this.updaterId = updaterId;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }
    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }
    public Integer getApprovalState() {
        return approvalState;
    }

    public void setApprovalState(Integer approvalState) {
        this.approvalState = approvalState;
    }
    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    public String getPolicyTemplateId() {
        return policyTemplateId;
    }

    public void setPolicyTemplateId(String policyTemplateId) {
        this.policyTemplateId = policyTemplateId;
    }
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getPrivateState() {
        return privateState;
    }

    public void setPrivateState(Integer privateState) {
        this.privateState = privateState;
    }
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public Integer getPolicyType() {
        return policyType;
    }

    public void setPolicyType(Integer policyType) {
        this.policyType = policyType;
    }
    public Integer getTargetState() {
        return targetState;
    }

    public void setTargetState(Integer targetState) {
        this.targetState = targetState;
    }
    public Integer getTargetCollectNum() {
        return targetCollectNum;
    }

    public void setTargetCollectNum(Integer targetCollectNum) {
        this.targetCollectNum = targetCollectNum;
    }
    public Integer getTargetCompletedNum() {
        return targetCompletedNum;
    }

    public void setTargetCompletedNum(Integer targetCompletedNum) {
        this.targetCompletedNum = targetCompletedNum;
    }

    @Override
    public String toString() {
        return "ClientPolicy{" +
            "id=" + id +
            ", objType=" + objType +
            ", name=" + name +
            ", content=" + content +
            ", crc=" + crc +
            ", creatorId=" + creatorId +
            ", createTime=" + createTime +
            ", updaterId=" + updaterId +
            ", updateTime=" + updateTime +
            ", isPublish=" + isPublish +
            ", publishTime=" + publishTime +
            ", approvalId=" + approvalId +
            ", approvalState=" + approvalState +
            ", approvalLevel=" + approvalLevel +
            ", state=" + state +
            ", organizationId=" + organizationId +
            ", policyTemplateId=" + policyTemplateId +
            ", level=" + level +
            ", description=" + description +
            ", privateState=" + privateState +
            ", priority=" + priority +
            ", policyType=" + policyType +
            ", targetState=" + targetState +
            ", targetCollectNum=" + targetCollectNum +
            ", targetCompletedNum=" + targetCompletedNum +
        "}";
    }
}
