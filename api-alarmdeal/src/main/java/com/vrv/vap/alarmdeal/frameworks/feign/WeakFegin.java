package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.business.threat.bean.VulInfoVo;
import com.vrv.vap.alarmdeal.business.threat.bean.VulIpValue;
import com.vrv.vap.alarmdeal.business.threat.bean.VulManage;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.VulManageVo;
import com.vrv.vap.jpa.web.Result;
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
* @version 创建时间：2019年4月28日 下午5:10:27 
* 类说明   资产 feign接口调用
*/
@FeignClient(name = "api-weak",configuration = ConfigurationFegin.class)
public interface WeakFegin {
	@RequestMapping(value = "/largeScreen/getAssetsTotalVulValue",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Result<VulInfoVo> getAssetsTotalVulValue(@RequestBody Map<String,Object> map);
}
	
	
	
