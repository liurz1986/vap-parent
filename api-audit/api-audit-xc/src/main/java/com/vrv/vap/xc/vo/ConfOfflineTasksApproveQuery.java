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
 * @since 2021-05-28
 */
@ApiModel(value="ConfOfflineTasksApprove对象", description="")
public class ConfOfflineTasksApproveQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Date endTime;

    private Date startTime;

    private Date creatTime;

    private String taskDetail;

    private String type;

    private String taskName;

    private String taskId;

    @ApiModelProperty(value = "审批状态 0 待审批；1审批成功；2审批失败；")
    private String status;

    @ApiModelProperty(value = "审批意见")
    private String remark;

    @ApiModelProperty(value = "申请人账号")
    private String applyAccount;

    @ApiModelProperty(value = "审批人账号")
    private String approveAccount;

    @ApiModelProperty(value = "审批时间")
    private Date approveTime;

    @ApiModelProperty(value = "审批文件名")
    private String approveFile;

    @ApiModelProperty(value = "审批文件路径")
    private String approveFilePath;

    @ApiModelProperty(value = "申请缘由")
    private String reason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }
    public String getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        this.taskDetail = taskDetail;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getApplyAccount() {
        return applyAccount;
    }

    public void setApplyAccount(String applyAccount) {
        this.applyAccount = applyAccount;
    }
    public String getApproveAccount() {
        return approveAccount;
    }

    public void setApproveAccount(String approveAccount) {
        this.approveAccount = approveAccount;
    }
    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }
    public String getApproveFile() {
        return approveFile;
    }

    public void setApproveFile(String approveFile) {
        this.approveFile = approveFile;
    }
    public String getApproveFilePath() {
        return approveFilePath;
    }

    public void setApproveFilePath(String approveFilePath) {
        this.approveFilePath = approveFilePath;
    }
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ConfOfflineTasksApprove{" +
            "id=" + id +
            ", endTime=" + endTime +
            ", startTime=" + startTime +
            ", creatTime=" + creatTime +
            ", taskDetail=" + taskDetail +
            ", type=" + type +
            ", taskName=" + taskName +
            ", taskId=" + taskId +
            ", status=" + status +
            ", remark=" + remark +
            ", applyAccount=" + applyAccount +
            ", approveAccount=" + approveAccount +
            ", approveTime=" + approveTime +
            ", approveFile=" + approveFile +
            ", approveFilePath=" + approveFilePath +
            ", reason=" + reason +
        "}";
    }
}
