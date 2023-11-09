package com.vrv.vap.alarmdeal.business.threat.controller;

import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskInfoRes;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskRes;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatManageService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 上午11:23:43 
* 类说明 
*/
@Api(description="威胁数据")
@RestController
@RequestMapping(value = "/threatManage")
public class ThreatManageController {

	private static Logger logger = Logger.getLogger(ThreatManageController.class);
	
	@Autowired
	private ThreatManageService threatManageService;

	@PostMapping(value = "/getAssetRisk")
	@ApiOperation(value="资产威胁程度",notes="")
	public Result<AssetRiskRes> getAssetRisk(@RequestBody ThreatReq threatReq) {
		AssetRiskRes result = threatManageService.getAssetRisk(threatReq);
		return ResultUtil.success(result);
	}

	@PostMapping(value = "/getAssetThreatInfo/{top}")
	@ApiOperation(value="威胁资产",notes="")
	public Result<List<AssetRiskInfoRes>> getAssetThreatInfo(@RequestBody ThreatReq threatReq, @PathVariable("top") Integer top) {
		List<AssetRiskInfoRes> result = threatManageService.getAssetThreatInfo(threatReq,top);
		return ResultUtil.successList(result);
	}

}
