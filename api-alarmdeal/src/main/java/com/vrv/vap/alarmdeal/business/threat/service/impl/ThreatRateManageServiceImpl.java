package com.vrv.vap.alarmdeal.business.threat.service.impl;

import com.vrv.vap.alarmdeal.business.threat.bean.ThreatRateManage;
import com.vrv.vap.alarmdeal.business.threat.repository.ThreatRateManageRepository;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatRateManageService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Administrator
 * @since: 2022/8/29 16:06
 * @description:
 */
@Service
public class ThreatRateManageServiceImpl extends BaseServiceImpl<ThreatRateManage, String> implements ThreatRateManageService {


    @Autowired
    private ThreatRateManageRepository threatRateManageRepository;

    @Override
    public BaseRepository<ThreatRateManage, String> getRepository() {
        return threatRateManageRepository;
    }
}
