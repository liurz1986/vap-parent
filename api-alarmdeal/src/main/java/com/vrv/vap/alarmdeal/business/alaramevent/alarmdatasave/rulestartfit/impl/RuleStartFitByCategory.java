package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.RuleTypeConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.RuleStartFit;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.dao.EventCategoryDao;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 梁国露
 * @Date: 2022年08月10日 14:03:58
 * @Description: 规则启动（分类方式）
 */
@Service("ruleStartFitByCategory")
public class RuleStartFitByCategory implements RuleStartFit {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    @Autowired
    private EventCategoryDao eventCategoryDao;

    /**
     * 初始启动
     *
     * @param jobList
     */
    @Override
    public void startRule(List<String> jobList) {
        //查询所有的二级事件分类
        List<Map<String, Object>> list = eventCategoryDao.getGetSecondLevelEvent();
        //重启flink挂掉的规则
        for (Map<String, Object> map : list) {
            String riskEventName = map.get("title").toString();
            List<String> jobIdList = SocUtil.getJobIdList(riskEventName, jobList);
            String riskEventId = map.get("id").toString();
            List<String> guidList = filterOperatorService.getStartFlinkCodes(riskEventId);
            if (CollectionUtils.isEmpty(jobIdList)) {
                if (guidList.size() > 0) {
                    filterOperatorService.startJob(riskEventId, guidList, RuleTypeConstant.CATEGORY);
                }
                //缓解压力，休眠10s
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //事件下启动规则事数为0，需停掉flink任务
                if (jobIdList.size() > 0 && guidList.size() > 0) {
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
        // 分类
        // 查询分类ID与策略ID
        String sql = "select t1.id as id,t2.parent_id as parentId from risk_event_rule t1,event_category t2 where t1.riskEventId = t2.id and t1.isStarted in ('1','2') and delete_flag = 1";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        // 用分类ID进行分组
        Map<String, List<String>> groupByEmpId = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("parentId")), Collectors.mapping(map -> String.valueOf(map.get("id")), Collectors.toList())));
        // 停止相关策略
        List<String> guids = groupByEmpId.get(riskEventId);
        guids.addAll(guidList);
        groupByEmpId.put(riskEventId, guids);
        // 批量启动
        groupByEmpId.forEach((key, value) -> {
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
        for (Map.Entry<String, List<String>> flinkEntry : flinkTaskMap.entrySet()) {
            String[] riskEventIdArr = riskEventId.split(",");
            for (String riskEventIdArrStr : riskEventIdArr) {
                if (flinkEntry.getKey().split("_")[0].equals(riskEventIdArrStr)) {
                    if (flinkEntry.getValue().size() < num) {
                        flag = true;
                        // 在已运行任务中
                        filterOperatorService.stopOperatorByRiskEventId(flinkEntry.getKey(), flinkEntry.getValue(), startType);
                        ids.addAll(flinkTaskMap.get(flinkEntry.getKey()));
                        ids.addAll(guidList);
                        flinkTaskMap.put(flinkEntry.getKey(), ids);
                        updateList.add(flinkEntry.getKey());
                    } else if (flinkEntry.getValue().size() == num) {
                        String[] keyArr = flinkEntry.getKey().split("_");
                        flag = true;
                        ids.addAll(guidList);
                        String newKey = "";
                        if (keyArr.length == 1) {
                            newKey = keyArr[0] + "_" + 1;
                        } else {
                            newKey = keyArr[0] + "_" + keyArr[1] + 1;
                        }
                        flinkTaskMap.put(newKey, ids);
                        updateList.add(newKey);
                    }
                }
            }
        }
        if (flag) {
            updateList = updateList.stream().distinct().collect(Collectors.toList());
            for (String taskId : updateList) {
                List<String> ruleIds = flinkTaskMap.get(taskId);
                ruleIds=ruleIds.stream().distinct().collect(Collectors.toList());
                Map<String, List<String>> map = new HashMap<>();
                for (String ruleId : ruleIds) {
                    List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId, false);
                    filterCodes=filterCodes.stream().collect(Collectors.toList());
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
            guidList=guidList.stream().collect(Collectors.toList());
            for (String ruleId : guidList) {
                List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId, false);
                filterCodes=filterCodes.stream().collect(Collectors.toList());
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
     * map不为空时停止任务
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
                        //只需要移除停止的
                        updateTask.add(map.getKey());
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            updateTask = updateTask.stream().distinct().collect(Collectors.toList());
            Map<String, List<String>> flinkTaskMap_ = filterOperatorService.getFlinkTaskMap();
            //停止规则
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

    public void flinkTaskMapIsEmptyByStop(List<String> guidList, String riskEventId, String startType) {
        // 分类
        // 查询分类ID与策略ID
        String sql = "select t1.id as id,t2.parent_id as parentId from risk_event_rule t1,event_category t2 where t1.riskEventId = t2.id and t1.isStarted = '1' and delete_flag = 1";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        // 用分类ID进行分组
        Map<String, List<String>> groupByEmpId = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("parentId")), Collectors.mapping(map -> String.valueOf(map.get("id")), Collectors.toList())));
        // 停止相关策略
        List<String> guids = groupByEmpId.get(riskEventId);
        if (CollectionUtils.isNotEmpty(guids)) {
            filterOperatorService.stopOperatorByRiskEventId(riskEventId, guids, startType);
            guids.removeAll(guidList);
            groupByEmpId.put(riskEventId, guids);
        } else {
            filterOperatorService.stopOperatorByRiskEventId(riskEventId, guidList, startType);
        }

        // 批量启动
        groupByEmpId.forEach((key, value) -> {
            filterOperatorService.startJob(key, value, startType);
        });
    }
}
