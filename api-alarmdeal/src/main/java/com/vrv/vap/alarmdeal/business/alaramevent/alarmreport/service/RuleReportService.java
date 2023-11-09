package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventRuleTotalResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.RuleTypeResponse;

import java.util.List;

public interface RuleReportService {

    EventRuleTotalResponse queryTotal(EventRuleTotalResponse eventRuleTotalResponse);

    List<EventTypeResponse> isStarted();

    List<RuleTypeResponse> statistics();
}
