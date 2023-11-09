package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 14:46
 */
@Data
public class DealTotalReponse {
    // 上级监管平台下发预警任务
    private long issueAlertTaskNum;

    // 已处预警任务
    private long isWarningTask;

    // 未处理预警
    private long isNotWarningTask;

    // 下发督办事件任务
    private long superviseCount;

    // 已处理督办事件
    private long isSuperviseNum;

    // 未处理督办事件
    private long isNotSuperviseNum;

    // 下发协办任务
    private long assistingCount;

    // 已处理协办任务
    private long isAssistingNum;

    // 未处理协办任务
    private long isNotAssistingNum;
}
