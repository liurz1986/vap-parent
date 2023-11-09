package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:07
 */
@Data
public class EventTotalResponse {
    // 新增监管事件
    private long total;

    // 特别重大事件
    private long hocNum;

    // 重大事件
    private long majorNum;

    // 较大事件
    private long moreNum;

    // 已处置事件
    private long dealNum;

    // 待处置事件
    private long notDealNum;

    // 督促事件
    private long urgeNum;

    // 督办事件
    private long superviseNum;
}
