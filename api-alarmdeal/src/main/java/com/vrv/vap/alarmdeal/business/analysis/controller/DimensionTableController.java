package com.vrv.vap.alarmdeal.business.analysis.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.DimensionFieldsVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.DimensionTableVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableFieldService;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableService;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@Api(description="维表配置接口")
@RestController
@RequestMapping("/dimension")
public class DimensionTableController extends BaseController {

	
	private static Logger logger = LoggerFactory.getLogger(DimensionTableController.class);
	
	@Autowired
	private DimensionTableService dimensionTableService;
	@Autowired
	private DimensionTableFieldService dimensionTableFieldService;
	@Autowired
	private MapperUtil mapper;
	
	
	@PostMapping("/dimensionTable")
	@ApiOperation(value="获得维表分页列表",notes="")
	@SysRequestLog(description = "获得维表分页列表", actionType = ActionType.SELECT, manually = false)
	public PageRes<DimensionTableInfo> getEventCategoryInfo(@RequestBody DimensionTableVO dimensionTableVO , PageReq pageReq){
		 pageReq.setCount(dimensionTableVO.getCount_());
		 pageReq.setStart(dimensionTableVO.getStart_());
		 pageReq.setOrder(dimensionTableVO.getOrder_());
		 PageRes<DimensionTableInfo> pageRes = dimensionTableService.getDimensionTableInfoPager(dimensionTableVO, pageReq.getPageable());
		 return pageRes;
	}
	
	@PutMapping("/dimensionTable")
	@ApiOperation(value="新增维表")
	@SysRequestLog(description = "新增维表", actionType = ActionType.ADD, manually = false)
	public Result<DimensionTableInfo> saveDimensionTable(@RequestBody DimensionTableVO dimensionTableVO){
		DimensionTableInfo dimensionTableInfo = mapper.map(dimensionTableVO, DimensionTableInfo.class);
		dimensionTableInfo.setGuid(UUIDUtils.get32UUID());
		dimensionTableInfo.setCreateTime(new Date());
		if(dimensionTableInfo.getDays() == 0){
			dimensionTableInfo.setDays(1);
		}
		DimensionTableInfo dimensionTableInfos = dimensionTableService.save(dimensionTableInfo);
		return ResultUtil.success(dimensionTableInfos);
	}
	
	
	@PatchMapping("/dimensionTable")
	@ApiOperation(value="编辑维表")
	@SysRequestLog(description = "编辑维表", actionType = ActionType.UPDATE, manually = false)
	public Result<DimensionTableInfo> editDimensionTable(@RequestBody DimensionTableVO dimensionTableVO){
		String guid = dimensionTableVO.getGuid();
		DimensionTableInfo dimensionTableInfo = dimensionTableService.getOne(guid);
		mapper.copy(dimensionTableVO, dimensionTableInfo);
		DimensionTableInfo dimensionTableInfo2 = dimensionTableService.save(dimensionTableInfo);
		return ResultUtil.success(dimensionTableInfo2);
	}
	
	@DeleteMapping("/dimensionTable")
	@ApiOperation(value="删除维表")
	@SysRequestLog(description = "删除维表", actionType = ActionType.DELETE, manually = false)
	public Result<Boolean> delDimensionTable(@RequestBody Map<String,Object> map){
		String ids=map.get("ids").toString();
		String[] idsArray = ids.split(",");
		for (String id : idsArray) {
			dimensionTableService.deleteDimensionById(id);
		}
		return ResultUtil.success(true);
	}
	
	
	
	
	@PostMapping("/dimensionTableField")
	@ApiOperation(value="获得维表字段表分页",notes="")
	@SysRequestLog(description = "获得维表字段表分页", actionType = ActionType.SELECT, manually = false)
	public PageRes<DimensionTableField> getDimensionTableField(@RequestBody DimensionFieldsVO dimensionFieldsVO , PageReq pageReq){
		 pageReq.setCount(dimensionFieldsVO.getCount_());
		 pageReq.setStart(dimensionFieldsVO.getStart_());
		 pageReq.setOrder(dimensionFieldsVO.getOrder_());
		 PageRes<DimensionTableField> pageRes = dimensionTableFieldService.getDimensionTableFieldPager(dimensionFieldsVO, pageReq.getPageable());
		 return pageRes;
	}
	
	
	@PutMapping("/dimensionTableField")
	@ApiOperation(value="新增维表字段")
	@SysRequestLog(description = "新增维表字段", actionType = ActionType.ADD, manually = false)
	public Result<DimensionTableField> saveDimensionTableField(@RequestBody DimensionFieldsVO dimensionFieldsVO){
		DimensionTableField dimensionTableField = mapper.map(dimensionFieldsVO, DimensionTableField.class);
		dimensionTableField.setGuid(UUIDUtils.get32UUID());
		DimensionTableField dimensionTableField2 = dimensionTableFieldService.save(dimensionTableField);
		Result<DimensionTableField> result = ResultUtil.success(dimensionTableField2);
		return result;
	}
	
	
	@PatchMapping("/dimensionTableField")
	@ApiOperation(value="编辑维表字段")
	@SysRequestLog(description = "编辑维表字段", actionType = ActionType.UPDATE, manually = false)
	public Result<DimensionTableField> editDimensionTableFields(@RequestBody DimensionFieldsVO dimensionFieldsVO){
		String guid = dimensionFieldsVO.getGuid();
		DimensionTableField dimensionTableField = dimensionTableFieldService.getOne(guid);
		mapper.copy(dimensionFieldsVO, dimensionTableField);
		dimensionTableField = dimensionTableFieldService.save(dimensionTableField);
		return ResultUtil.success(dimensionTableField);
	}
	
	@DeleteMapping("/dimensionTableField")
	@ApiOperation(value="删除维表字段")
	@SysRequestLog(description = "删除维表字段", actionType = ActionType.DELETE, manually = false)
	public Result<Boolean> delEventColumns(@RequestBody Map<String,Object> map){
		String ids=map.get("ids").toString();
		String[] idsArray = ids.split(",");
		for (String id : idsArray) {
			dimensionTableFieldService.delete(id);	
		}
		return ResultUtil.success(true);
	}
	
	
	@PostMapping("/judgeIsExistRepeatField")
	@ApiOperation(value="判断维表字段表是否重复",notes="")
	@SysRequestLog(description = "判断维表字段表是否重复", actionType = ActionType.SELECT, manually = false)
	public Result<Boolean> judgeIsExistRepeatField(@RequestBody DimensionFieldsVO dimensionFieldsVO){
		String fieldName = dimensionFieldsVO.getFieldName();
		String tableGuid = dimensionFieldsVO.getTableGuid();
		if(StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(tableGuid)) {
			boolean result = dimensionTableFieldService.judgeIsExistRepeatField(tableGuid, fieldName);
			return ResultUtil.success(result);
		}else {
			throw new RuntimeException("字段名称  or 表GUID为空，请检查！");
		}
	}
	
	
	
	
}
