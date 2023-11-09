package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.AlarmDistributionRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.AlarmEventRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.AlarmEventRankRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.EventLevelTotalRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.EventTrendRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.ViolationPersonRes;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/9/13 10:33
 * @description:
 */
public interface AlarmScreenService {
    /**
     * 获取资产告警分布
     * @param req
     * @return
     */
    List<AlarmDistributionRes> getAssetAlarmDistribution(RequestBean req);

    /**
     * 获取资产告警事件
     * @param req
     * @return
     */
    List<AlarmEventRes> getAssetAlarmEvent(RequestBean req);

    /**
     * 违规人员统计top5
     * @param req
     * @param top
     * @return
     */
    List<ViolationPersonRes> getViolationPersonTotal(RequestBean req,Integer top);

    /**
     * 威胁事件排名top5
     * @param req
     * @param top
     * @return
     */
    List<AlarmEventRankRes> getAlarmEventRank(RequestBean req,Integer top);

    /**
     * 违规行为
     * @param req
     * @return
     */
    List<String> getViolations(RequestBean req);

    /**
     * 告警统计
     * @param req
     * @return
     */
    EventLevelTotalRes getAlarmEventLevelTotal(RequestBean req);

    /**
     * 安全事件趋势
     * @param req
     * @return
     */
    List<EventTrendRes> getAlarmEventTrend(RequestBean req);

    /**
     * 统计告警资产数
     * @param req
     * @return
     */
    Integer getAssetCountByAlarm(RequestBean req);
}
