package com.vrv.vap.alarmdeal.business.analysis.server;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.analysis.job.FlinkOfflineJob;
import com.vrv.vap.alarmdeal.business.analysis.vo.FilterOperatorGroupStartVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.CycleStrategyCondition;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.StartConfigVO;
import com.vrv.vap.jpa.quartz.QuartzFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 规则公共服务的类
 */
@Service
public class CommonFilterOperatorService {
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final String OFFLINE_RUN_TYPE_START = "start";
    public static final String OFFLINE_RUN_TYPE_STOP = "stop";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    //参数模板正则匹配
    public static final String PARAM_PATTERN = "\\$\\{\\w+\\}";
    private static Logger logger = LoggerFactory.getLogger(CommonFilterOperatorService.class);
    @Autowired
    private QuartzFactory quartzFactory;
    @Autowired
    private RiskEventRuleService riskEventRuleService;
    private Gson gson = new Gson();

    /**
     * 是否是离线flink任务，离线flink任务是不需要校验数据源，也不需要校验基线数据是否有数据。
     *
     * @param ruleId 策略id
     **/
    private boolean isOfflineFlinkJob(String ruleId) {
        String sql = "select count(1) from rule_filter rf inner join filter_operator fo on rf.filter_code=fo.code where rf.rule_id=? and fo.delete_flag=1 and fo.tag='offline'";
        Integer offlineFilterOperatorCount = jdbcTemplate.queryForObject(sql, new Object[]{ruleId}, Integer.class);
        return offlineFilterOperatorCount > 0;
    }

    /**
     * 参数模板启动校验方法
     * 获取规则动态参数状态，如果是规则加入了参数模板，但是没有实际参数替换模板参数时候，绑定该规则的策略是无法进行启动的
     * 返回null，则说明是符合启动条件的，返回有信息，则是不满足条件，返回的信息就是校验失败的具体原因。
     * 离线和实时任务的公共判断方法
     *
     * @param filterOpertorVO 策略id
     */
    public String getFilterOperatorParamValueStatus(FilterOperatorGroupStartVO filterOpertorVO) {
        String guids = filterOpertorVO.getGuids();
        String[] idArray = guids.split(",");
        String inSqlCondition = "('" + StringUtils.join(idArray, "','") + "')";
        String sql = "select fo.config from rule_filter rf inner join filter_operator fo on rf.filter_code=fo.code where rf.rule_id in " + inSqlCondition + "  and delete_flag=1";
        List<String> filterConfigList = jdbcTemplate.queryForList(sql, String.class);
        for (String filterConfig : filterConfigList) {
            if (StringUtils.isEmpty(filterConfig)) {
                continue;
            }
            //正则匹配参数模板
            Matcher matcher = Pattern.compile(PARAM_PATTERN).matcher(filterConfig);
            if (matcher.find()) {
                logger.error("策略ids={}所绑定的规则含有参数模板，且没有配置参数，请配置完参数后再尝试启动策略", guids);
                return "策略id=" + guids + "所绑定的规则含有参数模板，且没有配置参数，请配置完参数后再试！";
            }
        }
        return null;

    }

