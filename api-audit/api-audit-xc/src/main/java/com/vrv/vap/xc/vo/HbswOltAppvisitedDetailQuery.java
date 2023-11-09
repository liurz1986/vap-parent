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
 * @since 2021-05-27
 */
@ApiModel(value="HbswOltAppvisitedDetail对象", description="")
public class HbswOltAppvisitedDetailQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String taskId;

    private String areaCode;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "机构名称")
    private String organ;

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    @ApiModelProperty(value = "操作条件")
    private String operateCondition;

    @ApiModelProperty(value = "操作时间")
    private String operateTime;

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
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "HbswOltAppvisitedDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", ip=" + ip +
            ", userName=" + userName +
            ", organ=" + organ +
            ", sysId=" + sysId +
            ", operateCondition=" + operateCondition +
            ", operateTime=" + operateTime +
        "}";
    }
}
