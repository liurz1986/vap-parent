package com.vrv.vap.alarmdeal.business.analysis.job;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.analysis.model.FlinkOfflineLog;
import com.vrv.vap.alarmdeal.business.analysis.repository.FlinkOfflineLogRepository;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.vo.FlinkStartVO;
import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.quartz.QuartzFactory;
import com.vrv.vap.jpa.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * flink离线定时任务job
 */
public class FlinkOfflineJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(FlinkOfflineJob.class);
    private FilterOperatorService filterOperatorService = SpringUtil.getBean(FilterOperatorService.class);
    private FlinkOfflineLogRepository flinkOfflineLogRepository = SpringUtil.getBean(FlinkOfflineLogRepository.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Map<String, Object> jobData = (Map) jobDataMap.get(QuartzFactory.CUSTOM_DATA_KEY);
        RiskEventRule riskEventRule = (RiskEventRule) jobData.get("riskEventRule");
        FilterOperator filterOperator = (FilterOperator) jobData.get("filterOperator");
        Map<String, String> ruleCodeFilterCodeMap = new HashMap<>();
        //策略code:规则code
        ruleCodeFilterCodeMap.put(riskEventRule.getRuleCode(), filterOperator.getCode());
        //离线任务的flink job名称就是策略名称。
        FlinkStartVO flinkStartVO = FlinkStartVO.builder().codeObj(ruleCodeFilterCodeMap).jobName(riskEventRule.getName()).parallelism(1).build();
        //获取提交任务脚本
        String[] exeShellArray = filterOperatorService.getExeShellArrayByFlinkStartVO(new Gson().toJson(flinkStartVO));
        //插入日志记录
        insertFlinkOfflineLog(riskEventRule.getRuleCode(), filterOperator.getCode());
        logger.info("###############离线flink任务提交的脚本exeShellArray={},当前执行的时间为{}", StringUtils.join(exeShellArray, " "), DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS));
        //提交flink job脚本
        ShellExecuteScript.executeShellByResultArray(exeShellArray);
    }

    /**
     * 插入离线启动记录日志，方便追溯离线任务的启动情况
     *
     * @param ruleCode   策略ruleCode，也就是离线flink任务的名称
     * @param filterCode 规则code
     */
    private void insertFlinkOfflineLog(String ruleCode, String filterCode) {
        try {
            FlinkOfflineLog flinkOfflineLog = new FlinkOfflineLog();
            flinkOfflineLog.setGuid(UUIDUtils.get32UUID());
            flinkOfflineLog.setCreateTime(new Date());
            flinkOfflineLog.setFilterCode(filterCode);
            flinkOfflineLog.setRuleCode(ruleCode);
            flinkOfflineLogRepository.save(flinkOfflineLog);
        } catch (Exception e) {
            logger.error("#############insertFlinkOfflineLog happen error={}", e);
        }
    }
}