    /**
     * 根据策略id获取策略所绑定的规则code
     * todo 这里如果策略绑定了2个离线任务的话，会出现报错情况！
     *
     * @param ruleId
     */
    public FilterOperator getOfflineFilterOperatorByRuleId(String ruleId) {
        try {
            String sql = "select fo.* from rule_filter rf inner join filter_operator fo on rf.filter_code=fo.code where rf.rule_id=? and fo.delete_flag=1";
            return jdbcTemplate.queryForObject(sql, new Object[]{ruleId}, new RowMapper<FilterOperator>() {
                @Override
                public FilterOperator mapRow(ResultSet resultSet, int i) throws SQLException {
                    FilterOperator filterOperator = new FilterOperator();
                    filterOperator.setGuid(resultSet.getString("guid"));
                    filterOperator.setName(resultSet.getString("name"));
                    filterOperator.setFilterConfig(resultSet.getString("config"));
                    filterOperator.setSourceIds(resultSet.getString("source"));
                    filterOperator.setOutFieldInfos(resultSet.getString("output_fields"));
                    filterOperator.setVersion(resultSet.getInt("version"));
                    filterOperator.setDeleteFlag(resultSet.getBoolean("delete_flag"));
                    filterOperator.setDependencies(resultSet.getString("dependencies"));
                    filterOperator.setStatus(resultSet.getBoolean("status"));
                    filterOperator.setOutputs(resultSet.getString("outputs"));
                    filterOperator.setOperatorType(resultSet.getString("operator_type"));
                    filterOperator.setMultiVersion(resultSet.getString("multi_version"));
                    filterOperator.setCode(resultSet.getString("code"));
                    filterOperator.setLabel(resultSet.getString("label"));
                    filterOperator.setDesc(resultSet.getString("desc_"));
                    filterOperator.setCreateTime(resultSet.getDate("create_time"));
                    filterOperator.setUpdateTime(resultSet.getDate("update_time"));
                    filterOperator.setRoomType(resultSet.getString("room_type"));
                    filterOperator.setFilterConfigTemplate(resultSet.getString("config_template"));
                    filterOperator.setParamConfig(resultSet.getString("param_config"));
                    filterOperator.setParamValue(resultSet.getString("param_value"));
                    filterOperator.setTag(resultSet.getString("tag"));
                    filterOperator.setModelId(resultSet.getString("model_id"));
                    filterOperator.setRuleType(resultSet.getString("rule_type"));
                    filterOperator.setAllowStart(resultSet.getBoolean("allow_start"));
                    filterOperator.setNewlineFlag(resultSet.getString("newline_flag"));
                    filterOperator.setFilterType(resultSet.getString("filter_type"));
                    filterOperator.setRuleFilterType(resultSet.getString("rule_filter_type"));
                    filterOperator.setInitStatus(resultSet.getString("init_status"));
                    filterOperator.setAttackLine(resultSet.getString("attack_line"));
                    filterOperator.setThreatCredibility(resultSet.getString("threat_credibility"));
                    filterOperator.setDealAdvcie(resultSet.getString("deal_advcie"));
                    filterOperator.setHarm(resultSet.getString("harm"));
                    filterOperator.setPrinciple(resultSet.getString("principle"));
                    filterOperator.setViolationScenario(resultSet.getString("violation_scenario"));
                    filterOperator.setFilterDesc(resultSet.getString("filter_desc"));
                    filterOperator.setStartConfig(resultSet.getString("start_config"));
                    return filterOperator;
                }
            });
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.error("##################策略ruleId={}绑定了多个规则启动失败了，只能够绑定一个规则#################", ruleId);
            return null;
        }

    }


    /**
     * 过滤掉离线任务的策略id，界面启动flink任务，可能是批量的，批量当中含有离线和实时的，这里是过滤掉离线的，把离线单独抽取出来，
     * 后面只有实时的，实时的可以维持原来的不发生变化。
     *
     * @param filterOperatorGroupStartVO
     */
    public List<String> filterOfflineFlinkJob(FilterOperatorGroupStartVO filterOperatorGroupStartVO) {
        //离线的ruleId集合
        List<String> offlineRuleIdList = new ArrayList<>();
        //实时的ruleId集合
        List<String> realtimeRuleIdList = new ArrayList<>();
        String ruleIds = filterOperatorGroupStartVO.getGuids();
        String[] ruleIdArray = ruleIds.split(",");
        for (String ruleId : ruleIdArray) {
            if (isOfflineFlinkJob(ruleId)) {
                offlineRuleIdList.add(ruleId);
                continue;
            }
            realtimeRuleIdList.add(ruleId);
        }
        filterOperatorGroupStartVO.setGuids(StringUtils.join(realtimeRuleIdList, ","));
        return offlineRuleIdList;
    }

    /**
     * 通过ruleIdList添加离线flink任务
     *
     * @param ruleIdList 离线定时任务策略id集合
     */
    public void addOfflineFlinkJobByRuleIdList(List<String> ruleIdList) {
        try {
            for (String ruleId : ruleIdList) {
                addOfflineFlinkJob(ruleId, null);
            }
        } catch (Exception e) {
            logger.error("##########addOfflineFlinkJobByRuleIdList hanppen error={}", e);
        }
    }

