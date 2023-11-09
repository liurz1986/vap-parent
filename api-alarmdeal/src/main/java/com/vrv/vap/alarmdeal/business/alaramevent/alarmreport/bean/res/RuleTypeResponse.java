package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author tyj
 * @date 2023年08月04日 9:26
 */
@Data
public class RuleTypeResponse {
    //策略分类
    private String name;
    //运行中
    private Integer isStartedCount;
    //已停用
    private Integer notStartedCount;
    //总计
    private Integer total;
}
