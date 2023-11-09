package com.vrv.vap.alarmdeal.business.evaluation.repository;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 自查自评策略配置
 *
 * @Date 2023-09
 * @author liurz
 */
@Repository
public interface SelfInspectionEvaluationConfigRepository extends BaseRepository<SelfInspectionEvaluationConfig, Integer> {
}
