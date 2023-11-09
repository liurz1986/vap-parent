package com.vrv.vap.alarmdeal.business.evaluation.service;
import com.vrv.vap.alarmdeal.business.evaluation.vo.*;
import com.vrv.vap.jpa.web.Result;
import java.util.List;
import java.util.Map;

public interface EvaluationReportService {
    public Result<SummaryVO> queryEvaluationResult(EvaluationReportSearchVO evaluationReportSearchVO);

    public Result<Map<String, Object>> queryStatusStatistics(EvaluationReportSearchVO evaluationReportSearchVO);

    public  Result<List<KeyValueVO>> queryStatusList(EvaluationReportSearchVO evaluationReportSearchVO);

    public Result<List<KeyValueVO>> queryGeneticTypeStatistics(EvaluationReportSearchVO evaluationReportSearchVO);

    public Result<List<KeyValueVO>> queryHandlIssuesStatistics(EvaluationReportSearchVO evaluationReportSearchVO);

    public Result<List<KeyValueVO>> queryStatisticsByType(EvaluationReportSearchVO evaluationReportSearchVO, String type);

    public Result<List<NoInforOrgResultVO>> queryNoInforOrgStatistic(EvaluationReportSearchVO evaluationReportSearchVO);

    public Result<List<SummaryStatisticVO>> querySummaryStatistics(EvaluationReportSearchVO evaluationReportSearchVO);

    public  Result<List<GeneticTypeDetailVO>> queryGeneticTypeDetail(EvaluationReportSearchVO evaluationReportSearchVO);


}
