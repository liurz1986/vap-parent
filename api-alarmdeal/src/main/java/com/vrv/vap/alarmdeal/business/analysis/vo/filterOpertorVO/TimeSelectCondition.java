package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import lombok.Data;

/**
 * 时间选择条件
 */
@Data
public class TimeSelectCondition {
    /**
     * 时间筛选字段
     */
    private String timeField;
    /**
     * 类型  天day  周week  月month  时hour
     */
    private String type;


}
