package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventRuleParams;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.SyncRequest;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.res.SyncRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.*;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilterVo;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.business.analysis.vo.RiskRuleEditVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterOpertorVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventLogFieldVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventLogTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;

public interface RiskEventRuleService extends BaseService<RiskEventRule, String> {

    /**
     * 获得告警规则分页列表
     *
     * @param riskEventRuleVO
     * @param pageable
     * @return
     */
    public PageRes<RiskRuleListVO> getRiskEventRulePager(RiskEventRuleQueryVO riskEventRuleVO, Pageable pageable);

    /**
     * 通过策略获取规则信息
     * @param id
     * @return
     */
    public List<RuleFilterVo> riskEventRuleFilterData(String id);

    /**
     * 通过规则ID，获取该规则涉及到的维表名称
     * @param filterCode
     * @return
     */
    public Set<String> getDimensionTableNames(String filterCode);

    List<ParamsContent> getParamsContents(String ruleId, String filterCode);

    /**
     * 新增告警规则
     *
     * @param riskRuleEditVO
     * @return
     */
    public Result<RiskRuleListVO> addRiskEventRule(RiskRuleEditVO riskRuleEditVO);

    /**
     * 编辑告警规则
     *
     * @param riskRuleEditVO
     * @return
     */
    public Result<RiskRuleListVO> editRiskEventRule(RiskRuleEditVO riskRuleEditVO);

    /**
     * 删除告警规则，支持批量删除
     *
     * @param ids
     * @return
     */
    public Result<Boolean> delRiskEventRules(List<String> ids);

    /**
     * 改变告警规则状态
     *
     * @param result
     * @return
     */
    public Result<RiskRuleListVO> changeRiskEventRule(RiskRuleIdVO riskRuleIdVO);


    /**
     * 启动流计算分析引擎
     *
     * @param main_class
     * @return
     */
    public Result<Boolean> startAlarmAnalysisEngine(RuleProcessVO ruleProcessVO);

    /**
     * 取消流计算分析引擎
     *
     * @param job_name
     * @return
     */
    public Result<Boolean> stopAlarmAnalysisEngine(String jobName);

    /**
     * 重启分析引擎
     *
     * @param ruleProcessVO
     * @return
     */
    public Result<Boolean> reStartAlarmAnalysisEngine(RuleProcessVO ruleProcessVO);


    /**
     * 判断ruleCode是否是唯一的
     *
     * @param ruleCode
     * @return
     */
    public Result<Boolean> judgeRuleCodeIsRepeat(String ruleCode, String guid);


    /**
     * 选择过滤日志表
     *
     * @param tagName
     * @return
     */
    public Result<List<EventLogTable>> getEventLogTable(String tagName);

    /**
     * 根据日志表获得对应的字段
     *
     * @param tableName
     * @param sourceType
     * @return
     */
    public Result<List<EventLogFieldVO>> getEventLogFieldVO(String sourceType, String tableName);


    /**
     * 获得动态sql脚本
     *
     * @param tableName
     * @param relateList
     * @return
     */
    public String getDynamicRelateSql(String tableName, List<RelateSqlVO> relateList);

    /**
     * 根据表名获得对应的原始日志路径
     *
     * @param tableName
     * @return
     */
    public String getOrginalLogPath(String tableName);

    /**
     * 添加到对应的map当中
     *
     * @param riskEventRule
     */
    public void addRuleInfoToAlarmHandler(RiskEventRule riskEventRule);

    public void streamCalcucateSyncHandler();

    public Result<String> exportRiskEventRule();

    public Result<Boolean> importRiskEventRuleInfo(MultipartFile file);

    /**
     * 复制规则
     */
    public Result<RiskRuleListVO> copyRiskEventRule(String guid);


    /**
     * 通过分析器id查询规则
     */
    public RiskEventRule getRiskEventRuleByAnalysisId(String code);


    /**
     * 自动启动资产风险分析引擎
     */
    public void autoStartThreatAnalysisEngine();

    public List<RunningTaskVO> getRunningTasks();

    public List<QueryCondition> getRiskEventQueryConditions(RiskEventRuleQueryVO riskEventRuleVO);


    public RiskRuleListVO createRuleInstance(Map<String, Object> map);

    public RiskRuleListVO editRuleInstance(Map<String, Object> map);


    Integer countStartRule();

    void changeRiskEventRuleOnlyStatus(String guid, String status);

    /**
     * 查询全部的告警规则
     *
     * @return
     */
    public List<RiskEventRule> getAllRiskEventRule();

    /**
     * 手动同步维表数据
     *
     * @param syncRequest
     */
    boolean syncDimensionData(SyncRequest syncRequest);

    /**
     * 保存同步维表信息
     *
     * @param syncRequest
     * @return
     */
    boolean saveSyncDimension(SyncRequest syncRequest);

    /**
     * 查询同步维表信息
     *
     * @param syncRequest
     * @return
     */
    List<SyncRes> querySyncDimension(SyncRequest syncRequest);
}
