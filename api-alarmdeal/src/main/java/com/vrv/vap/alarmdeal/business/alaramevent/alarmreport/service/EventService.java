package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventListResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTotalResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.RuleTypeInfoResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AbnormalEventVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AbnormalUserVo;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:02
 */
public interface EventService {
    /**
     * 查询监管事件分析 统计信息
     * @param req
     * @return EventTotalResponse
     */
    EventTotalResponse queryEventTotal(RequestBean req);

    /**
     * 查询监管事件趋势图
     * @param req
     * @return Map
     */
    List<EventTypeResponse> queryEventTrend(RequestBean req);

    /**
     * 根据类型统计
     * @param req
     * @param type
     * @return List
     */
    List<EventTypeResponse> queryEventByType(RequestBean req, String type);

    /**
     * 查询事件核查处置信息列表
     * @param req
     * @return List
     */
    List<EventListResponse> queryEventList(RequestBean req);

    List<EventTypeResponse> typeTop10(RequestBean item, Integer integer);

    List<AbnormalEventVo> abnormalInfo10(RequestBean item, Integer integer);

    List<AbnormalUserVo> abnormalUserInfo(RequestBean item,Integer integer );

    List<AbnormalUserVo>  eventUser(RequestBean item);

    List<EventTypeResponse> eventUserTop10(RequestBean item);

    List<RuleTypeInfoResponse> eventTypeInfo(RequestBean item);
}
