package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.business.threat.bean.VulInfoVo;
import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import com.vrv.vap.jpa.web.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月28日 下午5:10:27 
* 类说明   资产 feign接口调用
*/
@FeignClient(name = "api-risk",configuration = ConfigurationFegin.class)
public interface RiskFegin {
	@RequestMapping(value = "/riskScreen/queryHighRiskCount",method = RequestMethod.GET,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Result<Map<String, Long>> queryHighRiskCount();
	@RequestMapping(value = "/riskScreen/queryHighRiskTrend",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Result<Map<String,List<String>>> queryHighRiskTrend(@RequestBody ThreatReq threatReq);
}
	
	
	
