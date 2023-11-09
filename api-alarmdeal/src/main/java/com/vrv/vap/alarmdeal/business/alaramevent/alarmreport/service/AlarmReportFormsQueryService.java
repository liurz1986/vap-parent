package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AlarmUniteResultVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AlarmUniteSearchVO;

import java.util.List;

public interface AlarmReportFormsQueryService {

    public List<AlarmUniteResultVO> queryAlarmDetail(AlarmUniteSearchVO alarmUniteSearchVO);
}
