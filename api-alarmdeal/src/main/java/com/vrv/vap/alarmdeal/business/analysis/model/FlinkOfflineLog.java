package com.vrv.vap.alarmdeal.business.analysis.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 离线任务日志记录表
 */
@Data
@Entity
@Table(name = "flink_offline_log")
public class FlinkOfflineLog {
    /**
     * 主键guid
     */
    @Id
    @Column(name = "guid")
    private String guid;
    /**
     * 策略code
     */
    @Column(name = "rule_code")
    private String ruleCode;
    /**
     * 规则code
     */
    @Column(name = "filter_code")
    private String filterCode;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
}
