package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req;

import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/10/12 11:11
 * @description:
 */
@Data
public class SyncRequest {
    // 规则code
    private String filterCode;

    // 策略code
    private String ruleCode;

    // 维表表名
    private String dimensionTableName;

    // 筛选条件list
    private List<String> conditions;
}
