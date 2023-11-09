package com.vrv.vap.alarmdeal.business.asset.controller;

import com.vrv.vap.alarmdeal.business.asset.model.AssetOperationLog;
import com.vrv.vap.alarmdeal.business.asset.service.AssetOperationLogService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/assetOperationLog")
public class AssetOperationLogController {

	
	@Autowired
	private AssetOperationLogService assetOperationLogService;
	
	/**
	 * 获得标签数据
	 * @return
	 */
	@GetMapping(value="/getLogs/{guid}")
	@ApiOperation(value="获得资产日志数据",notes="")
	@SysRequestLog(description="获得资产日日志数据", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetOperationLog>> getLogs(@PathVariable String guid){
		List<QueryCondition> conditions=new ArrayList<>();
		conditions.add(QueryCondition.eq("assetGuid", guid));
		Sort sort=Sort.by("operateTime");
		List<AssetOperationLog> findAll = assetOperationLogService.findAll(conditions,sort);
		
		return ResultUtil.success(findAll);
	}
}
