package com.vrv.vap.alarmdeal.business.attack.controller;

import com.vrv.vap.alarmdeal.business.attack.service.AttackAuditService;
import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskInfoRes;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskRes;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatManageService;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description="攻击")
@RestController
@RequestMapping(value = "/attack")
public class AttackAuditController {

	private static Logger logger = Logger.getLogger(AttackAuditController.class);
	
	@Autowired
	private AttackAuditService attackAuditService;

	@PostMapping(value = "/getAttackStageCount")
	@ApiOperation(value="资产威胁程度",notes="")
	public Result<List<NameValue>> getAssetRisk(@RequestBody Map<String,String> threatReq) {
		List<NameValue> result = attackAuditService.getAttackStageCount(threatReq);
		return ResultUtil.success(result);
	}


}
