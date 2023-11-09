package com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.repository.RuleFilterRepository;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月06日 18:04
 */
@Service
public class RuleFilterServiceImpl extends BaseServiceImpl<RuleFilter, String> implements RuleFilterService {

    @Autowired
    private RuleFilterRepository ruleFilterRepository;

    @Override
    public BaseRepository<RuleFilter, String> getRepository() {
        return ruleFilterRepository;
    }
}
