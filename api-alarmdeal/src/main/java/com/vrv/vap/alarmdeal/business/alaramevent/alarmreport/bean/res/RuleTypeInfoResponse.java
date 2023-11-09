package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 9:26
 */
@Data
public class RuleTypeInfoResponse {
    private String name;
    //总计
    private long total;
    //较低
    private long totalLower;
    //一般
    private long totalGeneral;
    //重要
    private long totalImportant;
    //严重
    private long totalSerious;
    //紧急
    private long totalEmergent;
}
