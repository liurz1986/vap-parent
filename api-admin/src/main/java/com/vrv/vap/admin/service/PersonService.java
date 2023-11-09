package com.vrv.vap.admin.service;

import com.vrv.vap.admin.vo.BasePersoninfo;
import com.vrv.vap.common.vo.VList;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年9月3日 下午2:33:03
 * 类说明：Person Service
 */
@FeignClient("api-audit-business")
public interface PersonService {

	@ResponseBody
	@ApiOperation("查询人员信息")
	@PostMapping(path = "/person")
	public VList<BasePersoninfo> queryPersonInfoList(@RequestBody BasePersoninfo basePersoninfo);
}
