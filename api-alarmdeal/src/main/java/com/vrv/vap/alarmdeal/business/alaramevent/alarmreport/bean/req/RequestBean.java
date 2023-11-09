package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 16:46
 */
@Data
public class RequestBean {
    // 开始时间
    private String startTime;

    // 结束时间
    private String endTime;
}
