package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

/**
 * @author lps 2021/9/26
 */

@Data
public class FilterOperatorGroupStartVO {

    private String riskEventId; //策略Id

    private String guids;   //规则id

    private Integer sourceId; //规则关联的数据源id

}
