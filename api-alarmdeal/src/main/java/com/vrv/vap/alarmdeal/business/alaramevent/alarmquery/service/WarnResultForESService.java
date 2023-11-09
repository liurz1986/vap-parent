package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmModel.model.WarnAnalysisVO;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AlarmAttackPath;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ExpertVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AnalysisStatusVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.AssetIpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.LogTableNameVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ThreatIntelligenceVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.AttackVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultObjVO;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月20日 10:26
 */
public interface WarnResultForESService {
    public static final String WARN_RESULT_TMP = "warnresulttmp";
    public List<Map<String, Object>> getStasticsByRelateField(String fieldName, Integer count);
    public List<Map<String, Object>> getStasticsByRelateField(AnalysisVO analysisVO, String fieldName, Integer count);
    public  List<Map<String,Object>> getAlarmLogByTime(String guid);
    public WarnResultLogTmpVO getAlarmById(String guid);
    public ThreatIntelligenceVO getThreatIntelligenceVO(String guid);
    public Result<String> exportAlarmLogs(Map<String,Object> map);
    public List<QueryCondition_ES> getCondition(AnalysisVO analysisVO);
    public List<Map<String, Object>> getWeightMaps(List<QueryCondition_ES> conditions);
    public boolean changeAnalysisResultStatus(AnalysisStatusVO analysisStatusVO);
    public PageRes_ES<WarnAnalysisVO> getAttackAlarmPager(AnalysisVO analysisVO, PageReq_ES request);
    public List<String> getSafeAlarmTitle();
    public List<Map<String, Object>> getSafeAlarmTrendBy30DaysByEventCategory(String riskEventCode);
    public List<List<Map<String, Object>>> getSafeAlarmTrendBy30Days(String riskEventName);
    public List<Map<String, Object>> getSafeAlarmTrendBy7Days();
    public List<Map<String, Object>> getAlarmDealCountByOneYear();
    public List<Map<String, Object>> getAlarmNames(AnalysisVO analysisVO);
    public Map<String, Object> getAnalysisTable(LogTableNameVO logTableNameVO);
    public List<LogTableNameVO> getLogTableNamVOs(String guid);

    public List<Map<String, Object>> analysisBarList(AnalysisVO analysisVO);
    public List<WarnResultLogTmpVO> getAlarmByIds(String guids);
    public List<Map<String, Object>> getCountByAlarmType(AnalysisVO analysisVO);
    public Result<BusinessIntance> transferExpert(ExpertVO experVO);
    public Result<BusinessIntance> transferAlarm(ExpertVO experVO);
    public int getDstIpSum(AnalysisVO analysisVO);
    public int getSrcIpSum(AnalysisVO analysisVO);
    public  Result<String> exportAlarmDeal(Map<String,Object> map);
    public Result<Map<String,Object>> eventAlarmStatusTotalByWorkBench(AnalysisVO analysisVO);
    public Result<List<Map<String, Object>>> getWeightTrend(AnalysisVO analysisVO);
    public  Result<List<Map<String,Object>>> getStatusBar(AnalysisVO analysisVO);
    public List<Map<String, Object>> getSrcIpList(String start_time, String end_time);
    public Result<Long> eventAlarmTotal(AnalysisVO analysisVO);
    public Result<List<Map<String, Object>>> getAlarmTrendBy7Days(AnalysisVO analysisVO);
    public Result<Map<String, Object>> getAlarmEventLevel(AnalysisVO analysisVO);
    public Result<List<Map<String, Object>>> getLevelEventCateoryByTriggerTime(AnalysisVO analysisVO);
    public Result<List<Map<String, Object>>> getLevelEventCateoryByRegion(AnalysisVO analysisVO);
    public Result<Map<String,Object>> getMultiAlarmTrendBy7Days(List<AnalysisVO> analysisVOs);
    public Result<Map<String, Object>> getMultiEventAlarmTotal(List<AnalysisVO> analysisVOs);
    public Result<Map<String,Object>> getCountByRegion(List<AnalysisVO> analysisVOs);
    public ResultObjVO<Map<String, Object>> getKnowledgeByTag(String ruleId);
    public List<Map<String, Object>> getOrignalLogInfo();
    public List<Map<String, Object>> getALarmRiskEvent(Map<String, Object> map);
    public List<Map<String, Object>> getAlarmByWeight(AnalysisVO analysisVO);
    public List<Map<String, Object>> getAlarmdByWeightAndTime(Map<String, Object> map);
    public List<Map<String, Object>> getAlarmTypeStaticsByRegion(Map<String, Object> map);
    public List<Map<String,Object>> getAlarmStaticsByRegionAndType(AnalysisVO analysisVO);
    public List<Map<String, Object>> getAlarmTypeStaticsCount(Map<String, Object> map);
    public List<Map<String, Object>> getAlarmTypeStaticsByRegionTop20(Map<String,Object> map);
    public List<Map<String, Object>> getAlarmTypeOrderByCount(Map<String, Object> map);
    public List<Map<String, Object>> getSrcIpTop5(AnalysisVO analysisVO);
    public AlarmAttackPath getAlarmAttackPath(String riskEventCode);
    public AttackVO getAttackPath(String riskEventCode);
    public List<Map<String,Object>> attackTypeDistribute(String field);
    public List<Map<String, Object>> attackTypeHotPoint();
    public List<Map<String, Object>> alarmAttackByTimeStatics();
    public List<Map<String, Object>> getAnalysisInfoByArea(AnalysisVO analysisVO);
    public PageRes_ES<WarnResultLogTmpVO> getAlarmPager(AnalysisVO analysisVO, PageReq_ES request);
    public String[] getRuleCodeArr();
    public List<Map<String, Object>> eventAlarmStatistics(AnalysisVO analysisVO, SearchField searchField);
    public Result<List<Map<String, Object>>> getAttackTypeTop20(AnalysisVO analysisVO);
    public Result<List<Map<String, Object>>> getAttackTrendByTimeRange(AnalysisVO analysisVO);
    public List<Map<String, Object>> getAlarmScoreBySrcIp(Map<String, Object> map);
    public List<Map<String, Object>> getAlarmInfoBySrcIpAndRuleId(Map<String, Object> map);
    public Integer getAffectedAssetCount(AnalysisVO analysisVO);
    public List<Map<String, Object>> getAnalysisInfoBySrcArea(AnalysisVO analysisVO);
    public List<Map<String, Object>> getAffectedAssetDetail(AnalysisVO analysisVO,Integer top);
    public List<AssetIpVO> getDstIpTop5(String riskEventCode);
}
