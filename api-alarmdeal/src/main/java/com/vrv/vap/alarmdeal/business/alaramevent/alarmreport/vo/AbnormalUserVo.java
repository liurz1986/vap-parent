package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import lombok.Data;

/**
 * 日志信息
 * 2023-03-23
 */
@Data
public class AbnormalUserVo {
    //序号
    Integer id;
    //编号
    String staffNo;
    //	姓名
    String staffName;
    // 部门
    String staffDepartment;
    //	密集
    String staffLevel;
    //	事件数量
    Integer eventCount;
    //异常事件最高等级
    String alarmRiskLevel;
}
