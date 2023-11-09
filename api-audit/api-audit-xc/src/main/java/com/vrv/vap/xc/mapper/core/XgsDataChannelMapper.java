package com.vrv.vap.xc.mapper.core;

import com.vrv.vap.xc.model.CascadeStrategyReceive;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface XgsDataChannelMapper {
    String queryLocalId();

	List<CascadeStrategyReceive> queryCascadeStrategy();

	void updateTimeFlag(Map<String, Object> map);
}
