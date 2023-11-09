package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.ServiceApi;
import com.vrv.vap.admin.model.ServiceModule;
import com.vrv.vap.admin.service.ServiceApiService;
import com.vrv.vap.admin.service.ServiceModuleService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/10/9
 * @description
 */
@RestController
@RequestMapping(path = "/serviceApi")
public class ServiceApiController extends ApiController {

    @Resource
    ServiceApiService serviceApiService;

    @Resource
    ServiceModuleService serviceModuleService;

    @ApiOperation("获取所有服务接口")
    @GetMapping
    @SysRequestLog(description = "获取所有服务接口", actionType = ActionType.SELECT)
    public VData getApiList() {
        List<ServiceApi> serviceApiList = serviceApiService.findAll();
        return this.vData(serviceApiList);
    }

    @ApiOperation("获取所有服务接口树")
    @GetMapping(path = "/tree")
    @SysRequestLog(description = "获取所有服务接口树", actionType = ActionType.SELECT)
    public VData getApiTree() {
        List<ServiceApi> serviceApiList = serviceApiService.findAll();
        Set<Integer> serviceIdSet = new TreeSet<>();
        if (CollectionUtils.isNotEmpty(serviceApiList)) {
            serviceApiList.stream().forEach(item -> serviceIdSet.add(item.getServiceId()));
        }
        List<Map> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(serviceIdSet)) {
            Map<String,List> serviceMap = new TreeMap();
            for (Integer serviceId : serviceIdSet) {
                    List<ServiceApi> serviceList = new ArrayList<>();
                    for (ServiceApi serviceApi : serviceApiList) {
                        if (serviceId.equals(serviceApi.getServiceId())) {
                            serviceList.add(serviceApi);
                        }
                    }
                serviceList = serviceList.stream().sorted(Comparator.comparing(ServiceApi::getPath)).collect(Collectors.toList());
                ServiceModule serviceModule = serviceModuleService.findById(serviceId);
                serviceMap.put(serviceModule.getName(),serviceList);
            }
            result.add(serviceMap);
        }
        return this.vData(result);
    }
}
