package com.vrv.vap.alarmdeal.business.analysis.controller;

import java.util.ArrayList;
import java.util.List;

import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FlinkRunningTimeErrorLog;
import com.vrv.vap.alarmdeal.business.analysis.server.FlinkErrorLogService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RunningTaskVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;

import io.swagger.annotations.ApiOperation;

/**
 * 事件分析引擎错误日志Controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/flinkerror")
public class FlinkErrorController {

	
	@Autowired
	private FlinkErrorLogService flinkErrorLogService;
	@Autowired
	private RiskEventRuleService riskEventRuleService;
	
	
	
	
	/**
	 * 获得flink运行异常集合
	 * 
	 * @param alarmDealVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("/getFlinkErrorList")
	@ApiOperation(value = "获得flink运行异常集合", notes = "")
	@SysRequestLog(description = "获得flink运行异常集合", actionType = ActionType.SELECT, manually = false)
	public Result<List<FlinkRunningTimeErrorLog>> getFlinkErrorList() {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("exceptionType", "starting"));
		List<FlinkRunningTimeErrorLog> list = flinkErrorLogService.findAll(conditions);
		Result<List<FlinkRunningTimeErrorLog>> result = ResultUtil.successList(list);
		return result;
	}
	
	
	
	@PostMapping("/getRunningTaskList")
	@ApiOperation(value = "获得正在运行的任务集合", notes = "")
	@SysRequestLog(description = "获得正在运行的任务集合", actionType = ActionType.SELECT, manually = false)
	public Result<List<RunningTaskVO>> getRunningTaskList() {
		List<RunningTaskVO> runningTasks = riskEventRuleService.getRunningTasks();
		Result<List<RunningTaskVO>> result = ResultUtil.successList(runningTasks);
		return result;
	}
	
	
	
}
