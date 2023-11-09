package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 10:53
 */
@Data
public class EventListResponse extends RequestBean{
    // 事件名称
    private String zjgEventsName;

    // 核查情况
    private String zjgRevise;

    //成因分析
    private String zjgReason;

    // 整改情况
    private String status;
}
