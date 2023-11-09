package com.vrv.vap.alarmdeal.business.flow.core.controller;

import com.vrv.vap.alarmdeal.business.flow.core.model.CollaborationTask;
import com.vrv.vap.alarmdeal.business.flow.core.model.Mapregion;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.CollaborationTaskService;
import com.vrv.vap.alarmdeal.business.flow.core.service.MapRegionService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.CollaborationTaskSearchVO;
import com.vrv.vap.alarmdeal.business.flow.core.vo.CollaborationTaskVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 上午10:50:04 
* 类说明    协同工单controller层
*/
@RestController
@RequestMapping("expatriateTicket")
public class ExpatriateController {

	@Autowired
	private MapRegionService mapRegionService;
	@Autowired
	private CollaborationTaskService collaborationTaskService;
	@Autowired
	private BusinessIntanceService businessIntanceService;
	
	@GetMapping("/getMapRegionList")
	@ApiOperation(value="获得外派选择级联区域",notes="获得外派选择级联区域")
	@SysRequestLog(description="获得外派选择级联区域", actionType = ActionType.SELECT,manually = false)
	public Result<List<Mapregion>> getMapRegionTree(){
		List<Mapregion> list = mapRegionService.getMapRegionList();
		Result<List<Mapregion>> result = ResultUtil.successList(list);
		return result;
		
	}
	
	
	
	@PostMapping("/collaborationPager")
	@ApiOperation(value="协同任务分页查询",notes="")
	@SysRequestLog(description="协同任务分页查询", actionType = ActionType.SELECT,manually = false)
	public PageRes<CollaborationTask> getEventCategoryInfo(@RequestBody CollaborationTaskSearchVO collaborationTaskSearchVO , PageReq pageReq){
		 int count = collaborationTaskSearchVO.getCount_();
		 int start = collaborationTaskSearchVO.getStart_();
		 pageReq.setCount(count);
		 pageReq.setStart(start);
		 pageReq.setOrder(collaborationTaskSearchVO.getOrder_());
		 PageRes<CollaborationTask> pager = collaborationTaskService.getCollaborationTaskPager(collaborationTaskSearchVO, pageReq.getPageable());
		 return pager;
	}
	
	@PostMapping("/createCollaborationTask")
	@ApiOperation(value="通过HTTP请求创建级联任务(后台调用)",notes="")
	@SysRequestLog(description="通过HTTP请求创建级联任务(后台调用)", actionType = ActionType.SELECT,manually = false)
	public Result<Boolean> createCollaborationTask(CollaborationTaskVO collaborationTaskVO){
		Result<Boolean> result = collaborationTaskService.createCollaborationTask(collaborationTaskVO);
		return result;
	}
	
	@PostMapping("/initiateCollabrationTask")
	@ApiOperation(value="工单发起协同任务",notes="")
	@SysRequestLog(description="工单发起协同任务", actionType = ActionType.ADD,manually = false)
	public Result<Boolean> initiateCollabrationTask(@RequestBody CollaborationTaskSearchVO collaborationTaskSearchVO){

		return null;
	}
	
	@PostMapping("/updateCollaborationTaskInfo")
	@ApiOperation(value="更新协同任务状态",notes="创建协同任务工单：前端传参：dealing 返回BusinessInstance的processInstanceId")
	@SysRequestLog(description="更新协同任务状态", actionType = ActionType.UPDATE,manually = false)
	public Result<Boolean> updateCollaborationTaskInfo(@RequestBody CollaborationTaskVO collaborationTaskVO){
		Result<Boolean> result = collaborationTaskService.updateCollaborationTaskInfo(collaborationTaskVO);
		return result;
	}
	
}
