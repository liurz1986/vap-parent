package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmItemDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmDealServer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmItemDealService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmCommandVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmDealVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealInfoVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealWayVO;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alarmDeal")
@Api(description = "告警处置")
public class AlarmDealController extends BaseController {

	@Autowired
	private AlarmDealServer alarmDealServer;
	@Autowired
	private AlarmItemDealService alarmItemDealService;
	@Autowired
	private AdminFeign adminFeign;


	/**
	 * 获得告警处置列表
	 * 
	 * @param alarmDealVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("/getAlarmDealPager")
	@ApiOperation(value = "获得告警处置列表", notes = "")
	@SysRequestLog(description="告警处置-获得告警处置列表", actionType = ActionType.SELECT,manually=false)
	public PageRes<AlarmDeal> getAlarmDealPager(@RequestBody AlarmDealVO alarmDealVO, PageReq pageReq) {
		Integer start = alarmDealVO.getStart_();
		Integer count = alarmDealVO.getCount_();
		pageReq.setStart(start);
		pageReq.setCount(count);
		pageReq.setOrder("createTime");
		pageReq.setBy("desc");
		PageRes<AlarmDeal> pageRes = alarmDealServer.getAlarmDealPager(alarmDealVO, pageReq.getPageable());
		return pageRes;
	}

	/**
	 * 根据预警处置guid获得预警处置的值
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/getAlarmDealById/{id}")
	@ApiOperation(value = "获得预警处置的信息", notes = "根据预警处置guid查询告警信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "告警ID", required = true, dataType = "String") })
	@SysRequestLog(description="告警处置-获得预警处置的信息", actionType = ActionType.SELECT,manually=false)
	public Result<AlarmDeal> getAlarmDealById(@PathVariable("id") String id) {
		AlarmDeal alarmDeal = alarmDealServer.getOne(id);
		Result<AlarmDeal> result = ResultUtil.success(alarmDeal);
		return result;
	}

	/**
	 * 未下发
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/getAlarmitemDealBydealId/{id}")
	@ApiOperation(value = "获得预警处置的处置方式", notes = "根据预警处置guid查询告警处置方式")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "告警ID", required = true, dataType = "String") })
	@SysRequestLog(description="告警处置-获得预警处置的处置方式", actionType = ActionType.SELECT,manually=false)
	public Result<List<AlarmItemDeal>> getAlarmitemDealBydealId(@PathVariable("id") String id) {
		List<QueryCondition> cons = new ArrayList<QueryCondition>();
		QueryCondition con = QueryCondition.eq("dealGuid", id);
		cons.add(con);
		List<AlarmItemDeal> alarmitemdeal = alarmItemDealService.findAll(cons);
		Result<List<AlarmItemDeal>> result = ResultUtil.success(alarmitemdeal);
		return result;
	}

	/**
	 * 获取下发过的预警处置
	 * 
	 * @return
	 */
	@GetMapping(value = "/getAlarmDealByNoDealStatus/{id}")
	@ApiOperation(value = "获取下发过的预警处置", notes = "根据预警处置guid获取下发过的预警处置")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "告警ID", required = true, dataType = "String") })
	@SysRequestLog(description="告警处置-获取下发过的预警处置", actionType = ActionType.SELECT,manually=false)
	public Result<List<AlarmItemDeal>> getAlarmDealBydealStatus(@PathVariable("id") String id) {
		List<QueryCondition> cons = new ArrayList<>();
		cons.add(QueryCondition.eq("dealGuid", id));
		List<AlarmItemDeal> alarmitemdeal = alarmItemDealService.findAll(cons);
		Result<List<AlarmItemDeal>> result = ResultUtil.success(alarmitemdeal);
		return result;
	}

	/**
	 * 根据预警ID获得对应的告警处置
	 * 
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAlarmDealByAlarmId/{guid}")
	@ApiOperation(value = "获得对应的告警处置", notes = "根据预警ID获得对应的告警处置")
	@ApiImplicitParams({ @ApiImplicitParam(name = "guid", value = "告警ID", required = true, dataType = "String") })
	@SysRequestLog(description="告警处置-获得对应的告警处置", actionType = ActionType.SELECT,manually=false)
	public Result<List<AlarmDeal>> getAlarmDealByAlarmId(@PathVariable("guid") String guid) {
		List<QueryCondition> cons = new ArrayList<QueryCondition>();
		cons.add(QueryCondition.like("alarmGuid", guid));
		Sort sort = Sort.by(Direction.DESC, "createTime");
		List<AlarmDeal> alarmdeal = alarmDealServer.findAll(cons, sort);
		Result<List<AlarmDeal>> result = ResultUtil.success(alarmdeal);
		return result;
	}

	/**
	 * 保存告警处置
	 * 
	 * @param dealWayVO
	 * @return
	 */
	@PostMapping(value = "/saveAlarmDeal")
	@ApiOperation(value = "保存告警处置", notes = "")
	@SysRequestLog(description="告警处置-保存告警处置", actionType = ActionType.ADD,manually=false)
	public Result<String> saveAlarmDeal(@RequestBody DealWayVO dealWayVO) {
		Result<String> result = alarmDealServer.saveAlarmDeal(dealWayVO);
		return result;
	}

	/**
	 * 新增处置
	 * 
	 * @param dealWayVO
	 * @return
	 */
	@PostMapping(value = "/alarmDeal")
	@ApiOperation(value = "新增处置", notes = "")
	@SysRequestLog(description="告警处置-新增处置", actionType = ActionType.ADD,manually=false)
	public Result<String> alarmDeal(@RequestBody DealInfoVO dealWayVO) {
		Result<String> result = alarmDealServer.alarmDeal(dealWayVO);
		return result;
	}

	/**
	 * 进行告警处置
	 * 
	 * @param alarmCommandVO
	 * @return
	 */
	@PostMapping(value = "/issuedAlarm")
	@ApiOperation(value = "进行告警处置", notes = "")
	@SysRequestLog(description="告警处置-进行告警处置", actionType = ActionType.UPDATE,manually=false)
	public Result<Boolean> issuedAlarm(@RequestBody AlarmCommandVO alarmCommandVO) {
		Result<Boolean> result = alarmDealServer.issueAlarm(alarmCommandVO);
		return result;
	}

	/**
	 * 重复下发
	 * 
	 * @param list
	 * @return
	 */
	@PostMapping(value = "/issueRepeatAlarm")
	@ApiOperation(value = "重复下发告警处置", notes = "")
	@SysRequestLog(description="告警处置-重复下发告警处置", actionType = ActionType.UPDATE,manually=false)
	public Result<Boolean> issueRepeatAlarm(@RequestBody List<AlarmCommandVO> list) {
		Result<Boolean> result = alarmDealServer.issueRepeatAlarm(list);
		return result;
	}


	@PostMapping(value = "byIp")
	@ApiOperation(value = "通过IP查询组织结构", notes = "")
	@SysRequestLog(description="告警处置-通过IP查询组织结构", actionType = ActionType.SELECT,manually=false)
	public ResultObjVO<BaseKoalOrg> byIp(@RequestBody Map<String, String> map) {
		ResultObjVO<BaseKoalOrg> resultObjVO = adminFeign.byIp(map);
		return resultObjVO;
	}

	@GetMapping(value = "bycode/{code}")
	@ApiOperation(value = "通过code查询组织机构", notes = "")
	@SysRequestLog(description="告警处置-通过code查询组织机构", actionType = ActionType.SELECT,manually=false)
	public VData<BaseKoalOrg> byCode(@PathVariable("code") String code) {
		VData<BaseKoalOrg> result = adminFeign.orgByCode(code);
		return result;
	}

	@GetMapping(value = "organization/rootinfo")
	@ApiOperation(value = "通过code查询组织机构", notes = "")
	@SysRequestLog(description="告警处置-通过code查询组织机构", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> rootinfo(HttpServletRequest request) {
		List<Map<String,Object>> result = new ArrayList<>();
		VData<BaseKoalOrg> baseKoalOrgVdata = adminFeign.getRoot();
		BaseKoalOrg baseKoalOrg = baseKoalOrgVdata.getData();
		Map<String,Object> baseKoalOrg1 = JSONObject.parseObject(JSON.toJSONString(baseKoalOrg),Map.class);
		result.add(baseKoalOrg1);
		Map<String,Object> other = new HashMap<>();
		other.put("code","8888888");
		other.put("name","未知部门");
		other.put("subCode","8888888");
		other.put("hasChildren",false);
		result.add(other);
		return ResultUtil.success(result);
	}

}
