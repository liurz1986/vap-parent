package com.vrv.vap.alarmdeal.business.flow.processdef.service;

import com.google.common.base.Strings;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.define.service.ModelService;
import com.vrv.vap.alarmdeal.business.flow.processdef.dao.MyTicketDao;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.*;
import com.vrv.vap.alarmdeal.business.flow.processdef.repository.MyTicketRepository;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FlowQueryVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.log.LoggerUtil;
import com.vrv.vap.jpa.web.*;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MyTicketService extends BaseServiceImpl<MyTicket, String> {

	Logger logger = LoggerFactory.getLogger(MyTicketService.class);
	@Autowired
	private MyTicketRepository myTicketRepository;
	@Autowired
	private MyTicketPrivildgeService myTicketPrivildgeService;
	@Autowired
	private MapperUtil mapper;
	@Autowired
	ModelService modelService;
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	FlowService flowService;
	@Autowired
	private MyTicketDao myTicketDao;
	@Autowired
	private AdminFeign authFeign;
	@Autowired
	private AuthService authService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private FileConfiguration fileConfiguration;
	@Autowired
	private FlowConfigService flowConfigService;

	@Value("${bpmn.filePath}")
	private String bpmnFilePath; //bpmn放置的路径
	@Override
	public BaseRepository<MyTicket, String> getRepository() {
		return myTicketRepository;
	}
	
	/**
	 * 根据id和name,version来判断这个名称和版本号是否可用
	 * 新增：名称+版本号不能和已有名称重复
	 * 编辑：名称不能和其他的已有名称重复
	 * 扩展版本问题：如果versionGuid有值，则表示可以和同版本的相同
	 */
	public boolean canUseName(String id, String name, String version) {
		if(Strings.isNullOrEmpty(id)) {
			return !existsNameAndVersion(name, version);
		}
		MyTicket myTicket = getOne(id);
		if(myTicket == null){
			// 表示新增
			return !existsNameAndVersion(name, version);
		} else {
			// 编辑，自己或自己同版本号的可以重复。其他的不可以重复
			boolean result = existsNameAndVersionById(id, name, version);
			
			return !result;
		}
	}

	/**
	 * 是否存在名称
	 * @param name
	 * @return
	 */
	public boolean existsNameAndVersion(String name, String version){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", name));
		conditions.add(QueryCondition.eq("ticketVersion", version));
		return exists(conditions);
	}
	
	/**
	 * 是否存在名称
	 * @param name
	 * @return
	 */
	public boolean existsNameAndVersionById(String id, String name, String version){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.notEq("guid", id));
		conditions.add(QueryCondition.eq("name", name));
		conditions.add(QueryCondition.eq("ticketVersion", version));
		boolean exists = exists(conditions);
		
		return exists;
	}
	
	/**
	 * 除指定id外，是否还存在名称
	 * @param id
	 * @param name
	 * @return
	 */
	public boolean existsNameBcId(String id, String name){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", name));
		conditions.add(QueryCondition.notEq("guid", id));
		return exists(conditions);
	}
	
	/**
	 * 除指定版本系列外，是否还存在名称
	 * @param versionGuid
	 * @param name
	 * @return
	 */
	public boolean existsNameBcVersionGuid(String versionGuid, String name){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", name));
		conditions.add(QueryCondition.notEq("versionGuid", versionGuid));
		return exists(conditions);
	}

	public int getNewOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

	/**
	 * 获得最大排序
	 * @return
	 */
	public Integer getMaxOrderNum() {
		Integer maxOrderNum = myTicketDao.getMaxOrderNum();
		return maxOrderNum;
	}
	
	@Transactional
	public MyTicket saveWithPrivildge(MyTicket model) {
		try {
			MyTicket save = save(model);
			String mytickGuid = save.getGuid();
			String personSelect = model.getPersonSelect();
			List<MyTicketPrivildge> privildges = JsonMapper.fromJsonString2List(personSelect, MyTicketPrivildge.class);
			for (MyTicketPrivildge myTicketPrivildge : privildges) {
				myTicketPrivildge.setGuid(UUIDUtils.get32UUID());
				myTicketPrivildge.setMyTicketGuid(mytickGuid);
			}
			myTicketPrivildgeService.deleteByTicketGuid(mytickGuid);
			myTicketPrivildgeService.save(privildges);
			return save;
		} catch (Exception e){
			logger.error("保存工单权限数据异常", e);
			throw new ResponseException(1002, "保存工单权限数据异常");
		}
	}
	
	@Override
	public void delete(MyTicket entity) {
		entity.setTicketStatus(entity.getTicketStatus().delete());
		save(entity);
	}
	
	@Override
	public void delete(String id) {
		MyTicket one = getOne(id);
		this.delete(one);
	}

	/**
	 * 启动流程，在这个过程中停用同名的流程
	 * @param one
	 * @return
	 */
	public MyTicket start(MyTicket one) {
		// 查找同名的启动流程
		List<MyTicket> tickets = getStartedTicketByName(one.getName());
		for (MyTicket myTicket : tickets) {
			myTicket.setTicketStatus(myTicket.getTicketStatus().disable());
			save(myTicket);
		}
		one.setTicketStatus(one.getTicketStatus().enable());
		one.setUsed(true);
		save(one);
		
		return one;
	}

	private List<MyTicket> getStartedTicketByName(String name) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", name));
		conditions.add(QueryCondition.eq("ticketStatus", ProcessStateEnum.used));
		List<MyTicket> findAll = findAll(conditions);
		
		return findAll;
	}

	public List<MyTicket> getAllStarted() {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ticketStatus", ProcessStateEnum.used));
		List<MyTicket> findAll = findAll(conditions);
		
		return findAll;
	}
	
	/**
	 * userId是当前登陆用户Id
	 * @param name
	 * @param userId
	 * @return
	 */
	public List<MyTicket> getAllStartedByName(String name,String userId,String processDefName) {
		Set<String> myTicketPrivildgeSet = getMyTicketPrivildgeSet(userId);  
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ticketStatus", ProcessStateEnum.used));
		conditions.add(QueryCondition.eq("ticketType", "1"));  // 内部工单
		if(!Strings.isNullOrEmpty(processDefName)){
			conditions.add(QueryCondition.eq("name", processDefName));
		}else if(!Strings.isNullOrEmpty(name)) {
			conditions.add(QueryCondition.like("name", name));
		}
		if(!myTicketPrivildgeSet.isEmpty()) {
			conditions.add(QueryCondition.in("guid", myTicketPrivildgeSet));
		}
		PageReq pageReq=new PageReq();
		pageReq.setCount(10000);
		pageReq.setBy("desc");
		pageReq.setOrder("createTime");
		pageReq.setStart(0);
		Page<MyTicket> pager = findAll(conditions, pageReq.getPageable());
		List<MyTicket> findAll = pager.getContent();
		return findAll;
	}

	
	private Set<String> getMyTicketPrivildgeSetByAllStatus(){
		Set<String> myTicketGuid_set = new HashSet<>();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("privildgeType", PrivildgeTypeEnum.all));
		conditions.add(QueryCondition.eq("ticketStatus", ProcessStateEnum.used));
		List<MyTicket> findAll = findAll(conditions);
		for (MyTicket myTicket : findAll) {
			String guid = myTicket.getGuid();
			myTicketGuid_set.add(guid);
		}
		return myTicketGuid_set;
		
	}
	
	/**
	 * 获得当前登陆用户能够获得的权限流程(分配权限的用户)
	 * @param userId
	 * @return
	 */
	private Set<String> getMyTicketPrivildgeSet(String userId) {
		Set<String> myTicketGuid_set = new HashSet<>();
		Set<String> myTicketPrivildgeSetByAllStatus = this.getMyTicketPrivildgeSetByAllStatus();
		myTicketGuid_set.addAll(myTicketPrivildgeSetByAllStatus);
		List<MyTicketPrivildge> my_ticket_privildge = myTicketPrivildgeService.findAll();
		for (MyTicketPrivildge myTicketPrivildge : my_ticket_privildge) {
			String userType = myTicketPrivildge.getUserType();
			if(userType.equals("user")) { //用户类型是user的情况下
				String dataGuid = myTicketPrivildge.getDataGuid();
				if(StringUtils.isNotEmpty(dataGuid)) {
					if(dataGuid.contains(userId)){
						myTicketGuid_set.add(myTicketPrivildge.getMyTicketGuid());
					}					
				}
			}else if(userType.equals("role")){ //用户类型是role的情况下
				ResultObjVO<User> resultObjVO = authFeign.getUserById(Integer.valueOf(userId)); //通过userId获得用户信息
				String roleId = resultObjVO.getData().getRoleId(); //当前登陆用户roleId
				String dataGuid = myTicketPrivildge.getDataGuid();
				String myTicketGuid = myTicketPrivildge.getMyTicketGuid();
				if(myTicketGuid.equals("2d64aaf49fcd41b1b7b5ec7cb08b6d1d")) {
					logger.info("dataGuid:"+dataGuid+"roleId:"+roleId);
				}
				if(StringUtils.isNotEmpty(dataGuid)) {
					String[] split = dataGuid.split(",");
					for (String guid : split) {
						String[] roleArr = roleId.split(",");
						for (String role : roleArr) {
							if(role.equals(guid)) {
								myTicketGuid_set.add(myTicketPrivildge.getMyTicketGuid());															
							}
						}
					}
				}
			}
		}
		return myTicketGuid_set;
	}

	
	/**
	 * 复制一份myticket
	 */
	public MyTicket copyOne(String newVersion, MyTicket one,String userName) {
		String newGuid = UUIDUtils.get32UUID();
		boolean canUseName = canUseName(newGuid, one.getName(), newVersion);
		if(!canUseName) {
			throw new ResponseException(1101, "版本号有重复，请重新输入版本号");
		}
		MyTicket map = mapper.map(one, MyTicket.class);
		map.setGuid(newGuid);
		map.setCreateTime(new Date());
		map.setCreateUser(userName);
		map.setUpdateTime(new Date());
		map.setUpdateUser(userName);
		map.setTicketVersion(Integer.valueOf(newVersion));
		map.setTicketStatus(ProcessStateEnum.draft);
		map.setUsed(false);
		if(!Strings.isNullOrEmpty(one.getModelId())) {
			Model copyModel = modelService.copyModel(one.getModelId());
			map.setModelId(copyModel.getId());  
			// 复制模型的时候，不直接进行模型的发布。需要主动编辑发布
			map.setDeployId("");
		} else {
			map.setModelId("");
			map.setDeployId("");
		}
		
		save(map);
		return map;
	}
	
	/**
	 * 获取所有正在删除和正在停用中的流程定义实例
	 * @return
	 */
	public List<MyTicket> getIngTickets(){
		List<QueryCondition> cons = new ArrayList<>();
		QueryCondition deleting = QueryCondition.eq("ticketStatus", ProcessStateEnum.deleting);
		QueryCondition stopping = QueryCondition.eq("ticketStatus", ProcessStateEnum.stopping);
		cons.add(QueryCondition.or(deleting, stopping));
		List<MyTicket> findAll = findAll(cons);
		
		return findAll;
	}
	
	/**
	 * 判断是否有正在运行的流程实例
	 * @param myticket
	 * @return
	 */
	public boolean hasInstanceRun(MyTicket myticket) {
		String deployId = myticket.getDeployId();
		String defineIdByDeploy = flowService.getDefineIdByDeploy(deployId);
		boolean result = flowService.containsInstance(defineIdByDeploy);
		
		return result;
	}

	/**
	 * 获取所有在使用中，或者删除中，停用中的流程
	 * @return
	 */
	public List<MyTicket> getUsingTicket() {
		QueryCondition used = QueryCondition.eq("ticketStatus", ProcessStateEnum.used);
		QueryCondition deleting = QueryCondition.eq("ticketStatus", ProcessStateEnum.deleting);
		QueryCondition stopping = QueryCondition.eq("ticketStatus", ProcessStateEnum.stopping);
		QueryCondition or = QueryCondition.or(used, deleting, stopping);
		List<QueryCondition> cons = new ArrayList<>();
		cons.add(or);
		List<MyTicket> findAll = findAll(cons);
		
		return findAll;
	}
	
	
	public List<MyTicket> getProcessTree() {
		List<MyTicket> list = myTicketDao.queryMonitorTicket();
		List<Map<String,Object>> monitorExistTicket = myTicketDao.queryMonitorExistTicket();
		for (MyTicket myticket : list) {
			String name = myticket.getName();
			int count = getCount(name,monitorExistTicket);
			myticket.setCount(count);
		}
		return list;
	}
	private int getCount(String name, List<Map<String, Object>> monitorExistTicket) {
		for (Map<String, Object> map : monitorExistTicket) {
			if(map.containsKey("process_def_name")&&map.get("process_def_name")!=null){
				String process_def_name = map.get("process_def_name").toString();
				if(process_def_name.equals(name)){
					return Integer.valueOf(map.get("count").toString());
				}
			}
		}
		return 0;
	}
	public List<MyTicket> getAllNotRealdelete(){
		List<QueryCondition> cons = new ArrayList<>();
		cons.add(QueryCondition.notEq("ticketStatus", ProcessStateEnum.realDelete));
		
		List<MyTicket> findAll = findAll(cons);
		
		return findAll;
	}
	
	/**
	 * 交换排序
	 * @param beforeProcess
	 * @param afterProcess
	 * @return
	 */
	public boolean changeProcessOrder(String beforeProcess,String afterProcess) {
		List<Integer> newOrderInfo = changeOrderInfo(beforeProcess, afterProcess);
		setNewOrderNum(beforeProcess, newOrderInfo.get(0));
		setNewOrderNum(afterProcess, newOrderInfo.get(1));
		return true;
	}

	/**
	 * 交换信息
	 * @param beforeProcess
	 * @param afterProcess
	 */
	private List<Integer> changeOrderInfo(String beforeProcess, String afterProcess) {
		List<Integer> list = new LinkedList<>();
		Integer temp = 0;
		Integer beforeOrderNum = getOrderNum(beforeProcess);
		Integer afterOrderNum = getOrderNum(afterProcess);
		temp = beforeOrderNum;
		beforeOrderNum = afterOrderNum;
		afterOrderNum = temp;
		list.add(beforeOrderNum);
		list.add(afterOrderNum);
		return list;
	}

	private void setNewOrderNum(String process,Integer orderNum) {
		List<QueryCondition> condition = new ArrayList<>();
		condition.add(QueryCondition.eq("name", process));
		List<MyTicket> list = findAll(condition);
		for (MyTicket myTicket : list) {
			myTicket.setOrderNum(orderNum);
		}
		save(list);
	}
	
	private Integer getOrderNum(String process) {
		int orderNum = 0;
		List<QueryCondition> condition = new ArrayList<>();
		condition.add(QueryCondition.eq("name", process));
		List<MyTicket> list  = findAll(condition);
		if(list.size()!=0) {
			MyTicket myTicket = list.get(0);
			orderNum = myTicket.getOrderNum();
		}
		return orderNum;
	}

    /**
     * 查询树表流程信息列表	
     * @param flowQueryVO
     * @return
     */
	public List<Map<String,Object>> queryForProcessList(FlowQueryVO flowQueryVO){
		List<Map<String, Object>> rootList = getRootProcessList(flowQueryVO);
		for (Map<String, Object> map : rootList) {
			String name = map.get("name").toString();
			List<Map<String,Object>> childrenList = myTicketDao.queryChildrenProcess(name);
			for (Map<String, Object> childMap : childrenList) {
				childMap.remove("order_num");
			}
			//按照网上的方法之前改过，没用，还谈不上会不会影响功能。
			map.put("children", childrenList);
		}
		return rootList;
	}

	private List<Map<String, Object>> getRootProcessList(FlowQueryVO flowQueryVO) {
 		List<Map<String,Object>> rootList = new ArrayList<>();
		List<Map<String, Object>> nameList = myTicketDao.queryRootProcessName(flowQueryVO);
		for (Map<String, Object> map : nameList) {
			String name = map.get("name").toString();
			List<Map<String, Object>> processList = myTicketDao.queryRootUsedProcess(name);
			//启动流程
			if(processList.size()>0){
				Map<String, Object> usedMap = processList.get(0);
				rootList.add(usedMap);
			}else{
				List<Map<String,Object>> process = myTicketDao.queryRootProcess(name);
				if(process.size()>0) {
					rootList.add(process.get(0));
				}
			}
		}
		return rootList;
	}
	
	public Integer getMaxVersion(String processName) {
		Integer version = myTicketDao.getMaxVersion(processName);
		return version;
	}
	
	

	
	
	/**
	 * 获得导出文件的实体信息
	 * @param guids
	 * @param userName
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public List<ProcessExportVO> getProcessExportVO(List<String> guids, String userName) throws UnsupportedEncodingException{
		List<ProcessExportVO> process_list = new ArrayList<>();
		for (String guid : guids) {
			ProcessExportVO processExportVO = new ProcessExportVO();
			MyTicket myTicket = getProcessTicket(userName, guid);
			processExportVO.setTicket(myTicket); //流程基本信息
			String modelId = getOne(guid).getModelId(); //获得model的编号
			if(!StringUtils.isEmpty(modelId)) {   //对于工单2.0没有modelId概念，向上做兼容，老版本处理，
				Model newModel = getProcessModel(modelId);
				processExportVO.setNewModel(newModel); //流程类模型信息
				byte[] modelEditorSource = repositoryService.getModelEditorSource(modelId); //获得流程编辑资源
				processExportVO.setModelEditorSource(modelEditorSource);
				byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(modelId); //获得流程额外编辑资源
				processExportVO.setModelEditorSourceExtra(modelEditorSourceExtra);
			}
			process_list.add(processExportVO);
		}
		return process_list;
		
	}

	/**
	 * 获得导出流程的基本信息
	 * @param userName
	 * @param guid
	 * @return
	 */
	private MyTicket getProcessTicket(String userName, String guid) {
		MyTicket myTicket = getOne(guid);
		MyTicket new_myTicket = mapper.map(myTicket, MyTicket.class);
		new_myTicket.setGuid(UUIDUtils.get32UUID());
		new_myTicket.setCreateTime(new Date());
		new_myTicket.setUpdateTime(new Date());
		new_myTicket.setUpdateUser(userName);
		new_myTicket.setCreateUser(userName);
		new_myTicket.setTicketStatus(ProcessStateEnum.draft);
		new_myTicket.setUsed(false);
		return new_myTicket;
	}

	/**
	 * 获得流程的model
	 * @param modelId
	 */
	private Model getProcessModel(String modelId) {
		if(StringUtils.isNotEmpty(modelId)){
			Model model = repositoryService.getModel(modelId); //获得原model
			Model newModel = repositoryService.newModel();
			newModel.setName(model.getName());
			newModel.setMetaInfo(model.getMetaInfo());
			newModel.setKey(model.getKey());			
			return newModel;
		}
		return null;
	}


	
	/**
	 * 判断是否存在相同类型的流程
	 * @return
	 */
	private Boolean isExistSameTypeProcess(MyTicket ticket){
		String name = ticket.getName(); //流程名称
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", name));
		long count = count(conditions);
		if(count>0){
			return false; //说明存在同类型的流程
		}else {
			return true; //说明没有同类型的流程
		}
	}
	
	/**
	 * 获得流程的最大版本号
	 * @param ticket
	 * @return
	 */
	private Integer getProcessMaxVersion(MyTicket ticket){
		Boolean result = isExistSameTypeProcess(ticket);
		if(result){ //新流程
			return 1;
		}else {
			Integer maxVersion = getMaxVersion(ticket.getName());
			maxVersion+=1;
			return maxVersion;
		}
	}
	
	public Result<Boolean> judgeProcessTaskPeopleisExist(String modelId){
		List<Map<String,Object>> list = modelService.getProcessDealPersonType(modelId);
		for (Map<String, Object> map : list) {
			String candidateType = map.get("candidateType").toString();
			String candidate = map.get("candidate").toString();
			if(candidateType.equals("role")){ //候选者类型为角色
				List<String> role_user_list = authService.getUsersByRole(candidate);
				if(role_user_list.size()==0){
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "当前系统还没有"+candidate+"角色的用户，请检查！");
				}
			}
			if(candidateType.equals("user")){ //候选者为用户
				String[] candidate_user = candidate.split(",");
				for (String userId : candidate_user) { 
					User user = authService.getUserInfoByUserId(userId);
					if(user==null){
						return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "当前系统还没有Id为"+userId+"的用户，请检查！");
					}
				}
			}
		}
		return ResultUtil.success(true);

	}


	public Result<Object>  queryNodeInfos(String processInstanceId){
		ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		BpmnModel model=repositoryService.getBpmnModel(pi.getProcessDefinitionId());
		List<Map<String,Object>>  nodeList=new ArrayList<>();
		List<Map<String,Object>>  sequenceList=new ArrayList<>();
		if(model!=null){
			Collection<FlowElement> flowElements=model.getMainProcess().getFlowElements();
			for(FlowElement flowElement:flowElements){
				logger.info(flowElement.getId() + "," + flowElement.getName() + "," + flowElement.getClass().toString());
				Map<String,Object> nodeMap=new HashMap<>();
				if(!flowElement.getClass().toString().contains("SequenceFlow")){
					nodeMap.put("id",flowElement.getId());
					nodeMap.put("name",flowElement.getName());
					nodeList.add(nodeMap);
				}else{
					SequenceFlow sequenceFlow=(SequenceFlow)flowElement;
					nodeMap.put("sourceNode",sequenceFlow.getSourceRef());
					nodeMap.put("targetNode",((SequenceFlow) flowElement).getTargetRef());
					sequenceList.add(nodeMap);
				}
			}
			nodeList=nodeInfos(nodeList,sequenceList);
		}

		return  ResultUtil.success(nodeList);
	}
	/**
	 * 节点信息结果处理
	 */
	public  List<Map<String,Object>> nodeInfos(List<Map<String,Object>> nodeList,List<Map<String,Object>> sequenceList){
		for(Map<String,Object> map :nodeList ){
			Map<String,Object> nearMap=getNearNode(sequenceList,map.get("id").toString());
			map.putAll(nearMap);
		}
		return  nodeList;

	}

	/**
	 * 获取上或下一节点
	 */
	public  Map<String,Object>  getNearNode(List<Map<String,Object>> sequenceList,String nodeName){
		String sourceNode="";
		String targetNode="";
		Map<String,Object> resultMap=new HashMap<>();
		for(Map<String,Object> map :sequenceList ){
			String sourceName=map.get("sourceNode").toString();
			String targetName=map.get("targetNode").toString();
			if(sourceName.equals(nodeName)){
				targetNode=targetName+",";
			}else{
				if(targetName.equals(nodeName)){
					sourceNode=sourceName+",";
				}
			}

		}
		resultMap.put("sourceNode",sourceNode);
		resultMap.put("targetNode",targetNode);
		return  resultMap;
	}
	/**
	 * 更新工单的名称
	 *
	 * @param oldName oldName
	 * @param newName newName
	 */
	public void updateNameMyTicket(String oldName, String newName){
		myTicketDao.updateNameMyTicket(oldName,newName);
	}



	/**
	 * 生成导出的文件信息
	 * @param guids
	 * @param userName
	 */
	public Result<Boolean> generateProcessExportFile(List<String> guids,String userName){
		String path = fileConfiguration.getFilePathFlow();
		String filePath = Paths.get(path,fileConfiguration.getFileNameFlow()).toString(); //获得文件路径
		String zipFilePath = Paths.get(path, fileConfiguration.getZipNameFlow()).toString(); //获得压缩文件路径
		logger.info("path: " + path);
		createFile(path);
		String processExport_Content = constructProcessData(guids, userName);
		FileUtil.writeFile(processExport_Content, filePath, false); //写入文件
		try {
			FileUtil.zipCompressing(zipFilePath, new File(filePath));
		} catch (Exception e) {
			logger.error("压缩文件失败", e);
		}
		Result<Boolean> result = ResultUtil.success(true);
		return result;
	}
	/**
	 * 构造导出的数据
	 * @param guids
	 * @param userName
	 * @return
	 */
	private String constructProcessData(List<String> guids, String userName) {
		List<ProcessExportVO> list = new ArrayList<>();
		try {
			list = getProcessExportVO(guids, userName);
		} catch (UnsupportedEncodingException e1) {
			logger.error("获得导出数据失败", e1);
		}
		String processExport_Content = JsonMapper.toJsonString(list); //序列化为json对象
		return processExport_Content;
	}

    /**
     * 导入flow文件
     * @param file
     * @return
     * @throws IOException
     */
    public Result<Boolean> importFlowFile(MultipartFile file, String userName) throws IOException{
        byte[] file_bytes = file.getBytes();
        String realPath = fileConfiguration.getFilePathFlow(); // 文件路径
        String fileName = file.getOriginalFilename(); //文件名称(是一个zip压缩包)
        String config_file_name = fileConfiguration.getFileNameFlow();//TODO 文件名称文件名称通过文件上传传入
        String filePath = Paths.get(FilenameUtils.normalize(realPath), FilenameUtils.normalize(fileName)).toString();
        try {
            FileUtil.uploadFile(file_bytes, realPath, fileName); //上传文件
        } catch (Exception e) {
            logger.error("上传文件失败", e);
        }
        String flow_content = FileUtil.readZipContext(filePath, config_file_name);
        //把flow_content转化成对象(进行测试工作)
        List<ProcessExportVOStr> list = new ArrayList<>();
        try {
            list = JsonMapper.fromJsonString2List(flow_content, ProcessExportVOStr.class);
        }catch(Exception e) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "文件解析异常，请检查！");
        }
        for (ProcessExportVOStr processExportVOStr : list) {
            MyTicket ticket = processExportVOStr.getTicket();
            Integer maxOrderNum = getMaxOrderNum();
            if(maxOrderNum==null) { //最大排序为null，则初始化是1
                maxOrderNum=0;
            }
            maxOrderNum+=1;
            ticket.setOrderNum(maxOrderNum);
            ticket.setGuid(UUIDUtils.get32UUID()); //重新设置guid
            ticket.setUpdateUser(userName);
            ticket.setCreateUser("");
            ticket.setCreateTime(new Date());
            ticket.setUpdateTime(new Date());
            Integer processMaxVersion = getProcessMaxVersion(ticket); //获得最大的版本号
            ticket.setTicketVersion(processMaxVersion);
			ModelEntity modelEntity = processExportVOStr.getNewModel();
			if(modelEntity!=null) {  // 老版本
				Model new_model = generateNewModel(processExportVOStr);
				ticket.setModelId(new_model.getId());
			}
			ticket.setDeployId("");
            save(ticket);
        }
        return ResultUtil.success(true);
    }

	/**
	 * 创建导出文件
	 * @param filePath
	 */
	private void createFile(String filePath) {
		File file = new File(filePath);
		if(!file.exists()){ //判断文件路径是否存在
			try {
				Boolean bool=file.mkdirs();
				if(!bool){
					throw new RuntimeException("创建文件失败");
				}
			} catch (Exception e) {
				logger.error("创建文件失败", e);
			}
		}
	}
    /**
     * 产生一个新的Model(老版本)
     * @param processExportVOStr
     * @return
     */
    private Model generateNewModel(ProcessExportVOStr processExportVOStr){
        Model newModel = repositoryService.newModel();
        ModelEntity modelEntity = processExportVOStr.getNewModel();
        newModel.setName(modelEntity.getName());
        newModel.setMetaInfo(modelEntity.getMetaInfo());
        newModel.setKey(modelEntity.getKey());
        repositoryService.saveModel(newModel);
        repositoryService.addModelEditorSource(newModel.getId(), processExportVOStr.getModelEditorSource());
        repositoryService.addModelEditorSourceExtra(newModel.getId(), processExportVOStr.getModelEditorSourceExtra());
        return newModel;
    }

	/**
	 * 通过名称获取流程信息
	 * @param ticketName
	 * @return
	 */
	public Result<MyTicket> getTicketByByName(String ticketName) {
		List<QueryCondition> conditions=new ArrayList<>();
		conditions.add(QueryCondition.eq("name",ticketName));
		conditions.add(QueryCondition.eq("ticketStatus",ProcessStateEnum.used));
		List<MyTicket> ticketList=this.findAll(conditions);
		if(ticketList.size()==1){
			return ResultUtil.success(ticketList.get(0));
		}else if(ticketList.size()>1){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"存在同名工单");
		}else{
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"未查到相关数据");
		}
	}


	/**
	 * 通过配置名称获取流程名称
	 * @param configCode
	 * @return
	 */
	public Result<String> getTicketName(String configCode) {
		if(StringUtils.isEmpty(configCode)){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),configCode+"参数不能为空");
		}
		FlowConfig flowConfig = flowConfigService.getOne(configCode.trim());
		if(null == flowConfig){
			logger.info(configCode+" 没有配置");
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),configCode+"没有找到配置信息");
		}
		return ResultUtil.success(flowConfig.getValue());
	}
}
