package com.vrv.vap.alarmdeal.business.alaramevent.strategy.service;

import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.FilterSourceStatus;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.FilterSourceStatusInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.jpa.baseservice.BaseService;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月06日 18:03
 */
public interface FilterSourceStatusService extends BaseService<FilterSourceStatus,String> {

    boolean getFilterSourceStatusByRedis(String sourceId);

    String getFilterSourceStatusMsgByRedis(String sourceId);

    void saveFilterSourceStatus(FilterSourceStatusInfo filterSourceStatusInfo);

    String filterChange(String indexName,String insertTime);




}
