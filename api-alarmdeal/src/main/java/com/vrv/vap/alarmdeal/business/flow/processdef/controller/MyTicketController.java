package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.google.common.base.Strings;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessTicketVO;
import com.vrv.vap.alarmdeal.business.flow.monitor.vo.MyTicketFormVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicketPrivildge;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketTemplate;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.ProcessStateEnum;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketPrivildgeService;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.formExcel.impl.AnalysisOneVersionData;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.formExcel.impl.AnalysisTwoVersionData;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FlowQueryVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.TicketVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.ResponseException;
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
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("myTicket")
public class MyTicketController extends BaseController<MyTicket, String> {

	private static Logger logger = LoggerFactory.getLogger(MyTicketService.class);
	@Autowired
	private MyTicketService myTicketService;
	@Autowired
	private MyTicketPrivildgeService myTicketPrivildgeService;

	@Autowired
	private BusinessIntanceService businessIntanceService;
	@Autowired
	private MapperUtil mapper;

	@Autowired
	private TbConfService tbConfService;

	@Autowired
	private AnalysisOneVersionData analysisOneVersionData;

	@Autowired
	private AnalysisTwoVersionData analysisTwoVersionData;

	@Autowired
	private FileConfiguration fileConfiguration;

	@Override
	protected BaseService<MyTicket, String> getService() {
		return myTicketService;
	}


	final String[] DISALLOWED_FIELDS = new String[]{"", "",
			""};

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@Override
	@GetMapping("{guid}")
	@ApiOperation(value="获得对应的工单",notes="")
	@SysRequestLog(description="获得对应的工单", actionType = ActionType.SELECT,manually=false)
	public Result<MyTicket> get(@PathVariable @ApiParam("工单ID")String guid) {
		MyTicket one = myTicketService.getOne(guid);
		List<MyTicketPrivildge> privildge = myTicketPrivildgeService.getByTicketid(one.getGuid());
		String jsonString = JsonMapper.toJsonString(privildge);
		one.setPersonSelect(jsonString);
		return ResultUtil.success(one);
	}

	
	
