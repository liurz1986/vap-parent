package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月23日 10:34
 */
@Service("elasticSearchRestService")
public class AlarmEventAttributeEsClientServiceImpl extends ElasticSearchRestClientService<AlarmEventAttribute> {
    public static final String WARN_RESULT_TMP = "alarmeventmanagement";

    @Override
    public String getIndexName() {
        return WARN_RESULT_TMP;
    }
}
