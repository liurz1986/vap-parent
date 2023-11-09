package com.vrv.vap.alarmdeal.business.flow.core.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.vrv.vap.alarmdeal.business.flow.monitor.vo.MyTicketTreeVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.BusinessTaskLogVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessTicketVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.controller.BaseController;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("businessTask")
public class BusinessTaskController extends BaseController<BusinessTask, String> {
	

	@Autowired
	private BusinessTaskService businessTaskService;

	@Autowired
	private BusinessIntanceService businessIntanceService;
	
	@Override
	protected BaseService<BusinessTask, String> getService() {
		return businessTaskService;
	}


	final String[] DISALLOWED_FIELDS = new String[]{"", "",
			""};

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}
	
	@PostMapping("mytaskTree")
	@ApiOperation(value="待办工单树",notes="与登录者相关的实例")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="userId",dataType="String")
	})
	@SysRequestLog(description="待办工单树", actionType = ActionType.SELECT,manually = false)
	public Result<List<MyTicketTreeVO>> findUserTasksTree(@RequestBody Map<String,Object> map){
		String userId = map.get("userId").toString();
		List<MyTicketTreeVO> list = businessTaskService.findUserTasksTree(userId);
		Result<List<MyTicketTreeVO>> result = ResultUtil.success(list);
		return result;
		
	}
	
	@PostMapping("mytask")
	@ApiOperation(value="我的工单",notes="与登录者相关的实例")
	@SysRequestLog(description="获取我的待办工单", actionType = ActionType.SELECT,manually = false)
	public PageRes<BusinessTask> mytask(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq){
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		pageReq.setCount(count_);
		pageReq.setStart(start_);
		pageReq.setOrder(businessTicketVO.getOrder_());
		pageReq.setBy(businessTicketVO.getBy_());
		Pageable pageable = PageReq.getPageable(pageReq, true);
		String userId = businessTicketVO.getUserId();
		BusinessTask businessTask = businessTicketVO.getBusinessTask();
		if(businessTask==null) {
			businessTask = new BusinessTask();
		}
		Page<BusinessTask> findAll = businessTaskService.findUserTasks(userId, businessTask, pageable);
		PageRes<BusinessTask> res = PageRes.toRes(findAll);
		List<BusinessTask> list = res.getList();
		businessTaskService.selectBusiArgTask(list);
		res.setList(list);
		return res;
	}
	
	@GetMapping("mytaskbyId/{guid}")
	@ApiOperation(value="根据Id获得工单",notes="根据Id获得工单")
	@SysRequestLog(description="根据Id获得工单", actionType = ActionType.SELECT,manually = false)
	public Result<BusinessTask> mytaskbyId(@PathVariable String guid){
		BusinessTask businessTask = businessTaskService.getOne(guid);
		Result<BusinessTask> result = ResultUtil.success(businessTask);
		return result;
	}

	/*
	*获取当前节点处理人和节点名称
	* */
	@PostMapping("dealingbusinessTicketLog")
	@ApiOperation(value="获取当前节点处理人和节点名称",notes="获取当前节点处理人和节点名称")
	@SysRequestLog(description="获取当前节点处理人和节点名称", actionType = ActionType.SELECT,manually = false)
	public Result<Map<String,Object>> dealingbusinessTicketLog(@RequestBody BusinessTaskLogVO businessTaskLog){
		Map<String,Object> map=new HashMap<>();
		String processInstanceId=businessTaskLog.getProcessInstanceId();
		List<QueryCondition> conditions=new ArrayList<>();
		conditions.add(QueryCondition.eq("busiId",processInstanceId));
		List<BusinessTask>  businessTasks=businessTaskService.findAll(conditions);
		if(businessTasks.size()>0){
			BusinessTask businessTask=businessTasks.get(0);
			map.put("taskDefindName",businessTask.getTaskDefindName());
			String nameStr="";
			for(BusinessTask businessTask1:businessTasks){
				String dealPeoples=businessIntanceService.getDealPeopleNames(businessTask1.getTaskId());
				nameStr= dealPeoples+","+nameStr;
			}
			if(StringUtils.isNotEmpty(nameStr)){
				map.put("candidatePerson",nameStr.substring(0,nameStr.length()-1));
			}
			return ResultUtil.success(map);
		}else{
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"该工单没有任务");
		}

	}

	
}
