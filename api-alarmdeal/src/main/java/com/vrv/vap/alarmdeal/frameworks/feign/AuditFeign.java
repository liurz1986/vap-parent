package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfo;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfoServer;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.LabelConf;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
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
* @version 创建时间：2018年10月10日 下午5:23:40 
* 类说明   audit应用feign接口调用
*/
@FeignClient(name = "api-audit-business",configuration = ConfigurationFegin.class)
public interface AuditFeign {

	/**
	 * 标签查询
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/label",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<List<LabelConf>> label(@RequestBody Map<String,Object> param);
	
	/**
	 * 新增标签
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/label/mark",method = RequestMethod.PUT,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Result labelmark(@RequestBody Map<String,Object> param);
	
	
	@RequestMapping(value = "/sysinfo/cty/sd_ip",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public com.vrv.vap.jpa.web.page.PageRes<BaseSysinfo> getAllAppSystem(@RequestBody Map<String,Object> param);
	
	
	/**
	 * 根据systemId获得对应的应用系统信息
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/sysinfo",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<List<BaseSysinfo>> sysinfo(@RequestBody Map<String,Object> param);


	/**
	 * 根据id获得对应的应用系统集合
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/sysinfo/server",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<List<BaseSysinfoServer>> sysServer(@RequestBody Map<String,Object> param);
}
