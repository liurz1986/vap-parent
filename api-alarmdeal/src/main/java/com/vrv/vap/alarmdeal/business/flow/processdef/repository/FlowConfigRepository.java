package com.vrv.vap.alarmdeal.business.flow.processdef.repository;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.FlowConfig;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketInnerForm;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowConfigRepository extends BaseRepository<FlowConfig, String> {

}
