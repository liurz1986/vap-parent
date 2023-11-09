package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.EventTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.FocalResultResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.FocalTotalResponse;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:03
 */
public interface FocalReportService {
    /**
     * 查询 重点事件监管分析
     * @param req
     * @return FocalTotalResponse
     */
    FocalTotalResponse queryFocalTotal(RequestBean req);

    /**
     * 重点事件分类统计
     * @param req
     * @return List
     */
    List<EventTypeResponse> queryFocalType(RequestBean req,String type,int topNum);

    /**
     * 查询结论数据信息
     * @param req
     * @return FocalResultResponse
     */
    FocalResultResponse queryFocalResult(RequestBean req);

}
