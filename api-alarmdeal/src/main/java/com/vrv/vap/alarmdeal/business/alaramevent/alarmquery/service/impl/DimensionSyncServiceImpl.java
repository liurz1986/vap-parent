package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.DimensionSync;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.DimensionSyncService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.DimensionSyncRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.RiskEventRuleRespository;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: 梁国露
 * @since: 2022/10/12 14:26
 * @description:
 */
@Service
public class DimensionSyncServiceImpl extends BaseServiceImpl<DimensionSync, String> implements DimensionSyncService {

    @Autowired
    private DimensionSyncRespository dimensionSyncRespository;

    @Override
    public BaseRepository<DimensionSync, String> getRepository() {
        return dimensionSyncRespository;
    }
}
