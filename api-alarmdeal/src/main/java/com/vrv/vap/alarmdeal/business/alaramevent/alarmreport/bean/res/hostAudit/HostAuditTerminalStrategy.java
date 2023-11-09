package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.hostAudit;

import lombok.Data;

import java.util.List;

/**
 * 终端策略变成统计
 * @author wudi
 * @date 2022/4/20 16:19
 */
@Data
public class HostAuditTerminalStrategy {

    private TerminalData data;
    private List<TerminalList> list;


}
