package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2022/7/21
 * @description
 */
@Table(name = "sync_base_data_log")
public class SyncBaseDataLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("任务名称")
    @Column(name = "task_name")
    private String taskName;

    @ApiModelProperty("任务类型")
    @Column(name = "type")
    private String type;

    @ApiModelProperty("数据来源")
    private String source;

    @ApiModelProperty("数据总量")
    @Column(name = "total_count")
    private Integer totalCount;

    @ApiModelProperty("同步时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("同步状态")
    private Integer status;

    @ApiModelProperty("描述")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