	/**
	 * 查询可以发起的流程
	 * @param businessTicketVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("queryMyTicket")
	@ApiOperation(value="查询可以发起的流程",notes="")
	@SysRequestLog(description="查询可以发起的流程", actionType = ActionType.SELECT,manually=false)
	public PageRes<MyTicket> page(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq) {
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		pageReq.setCount(count_);
		pageReq.setStart(start_/count_);
		String order_ = businessTicketVO.getOrder_();
		if(StringUtils.isEmpty(order_)){
			order_ = businessTicketVO.getUserId();
		}
		pageReq.setOrder(businessTicketVO.getOrder_());
		pageReq.setBy(businessTicketVO.getBy_());
		MyTicket myTicket = businessTicketVO.getMyTicket();
		Pageable pageable = PageReq.getPageable(pageReq, true);
		List<QueryCondition> cons = new ArrayList<>();
		cons.add(QueryCondition.notEq("ticketStatus", ProcessStateEnum.realDelete));
		if(myTicket==null){
			myTicket = new MyTicket();
		}
		Page<MyTicket> page = myTicketService.findAll(myTicket , cons, pageable);
		return PageRes.toRes(page);
	}
	
	@PostMapping("queryRootTicket")
	@ApiOperation(value="查询根节点的流程",notes="")
	@SysRequestLog(description="查询根节点的流程", actionType = ActionType.SELECT,manually = false)
	public Result<List<Map<String,Object>>> page(@RequestBody FlowQueryVO flowQueryVO) {
		List<Map<String,Object>> list = myTicketService.queryForProcessList(flowQueryVO);
		Result<List<Map<String,Object>>> result = ResultUtil.successList(list);
		return result;
	}
	
	@GetMapping("changeProcessOrder/{beforeProcess}/{afterProcess}")
	@ApiOperation(value="交换流程排序",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="beforeProcess",value="上一个流程",required=true,dataType="String"),
		@ApiImplicitParam(name="afterProcess",value="下一个流程",required=true,dataType="String")
	})
	@SysRequestLog(description="交换流程排序", actionType = ActionType.SELECT,manually = false)
	public Result<Boolean> changeProcessOrder(@PathVariable @ApiParam("上一个流程")String beforeProcess,@PathVariable @ApiParam("下一个流程")String afterProcess) {
		boolean changeProcessOrder = myTicketService.changeProcessOrder(beforeProcess, afterProcess);
		Result<Boolean> result = ResultUtil.success(changeProcessOrder);
		return result;
	}

	@GetMapping("checkName")
	@ApiOperation(value="检查名称",notes="")
	@SysRequestLog(description="检查名称", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> checkName(String id, String name, String version){
		boolean can = myTicketService.canUseName(id, name, version);
		return ResultUtil.success(can);
	}
	
	@PostMapping("addMyTicket")
	@ApiOperation(value="保存工单",notes="")
	@SysRequestLog(description="保存工单", actionType = ActionType.ADD,manually=false)
	public Result<MyTicket> addMyTicket(@RequestBody TicketVO ticketVO, BindingResult bindingResult){
		MyTicket model = getTicketVOInfo(ticketVO);
		model.setTicketStatus(ProcessStateEnum.stoped);
		if(StringUtils.isEmpty(model.getGuid())) {
			model.setGuid(UUIDUtils.get32UUID());
		}
		Integer maxOrderNum = myTicketService.getMaxOrderNum();
		if(maxOrderNum==null) { //最大排序为null，则初始化是1
			maxOrderNum=0;
		}
		maxOrderNum+=1;
		Integer ticketVersion = model.getTicketVersion();
		if(ticketVersion==null) {
			model.setTicketVersion(1);
		}
		model.setOrderNum(maxOrderNum);
		MyTicket save = myTicketService.saveWithPrivildge(model);
		return ResultUtil.success(save);
	}

	private MyTicket getTicketVOInfo(TicketVO ticketVO) {
		MyTicket model = ticketVO.getMyTicket();
		String userName = ticketVO.getUserName();
		model.setCreateTime(new Date());
		model.setCreateUser(userName);
		model.setUpdateUser(model.getCreateUser());
		model.setUpdateTime(model.getCreateTime());
		model.setUsed(false);
		if(StringUtils.isEmpty(model.getFlowContent())){
			model.setFlowContent(null);
		}
		return model;
	}
	
	@PostMapping("saveDraft")
	@ApiOperation(value="保存工单草稿",notes="")
	@SysRequestLog(description="保存工单草稿", actionType = ActionType.ADD,manually = false)
	public Result<MyTicket> saveDraft(@RequestBody TicketVO ticketVO, BindingResult bindingResult){
		MyTicket model = getTicketVOInfo(ticketVO);
		// 工单2.0针对保存草稿模块为空的处理   2022-06-23
		myticketTemplateHandle(model);
		model.setTicketStatus(ProcessStateEnum.draft);
		if(StringUtils.isEmpty(model.getGuid())) {
			model.setGuid(UUIDUtils.get32UUID());
		}
		Integer ticketVersion = model.getTicketVersion();
		if(ticketVersion==null) {
			model.setTicketVersion(1);
		}
		Integer maxOrderNum = myTicketService.getMaxOrderNum();
		maxOrderNum+=1;
		model.setOrderNum(maxOrderNum);
		MyTicket save = myTicketService.saveWithPrivildge(model);
		return ResultUtil.success(save);
	}

	/**
	 * 工单2.0针对保存草稿模块为空的处理
	 * 例子：
	 * "template": {
	 * 				"guid": null,
	 * 				"deleteFlag": false,
	 * 				"name": "",
	 * 				"formData": "{\"formInfos\":null}"
	 *                        }
	 * 以前的空模板：
	 * "template": null
	 * 2022-06-23
	 * @param model
	 */
	private void myticketTemplateHandle(MyTicket model) {
		MyticketTemplate template = model.getForminfo().getTemplate();
		if(null == template){   // 历史不处理
           return;
		}
		String guid = template.getGuid();
		if(StringUtils.isEmpty(guid)){
			model.getForminfo().setTemplate(null);  //设置为null
		}
	}


