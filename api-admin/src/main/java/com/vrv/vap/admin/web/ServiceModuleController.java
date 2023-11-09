package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.model.ServiceApi;
import com.vrv.vap.admin.model.ServiceModule;
import com.vrv.vap.admin.service.ServiceApiService;
import com.vrv.vap.admin.service.ServiceModuleService;
import com.vrv.vap.admin.vo.ServiceModuleVO;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(path = "/service")
public class ServiceModuleController extends ApiController {

    @Resource
    private ServiceModuleService serviceModuleService;

    @Resource
    private ServiceApiService serviceApiService;

    /**
     * 添加服务
     */
    @ApiOperation(value = "添加服务")
    @PutMapping
    @SysRequestLog(description="添加服务", actionType = ActionType.ADD)
    public Result addServiceModule(@RequestBody ServiceModule serviceModule) {
        int result = serviceModuleService.save(serviceModule);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(serviceModule,"添加服务");
            return this.vData(serviceModuleService.findOne(serviceModule));
        }
        return this.result(false);
    }


    /**
     * 修改服务
     */
    @PatchMapping
    @ApiOperation(value = "修改服务")
    @SysRequestLog(description="修改服务", actionType = ActionType.UPDATE)
    public Result editServiceModule(@RequestBody ServiceModule serviceModule) {
        ServiceModule serviceModuleSec = serviceModuleService.findById(serviceModule.getId());
        int result = serviceModuleService.updateSelective(serviceModule);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(serviceModuleSec,serviceModule,"修改服务");
        }
        return this.result(result == 1);
    }


    /**
     * 删除服务
     */
    @ApiOperation(value = "删除服务")
    @DeleteMapping
    @SysRequestLog(description="删除服务", actionType = ActionType.DELETE)
    public Result delServiceModule(@RequestBody DeleteQuery deleteQuery) {
        List<ServiceModule> moduleList = serviceModuleService.findByids(deleteQuery.getIds());
        int result = serviceModuleService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            moduleList.forEach(serviceModule -> {
                SyslogSenderUtils.sendDeleteSyslog(serviceModule,"删除服务");
            });
            String[] ids = deleteQuery.getIds().split(",");
            for (String id : ids) {
                List<ServiceApi> apiList = serviceApiService.findByProperty(ServiceApi.class,"serviceId",id);
                if (CollectionUtils.isNotEmpty(apiList)) {
                    apiList.stream().forEach(item -> serviceApiService.deleteById(item.getId()));
                }
            }
            return this.vData(true);
        }
        return this.vData(false);
    }


    /**
     * 查询服务（分页）
     */
    @ApiOperation(value = "查询服务（分页）")
    @PostMapping
    @SysRequestLog(description="查询服务", actionType = ActionType.SELECT)
    public VList<ServiceModule> queryServiceModule(@RequestBody ServiceModuleVO serviceModuleVO) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(serviceModuleVO, ServiceModule.class);
        return this.vList(serviceModuleService.findByExample(example));
    }


    /**
     * 获取所有服务
     */
    @ApiOperation(value = "获取所有服务")
    @GetMapping(value = "/all")
    public VData<List<ServiceModule>> getAllServiceModule() {
        return this.vData(serviceModuleService.findAll());
    }

    @ApiOperation(value = "同步所有服务接口")
    @GetMapping(value = "/syncApi")
    @SysRequestLog(description="同步所有服务接口", actionType = ActionType.UPDATE,manually = false)
    public VData syncAllServiceApi() {
        Result result = new Result();
        String syncResult = serviceApiService.syncServiceApi();
        if (StringUtils.isNotEmpty(syncResult)) {
            result.setCode(ErrorCode.RESOURCE_API_SYNC_ERROR.getResult().getCode());
            result.setMessage(syncResult + ErrorCode.RESOURCE_API_SYNC_ERROR.getResult().getMessage());
            return this.vData(result);
        }
        return this.vData(Global.OK);
    }

    @ApiOperation(value = "根据服务ID同步服务接口")
    @GetMapping(value = "/sync/{moduleId}")
    @SysRequestLog(description="同步服务接口", actionType = ActionType.UPDATE)
    public VData syncServiceApi(@PathVariable @ApiParam("服务ID") Integer moduleId) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        syslogSender.sendSysLog(ActionType.UPDATE, "同步服务接口:【服务ID:" + moduleId + "】", null, "1");
        ServiceModule serviceModule = serviceModuleService.findById(moduleId);
        if (serviceModule == null) {
            return this.vData(Global.ERROR);
        }
        Boolean result = serviceApiService.syncServieApi(serviceModule);
        if (!result) {
            return this.vData(ErrorCode.RESOURCE_API_SYNC_ERROR);
        }
        return this.vData(Global.OK);
    }
}
