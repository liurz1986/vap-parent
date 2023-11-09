package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.RuleTypeConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.impl.RuleStartAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.dao.EventCategoryDao;
import com.vrv.vap.alarmdeal.business.analysis.server.CommonFilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.asset.service.SystemConfigService;
import com.vrv.vap.alarmdeal.frameworks.config.FlinkConfiguration;
import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: 梁国露
 * @Date: 2022年08月02日 13:48:57
 * @Description:
 */
@Service
public class RuleFlinkTypeServiceImpl implements RuleFlinkTypeService {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(RuleFlinkTypeService.class);

    @Autowired
    private EventCategoryDao eventCategoryDao;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private FlinkConfiguration flinkConfiguration;

    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CommonFilterOperatorService commonFilterOperatorService;


    /**
     * 数据源启动方式
     */
    private static final String DATASOURCE_TYPE = "datasource";

    // 全局变量 保存现有启动类型
    public String type;

    // 全局变量 保存规则启动 X个规则一组
    private int num = 3;

    /**
     * 同步规则启动类型
     *
     * @param type
     */
    @Override
    public void putRuleFlinkStart(String type) {
        this.type = type;
    }

    /**
     * 判断现有规则启动类型是否与设定一致
     * true  保持一致（未改变）
     * false 不一致（已改变）
     *
     * @return
     */
    @Override
    public boolean checkNowRuleStartType(List<String> jobNames) {
        if (CollectionUtils.isEmpty(jobNames)) {
//            return false;
            return true;
        }
        // 1.判断任务名称 属于的类型
        List<Map<String, Object>> list = eventCategoryDao.getGetSecondLevelEvent();
        List<String> eventTitles = list.stream().map(item -> String.valueOf(item.get("title"))).collect(Collectors.toList());
        String jobType = "datasource";
        List<String> jobNameList = new ArrayList<>();
        for (String jobName : jobNames) {
            String[] jobNameArr = jobName.split("_");
            jobNameList.add(jobNameArr[0]);
        }
        jobNameList = jobNameList.stream().distinct().collect(Collectors.toList());

        for (String eventTitle : eventTitles) {
            for (String jobName : jobNames) {
                if (jobName.contains(eventTitle)) {
                    // 现有任务为分类启动
                    jobType = "category";
                    break;
                }
            }
        }

        logger.info("checkNowRuleStartType jobNameList={},jobType={}", jobNameList, jobType);
        // 2.判断类型与设定类型是否一致
        if (jobType.equals(type)) {
            return true;
        }
        return false;
    }

    @Override
    public String getRuleFlinkStart() {
        return type;
    }

    @Override
    public void putRuleFlinkStartNum(int num) {
        this.num = num;
    }

    @Override
    public int getRuleFlinkStartNum() {
        return num;
    }

    public void getRuleType() {
        String startType = systemConfigService.getSysConfigById("rule_filter_start");
        if (StringUtils.isNotBlank(startType)) {
            ruleFlinkTypeService.putRuleFlinkStart(startType);
        } else {
            ruleFlinkTypeService.putRuleFlinkStart("category");
        }

        String startRuleNum = systemConfigService.getSysConfigById("rule_filter_start_num");
        if (StringUtils.isNotBlank(startRuleNum)) {
            ruleFlinkTypeService.putRuleFlinkStartNum(Integer.valueOf(startRuleNum));
        } else {
            ruleFlinkTypeService.putRuleFlinkStartNum(6);
        }
        logger.info("getRuleType startType={},startRuleNum={}", startType, startRuleNum);
    }

    /**
     * 初始化任务
     */
    @Override
    public void initRiskEventRule() {
        //1 检查离线任务
        List<RiskEventRule> offlineRiskEventRuleList = checkOfflineJob();
        getRuleType();
        // 2、获取flink task map
        Map<String, List<String>> flinkTaskMap = filterOperatorService.getFlinkTaskMap();
        // 3、获取在线任务列表,
        List<String> jobList = getOnlineJobList(offlineRiskEventRuleList);
        logger.info("###在线任务jobList= {}", JSON.toJSONString(jobList));
        // 4、判断启动方式是否改变
        boolean isCheck = ruleFlinkTypeService.checkNowRuleStartType(jobList);
        String startType = ruleFlinkTypeService.getRuleFlinkStart();
        logger.info("isCheck={},startType={}", isCheck, startType);
        if (!isCheck) {
            // 5、初始化flinkTaskMap
            flinkTaskMap = new HashMap<>();
            filterOperatorService.initFlinkTaskMap();
            // 6、关掉全部任务
            for (String jobName : jobList) {
                filterOperatorService.stopJobByJobName(jobName);
            }
        }

        // 5、启动任务(在线任务，已经剔除了离线的，里面的查询逻辑中已经剔除了)
        startRuleTask(flinkTaskMap, jobList, startType);

    }

