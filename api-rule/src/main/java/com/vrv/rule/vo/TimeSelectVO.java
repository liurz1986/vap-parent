package com.vrv.rule.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 时间筛选条件VO
 */
@Data
@Builder
public class TimeSelectVO {

    private String startTime; //开始时间
    private String endTime;  //结束时间

}
