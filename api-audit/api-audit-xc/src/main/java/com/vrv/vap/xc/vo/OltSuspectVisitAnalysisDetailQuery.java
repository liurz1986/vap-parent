package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="OltSuspectVisitAnalysisDetail对象", description="")
public class OltSuspectVisitAnalysisDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务编号")
    private String taskId;

    @ApiModelProperty(value = "地区编码")
    private String areaCode;

    @ApiModelProperty(value = "地区名称")
    private String areaName;

    @ApiModelProperty(value = "警种编码")
    private String policeTypeCode;

    @ApiModelProperty(value = "警种名称")
    private String policeTypeName;

    @ApiModelProperty(value = "身份证号码")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "组织机构")
    private String organ;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "操作时间")
    private String operateTime;

    @ApiModelProperty(value = "操作名称")
    private String operateName;

    @ApiModelProperty(value = "操作条件")
    private String operateCondition;

    @ApiModelProperty(value = "操作对象")
    private String objectValue;

    @ApiModelProperty(value = "应用系统编号")
    private String sysId;

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
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    @Override
    public String toString() {
        return "OltSuspectVisitAnalysisDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", policeTypeCode=" + policeTypeCode +
            ", policeTypeName=" + policeTypeName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", organ=" + organ +
            ", ip=" + ip +
            ", operateTime=" + operateTime +
            ", operateName=" + operateName +
            ", operateCondition=" + operateCondition +
            ", objectValue=" + objectValue +
            ", sysId=" + sysId +
        "}";
    }
}
