package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.flow.core.constant.CollabrationConstant;
import com.vrv.vap.alarmdeal.business.flow.core.repository.CollaborationTaskRepository;
import com.vrv.vap.alarmdeal.business.flow.core.service.flowRequest.FlowInfoCallBackRequest;
import com.vrv.vap.alarmdeal.business.flow.core.service.flowRequest.FlowInfoResponse;
import com.vrv.vap.alarmdeal.business.flow.core.config.GlobalEventListener;
import com.vrv.vap.alarmdeal.business.flow.core.config.InstanceEndListener;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessInstanceStatEnum;
import com.vrv.vap.alarmdeal.business.flow.core.model.CollaborationTask;
import com.vrv.vap.alarmdeal.business.flow.core.model.Mapregion;
import com.vrv.vap.alarmdeal.business.flow.core.vo.CollaborationTaskSearchVO;
import com.vrv.vap.alarmdeal.business.flow.core.vo.CollaborationTaskVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.req.HttpSyncRequest;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 上午11:13:42 
* 类说明 
*/
@Service
public class CollaborationTaskService extends BaseServiceImpl<CollaborationTask, String> {

	private static Logger logger = LoggerFactory.getLogger(CollaborationTaskService.class);
	public static final String ASSIGN = "all";
	
	@Autowired
	private CollaborationTaskRepository collaborationTaskRepository;
	@Autowired
	private GlobalEventListener globalEventListener;

	private String collaborationCallBackHttpUrl;

	private HttpSyncRequest httpSyncRequest;
	
	@Override
	public CollaborationTaskRepository getRepository() {
		return collaborationTaskRepository;
	}

	/**
	 * 创建协同工单
	 * @param collaborationTask
	 * @return
	 */
	public Result<Boolean> createCollaborationTask(CollaborationTaskVO collaborationTaskVO){
		String mapRegionContent = collaborationTaskVO.getMapRegionContent();
		Gson gson = new Gson();
		Mapregion mapregion = gson.fromJson(mapRegionContent, Mapregion.class);
		CollaborationTask collaborationTask = new CollaborationTask();
		collaborationTask.setGuid(UUID.randomUUID().toString());
		collaborationTask.setAssign(ASSIGN);
		collaborationTask.setAssignTime(new Date());
		collaborationTask.setMapRegionInfo(mapRegionContent);
		collaborationTask.setTaskStatus(CollabrationConstant.UN_DEAL);
		collaborationTask.setTaskType(mapregion.getName());
		collaborationTask.setTicketContent(collaborationTaskVO.getTicketContent());
		collaborationTask.setTicketId(collaborationTaskVO.getTicketId());
		collaborationTask.setUpIp(collaborationTaskVO.getUpIp());
		try{
			save(collaborationTask);
			return ResultUtil.success(true);
		}catch(Exception e){
			throw new RuntimeException("保存失败", e);
		}
	}
	
	/**
	 * 更新协同工单的任务
	 * @param businessInstanceId
	 * @return
	 */
	public Result<Boolean> updateCollaborationTaskInfo(CollaborationTaskVO collaborationTaskVO){
		String collabrationId = collaborationTaskVO.getCollabrationId();
		CollaborationTask collaborationTask = getOne(collabrationId);
		if(collaborationTask!=null){
			String collabrationTicketId = collaborationTaskVO.getCollabrationTicketId();
			collaborationTask.setCollaborationTicketId(collabrationTicketId);
			collaborationTask.setTaskStatus(collaborationTaskVO.getStatus());
			save(collaborationTask);
			return ResultUtil.success(true);
		}else{
			throw new RuntimeException("没有该协同任务，请检查");
		}
	}
	
	
	
	
	/**
	 * 协同任务分页查询
	 * @param eventTableVO
	 * @param pageable
	 * @return
	 */
	public PageRes<CollaborationTask> getCollaborationTaskPager(CollaborationTaskSearchVO collaborationTaskVO, Pageable pageable) {
		//查询条件
		List<QueryCondition> conditions = new ArrayList<>();
		String startTime = collaborationTaskVO.getStartTime();
		String endTime = collaborationTaskVO.getEndTime();
		if(StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)){
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = DateUtil.parseDate(startTime, DateUtil.DEFAULT_DATE_PATTERN);
				endDate = DateUtil.parseDate(endTime, DateUtil.DEFAULT_DATE_PATTERN);
			} catch (ParseException e) {
				logger.error("解析异常:{}",e);
			}
			conditions.add(QueryCondition.between("assignTime", startDate, endDate));
		}
		
		Page<CollaborationTask> page = findAll(conditions, pageable);
		PageRes<CollaborationTask> res = PageRes.toRes(page);
		return res;
	}
	
	
	
	
	/**
	 * 改变协同任务的状态
	 */
	@PostConstruct
	public void init(){
		globalEventListener.register(new InstanceEndListener() {
			@Override
			public void end(String processInstanceId, BusinessInstanceStatEnum endcanceled) {
				changeCollabrationStatus(processInstanceId);
			}

			/**
			 * 回调启动协同工单
			 * @param collaborationTask
			 */
			private void callBackInitateTicket(CollaborationTask collaborationTask){
				String upIp = collaborationTask.getUpIp();
				String ticketId = collaborationTask.getTicketId();
				String userId = "";
				String url = replaceIp(collaborationCallBackHttpUrl, upIp);
				logger.info("collaborationCallBackHttpUrl:{}",url);
				FlowInfoCallBackRequest flowInfoCallBackRequest = new FlowInfoCallBackRequest(url,ticketId,userId);
				FlowInfoResponse flowInfoResponse = httpSyncRequest.getResult(flowInfoCallBackRequest);
				Boolean data = flowInfoResponse.getData();
				if(data) {
					collaborationTask.setTaskStatus(CollabrationConstant.DEALEDCALLBACK);
					save(collaborationTask);
				}
			}
			
			private String replaceIp(String url, String ip) {
				String replaceFirst = url.replaceAll("\\{ip\\}", ip);
				return replaceFirst;
			}
			
			/**
			 * 修改协同工单的状态
			 * @param processInstanceId
			 */
			private void changeCollabrationStatus(String processInstanceId) {
				List<QueryCondition> conditions = new ArrayList<>();
				conditions.add(QueryCondition.eq("collaborationTicketId", processInstanceId));
				List<CollaborationTask> list = findAll(conditions);
				if(list.size()!=0){
					if(list.size()==1){
						CollaborationTask collaborationTask = list.get(0);
						callBackInitateTicket(collaborationTask);
					}else{
						throw new RuntimeException("流程实例出现多个:" + processInstanceId);
					}
				}
			}
		});
	}
	
	
}
