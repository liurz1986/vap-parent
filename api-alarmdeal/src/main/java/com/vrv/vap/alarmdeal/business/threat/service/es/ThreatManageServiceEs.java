package com.vrv.vap.alarmdeal.business.threat.service.es;

import com.vrv.vap.alarmdeal.business.threat.bean.ThreatManage;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import org.springframework.stereotype.Service;

/**
 * @author: 梁国露
 * @since: 2022/9/7 17:51
 * @description:
 */
@Service("threatManageServiceEs")
public class ThreatManageServiceEs extends ElasticSearchRestClientService<ThreatManage> {
    public static final String WARN_RESULT_TMP = "threatmanage";

    @Override
    public String getIndexName() {
        return WARN_RESULT_TMP;
    }

}
