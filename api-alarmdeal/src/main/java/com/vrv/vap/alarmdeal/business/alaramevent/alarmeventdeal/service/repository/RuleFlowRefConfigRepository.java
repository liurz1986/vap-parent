package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model.EventFlowRefConfig;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author liurz
 * @date 202310
 */
@Repository
public interface RuleFlowRefConfigRepository extends BaseRepository<EventFlowRefConfig,String> {
}
