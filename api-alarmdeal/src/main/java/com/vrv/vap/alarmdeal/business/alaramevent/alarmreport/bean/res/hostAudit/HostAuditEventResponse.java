package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit;

import lombok.Data;

import java.util.List;

/**
 * @author wudi
 * @date 2022/4/20 14:15
 */
@Data
public class HostAuditEventResponse {


    private List<StaticsList> list;
    private StaticData data;



}
