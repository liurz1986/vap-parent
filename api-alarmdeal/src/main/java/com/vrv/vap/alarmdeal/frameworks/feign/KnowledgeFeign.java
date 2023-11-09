package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.jpa.web.ResultObjVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月8日 下午1:42:55 
* 类说明   知识库相关feign接口调用
*/
@FeignClient(name = "api-knowledge")
public interface KnowledgeFeign {

	/**
	 * 根据标签获得对应的知识库信息
	 * @param map1
	 * @return
	 */
	@RequestMapping(value = "/knowledgeBase/getDataPage",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultObjVO<Map<String,Object>> getKnowledgeByTag(@RequestBody Map<String,Object> map);
	
}
