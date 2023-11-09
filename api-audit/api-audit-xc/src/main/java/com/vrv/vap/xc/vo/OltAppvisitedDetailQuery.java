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
@ApiModel(value="OltAppvisitedDetail对象", description="")
public class OltAppvisitedDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "警种名称")
    private String policeTypeName;

    @ApiModelProperty(value = "身份证号")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "机构名称")
    private String organ;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "操作时间")
    private String operateTime;

    @ApiModelProperty(value = "操作条件")
    private String operateCondition;

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    private String areaCode;

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
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
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
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "OltAppvisitedDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaName=" + areaName +
            ", policeTypeName=" + policeTypeName +
            ", userId=" + userId +
            ", userName=" + userName +
            ", organ=" + organ +
            ", ip=" + ip +
            ", operateTime=" + operateTime +
            ", operateCondition=" + operateCondition +
            ", sysId=" + sysId +
            ", areaCode=" + areaCode +
        "}";
    }
}
