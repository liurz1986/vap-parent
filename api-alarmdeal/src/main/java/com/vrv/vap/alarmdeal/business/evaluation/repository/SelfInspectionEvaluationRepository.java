package com.vrv.vap.alarmdeal.business.evaluation.repository;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 自查自评结果表
 *
 * @Date 2023-09
 * @author liurz
 */
@Repository
public interface SelfInspectionEvaluationRepository extends BaseRepository<SelfInspectionEvaluation, String> {
}
