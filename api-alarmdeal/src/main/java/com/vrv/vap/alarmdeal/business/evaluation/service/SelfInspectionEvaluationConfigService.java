package com.vrv.vap.alarmdeal.business.evaluation.service;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationConfig;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import java.util.List;
import java.util.Map;

public interface SelfInspectionEvaluationConfigService extends BaseService<SelfInspectionEvaluationConfig,Integer> {

   public PageRes<SelfInspectionEvaluationConfig> getPage(SelfInspectionEvaluationConfigSearchVO data);

    public  Result<String> editValidate(SelfInspectionEvaluationConfigVO data);

    public  Result<SelfInspectionEvaluationConfig> edit(SelfInspectionEvaluationConfigVO data);

    public  Result<List<ConifgTreeVO>> getTree();

    public  Result<ConfigDeatilVO> getDetail(int  id);

    public Result<Map<String,Object>> getAllCheckTypeAndGeneticType();
}
