package com.vrv.vap.admin.service;

import com.vrv.vap.admin.vo.ListSysLogQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.vrv.vap.admin.model.PageResult;
import com.vrv.vap.admin.model.ResultBody;
import com.vrv.vap.admin.service.impl.SeparationSysLogFeignFallback;
import com.vrv.vap.common.vo.Result;


/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.service
 * @Author tongliang@VRV
 * @CreateTime 2019/04/08 14:20
 * @Description (远程调用server-syslog 对系统日志的导入和查询)
 * @Version
 */
@FeignClient(value = "server-sys", path = "/syslog/", fallback = SeparationSysLogFeignFallback.class,contextId = "log")
public interface SeparationSysLogFeign {

	@PostMapping("separationSyslog")
	PageResult separationSyslog(@RequestBody ListSysLogQuery listSysLogQuery);

	@PostMapping("listSysLog")
	PageResult listSyslog(@RequestBody ListSysLogQuery listSysLogQuery);

	@GetMapping("importSyslogExcel/{guid}")
	Result importSyslogExcel(@PathVariable("guid") String guid);

	@GetMapping("deleteIndex")
	ResultBody deleteIndex();

	@GetMapping("loginThirtyDay")
	ResultBody loginThirtyDay();

}
