package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import lombok.Data;

import java.util.List;

/**
 * 启动参数，离线任务才有这个配置
 */
@Data
public class StartConfigVO {
    //策略启动周期
    private CycleStrategyCondition cycleStrategyCondition;
    //时间查询条件
    private TimeSelectCondition timeSelectCondition;
    //业务筛选
    private List<BusinessSelectFields> businessSelectFields;
    //定时任务标记，如果定时任务的周期发生了改变调整，这个定时任务需要把原来的定时任务移除掉重新添加
    private Integer mark;
}

