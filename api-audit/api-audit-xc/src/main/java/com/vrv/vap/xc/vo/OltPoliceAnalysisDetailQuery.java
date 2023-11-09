package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="OltPoliceAnalysisDetail对象", description="")
public class OltPoliceAnalysisDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String taskId;

    private String areaCode;

    private String policeTypeCode;

    private String policeTypeName;

    private String idCard;

    private String userName;

    private String organ;

    private String ip;

    private String operateTime;

    private String operateName;

    private String operateCondition;

    private String sysId;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getPoliceTypeCode() {
        return policeTypeCode;
    }

    public void setPoliceTypeCode(String policeTypeCode) {
        this.policeTypeCode = policeTypeCode;
    }
    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
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
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }
    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "OltPoliceAnalysisDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaCode=" + areaCode +
            ", policeTypeCode=" + policeTypeCode +
            ", policeTypeName=" + policeTypeName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", organ=" + organ +
            ", ip=" + ip +
            ", operateTime=" + operateTime +
            ", operateName=" + operateName +
            ", operateCondition=" + operateCondition +
            ", sysId=" + sysId +
            ", content=" + content +
        "}";
    }
}
