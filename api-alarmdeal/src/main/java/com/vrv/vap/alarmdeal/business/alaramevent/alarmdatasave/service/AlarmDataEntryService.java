package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月18日 18:00
 */
public interface AlarmDataEntryService {
    public List<AlarmEventAttribute> handleAlarmDataEntry(List<WarnResultLogTmpVO> warnResultLogTmpVos);
}