    /**
     * 离线任务校验，不需要关注flink任务是否启动了，停止flink任务也仅仅是将离线任务从周期中移除
     * 这里一致性校验，保证数据库离线任务状态和实际周期调度状态保持一致即可。
     * <p>
     * 如果数据库中离线任务是开启的，但是没有周期调度，需要添加周期定时任务
     * 如果数据库中离线任务是关闭的，但是含有周期调度，需要移除定时任务
     * 这样保证数据库状态和周期调度状态的一致性
     */
    private List<RiskEventRule> checkOfflineJob() {
        //数据库中需要启动的离线任务
        List<RiskEventRule> riskEventStartRules = commonFilterOperatorService.getOfflineRiskEventList(CommonFilterOperatorService.OFFLINE_RUN_TYPE_START);
        commonFilterOperatorService.addOfflineFlinkJobByRiskEventRules(riskEventStartRules);
        //数据库中需要停止的离线任务
        List<RiskEventRule> riskEventStopRules = commonFilterOperatorService.getOfflineRiskEventList(CommonFilterOperatorService.OFFLINE_RUN_TYPE_STOP);
        commonFilterOperatorService.removeOfflineFlinkJobByRiskEventRules(riskEventStopRules);
        //所有的离线任务  启动+停止的。返回的用于后面过滤离线jobName，避免重复查询
        riskEventStartRules.addAll(riskEventStopRules);
        return riskEventStartRules;
    }

    /**
     * 启动任务
     *
     * @param flinkTaskMap
     * @param jobList
     * @param startType
     */
    private void startRuleTask(Map<String, List<String>> flinkTaskMap, List<String> jobList, String startType) {

        if (CollectionUtils.isEmpty(jobList) && !flinkTaskMap.isEmpty()) {
            logger.info(String.valueOf(flinkTaskMap));
            // flink任务列表为空，这里等于服务启动以后，通过界面启动flink任务，但是不满足启动条件后暂时放到了flinkTaskMap里面，启动以后不需要从数据库里面进行查询，只需要检查flinkTaskMap现在有没有满足启动条件，满足就可以启动。
            for (Map.Entry<String, List<String>> entry : flinkTaskMap.entrySet()) {
                filterOperatorService.startJob(entry.getKey(), entry.getValue(), startType);
            }
        } else {
            //实时任务已经分离了   服务初次启动或者界面启动没有启动失败放入flinkTaskMap中的
            if (DATASOURCE_TYPE.equals(startType)) {
                // 数据源方式启动
                StartRuleForDb(jobList);
            } else if ("category".equals(startType)) {
                // 分类方式启动
                StartRuleForCategory(jobList);
            }
            //清除flink在数据库不在的任务 
            if (jobList.size() > 0) {
                filterOperatorService.stopByJobList(jobList);
            }
        }
    }



    /**
     * 通过数据源启动任务
     *
     * @param jobList
     */
    private void StartRuleForDb(List<String> jobList) {
        RuleStartAdapter ruleStartAdapter = new RuleStartAdapter(RuleTypeConstant.DATASOURCE);
        ruleStartAdapter.startRule(jobList);
    }

    /**
     * 根据 分类启动
     *
     * @param jobList
     */
    private void StartRuleForCategory(List<String> jobList) {
        RuleStartAdapter ruleStartAdapter = new RuleStartAdapter(RuleTypeConstant.CATEGORY);
        ruleStartAdapter.startRule(jobList);
    }

    /**
     * 获取flink任务列表
     * 20230922：将离线的flink任务剔除掉，只需要实时的flink任务。离线的定时任务运行中，即使停止也只需要将离线任务从周期调度中剔除即可。
     * 剔除离线的任务的依据是flink任务的名称是根据策略名称来的。如果是获取的flink任务列表中含有
     *
     * @param riskEventRules 离线任务策略
     * @return
     */
    private List<String> getOnlineJobList(List<RiskEventRule> riskEventRules) {
        String flink_home_path = flinkConfiguration.getFlink_home_path();
//        File file = new File(flink_home_path);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }
        String cmd_path = flink_home_path + "/bin/flink" + " " + "list";
        boolean remote_flag = flinkConfiguration.isRemote_flag();
        if (remote_flag) {
            cmd_path = "ssh " + flinkConfiguration.getRemote_user() + "@" + flinkConfiguration.getRemote_ip() + " " + cmd_path;
        }
        String exe_shell = cmd_path;
        List<String> list = ShellExecuteScript.queryExecuteCmd(exe_shell);
        for (String jobName : list) {
            if(jobName.contains("序列实时得分任务")){
                list.remove(jobName);
                break;
            }
        }
        //移除离线任务
        removeOfflineFlinkJob(list, riskEventRules);
        return list;
    }

    /**
     * 移除离线任务，运行中得离线任务不需要停止，需要将混合中把离线的任务移除出去。
     *
     * @param jobNameList    flink list查询的所有的flink job集合
     * @param riskEventRules 离线策略集合
     */
    private void removeOfflineFlinkJob(List<String> jobNameList, List<RiskEventRule> riskEventRules) {
        for (String jobName : jobNameList) {
            for (RiskEventRule riskEventRule : riskEventRules) {
                if (jobName.contains(riskEventRule.getName())) {
                    jobNameList.remove(jobName);
                    break;
                }
            }
        }
    }
}
