package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.BlockVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.BlockResponseVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.CallLinkageVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.SoarDataVO;
import com.vrv.vap.alarmdeal.frameworks.contract.soar.SoarScript;
import com.vrv.vap.alarmdeal.frameworks.contract.soar.SoarScriptTask;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月28日 下午5:10:27 
* 类说明   soar的feign接口调用
*/
@FeignClient(name = "api-soar")
public interface SoarFegin {

	
	/**
	 * 设备联动设置
	 * @param callLinkageVO
	 * @return
	 */
	@RequestMapping(value = "/linkageRuleAction/callLinkageRule",method = RequestMethod.PUT,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<String> callLinkageRule(@RequestBody CallLinkageVO callLinkageVO);
	
	
	/**
	 * 设备联动
	 * @param blockVO
	 * @return
	 */
	@RequestMapping(value = "/blockAction/setBlockAction",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<BlockResponseVO> setBlockAction(@RequestBody BlockVO blockVO);
	
	
	
	/**
	 * 执行
	 * @param map1
	 * @return
	 */
	@RequestMapping(value = "/soarScript/start",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<SoarScriptTask> start(@RequestBody SoarDataVO soarDataVO);
	
	
	@RequestMapping(value = "/soarScript/getSoarScriptByCode/{code}",method = RequestMethod.GET,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<SoarScript> getSoarScriptByCode(@PathVariable(name = "code") String code);
	
	
	
	@RequestMapping(value = "/soarScript/getCurSoarScriptByCode/{code}",method = RequestMethod.GET,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<SoarScript> getCurSoarScriptByCode(@PathVariable(name = "code") String code);
	
	
}
	
	
	
