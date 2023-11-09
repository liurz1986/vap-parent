package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.DealTotalReponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.DealTypeResponse;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:03
 */
public interface DealReportService {
    /**
     * 预警、督办、协办概要信息
     * @param req
     * @return DealTotalReponse
     */
    DealTotalReponse queryDealTotal(RequestBean req);

    List<DealTypeResponse> queryDealListByType(RequestBean req, String type);
}
