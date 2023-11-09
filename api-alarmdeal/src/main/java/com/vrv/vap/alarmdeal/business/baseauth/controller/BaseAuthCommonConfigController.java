package com.vrv.vap.alarmdeal.business.baseauth.controller;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthCommonConfig;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthCommonConfigService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取配置信息
 *
 * 2023-08
 * @author liurz
 */
@RestController
@RequestMapping(value="/baseAuthCommonConfig")
public class BaseAuthCommonConfigController {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthCommonConfigController.class);

    @Autowired
    private BaseAuthCommonConfigService baseAuthCommonConfigService;


    /**
     * 获取配置信息
     * SRC_OBJ_TYPE:源对象类型
     * DST_OBJ_TYPE:目标对象类型
     * OPT_TYPE:动作
     * OPT_TYPE_REF：源与目的组合对应的操作类型
     * @return
     */
    @GetMapping(value="/getBaseConfig")
    @ApiOperation(value="获取审批类型配置的基础配置信息",notes="")
    @SysRequestLog(description="获取审批类型配置的基础配置信息", actionType = ActionType.SELECT,manually=false)
    public Result<List<BaseAuthCommonConfig>> getBaseConfig(){
        try {
            List<BaseAuthCommonConfig> result = baseAuthCommonConfigService.getBaseConfig();
            return  ResultUtil.successList(result);
        } catch (Exception e) {
            logger.error("获取审批类型配置的基础配置信息:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取审批类型配置的基础配置信息异常");
        }
    }

    /**
     * 获取关联基础数据
     * 网络设备	NetworkDevice
     * 服务器	service
     * 安全保密设备	SafeDevice
     * 终端	assetHost
     * 运维终端	maintenHost
     * USB存储	USBMemory
     * USB外设设备	USBPeripheral
     * 用户 user
     * 文件	dataInfoManage
     * 应用系统	app
     * @return
     */
    @PostMapping(value="/getRefBaseData")
    @ApiOperation(value="审批对象-获取关联基础数据",notes="")
    @SysRequestLog(description="审批对象-获取关联基础数据", actionType = ActionType.SELECT,manually=false)
    public Result<List<Map<String, Object>>> getRefBaseData(@RequestBody Map<String,String> param){
        try {
            String code = param.get("code");
            if(StringUtils.isEmpty(code)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "code不能为空");
            }
            logger.info("审批对象-获取关联基础数据，请求参数："+ JSON.toJSONString(param));
            return baseAuthCommonConfigService.getRefBaseData(code);
        } catch (Exception e) {
            logger.error("审批对象-获取关联基础数据异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批对象-获取关联基础数据异常");
        }
    }

    /**
     * 获取审批类型配置对应的展示列及操作类型
     * @param param
     * @return
     */
    @PostMapping(value="/getColumnsAndOptType")
    @ApiOperation(value="获取审批类型配置对应的展示列及操作类型",notes="")
    @SysRequestLog(description="获取审批类型配置对应的展示列及操作类型", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String, Object>> getColumns(@RequestBody Map<String,String> param){
        try {
            Map<String, Object> result = new HashMap<>();
            String typeId = param.get("typeId");
            if(StringUtils.isEmpty(typeId)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "typeId不能为空");
            }
            // 获取展示列
            Map<String,Object> columns =  baseAuthCommonConfigService.getColumns(typeId);
            // 获取操作类型
            List<Map> optTypes = baseAuthCommonConfigService.getOptType(typeId);
            result.put("columns",columns);
            result.put("optTypes",optTypes);
            return ResultUtil.success(result);
        } catch (Exception e) {
            logger.error("获取审批类型配置对应的展示列及操作类型异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取审批类型配置对应的展示列及操作类型异常");
        }
    }
}
