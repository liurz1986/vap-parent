package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Table(name = "db_backup_config")
@Data
public class DbBackupConfigInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_type")
    @ApiModelProperty("数据类型")
    private String dataType;

    @Column(name = "table_name")
    @ApiModelProperty("数据类型")
    private String tableName;

    @Column(name = "time_field")
    @ApiModelProperty("时间字段")
    private String timeField;

    @Column(name = "relate_service")
    @ApiModelProperty("关联服务")
    private String relateService;
}