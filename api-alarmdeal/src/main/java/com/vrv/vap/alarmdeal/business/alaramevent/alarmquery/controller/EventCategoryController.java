package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.ThreatLibraryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventColumService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.*;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(description="告警分类")
@RestController
@RequestMapping("/eventCategory")
public class EventCategoryController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(EventCategoryController.class);
	@Autowired
	private MapperUtil mapper;
	@Autowired
	private EventCategoryService eventCategoryService;
	@Autowired
	private EventTabelService eventTabelService;
	@Autowired
	private EventColumService eventColumService;
	@Autowired
	private FileConfiguration fileConfiguration;

	
	/**
	 * 获得事件分类树
	 * @return
	 */
	@GetMapping("/getEventCategoryTree")
	@ApiOperation(value="获得事件分类树",notes="")
	@SysRequestLog(description = "告警分类-获得事件分类树", actionType = ActionType.SELECT, manually = false)
	public List<EventCategoryVRVTreeVO>  getEventCategoryTree(){
		List<EventCategoryVRVTreeVO> tree = eventCategoryService.getEventCateTree();
		return tree;
	}

	/**
	 * 事件表树
	 * @param tableName
	 * @return
	 */
	@GetMapping("/getEventTableTree/{tableName}")
	@ApiOperation(value="事件表树",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="tableName",value="告警类型表名",required=true,dataType="String")
	})
	@SysRequestLog(description = "告警分类-事件表树", actionType = ActionType.SELECT, manually = false)
	public List<EventTableTreeVO>  getEventTableTree(@PathVariable("tableName") String tableName){
		List<EventTableTreeVO> tree = eventTabelService.getEventTableTree(tableName);
		return tree;
	}

	/**
	 * 增加事件分类
	 * @param eventCategoryVO
	 * @return
	 */
	@PostMapping("/addEventCategory")
	@ApiOperation(value="增加事件分类",notes="")
	@SysRequestLog(description = "告警分类-增加事件分类", actionType = ActionType.ADD, manually = false)
	public Result<Boolean> addEventCategory(@RequestBody EventCategoryVO eventCategoryVO){
		EventCategory eventCategory = mapper.map(eventCategoryVO, EventCategory.class);
		Result<Boolean> result = eventCategoryService.addEventCategory(eventCategory);
		return result;
	}

	/**
	 * 获取一级事件分类列表
	 */
	@GetMapping("getCategoryTopLevel")
	@ApiOperation(value="获取一级事件分类列表",notes="")
	@SysRequestLog(description = "告警分类-获取一级事件分类列表", actionType = ActionType.SELECT, manually = false)
	public  Result<List<EventCategory>> getCategoryTopLevel(){
		List<QueryCondition> conditions=new ArrayList<>();
		conditions.add(QueryCondition.eq("parentId","0"));
		List<EventCategory> list=eventCategoryService.findAll(conditions);
		return  ResultUtil.success(list);
	}



	/**
	 * 编辑事件分类
	 * @param eventCategoryVO
	 * @return
	 */
	@PostMapping("/editEventCategory")
	@ApiOperation(value="编辑事件分类",notes="")
	@SysRequestLog(description = "告警分类-编辑事件分类", actionType = ActionType.UPDATE, manually = false)
	public Result<EventCategory> editEventCategory(@RequestBody EventCategoryVO eventCategoryVO){
		EventCategory eventCategory = mapper.map(eventCategoryVO, EventCategory.class);
		Result<EventCategory> result = eventCategoryService.editEventCategory(eventCategory);
		return result;
	}
	
	/**
	 * 删除事件分类
	 * @param guid
	 * @return
	 */
	@GetMapping("/delEventCategory/{guid}")
	@ApiOperation(value="删除事件分类",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="guid",value="告警类型guid",required=true,dataType="String")
	})
	@SysRequestLog(description = "告警分类-删除事件分类", actionType = ActionType.DELETE, manually = false)
	public Result<Boolean> delEventCategory(@PathVariable("guid") String guid){
		Result<Boolean> result = new Result<>();
		try {
			eventCategoryService.delete(guid);
		}catch(Exception e){
			result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
			result.setData(false);
			result.setMsg(e.getMessage());
			return result;
		}
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}
	
	/**
	 * 获得事件分类
	 * @param guid
	 * @return
	 */
	@GetMapping("/getEventCategory/{guid}")
	@ApiOperation(value="获得事件分类",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="guid",value="告警类型guid",required=true,dataType="String")
	})
	@SysRequestLog(description = "告警分类-获得事件分类", actionType = ActionType.SELECT, manually = false)
	public Result<EventCategory> getEventCategoryInfo(@PathVariable("guid") String guid){
		Result<EventCategory> result = new Result<>();
		EventCategory eventCategory = eventCategoryService.getOne(guid);
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(eventCategory);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}

	@PostMapping("/eventTable")
	@ApiOperation(value="获得事件表分页列表",notes="")
	@SysRequestLog(description = "告警分类-获得事件表分页列表", actionType = ActionType.SELECT, manually = false)
	public PageRes<EventTable> getEventCategoryInfo(@RequestBody EventTableVO eventTableVO ,PageReq pageReq){
		 pageReq.setCount(eventTableVO.getCount_());
		 pageReq.setStart(eventTableVO.getStart_());
		 pageReq.setOrder(eventTableVO.getOrder_());
		 PageRes<EventTable> pageRes = eventTabelService.getEventTablePager(eventTableVO, pageReq.getPageable());
		 return pageRes;
	}

	/**
	 * 事件表展示用   2023-09-22
	 * @param eventTableVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("/eventTableCustom")
	@ApiOperation(value="获得事件表分页列表",notes="")
	@SysRequestLog(description = "告警分类-获得事件表分页列表", actionType = ActionType.SELECT, manually = false)
	public PageRes<EventTable> getEventTableCurPager(@RequestBody EventTableVO eventTableVO ,PageReq pageReq){
		pageReq.setCount(eventTableVO.getCount_());
		pageReq.setStart(eventTableVO.getStart_());
		pageReq.setOrder(eventTableVO.getOrder_());
		PageRes<EventTable> pageRes = eventTabelService.getEventTableCurPager(eventTableVO, pageReq.getPageable());
		return pageRes;
	}

	@PostMapping("/baseLineData")
	@ApiOperation(value="获得事件表分页列表",notes="")
	@SysRequestLog(description = "告警分类-获得基线表分页列表", actionType = ActionType.SELECT, manually = false)
	public PageRes<EventTable> getBaseLineData(@RequestBody EventTableVO eventTableVO ,PageReq pageReq){
		pageReq.setCount(eventTableVO.getCount_());
		pageReq.setStart(eventTableVO.getStart_());
		pageReq.setOrder(eventTableVO.getOrder_());
		PageRes<EventTable> pageRes = eventTabelService.getBaseLinePager(eventTableVO, pageReq.getPageable());
		return pageRes;
	}

	@PostMapping("/getEventCategoryPager")
	@ApiOperation(value="获得事件分类分页列表",notes="")
	@SysRequestLog(description = "告警分类-获得事件分类分页列表", actionType = ActionType.SELECT, manually = false)
	public PageRes<EventCategoryVO> getEventCategoryPager(@RequestBody EventCategoryVO eventCategoryVO ,PageReq pageReq){
		 pageReq.setCount(eventCategoryVO.getCount_());
		 pageReq.setStart(eventCategoryVO.getStart_());
		 pageReq.setOrder(eventCategoryVO.getOrder_());
		 pageReq.setBy(eventCategoryVO.getBy_());
		 PageRes<EventCategoryVO> pageRes = eventCategoryService.getEventCategoryPager(eventCategoryVO, pageReq.getPageable());
		 return pageRes;
	}
	
	@PutMapping("/eventTableCustom")
	@ApiOperation(value="新增事件表")
	@SysRequestLog(description = "告警分类-新增数据源表", actionType = ActionType.ADD, manually = false)
	public Result<EventTable> saveEventTable(@RequestBody EventTableVO eventTableVO){
		Result<EventTable> result = eventTabelService.saveEventTable(eventTableVO);
		return result;
	}
	
	
	@PatchMapping("/eventTableCustom")
	@ApiOperation(value="编辑事件表")
	@SysRequestLog(description = "告警分类-编辑事件表", actionType = ActionType.UPDATE, manually = false)
	public Result<EventTable> editEventTable(@RequestBody EventTableVO eventTableVO){
		Result<EventTable> result = eventTabelService.editEventTable(eventTableVO);
		return result;
	}
	
	@DeleteMapping("/eventTableCustom")
	@ApiOperation(value="删除事件表")
	@SysRequestLog(description = "告警分类-删除事件表", actionType = ActionType.DELETE, manually = false)
	public Result<Boolean> delEventTable(@RequestBody Map<String,Object> map){
		String ids=map.get("ids").toString();
		String[] idsArray = ids.split(",");
		for (String id : idsArray) {
			eventTabelService.delEventTable(id);			
		}
		return ResultUtil.success(true);
	}
	
	/**
	 * 获得对应对应的eventtable列字段
	 * 
	 * @param guid
	 * @return
	 */
	@GetMapping("/getEventColumnCurr/{guid}")
	@ApiOperation(value="根据告警表Id告警名称列",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="guid",value="告警表ID",required=true,dataType="String")
	})
	@SysRequestLog(description = "告警分类-根据告警表Id告警名称列", actionType = ActionType.SELECT, manually = false)
	public Result<List<EventColumn>> getEventColumnCurr(@PathVariable("guid") String guid) {
		 List<EventColumn> list = eventColumService.getEventColumnCurr(guid);  
//		 Collections.sort(list, Comparator.comparing(EventColumn::getOrder));
		 return ResultUtil.successList(list);
	}

	/**
	 * 通过eventTableId获取对应的EventColumn数据
	 *  2023-09-22
	 * @param guid
	 * @return
	 */
	@GetMapping("/getEventColumns/{guid}")
	@ApiOperation(value="通过eventTableId获取对应的EventColumn数据列",notes="")
	@SysRequestLog(description = "通过eventTableId获取对应的EventColumn数据列", actionType = ActionType.SELECT, manually = false)
	public Result<List<EventColumn>> getEventColumnByEventTableId(@PathVariable("guid") String guid) {
		List<EventColumn> list = eventColumService.getEventColumnByEventTableId(guid);
		Result<List<EventColumn>> result = ResultUtil.successList(list);
		return result;
	}



	@PutMapping("/eventColumn")
	@ApiOperation(value="新增事件列")
	@SysRequestLog(description = "告警分类-新增事件列", actionType = ActionType.ADD, manually = false)
	public Result<EventColumn> saveEventColumn(@RequestBody EventColumnVO eventColumnVO){
		Result<EventColumn> result = eventColumService.saveEventColumns(eventColumnVO);
		return result;
	}
	
	
	@PatchMapping("/eventColumn")
	@ApiOperation(value="编辑事件列")
	@SysRequestLog(description = "告警分类-编辑事件列", actionType = ActionType.UPDATE, manually = false)
	public Result<EventColumn> editEventColumns(@RequestBody EventColumnVO eventColumnVO){
		Result<EventColumn> result = eventColumService.editEventColumns(eventColumnVO);
		return result;
	}
	
	@DeleteMapping("/eventColumn")
	@ApiOperation(value="删除事件列")
	@SysRequestLog(description = "告警分类-删除事件列", actionType = ActionType.DELETE, manually = false)
	public Result<Boolean> delEventColumns(@RequestBody Map<String,Object> map){
		String ids=map.get("ids").toString();
		String[] idsArray = ids.split(",");
		for (String id : idsArray) {
			eventColumService.delEventColumns(id);			
		}
		return ResultUtil.success(true);
	}
	
	
	
	
	@GetMapping("/getEventCategoryTreeByEventCode")
	@ApiOperation(value="根据事件编号获得对应的事件分类树",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="eventCode",value="告警事件编号",required=true,dataType="String")
	})
	@SysRequestLog(description = "告警分类-根据事件编号获得对应的事件分类树", actionType = ActionType.SELECT, manually = false)
	public Result<List<EventCategoryVRVTreeVO>> getEventCategoryTreeByEventCode(HttpServletRequest request){
		String eventCode  = request.getParameter("eventCode");
		List<EventCategoryVRVTreeVO> list = eventCategoryService.getEventCategoryTreeByEventCode(eventCode);
		Result<List<EventCategoryVRVTreeVO>> result = ResultUtil.success(list);
		 return result;
	}
	
	
	@PostMapping("/judgeIsExistRepeatColumn")
	@ApiOperation(value="判断字段是否重复",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="eventTableId",value="事件表ID",dataType="String"),
		@ApiImplicitParam(name="field",value="对应字段",dataType="String")
	})
	@SysRequestLog(description = "告警分类-判断字段是否重复", actionType = ActionType.SELECT, manually = false)
	public  Result<Boolean> judgeIsExistRepeatColumn(@RequestBody Map<String,Object> map){
		 String eventTableId = map.get("eventTableId").toString();
		 String field = map.get("field").toString();
		 Result<Boolean> result = eventColumService.judgeIsExistRepeatColumn(eventTableId, field);
		 return result;
	}
	
	@PostMapping("/getAllEventTable")
	@ApiOperation(value="获得所有的eventtable表",notes="")
	@SysRequestLog(description = "告警分类-获得所有的eventtable表", actionType = ActionType.SELECT, manually = false)
	public Result<List<EventTable>> getAllEventTable(){
		Result<List<EventTable>> result = eventTabelService.getAllEventTable();
		return result;
	}
	
	@GetMapping("/checkEventTableIsExistTimeField/{guid}")
	@ApiOperation(value="检查事件表是否存在时间字段",notes="")
	@SysRequestLog(description = "告警分类-检查事件表是否存在时间字段", actionType = ActionType.SELECT, manually = false)
	public Result<Boolean> checkEventTableIsExistTimeField(@PathVariable String guid){
		Result<Boolean> result = eventColumService.checkEventTableIsExistTimeField(guid);
		return result;
	}
	
	
	
	@PostMapping(value = "/exportThreatLibraryInfo")
	@ApiOperation(value="导出威胁库信息",notes="")
	@SysRequestLog(description = "告警分类-导出威胁库信息", actionType = ActionType.EXPORT, manually = false)
	public Result<Boolean> exportThreatLibraryInfo(@RequestBody ThreatLibraryVO threatLibraryVO) {
		 Result<Boolean> result = eventCategoryService.exportThreatLibrary(threatLibraryVO);
		 return result;
	}
	
	/**
	 * 导出文件前端浏览器下载
	 * @param response
	 */
	@ApiOperation(value="导出威胁库文件前端浏览器下载",notes="导出威胁库文件前端浏览器下载")
	@GetMapping(value="/exportThreatLibraryFile")
	@SysRequestLog(description = "告警分类-导出威胁库文件前端浏览器下载", actionType = ActionType.EXPORT, manually = false)
	public void exportFlowFile(HttpServletResponse response) {
		String realPath = fileConfiguration.getFilePath(); // 文件路径
		String fileName = fileConfiguration.getThreatLibraryName(); //威胁库路径
		FileUtil.downLoadFile(fileName, realPath, response);
	}
	
	
	/**
	 * 导入文件信息
	 * @param file
	 * @return
	 */
	@PostMapping(value="/importThreatLibraryFile")
	@ApiOperation(value="导入威胁库文件信息",notes="")
	@SysRequestLog(description = "告警分类-导入威胁库文件信息", actionType = ActionType.IMPORT, manually = false)
	public  Result<Boolean> importFlowFile(@RequestParam("file") CommonsMultipartFile file){
		Result<Boolean> result = null;
		try {
			result = eventCategoryService.importThreatLibraryInfo(file);
			return result;
		} catch (IOException e) {
			logger.error("导入威胁库文件失败", e);
			throw new RuntimeException("导入威胁库文件失败");
		}
	}


	/**
	 * 发布eventtable
	 */
	@PostMapping(value="/publishEvevtTable")
	@ApiOperation(value="发布eventtable",notes="")
	@SysRequestLog(description = "告警分类-发布eventtable", actionType = ActionType.UPDATE, manually = false)
	public  Result<EventTable> publishEvevtTable(@RequestBody EventTableVO eventTableVO ){
		Result<EventTable> result=eventTabelService.addVersion(eventTableVO.getId());
		return result;

	}







}
