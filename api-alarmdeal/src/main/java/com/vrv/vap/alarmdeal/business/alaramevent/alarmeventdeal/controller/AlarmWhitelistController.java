package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.controller;

import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmWhitelistService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmWhitelistQueryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmWhitelistVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alarmWhiteList")
@Api(description="告警白名单")
public class AlarmWhitelistController extends BaseController {

	@Autowired
	private AlarmWhitelistService alarmWhitelistService;

	/**
	 * 告警白名单列表页面
	 * @param alarmWhitelistQueryVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping(value = "/alarmWhitelistPager")
	@ApiOperation(value="获得告警白名单列表",notes="")
	@SysRequestLog(description="告警白名单-获得告警白名单列表", actionType = ActionType.SELECT,manually=false)
	public PageRes<AlarmWhitelistVO> getAlarmWhitelistPager(@RequestBody AlarmWhitelistQueryVO alarmWhitelistQueryVO,
														 PageReq pageReq) {
		Integer start = alarmWhitelistQueryVO.getStart_();
		Integer count = alarmWhitelistQueryVO.getCount_();
		pageReq.setStart(start);
		pageReq.setCount(count);
		pageReq.setOrder("updateTime");
		pageReq.setBy("desc");
		PageRes<AlarmWhitelistVO> pageRes = alarmWhitelistService.getAlarmWhitelistPager(alarmWhitelistQueryVO,pageReq.getPageable());
		return pageRes;
	}


	/**
	 * 添加告警白名单
	 * @param alarmWhitelistVO
	 * @return
	 */
	@PostMapping(value="/add")
	@ApiOperation(value="添加告警白名单",notes="")
	@SysRequestLog(description="告警白名单-添加告警白名单", actionType = ActionType.ADD,manually=false)
	public Result<Boolean> addAlarmWhitelist(@RequestBody AlarmWhitelistVO alarmWhitelistVO){
		Result<Boolean> result = alarmWhitelistService.addAlarmWhitelist(alarmWhitelistVO);
		return result;
	}

	/**
	 * 编辑告警白名单
	 * @param alarmWhitelistVO
	 * @return
	 */
	@PostMapping(value="/edit")
	@ApiOperation(value="编辑告警白名单",notes="")
	@SysRequestLog(description="告警白名单-编辑告警白名单", actionType = ActionType.UPDATE,manually=false)
	public Result<Boolean> editAlarmWhitelist(@RequestBody AlarmWhitelistVO alarmWhitelistVO){
		Result<Boolean> result = alarmWhitelistService.editAlarmWhitelist(alarmWhitelistVO);
		return result;
	}
	/**
	 * 删除告警白名单
	 * @param riskRuleIdVO
	 * @return
	 */
	@PostMapping(value="/del")
	@ApiOperation(value="删除告警白名单",notes="")
	@SysRequestLog(description="告警白名单-删除告警白名单", actionType = ActionType.DELETE,manually=false)
	public Result<Boolean> delAlarmWhitelists(@RequestBody RiskRuleIdVO riskRuleIdVO){
		List<String> ids = riskRuleIdVO.getIds();
		Result<Boolean> result = alarmWhitelistService.delAlarmWhitelists(ids);
		return result;
	}

}
