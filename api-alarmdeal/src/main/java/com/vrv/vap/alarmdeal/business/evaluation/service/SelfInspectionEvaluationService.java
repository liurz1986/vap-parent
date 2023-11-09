package com.vrv.vap.alarmdeal.business.evaluation.service;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluation;
import com.vrv.vap.alarmdeal.business.evaluation.vo.EvaluationResultDeatilVO;
import com.vrv.vap.alarmdeal.business.evaluation.vo.SelfInspectionEvaluationSearchVO;
import com.vrv.vap.alarmdeal.business.evaluation.vo.SelfInspectionEvaluationVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import java.util.Map;

public interface SelfInspectionEvaluationService extends BaseService<SelfInspectionEvaluation, String> {
  public  PageRes<SelfInspectionEvaluation> getPage(SelfInspectionEvaluationSearchVO data);

  public Result<String> execute(SelfInspectionEvaluationVO data);

  public  Result<Map<String, Object>> statusStatistics();

  public  Result<Map<String,Object>> depAndGeneticStatistics();

  public Result<EvaluationResultDeatilVO> getDetailById(String id);
}
