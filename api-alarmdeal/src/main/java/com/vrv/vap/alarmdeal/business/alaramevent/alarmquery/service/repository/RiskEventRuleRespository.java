package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskEventRuleRespository extends BaseRepository<RiskEventRule, String> {

}
