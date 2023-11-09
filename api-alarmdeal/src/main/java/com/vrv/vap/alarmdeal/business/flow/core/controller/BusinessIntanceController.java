package com.vrv.vap.alarmdeal.business.flow.core.controller;

import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.constant.MyTicketConstant;
import com.vrv.vap.alarmdeal.business.flow.monitor.vo.MyTicketTreeVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessInstanceStatEnum;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessCollaboration;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessIntanceVO;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessTicketVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.controller.BaseController;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


 @RestController
 @RequestMapping("businessIntance")
public class BusinessIntanceController extends BaseController<BusinessIntance, String> {

	private static Logger logger = LoggerFactory.getLogger(BusinessIntanceController.class);
	@Autowired
	private BusinessIntanceService businessIntanceService;
	@Autowired
	private FlowService flowService;
	@Autowired
	private MyTicketService mTicketService;

	@Autowired
	private MapperUtil mapperUtil;
	
	@Override
	protected BaseService<BusinessIntance, String> getService() {
		return businessIntanceService;
	}


	 final String[] DISALLOWED_FIELDS = new String[]{"", "",
			 ""};

	 @InitBinder
	 public void initBinder(WebDataBinder binder) {
		 binder.setDisallowedFields(DISALLOWED_FIELDS);
	 }

	
	@PostMapping("myrelateInstanceTree")
	@ApiOperation(value="我的工单树",notes="与登录者相关的实例")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="userId",dataType="String")
	})
	@SysRequestLog(description="我的工单树", actionType = ActionType.SELECT,manually = false)
	public Result<List<MyTicketTreeVO>> findUserInstanceTree(@RequestBody Map<String,Object> map){
		String userId = map.get("userId").toString();
		List<MyTicketTreeVO> list = businessIntanceService.findUserInstancesTree(userId);
		Result<List<MyTicketTreeVO>> result = ResultUtil.success(list);
		return result;
		
	}
	
	
	@PostMapping("myrelateInstanceTreeBySec")
	@ApiOperation(value="我的工单树我相关的区域都有展现",notes="与登录者相关的实例")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="userId",dataType="String")
	})
	@SysRequestLog(description="我的工单树我相关的区域都有展现", actionType = ActionType.SELECT,manually = false)
	public Result<List<MyTicketTreeVO>> myrelateInstanceTreeBySec(@RequestBody Map<String,Object> map,HttpServletRequest request){
		String userId = map.get("userId").toString();
		String header = request.getHeader("Cookie");
		List<MyTicketTreeVO> list = businessIntanceService.findUserInstancesBysecTree(header,userId);
		Result<List<MyTicketTreeVO>> result = ResultUtil.success(list);
		return result;
		
	}
	
	
	/**
	 * 与登录者相关的实例
	 * @param businessTicketVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("myrelateInstance")
	@ApiOperation(value="与登录者相关的实例",notes="与登录者相关的实例")
	@SysRequestLog(description="获取我的工单信息列表", actionType = ActionType.SELECT,manually = false)
	public PageRes<BusinessIntanceVO> mytask(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq){
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		pageReq.setCount(count_);
		pageReq.setStart(start_);
		pageReq.setOrder(businessTicketVO.getOrder_());
		pageReq.setBy(businessTicketVO.getBy_());
		Pageable pageable = PageReq.getPageable(pageReq, true);
		String userId = businessTicketVO.getUserId();
		BusinessIntance businessIntance = businessTicketVO.getBusinessIntance();
		Page<BusinessIntanceVO> findAll = businessIntanceService.findUserInstances(userId, businessIntance, pageable);
		return PageRes.toRes(findAll);
	}
	
	/**
	 * 与登录者以及相关安全域的实例相关的实例
	 * @param businessTicketVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("myrelateInstanceBysec")
	@ApiOperation(value="与登录者以及相关安全域的实例相关的实例",notes="与登录者相关的实例")
	@SysRequestLog(description="获取我的工单（相关安全域的）信息列表", actionType = ActionType.SELECT,manually = false)
	public PageRes<BusinessIntanceVO> myrelateInstanceBysec(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq, HttpServletRequest request){
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		String header = request.getHeader("Cookie");
		pageReq.setCount(count_);
		pageReq.setStart(start_);
		pageReq.setOrder(businessTicketVO.getOrder_());
		pageReq.setBy(businessTicketVO.getBy_());
		Pageable pageable = PageReq.getPageable(pageReq, true);
		String userId = businessTicketVO.getUserId();
		BusinessIntance businessIntance = businessTicketVO.getBusinessIntance();
		Page<BusinessIntanceVO> findAll = businessIntanceService.findUserInstancesBySec(header,userId, businessIntance, pageable);
		return PageRes.toRes(findAll);
	}
	
	
	
	
	@GetMapping("queryBusinessInstanceStatics")
	@ApiOperation(value="首页工单分类统计",notes="首页工单分类统计")
	@SysRequestLog(description="首页工单分类统计", actionType = ActionType.SELECT,manually = false)
	public Result<List<Map<String,Object>>> queryBusinessInstanceStatics(){
		List<Map<String,Object>> list = businessIntanceService.queryBusinessInstanceStatics();
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	/**
	 * 获取工单监控列表信息
	 *
	 * 流程分类下：增加内部工单、外部工单菜单节点
	 * 2022-11-2
	 * @param businessTicketVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("businesslogInstances")
	@ApiOperation(value="工单监控信息",notes="工单监控信息")
	@SysRequestLog(description="获取工单监控列表信息", actionType = ActionType.SELECT,manually = false)
	public PageRes<BusinessIntanceVO> businesslogInstances(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq){
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		pageReq.setCount(count_);
		pageReq.setStart(start_);
		pageReq.setOrder(businessTicketVO.getOrder_());
		pageReq.setBy(businessTicketVO.getBy_());
		Pageable pageable = PageReq.getPageable(pageReq, true);
		BusinessIntance businessIntance = businessTicketVO.getBusinessIntance();
		List<QueryCondition> conditions = new ArrayList<>();
		String processDefName = businessIntance.getProcessDefName();
		boolean isTicketTypeNode = MyTicketConstant.isNodeTrue(processDefName);
		List<String> myTicketGuids = null;
		if(isTicketTypeNode){
			businessIntance.setProcessDefName(null);
			myTicketGuids= businessIntanceService.getMyTicketGuids(processDefName);
			if(CollectionUtils.isEmpty(myTicketGuids)){
				Page<BusinessIntanceVO> page1=new PageImpl<>(new ArrayList<>(),pageable,0);
				return  PageRes.toRes(page1);
			}else{
				// 内部、外部工单菜单节点处理 2022-11-2
				conditions.add(QueryCondition.in("processDefGuid",myTicketGuids));
			}
		}
		if(businessIntance!=null) {
			if(businessIntance.getProcessDefName()!=null){
				conditions.add(QueryCondition.eq("processDefName", businessIntance.getProcessDefName()));
			}
			if(businessIntance.getName()!=null){
				conditions.add(QueryCondition.like("name",businessIntance.getName()));
			}
			if(StringUtils.isNotEmpty(businessIntance.getCreateUserName())){
				conditions.add(QueryCondition.like("createUserName",businessIntance.getCreateUserName()));
			}
			if(businessIntance.getStatEnum()!=null && StringUtils.isNotEmpty(businessIntance.getStatEnum().toString())){
				conditions.add(QueryCondition.eq("statEnum",businessIntance.getStatEnum()));
			}

		}
		Page<BusinessIntance> page = businessIntanceService.findAll(conditions, pageable);
		List<BusinessIntanceVO> businessIntanceVOList=mapperUtil.mapList( page.getContent(),BusinessIntanceVO.class);
		for(BusinessIntanceVO businessIntanceVO:businessIntanceVOList){
			businessIntanceService.constructDealPeopleNames(businessIntanceVO);

		}
		Page<BusinessIntanceVO> page1=new PageImpl<>(businessIntanceVOList,pageable,page.getTotalElements());
		return  PageRes.toRes(page1);
	}


    // {"deleteReason":"强制办结","instanceId":"56073523e63f431a94995ae49cf5637e","userId":"31"}
	@PostMapping("forceEnd")
	@ApiOperation(value="强制办结",notes="工单强制办结")
	@ApiImplicitParams({
		@ApiImplicitParam(name="instanceId",value="流程实例ID",required=true,dataType="String"),
		@ApiImplicitParam(name="deleteReason",value="强制办结内容",required=true,dataType="String")
	})
	@SysRequestLog(description="工单强制办结", actionType = ActionType.DELETE,manually = false)
	public Result<BusinessIntance> forceEnd(@RequestBody Map<String,Object> map){
		String instanceId = map.get("instanceId").toString();
		String deleteReason = map.get("deleteReason").toString();
		Object userIdObj = map.get("userId");
		if(null == userIdObj || StringUtils.isEmpty(String.valueOf(userIdObj))){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前处理人userId不能为空");
		}
		String userId = String.valueOf(userIdObj);
		Map<String,Object> handler = new HashMap<>();
		handler.put(FlowConstant.USERID,userId);
		BusinessIntance one = businessIntanceService.getOne(instanceId);
		if(one.getStatEnum().equals(BusinessInstanceStatEnum.dealing)) {
			flowService.setVariables(one.getProcessInstanceId(), map);
			// 存放当前处理人
			flowService.setVariablesNew(one.getProcessInstanceId(),handler);
			flowService.deleteProcessInstance(one.getProcessInstanceId(), deleteReason);
		}
		return ResultUtil.success(one);
	}

	@PostMapping("changeInitatesTicketStatus")
	@ApiOperation(value="",notes="改变发起协同流程状态(回调)（后台调用）")
	public Result<Boolean> changeInitatesTicketStatus(BusinessCollaboration businessCollaboration){
		Result<Boolean> result = businessIntanceService.changeTicketStatus(businessCollaboration);
		return result;
	}

	 @PostMapping("getBusinessIntanceByInstanceId")
	 @ApiOperation(value = "", notes = "获取BusinessIntance")
	 @SysRequestLog(description="获取BusinessIntance", actionType = ActionType.SELECT,manually = false)
	 public Result<BusinessIntance> getBusinessIntanceByGuid(@RequestBody Map<String,String> map) {
		 String instanceId = map.get("instanceId");
		 BusinessIntance businessIntance = businessIntanceService.getOne(instanceId);
		 return ResultUtil.success(businessIntance);
	 }
	
}
