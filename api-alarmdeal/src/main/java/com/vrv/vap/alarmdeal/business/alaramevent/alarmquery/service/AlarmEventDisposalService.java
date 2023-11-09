package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventDisposal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventUrgeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.QueryIdsVO;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 15:20
 */
public interface AlarmEventDisposalService {
    /**
     * 添加督促信息
     * @param item
     * @param eventId
     * @return
     */
    AlarmEventAttribute appendAlarmEventUrge(AlarmEventUrgeVO item, String eventId);

    /**
     * 添加督促信息（批量）
     * @param vo
     * @return
     */
    List<AlarmEventAttribute> appendAlarmEventUrges(QueryIdsVO vo);

    /**
     * 保存处置信息
     * @param guid
     * @return
     */
    AlarmEventDisposal getAlarmEventDisposal(String guid);
}
