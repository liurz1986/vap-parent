package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "db_operation_info")
@Data
public class DbOperationInfo {
    @Id
    private String uuid;

    @Column(name = "operation_type")
    @ApiModelProperty("操作类型")
    private Integer operationType;

    @Column(name = "operation_status")
    @ApiModelProperty("操作状态")
    private Integer operationStatus;

    @Column(name = "data_types")
    @ApiModelProperty("数据类型")
    private String dataTypes;

    @Column(name = "file_name")
    @ApiModelProperty("文件名称")
    private String fileName;

    @Column(name = "file_md5")
    @ApiModelProperty("文件MD5")
    private String fileMd5;

    @Column(name = "file_storage")
    @ApiModelProperty("存储介质")
    private String fileStorage;

    @Column(name = "message")
    @ApiModelProperty("异常信息")
    private String message;

    @Column(name = "start_time")
    @ApiModelProperty("开始时间")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column(name = "end_time")
    @ApiModelProperty("结束时间")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}