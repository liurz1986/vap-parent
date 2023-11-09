package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.HostAuditEventResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit.HostAuditTerminalStrategy;

/**
 * 主机审计报表接口
 * @author wudi
 * @date 2022/4/20 14:13
 */
public interface HostAuditService {

    /**
     * 主机审计违规事件统计
     * @param requestBean
     * @return
     */
    HostAuditEventResponse searchHostAuditEvent(RequestBean requestBean);

    /**
     * 主机审计-终端策略变更统计
     * @param requestBean
     * @return
     */
    HostAuditTerminalStrategy searchHostAuditTerminalEvent(RequestBean requestBean);


}
