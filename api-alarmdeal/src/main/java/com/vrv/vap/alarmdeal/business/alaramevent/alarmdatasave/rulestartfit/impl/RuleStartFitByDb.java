package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.RuleTypeConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.RuleStartFit;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.FilterSourceStatusService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 梁国露
 * @Date: 2022年08月10日 14:03:28
 * @Description: 规则启动（数据源方式）
 */
@Service("ruleStartFitByDb")
public class RuleStartFitByDb implements RuleStartFit {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(RuleStartFitByDb.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private FilterSourceStatusService filterSourceStatusService;

    /**
     * 初始启动
     *
     * @param jobList 任务列表，并不是jobId
     */
    @Override
    public void startRule(List<String> jobList) {
        // 查询数据源
        List<String> sources = getSources();
        for (String sourceId : sources) {
            boolean status = filterSourceStatusService.getFilterSourceStatusByRedis(sourceId);
            if (!status) {
                continue;
            }
            // 通过数据源获取任务名称
            String title = getJobName(sourceId);
            if (StringUtils.isBlank(title)) {
                logger.error("任务ID对应名称为空！");
                continue;
            }
            List<String> jobIdList = SocUtil.getJobIdList(title, jobList);
            // 通过数据源查找任务
            List<String> riskEventRules = filterOperatorService.getStartFlinkCodesBySourceId(sourceId);
            riskEventRules = riskEventRules.stream().distinct().collect(Collectors.toList());
            // 取交集
            if (CollectionUtils.isEmpty(jobIdList) && CollectionUtils.isNotEmpty(riskEventRules)) {
                //缓解压力，休眠10s
                try {
                    filterOperatorService.startJob(sourceId, riskEventRules, RuleTypeConstant.DATASOURCE);
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //事件下启动规则事数为0，需停掉flink任务
            if (jobIdList.size() > 0 && riskEventRules.size() > 0) {
                for (String jobId : jobIdList) {
                    for (String job : jobList) {
                        if (job.contains(jobId)) {
                            jobList.remove(job);
                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * 启动任务
     *
     * @param guidList
     * @param riskEventId
     * @param startType
     * @param map
     */
    @Override
    public void startTask(List<String> guidList, String riskEventId, String startType, Map<String, List<String>> map) {
        if (map.isEmpty()) {
            flinkTaskMapIsEmptyByStart(guidList, riskEventId, startType);
        } else {
            flinkTaskMapIsNotEmptyByStart(guidList, riskEventId, startType, map);
        }
    }

    /**
     * 停止任务
     *
     * @param guidList
     * @param riskEventId
     * @param startType
     * @param map
     */
    @Override
    public void stopTask(List<String> guidList, String riskEventId, String startType, Map<String, List<String>> map) {
        if (map.isEmpty()) {
            flinkTaskMapIsEmptyByStop(guidList, riskEventId, startType);
        } else {
            flinkTaskMapIsNotEmptyByStop(guidList, riskEventId, startType, map);
        }
    }

    /**
     * map为空时开启任务
     *
     * @param guidList
     * @param riskEventId
     * @param startType
     */
    private void flinkTaskMapIsEmptyByStart(List<String> guidList, String riskEventId, String startType) {
        // 数据源
        String sql = "select t3.id as id,t1.source as sourceIds from filter_operator t1,rule_filter t2,risk_event_rule t3 where t1.`code` = t2.filter_code and t2.rule_id = t3.id and t1.delete_flag =1 {0} and t3.delete_flag = 1 ";
        String paramStr = "";
        if (CollectionUtils.isNotEmpty(guidList)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < guidList.size(); i++) {
                if (i == 0) {
                    sb.append("'").append(guidList.get(i)).append("'");
                } else {
                    sb.append(",").append("'").append(guidList.get(i)).append("'");
                }
            }
            paramStr = " and t3.id in (" + sb.toString() + ") ";
        }
        sql = sql.replace("{0}", paramStr);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        // 用分类ID进行分组
        Map<String, List<String>> groupByEmpId = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("sourceIds")), Collectors.mapping(map -> String.valueOf(map.get("id")), Collectors.toList())));
        Map<String, List<String>> jobListMap = new HashMap<>();
        groupByEmpId.forEach((key, value) -> {
            List<String> sourceIds = JSONArray.parseArray(key, String.class);
            for (String sourceId : sourceIds) {
                if (jobListMap.containsKey(sourceId)) {
                    List<String> ruleIds = jobListMap.get(sourceId);
                    ruleIds.addAll(value);
                    jobListMap.put(sourceId, value);
                } else {
                    jobListMap.put(sourceId, value);
                }
            }
        });

        // 删除任务
        List<String> guids = jobListMap.get(riskEventId);
        if (CollectionUtils.isNotEmpty(guids)) {
            guids.addAll(guidList);
            guids = guids.stream().distinct().collect(Collectors.toList());
            jobListMap.put(riskEventId, guids);
        } else {
            jobListMap.put(riskEventId, guidList);
        }


        // 批量启动
        jobListMap.forEach((key, value) -> {
            filterOperatorService.startJob(key, value, startType);
        });
    }

    /**
     * map不为空时开启任务
     *
     * @param guidList
     * @param riskEventId
     * @param startType
     * @param flinkTaskMap
     */
    private void flinkTaskMapIsNotEmptyByStart(List<String> guidList, String riskEventId, String startType, Map<String, List<String>> flinkTaskMap) {
        int num = ruleFlinkTypeService.getRuleFlinkStartNum();
        boolean flag = false;
        List<String> ids = new ArrayList<>();
        List<String> updateList = new ArrayList<>();
        String[] riskEventIdArr = riskEventId.split(",");
        List<Map.Entry<String, List<String>>> findFlinkEntryMapList = new ArrayList<>();
        //找到进行排序，根据分组数量大小从小到大排列riskEventIdArr肯定只有1个
        for (String riskEventIdArrStr : riskEventIdArr) {
            for (Map.Entry<String, List<String>> flinkEntry : flinkTaskMap.entrySet()) {
                if (flinkEntry.getKey().split("_")[0].equals(riskEventIdArrStr)) {
                    findFlinkEntryMapList.add(flinkEntry);
                }
            }
        }
        //根据分组大小增序，分组中集合小的靠前,方便后续新加进组的策略，进行优先选择加入少的分组。
        sortFlinkEntryMapListAsc(findFlinkEntryMapList);
        //最大后缀值
        int maxTaskSuffixValue = getMaxTaskSuffixValue(findFlinkEntryMapList);
        for (Map.Entry<String, List<String>> flinkEntry : findFlinkEntryMapList) {
            boolean isFind = false;
            if (flinkEntry.getValue().size() < num) {
                flag = true;
                isFind = true;
                // 在已运行任务中
                filterOperatorService.stopOperatorByRiskEventId(flinkEntry.getKey(), flinkEntry.getValue(), startType);
                ids.addAll(flinkEntry.getValue());
                //这个累加可能导致size>num,原本肯定是不会大于的，但是多加个可能出现一下子由小于变为了大于。
                ids.addAll(guidList);
                ids = ids.stream().distinct().collect(Collectors.toList());
                String sourceKey = flinkEntry.getKey();
                //如果加起来大于的话，则证明是需要进行分页的！
                if (ids.size() > num) {
                    int totalNum = ids.size();
                    int pageNum = totalNum % num == 0 ? totalNum / num : totalNum / num + 1;
                    int fromIndex, toIndex;
                    for (int i = 0; i < pageNum; i++) {
                        fromIndex = i * num;
                        toIndex = Math.min(totalNum, fromIndex + num);
                        List<String> subIds = ids.subList(fromIndex, toIndex);
                        if (i == 0) {
                            //第一页还是用原来的
                            flinkTaskMap.put(sourceKey, subIds);
                            filterOperatorService.putFlinkTaskToMap(sourceKey, subIds);
                            updateList.add(sourceKey);
                        } else {
                            //第二页用新的分组
                            String newKey = sourceKey + "_" + (maxTaskSuffixValue+1);
                            flinkTaskMap.put(newKey, subIds);
                            filterOperatorService.putFlinkTaskToMap(newKey, subIds);
                            updateList.add(newKey);
                        }

                    }
                } else {
                    //就直接在原来组里面启动，合并在一起也没有达到分组的数量
                    flinkTaskMap.put(sourceKey, ids);
                    filterOperatorService.putFlinkTaskToMap(sourceKey, ids);
                    updateList.add(sourceKey);
                }
            } else if (flinkEntry.getValue().size() >= num) {
                String[] keyArr = flinkEntry.getKey().split("_");
                flag = true;
                isFind = true;
                ids.addAll(guidList);
                //保证命名不一样就行了
                String newKey = keyArr[0] + "_" +(maxTaskSuffixValue+1);
                flinkTaskMap.put(newKey, ids);
                filterOperatorService.putFlinkTaskToMap(newKey, ids);
                updateList.add(newKey);
            }
            if (isFind) {
                break;

            }
        }
        if (flag) {
            updateList = updateList.stream().distinct().collect(Collectors.toList());
            for (String taskId : updateList) {
                List<String> ruleIds = flinkTaskMap.get(taskId);
                Map<String, List<String>> map = new HashMap<>();
                for (String ruleId : ruleIds) {
                    List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId, true);
                    if (CollectionUtils.isNotEmpty(filterCodes)) {
                        map.put(ruleId, filterCodes);
                    }
                }
                if (!map.isEmpty()) {
                    filterOperatorService.startOperatorJobGroup(taskId, map, startType);
                }
            }
        } else {
            // 不在，则直接启动
            Map<String, List<String>> map = new HashMap<>();
            for (String ruleId : guidList) {
                List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId, true);
                if (CollectionUtils.isNotEmpty(filterCodes)) {
                    map.put(ruleId, filterCodes);
                }
            }
            if (!map.isEmpty()) {
                filterOperatorService.startOperatorJobGroup(riskEventId, map, startType);
                filterOperatorService.putFlinkTaskToMap(riskEventId, guidList);
            }
        }
    }

    /**
     * 对找到的分组进行排序，主要是根据value size大小从低到高，优先去填补少的
     *
     * @param findFlinkEntryMapList
     */
    private static void sortFlinkEntryMapListAsc(List<Map.Entry<String, List<String>>> findFlinkEntryMapList) {
        findFlinkEntryMapList.sort(new Comparator<Map.Entry<String, List<String>>>() {
            @Override
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                return o1.getValue().size() - o2.getValue().size();
            }
        });

    }

    /**
     * 获取任务后最大值，即使后面产生了分页，往最大值后面累加1肯定不会出现重名的情况
     * 比如task_1,task_2，我启动时候，加入task_1分组，如果超过了分组数时候，直接在后面累加1的话，就与之前的task_2重名了，应该是task_3才对，需要有一个后缀的集合，
     * 这样保证不会出现重复情况
     */
    private static int getMaxTaskSuffixValue(List<Map.Entry<String, List<String>>> findFlinkEntryMapList) {
        int maxValue = 0;
        for (Map.Entry<String, List<String>> entry : findFlinkEntryMapList) {
            String key = entry.getKey();
            String[] keyArr = key.split("_");
            if (keyArr.length > 1) {
                String suffix = keyArr[1];
                int suffixValue = Integer.parseInt(suffix);
                if (suffixValue > maxValue) {
                    maxValue = suffixValue;
                }
            }
        }
        return maxValue;
    }


    /**
     * map不为空时停用规则
     *
     * @param guidList
     * @param riskEventId
     * @param startType
     * @param flinkTaskMap
     */
    public void flinkTaskMapIsNotEmptyByStop(List<String> guidList, String riskEventId, String startType, Map<String, List<String>> flinkTaskMap) {
        boolean flag = false;
        List<String> updateTask = new ArrayList<>();
        for (String guid : guidList) {
            for (Map.Entry<String, List<String>> map : flinkTaskMap.entrySet()) {
                String[] riskEventIdArr = riskEventId.split(",");
                for (String riskEventIdArrStr : riskEventIdArr) {
                    if ((map.getKey().split("_")[0].equals(riskEventIdArrStr)) && map.getValue().contains(guid)) {
                        filterOperatorService.stopOperatorByRiskEventId(map.getKey(), map.getValue(), startType);
                        filterOperatorService.removeFlinkTaskForMap(map.getKey(), guid);
                        updateTask.add(map.getKey());
                        flag = true;
                    }
                }
            }
        }
        if (flag) {
            //停止规则
            Map<String, List<String>> flinkTaskMap_ = filterOperatorService.getFlinkTaskMap();
            updateTask = updateTask.stream().distinct().collect(Collectors.toList());
            for (String taskId : updateTask) {
                // 重启启动
                List<String> ruleIds = flinkTaskMap_.get(taskId);
                Map<String, List<String>> map = new HashMap<>();
                for (String ruleId : ruleIds) {
                    List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId, true);
                    if (CollectionUtils.isNotEmpty(filterCodes)) {
                        map.put(ruleId, filterCodes);
                    }
                }
                if (!map.isEmpty()) {
                    filterOperatorService.startOperatorJobGroup(taskId, map, startType);
                }
            }
        } else {
            //停止规则
            filterOperatorService.stopOperatorByRiskEventId(riskEventId, guidList, startType);
        }
    }

    /**
     * map为空时，停用规则
     *
     * @param guidList
     * @param riskEventId
     * @param startType
     */
    public void flinkTaskMapIsEmptyByStop(List<String> guidList, String riskEventId, String startType) {
        // 数据源
        String sql = "select t3.id as id,t1.source as sourceIds from filter_operator t1,rule_filter t2,risk_event_rule t3 where t1.`code` = t2.filter_code and t2.rule_id = t3.id and t1.delete_flag =1 and t3.isStarted = '1' and t3.delete_flag = 1 ";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        // 用分类ID进行分组
        Map<String, List<String>> groupByEmpId = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("sourceIds")), Collectors.mapping(map -> String.valueOf(map.get("id")), Collectors.toList())));
        Map<String, List<String>> jobListMap = new HashMap<>();
        groupByEmpId.forEach((key, value) -> {
            List<String> sourceIds = JSONArray.parseArray(key, String.class);
            for (String sourceId : sourceIds) {
                if (jobListMap.containsKey(sourceId)) {
                    List<String> ruleIds = jobListMap.get(sourceId);
                    ruleIds.addAll(value);
                    jobListMap.put(sourceId, value);
                } else {
                    jobListMap.put(sourceId, value);
                }
            }
        });

        // 删除任务
        List<String> guids = jobListMap.get(riskEventId);
        if (CollectionUtils.isNotEmpty(guids)) {
            filterOperatorService.stopOperatorByRiskEventId(riskEventId, guids, startType);
            guids.removeAll(guidList);
            jobListMap.put(riskEventId, guids);
        } else {
            filterOperatorService.stopOperatorByRiskEventId(riskEventId, guidList, startType);
        }
        // 批量启动
        jobListMap.forEach((key, value) -> {
            filterOperatorService.startJob(key, value, startType);
        });
    }

    /**
     * 获取数据源
     *
     * @return
     */
    private List<String> getSources() {
        List<QueryCondition> param = new ArrayList<>();
        param.add(QueryCondition.eq("deleteFlag", true));
        List<FilterOperator> filterOperators = filterOperatorService.findAll(param);
        List<String> sourceIds = new ArrayList<>();
        filterOperators.stream().forEach(item -> {
            List<String> sourceList = JSONArray.parseArray(item.getSourceIds(), String.class);
            if (sourceList != null) {
                sourceIds.addAll(sourceList);
            }
        });
        // 去重
        List<String> sources = sourceIds.stream().distinct().collect(Collectors.toList());
        return sources;
    }


    /**
     * 通过数据源获取需要启动的任务名称
     *
     * @param sourceId
     * @return java.lang.String
     */
    private String getJobName(String sourceId) {
        EventTable eventTable = eventTabelService.getOne(sourceId);
        String title = "";
        if (eventTable != null) {
            title = eventTable.getLabel();
        }
        return title;
    }
}
