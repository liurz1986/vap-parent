package com.vrv.vap.alarmdeal.business.evaluation.repository;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationProcess;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 自查自评中间表
 *
 * @Date 2023-09
 * @author liurz
 */
@Repository
public interface SelfInspectionEvaluationProcessRepository extends BaseRepository<SelfInspectionEvaluationProcess, String> {
}
