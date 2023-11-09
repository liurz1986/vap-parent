package com.vrv.vap.monitor.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "db_operation_info")
@Data
public class DbOperationInfo {
    @Id
    private String uuid;

    @Column(name = "operation_type")
    private Integer operationType;

    @Column(name = "operation_status")
    private Integer operationStatus;

    @Column(name = "data_types")
    private String dataTypes;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_md5")
    private String fileMd5;

    @Column(name = "file_storage")
    private String fileStorage;

    @Column(name = "message")
    private String message;

    @Column(name = "start_time")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column(name = "end_time")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}