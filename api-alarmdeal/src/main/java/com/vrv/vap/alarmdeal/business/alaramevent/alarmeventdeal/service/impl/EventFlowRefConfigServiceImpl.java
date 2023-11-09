package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model.EventFlowRefConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.EventFlowRefConfigService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository.RuleFlowRefConfigRepository;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 规则关联流程的配置信息
 *
 * @author liurz
 * @date 202310
 */
@Service
public class EventFlowRefConfigServiceImpl extends BaseServiceImpl<EventFlowRefConfig,String> implements EventFlowRefConfigService {
    @Autowired
    private RuleFlowRefConfigRepository ruleFlowRefConfigRepository;

    @Override
    public BaseRepository<EventFlowRefConfig, String> getRepository() {
        return ruleFlowRefConfigRepository;
    }
}