	@PostMapping("editMyTicket")
	@ApiOperation(value="编辑工单",notes="")
	@SysRequestLog(description="编辑工单", actionType = ActionType.UPDATE,manually = false)
	public Result<MyTicket> editMyTicket(@RequestBody TicketVO ticketVO, BindingResult bindingResult) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		MyTicket model = ticketVO.getMyTicket();
		String userName = ticketVO.getUserName();
		model.setUpdateUser(userName);
		model.setUpdateTime(new Date());
		model.setCreateTime(new Date());
		model.setCreateUser(userName);
		model.setTicketStatus(model.getTicketStatus().save());
		 myTicketService.saveWithPrivildge(model);
		return super.edit(model, bindingResult);
	}
	
	@GetMapping("stop/{id}")
	@ApiOperation(value="停止工单",notes="")
	@SysRequestLog(description="停止工单", actionType = ActionType.DELETE,manually = false)
	public Result<MyTicket> stop(@PathVariable String id){
		if(Strings.isNullOrEmpty(id)) {
			return ResultUtil.error(1000, "参数错误，不能为空");
		}
		MyTicket one = myTicketService.getOne(id);
		if(one != null) {
			one.setTicketStatus(one.getTicketStatus().disable());
			myTicketService.save(one);
			return ResultUtil.success(one);
		} else {
			return ResultUtil.error(1004, "数据不存在");
		}
	}
	
	@GetMapping("start/{id}")
	@ApiOperation(value="开启工单",notes="")
	@SysRequestLog(description="开启工单", actionType = ActionType.UPDATE,manually = false)
	public Result<MyTicket> start(@PathVariable String id){
		if(Strings.isNullOrEmpty(id)) {
			return ResultUtil.error(1000, "参数错误，不能为空");
		}
		MyTicket one = myTicketService.getOne(id);
		if(one != null) {
			MyTicket ticket = myTicketService.start(one);
			
			return ResultUtil.success(ticket);
		} else {
			return ResultUtil.error(1004, "数据不存在");
		}
	}
	
	@GetMapping("getAllStarted")
	@ApiOperation(value="查询现有流程",notes="")
	@SysRequestLog(description="查询现有流程", actionType = ActionType.SELECT,manually = false)
	public Result<List<MyTicket>> getAllStarted(HttpServletRequest request){
		String name = request.getParameter("name");
		String userId = request.getParameter("userId");
		String processDefName = request.getParameter("processDefName");
		List<MyTicket> findAll = myTicketService.getAllStartedByName(name,userId,processDefName);
		TbConf tbConf=tbConfService.getOne("ticket_no_show");
		if(tbConf!=null){
			logger.info("tbConf：{}",JsonMapper.toJsonString(tbConf));
			String ticketNames=tbConf.getValue();
			logger.info("ticketNames：{}",ticketNames);
			Iterator<MyTicket> iterator = findAll.iterator();
			while(iterator.hasNext()){
				MyTicket myTicket = iterator.next();
				if(ticketNames.contains(myTicket.getName())){
					iterator.remove();
				}
			}
		}
		return ResultUtil.success(findAll);
	}
	
	@PostMapping("copy")
	@ApiOperation(value="复制流程实例",notes="")
	@SysRequestLog(description="复制流程实例", actionType = ActionType.ADD,manually = false)
	public Result<MyTicketFormVO> copy(@RequestBody TicketVO ticketVO){
		String id = ticketVO.getId();
		String userName = ticketVO.getUserName();
		String newVersion = ticketVO.getNewVersion();
		MyTicket one = myTicketService.getOne(id);
		if(one == null) {
			throw new ResponseException(1100, "流程实例不存在，无法进行复制");
		}
		MyTicket map = myTicketService.copyOne(newVersion, one,userName);
		MyTicketFormVO myTicketFormVO =  mapper.map(map, MyTicketFormVO.class);
		myTicketFormVO.setForm_data(map.getForminfo().getFormData());
		myTicketFormVO.setForm_type(map.getForminfo().getFormType());
		return ResultUtil.success(myTicketFormVO);
	}
	@GetMapping("getMaxVersion/{processName}")
	@ApiOperation(value="获得最大版本号",notes="已经完成加1操作")
	@ApiImplicitParams({
		@ApiImplicitParam(name="processName",value="流程名称",required=true,dataType="String")
	})
	@SysRequestLog(description="获得最大版本号", actionType = ActionType.SELECT,manually = false)
	public Result<Integer> getMaxVersion(@PathVariable String processName){
		Integer maxVersion = myTicketService.getMaxVersion(processName);
		maxVersion+=1;
		Result<Integer> result = ResultUtil.success(maxVersion);
		return result;
	}
	
	@ApiOperation(value="获得正在启用的流程",notes="获得正在启用的流程")
	@PostMapping("queryUsedTicket")
	@SysRequestLog(description="获得正在启用的流程", actionType = ActionType.SELECT,manually = false)
	public PageRes<MyTicket> queryUsedTicket(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq) {
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		pageReq.setCount(count_);
		pageReq.setStart(start_/count_);
		pageReq.setOrder(businessTicketVO.getOrder_());
		pageReq.setBy(businessTicketVO.getBy_());
		MyTicket myTicket = businessTicketVO.getMyTicket();
		Pageable pageable = PageReq.getPageable(pageReq, true);
		List<QueryCondition> cons = new ArrayList<>();
		cons.add(QueryCondition.eq("ticketStatus", ProcessStateEnum.used));
		if(myTicket==null){
			myTicket = new MyTicket();
		}
		Page<MyTicket> page = myTicketService.findAll(myTicket , cons, pageable);
		return PageRes.toRes(page);
	}

	
	/**
     * 判断各个节点配置的人是否存在
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("judgeProcessTaskPeopleisExist/{id}")
    @ApiOperation(value="发布流程的时候判断节点处理人是否存在",notes="")
	@SysRequestLog(description="发布流程的时候判断节点处理人是否存在", actionType = ActionType.SELECT,manually = false)
    public Result<Boolean> judgeProcessTaskPeopleisExist(@PathVariable("id")String id) throws Exception {
    	Result<Boolean> result = myTicketService.judgeProcessTaskPeopleisExist(id);
        return result;
    }

	/**
	 * 获取流程节点集合
	 */
	@GetMapping("queryNodeInfos")
	@ApiOperation(value="节点信息集合",notes="")
	@SysRequestLog(description="节点信息集合", actionType = ActionType.SELECT,manually = false)
	public  Result<Object> queryNodeInfos(String processInstanceId){
		BusinessIntance businessIntance=businessIntanceService.getByInstanceId(processInstanceId);
		if(businessIntance!=null){
			return myTicketService.queryNodeInfos(processInstanceId);
		}else{
			return ResultUtil.error(4002,"未查到实例");
		}
	}


	/**
	 * 通过工单名查询工单
	 */
	@GetMapping("getTicketByByName/{ticketName}")
	@ApiOperation("通过工单名查询工单")
	@SysRequestLog(description="通过工单名查询工单", actionType = ActionType.SELECT,manually = false)
	public Result<MyTicket> getTicketByByName(@PathVariable("ticketName") String ticketName){
		return myTicketService.getTicketByByName(ticketName);
	}

	/**
	 * 获取工单名称
	 *  工单名称做了一个配置，通过一个配置的code获取对应工单名称
	 *
	 *  2022-10-13
	 */
	@GetMapping("getTicketName/{configCode}")
	@ApiOperation("获取工单名称")
	@SysRequestLog(description="获取工单名称", actionType = ActionType.SELECT,manually = false)
	public Result<String> getTicketName(@PathVariable("configCode") String configName){
		return myTicketService.getTicketName(configName);
	}

	/**
	 * 更改工单名称
	 */
	@PostMapping("updateTicketName")
	@ApiOperation("更改工单名称")
	@SysRequestLog(description="更改工单名称", actionType = ActionType.UPDATE,manually = false)
	public Result<Boolean> updateTicketName(@RequestBody Map<String,String> params) {
		if(null == params){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "参数不能为空");
		}
		String  oldTicketName = params.get("ticketName");
		String  newTicketName = params.get("newTicketName");
		if (StringUtils.isEmpty(oldTicketName)) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "更改前名称不能为空");
		}
		if (StringUtils.isEmpty(newTicketName)) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "更改后名称不能为空");
		}
		try{
			myTicketService.updateNameMyTicket(oldTicketName, newTicketName);
			return ResultUtil.success(true);
		}catch (Exception e){
			logger.error("更改工单名称异常",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"更改工单名称异常");
		}


	}

	@ApiOperation(value="产生流程导出文件",notes="产生流程导出文件")
	@PostMapping("generateProcessExportFile")
	@ApiImplicitParams({
			@ApiImplicitParam(name="guids",value="流程guids",required=true,dataType="List<String>"),
			@ApiImplicitParam(name="userName",value="当前登陆用户",required=true,dataType="String")
	})
	@SysRequestLog(description="产生流程导出文件", actionType = ActionType.EXPORT,manually = false)
	public Result<Boolean> generateProcessExportFile(@RequestBody Map<String,Object> map){
		Object guids_obj = map.get("guids"); //流程guids
		Object userName_obj = map.get("userName"); //当前登陆用户名
		if(guids_obj!=null&&userName_obj!=null) {
			List<String> guids = (List<String>)guids_obj;
			String userName = userName_obj.toString();
			Result<Boolean> result = myTicketService.generateProcessExportFile(guids, userName);
			return result;
		}else {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "产生流程文件失败");
		}
	}

	/**
	 * 导出文件前端浏览器下载
	 * @param response
	 */
	@ApiOperation(value="导出流程文件",notes="导出流程文件")
	@GetMapping(value="/exportFlowFile")
	@SysRequestLog(description="导出流程文件", actionType = ActionType.EXPORT,manually = false)
	public void exportFlowFile(HttpServletResponse response) {
		String realPath = fileConfiguration.getFilePathFlow(); // 文件路径
		String fileName = fileConfiguration.getZipNameFlow();
		FileUtil.downLoadFile(fileName, realPath, response);
	}

    /**
     * 导入文件信息
     * @param file
     * @return
     */
    @PostMapping(value="/importFlowFile")
    @ApiOperation(value="导入流程文件信息",notes="")
    @SysRequestLog(description="导入流程文件信息", actionType = ActionType.IMPORT,manually = false)
    public  Result<Boolean> importFlowFile(@RequestParam("file") MultipartFile file, @RequestParam("userName") String userName){
        Result<Boolean> result = null;
        try {
            result = myTicketService.importFlowFile(file,userName);
            return result;
        } catch (IOException e) {
            logger.error("导入流程文件失败", e);
            throw new RuntimeException("导入流程文件失败");
        }
    }

	@GetMapping("exportExcel/{processDefName}")
	@ApiOperation("根据流程名导出工单监控数据")
	@SysRequestLog(description="根据流程名导出工单监控数据", actionType = ActionType.EXPORT,manually = false)
	public Result<String> exportExcel(HttpServletResponse response,@PathVariable("processDefName") String processDefName){
		if(org.springframework.util.StringUtils.isEmpty(processDefName)){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"流程名称不能为空！");
		}
		Result<String> result= businessIntanceService.exprotExcel(processDefName.trim());
		return result;
	}

	@GetMapping("/downFormExcel/{fileName:.+}")
	@ApiOperation("下载工单表单数据导出文件")
	@SysRequestLog(description="表单数据导出", actionType = ActionType.EXPORT,manually = false)
	public void  downFormExcel(HttpServletResponse response,@PathVariable("fileName") String fileName){
		FileUtil.downLoadFile(fileName, fileConfiguration.getFilePathFlow(), response);
	}


 	
}
