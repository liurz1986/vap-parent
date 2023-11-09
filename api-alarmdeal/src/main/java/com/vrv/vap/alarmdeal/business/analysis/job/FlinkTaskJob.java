package com.vrv.vap.alarmdeal.business.analysis.job;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.impl.RuleStartAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.RuleFlinkTypeService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author lps 2021/10/14
 */

@Service("flinkTaskJob")
public class FlinkTaskJob{

    private Logger logger= LoggerFactory.getLogger(FlinkTaskJob.class);

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RuleFlinkTypeService ruleFlinkTypeService;

    /**
     * 处理启动/停止任务
     */
    public  void  dealFlinkTask(Map<String, List<String>> guidGroup){
        try {
            if (guidGroup != null) {
                // 获取启动方式
                String startType = ruleFlinkTypeService.getRuleFlinkStart();

                // 方式改变时预处理
                handleTypeChange();

                // 处理任务
                handleTask(guidGroup, startType);
            }
        } catch (Exception e) {
            logger.error("flink任务启动队列消费异常，{}",e);
        }
    }

    /**
     * 处理任务
     * @param guidGroup
     * @param startType
     */
    private void handleTask(Map<String, List<String>> guidGroup, String startType) {
        for(Map.Entry<String, List<String>> entry : guidGroup.entrySet()){
            String riskEventId=entry.getKey();
            int index=riskEventId.lastIndexOf("_");
            String lastStr=riskEventId.substring(index);
            riskEventId=riskEventId.substring(0,index);
            if(lastStr.equals("_start")){
                startTask(entry, riskEventId, startType);
            }else if(lastStr.equals("_stop")){
                stopTask(entry, riskEventId, startType);
            }
        }
    }

    /**
     * 处理方式改变的操作
     */
    private void handleTypeChange() {
        // 查看任务列表
        List<String> jobNames = filterOperatorService.getJobList();

        // 判断方式是否改变
        boolean isTypeChange = ruleFlinkTypeService.checkNowRuleStartType(jobNames);
        if(!isTypeChange){
            // 改变了，初始化FlinkTaskMap
            filterOperatorService.initFlinkTaskMap();
            for(String jobName : jobNames){
                // 关闭任务
                filterOperatorService.stopJobByJobName(jobName);
            }
        }
    }

    /**
     * 获取启动方式
     * @return
     */
    public String getRuleStartType(){
        String startType = ruleFlinkTypeService.getRuleFlinkStart();
        return startType;
    }

    /**
     * 关闭规则任务
     * @param entry
     * @param riskEventId
     * @param startType
     */
    private void stopTask(Map.Entry<String, List<String>> entry, String riskEventId,String startType) {
        // 获取 flink 任务map
        Map<String,List<String>> flinkTaskMap = filterOperatorService.getFlinkTaskMap();
        RuleStartAdapter ruleStartAdapter = new RuleStartAdapter(startType);
        ruleStartAdapter.stopTask(entry.getValue(),riskEventId,startType,flinkTaskMap);
    }

    /**
     * 启动任务
     * @param entry
     * @param riskEventId
     * @param startType
     */
    private void startTask(Map.Entry<String, List<String>> entry, String riskEventId,String startType) {
        // 获取 flink 任务map
        Map<String,List<String>> flinkTaskMap = filterOperatorService.getFlinkTaskMap();
        RuleStartAdapter ruleStartAdapter = new RuleStartAdapter(startType);
        ruleStartAdapter.startTask(entry.getValue(),riskEventId,startType,flinkTaskMap);
    }

}
