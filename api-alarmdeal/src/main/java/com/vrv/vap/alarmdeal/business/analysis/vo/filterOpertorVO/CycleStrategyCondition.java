package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import lombok.Data;

/**
 * 周期策略条件
 */
@Data
public class CycleStrategyCondition {
    /**
     * 周期类型  天（day）  周(week)  月(month) 小时(hour)
     */
    private String type;
    /**
     * 周期cron表达式
     */
    private String cronExpression;

}
