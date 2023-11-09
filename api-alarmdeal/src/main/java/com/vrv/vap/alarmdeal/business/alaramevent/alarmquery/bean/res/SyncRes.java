package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.res;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.FieldConditionBean;
import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/10/12 14:34
 * @description:
 */
@Data
public class SyncRes {
    // 规则code
    private String filterCode;

    // 策略code
    private String ruleCode;

    // 维表表名
    private String dimensionTableName;

    // 筛选list
    List<List<FieldConditionBean>> list;
}
