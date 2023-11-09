package com.vrv.vap.monitor.server.model;

import lombok.Data;

import javax.persistence.*;

@Table(name = "db_backup_config")
@Data
public class DbBackupConfigInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "time_field")
    private String timeField;

    @Column(name = "relate_service")
    private String relateService;
}