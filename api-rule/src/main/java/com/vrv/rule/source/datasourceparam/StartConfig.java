package com.vrv.rule.source.datasourceparam;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 启动参数标准
 */
@Data
@Builder
public class StartConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private CycleStrategyCondition cycleStrategyCondition; //周期策略

    private TimeSelectCondition timeSelectCondition; //时间筛选条件

    private List<BusinessSelectField> businessSelectFields; //业务筛选条件





    //筛选类型（小时，天，周，月）
    @Data
    public static  class CycleStrategyCondition{
        private String type;
        private String cronExpression;
    }

    //时间筛选参数
    @Data
    public static class TimeSelectCondition{
        private String timeField;
        private String type;

    }


    //业务筛选参数
    @Data
    public static class BusinessSelectField{
        private String selectField;
        private String relationship;
        private String threshhold;
        private String selectFieldType;
    }


}
