package com.vrv.vap.alarmdeal.business.threat.service.impl;

import com.vrv.vap.alarmdeal.business.threat.bean.ThreatLevelManage;
import com.vrv.vap.alarmdeal.business.threat.repository.ThreatLevelManageRepository;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatLevelManageService;
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
public class ThreatLevelManageServiceImpl extends BaseServiceImpl<ThreatLevelManage, String> implements ThreatLevelManageService {

    @Autowired
    private ThreatLevelManageRepository threatLevelManageRepository;

    @Override
    public BaseRepository<ThreatLevelManage, String> getRepository() {
        return threatLevelManageRepository;
    }
}
