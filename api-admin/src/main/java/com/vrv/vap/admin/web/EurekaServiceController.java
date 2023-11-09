/**
 * 
 */
package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.admin.common.properties.Site;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wh1107066 获取注册中心的所有服务
 */
@RequestMapping(path = "/services")
@RestController
public class EurekaServiceController extends ApiController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Site siteProperties;

	@Autowired
	private DiscoveryClient discoveryClient;

	@GetMapping
	@ApiOperation(value = "获取注册中心的所有服务")
	public Result getRegistered() {
		return  this.vData(discoveryClient.getServices());
//		List<ServiceInstance> list = new ArrayList<ServiceInstance>();
//		// 得到所有服务
//		List<String> services = discoveryClient.getServices();
//		for (String s : services) {
//			// 得到所有服务实例，或许有多个
//			List<ServiceInstance> serviceInstances = discoveryClient.getInstances(s);
//			for (ServiceInstance si : serviceInstances) {
//				logger.info("services:" + s + ":getHost()=" + si.getHost());
//				logger.info("services:" + s + ":getPort()=" + si.getPort());
//				logger.info("services:" + s + ":getServiceId()=" + si.getServiceId());
//				logger.info("services:" + s + ":getUri()=" + si.getUri());
//				logger.info("services:" + s + ":getMetadata()=" + si.getMetadata());
//			}
//			list.addAll(serviceInstances);
//		}
//		return list;
	}


	@GetMapping("/site")
	@ApiOperation(value = "获取站点配置")
	public Result getSiteConfig() {
		return this.vData(siteProperties);
	}

}
