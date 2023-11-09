package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Table(name = "db_backup_strategy")
@Data
@ApiModel("备份策略信息")
public class DbBackupStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_types")
    @ApiModelProperty("数据类型")
    private String dataTypes;

    @Column(name = "backup_period")
    @ApiModelProperty("备份周期")
    private Integer backPeriod;

    @Column(name = "backup_time")
    @ApiModelProperty("备份开始时间")
    private String backupTime;

    @Column(name = "max_version")
    @ApiModelProperty("最大保存版本数")
    private Integer maxVersion;

    @Column(name = "strategy_status")
    @ApiModelProperty("策略状态")
    private Integer strategyStatus;

    @Column(name = "file_storage")
    @ApiModelProperty("存储介质")
    private String fileStorage;
}