    /***
     * 通过策略id来添加离线任务
     * @param ruleId 离线任务的策略id
     * @param riskEventRule 离线任务策略
     */
    private void addOfflineFlinkJob(String ruleId, RiskEventRule riskEventRule) {
        if (riskEventRule == null) {
            //如果不存在则从数据库中查询
            riskEventRule = riskEventRuleService.getOne(ruleId);
        } else {
            ruleId = riskEventRule.getId();
        }
        FilterOperator filterOperator = getOfflineFilterOperatorByRuleId(ruleId);
        if (filterOperator == null) {
            return;
        }
        //启动参数配置
        String startConfig = filterOperator.getStartConfig();
        if (StringUtils.isEmpty(startConfig)) {
            return;
        }
        StartConfigVO startConfigVO = gson.fromJson(startConfig, StartConfigVO.class);
        if (startConfigVO == null) {
            return;
        }
        CycleStrategyCondition cycleStrategyCondition = startConfigVO.getCycleStrategyCondition();
        //存入定时任务数据
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("riskEventRule", riskEventRule);
        jobData.put("filterOperator", filterOperator);
        //定时任务
        quartzFactory.addJob(ruleId, FlinkOfflineJob.class, cycleStrategyCondition.getCronExpression(), jobData);
    }

    /**
     * 通过策略集合添加离线flink任务
     *
     * @param riskEventRules 离线定时任务策略id集合
     */
    public void addOfflineFlinkJobByRiskEventRules(List<RiskEventRule> riskEventRules) {
        for (RiskEventRule riskEventRule : riskEventRules) {
            addOfflineFlinkJob(null, riskEventRule);
        }
    }

    /**
     * 通过策略id集合移除离线任务，离线任务停止，只需要将离线任务从调度周期中移除即可，不需要停止离线flink任务，哪怕flink任务正在运行中
     *
     * @param ruleIdList 离线策略id集合
     */
    public void removeOfflineFlinkJobByRuleIdList(List<String> ruleIdList) {
        for (String ruleId : ruleIdList) {
            quartzFactory.removeJob(ruleId, FlinkOfflineJob.class);
        }
    }

    /**
     * 通过策略集合来移除离线任务，离线任务停止，只需要将离线任务从调度周期中移除即可，不需要停止离线flink任务，哪怕flink任务正在运行中
     *
     * @param riskEventRuleList 策略规则集合
     */
    public void removeOfflineFlinkJobByRiskEventRules(List<RiskEventRule> riskEventRuleList) {
        for (RiskEventRule riskEventRule : riskEventRuleList) {
            quartzFactory.removeJob(riskEventRule.getId(), FlinkOfflineJob.class);
        }
    }

    /**
     * 查询得到绑定离线规则的策略名称列表
     * 离线任务的job名称是以策略名称来命名的。
     *
     * @param runType 运行类型 运行类型里面有2个，一个是stop（表示离线任务停止） 另外一个是start（离线任务启动）
     * @param
     */
    public List<RiskEventRule> getOfflineRiskEventList(String runType) {
        String sql = "select  name_ as name,id,rule_code  from risk_event_rule where id in (select rf.rule_id from rule_filter rf inner join filter_operator fo on rf.filter_code=fo.code where fo.tag='offline') and isStarted=? ";
        switch (runType) {
            case OFFLINE_RUN_TYPE_START:
                return jdbcTemplate.query(sql, new Object[]{1}, new BeanPropertyRowMapper<>(RiskEventRule.class));
            case OFFLINE_RUN_TYPE_STOP:
                return jdbcTemplate.query(sql, new Object[]{0}, new BeanPropertyRowMapper<>(RiskEventRule.class));
            default:
                return new ArrayList<>();
        }


    }

    /**
     * 查询找到在线策略id
     */
    public List<String> getOnlineRuleIdList() {
        String sql = "select rf.rule_id from rule_filter rf inner join filter_operator fo on rf.filter_code=fo.code where fo.tag!='offline'";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * 能否需要配置参数，也就是规则是否含有参数占位符
     *
     * @param filterOperator
     */
    public boolean isConfigParam(FilterOperator filterOperator) {
        String filterConfigTemplate = filterOperator.getFilterConfigTemplate();
        if (StringUtils.isEmpty(filterConfigTemplate)) {
            return false;
        }
        //正则匹配参数模板
        Matcher matcher = Pattern.compile(PARAM_PATTERN).matcher(filterConfigTemplate);
        if (matcher.find()) {
            return true;
        }
        return false;
    }


}
