package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 梁国露
 * @since: 2022/10/12 14:07
 * @description:
 */
@Data
@Table(name = "dimension_sync")
@Entity
public class DimensionSync {
    @Id
    @Column(name="guid")
    private String guid;

    @Column(name="filter_code")
    private String filterCode;

    @Column(name="rule_code")
    private String ruleCode;

    @Column(name="dimension_table_name")
    private String dimensionTableName;

    @Column(name="conditions")
    private String conditions;
}
