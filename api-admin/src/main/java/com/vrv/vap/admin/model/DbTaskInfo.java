package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.Date;

@Table(name = "db_backup_task")
public class DbTaskInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    @ApiModelProperty("任务类型")
    private Integer taskType;

    @Column(name = "business_id")
    @Ignore
    private Long businessId;

    @Column(name = "task_id")
    @ApiModelProperty("任务ID")
    private Long taskId;

    @Column(name = "file_name")
    @ApiModelProperty("文件名称")
    private String fileName;

    @Column(name = "file_Path")
    @ApiModelProperty("文件路径")
    private String filePath;

    @Column(name = "file_md5")
    @ApiModelProperty("文件MD5")
    private String fileMd5;

    @Column(name = "file_status")
    @ApiModelProperty("文件状态")
    private Integer status;

    @Column(name = "create_time")
    @ApiModelProperty("创建时间")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
