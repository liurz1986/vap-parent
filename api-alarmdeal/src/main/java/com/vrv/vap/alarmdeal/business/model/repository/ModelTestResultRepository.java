package com.vrv.vap.alarmdeal.business.model.repository;

import com.vrv.vap.alarmdeal.business.model.model.ModelTestResult;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelTestResultRepository extends BaseRepository<ModelTestResult,String> {
}
