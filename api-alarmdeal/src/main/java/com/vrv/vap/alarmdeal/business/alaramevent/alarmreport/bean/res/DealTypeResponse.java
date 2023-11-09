package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 15:18
 */
@Data
public class DealTypeResponse {
    // 事件名称
    private String taskName;

    // 下发时间
    private String downTime;

    // 响应时间
    private String resTime;

    // 任务状态
    private String taskStatus;
}
