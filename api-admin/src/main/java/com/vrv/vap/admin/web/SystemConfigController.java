package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.vo.PassTimeConf;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.web
 * @Author tongliang@VRV
 * @CreateTime 2019/03/11 16:52
 * @Description (账号安全性配置和获取)
 * @Version
 */

@Api("系统配置项相关接口")
@RestController
@RequestMapping("/system/config")
public class SystemConfigController extends ApiController {
    private static final String PARAM_ERROR = "传入参数异常";
    @Autowired
    private SystemConfigService systemConfigService;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("uppercase", "{\"0\":\"否\",\"1\":\"是\"}");
        transferMap.put("lowercase", "{\"0\":\"否\",\"1\":\"是\"}");
        transferMap.put("specialChart", "{\"0\":\"否\",\"1\":\"是\"}");
        transferMap.put("numbers", "{\"0\":\"否\",\"1\":\"是\"}");
        transferMap.put("confEnable", "{\"0\":\"关闭\",\"1\":\"开启\"}");
        transferMap.put("confValue","{\"datasource\":\"数据源方式\",\"category\":\"分类方式\"}");
    }


    @ApiOperation(value = "获取所有系统配置项")
    @SysRequestLog(description = "获取所有系统配置项",actionType = ActionType.SELECT)
    @GetMapping
    public VData<List<SystemConfig>> allConfig() {
        return this.vData(systemConfigService.findAll());
    }

    @ApiOperation(value = "根据配置项ID获取配置项")
    @GetMapping("/{confId}")
    public VData getSysInfo(@PathVariable("confId") String confId) {
        SystemConfig systemConfig = systemConfigService.findByConfId(confId);
        return this.vData(systemConfig);
    }

    @ApiOperation(value = "获取登录配置项")
    @GetMapping("/sysInfo")
    public VData<List<SystemConfig>> sysInfoConfig() {
        String[] sysInfoFields = new String[]{"system_login_sub","system_login_title","system_logo","system_name","system_version","system_login_tip","system_login_logo","system_login_foot"};
        List<String> infoList = Arrays.asList(sysInfoFields);
        List<SystemConfig> systemConfigList = systemConfigService.findAll().stream().filter(p-> StringUtils.isNotEmpty(p.getConfId()) && infoList.contains(p.getConfId())).collect(Collectors.toList());
        return this.vData(systemConfigList);
    }


    @ApiOperation(value = "修改配置项， 说明： confId必填，其它选填`")
    @SysRequestLog(description = "修改配置项",actionType = ActionType.UPDATE)
    @PutMapping
    public Result update(@RequestBody SystemConfig config) {
        SystemConfig configSrc = systemConfigService.findByConfId(config.getConfId());
        int result = systemConfigService.updateSelective(config);
        if (result == 1 && (!StringUtils.equals(configSrc.getConfValue(),config.getConfValue()) || configSrc.getConfEnable() != config.getConfEnable())) {
            String descption = this.getConfLogDescription(config.getConfId());
            if ("rule_filter_start".equals(config.getConfId())) {
                SyslogSenderUtils.sendUpdateAndTransferredField(configSrc,config,"修改" + descption + "配置项",transferMap);
            } else {
                SyslogSenderUtils.sendUpdateSyslog(configSrc,config,"修改" + descption + "配置项");
            }
        }
        return this.result(result == 1);
    }

    private String getConfLogDescription(String confId) {
        String descption = "";
        if ("nav_lock".equals(confId)) {
            descption = "页面锁定";
        }
        if ("rule_filter_start".equals(confId)) {
            descption = "规则启动方式";
        }
        if ("rule_filter_start_num".equals(confId)) {
            descption = "单任务规则数";
        }
        return descption;
    }

    @ApiOperation(value = "获取登录页面选项配置`")
    @SysRequestLog(description = "获取登录页面选项配置",actionType = ActionType.SELECT)
    @GetMapping("/loginPage")
    public Result getLoginPageConfig() {
        SystemConfig loginPageConfig = systemConfigService.findByConfId("loginPage");
        if (loginPageConfig == null) {
            return new Result("-1", "获取获取登录页面选项配置失败");
        }
        return this.vData(loginPageConfig);
    }

    @ApiOperation(value = "更新账号安全性配置`")
    @SysRequestLog(description="更新账号安全性配置", actionType = ActionType.UPDATE)
    @PutMapping("/safety")
    public Result safetyConfig(@RequestBody PassTimeConf passTimeConf) {
        if (passTimeConf == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Integer maxlength = passTimeConf.getMaxlength() == null ? 20 : passTimeConf.getMaxlength();
        Integer minlength = passTimeConf.getMinlength() == null ? 8 : passTimeConf.getMinlength();
        Integer maxFailNumber = passTimeConf.getMaxFailNumber();
        Integer checkTimeSet = passTimeConf.getCheckTimeSet();
        if (maxFailNumber < 0 || checkTimeSet < 0 || maxlength < 0 || minlength < 0 || (maxlength != 0 && maxlength < minlength)) {
            return new Result("-1", PARAM_ERROR);
        }
        if (maxFailNumber < 1 || maxFailNumber > 5) {
            return new Result("-1", "登录密码错误最大尝试次数为1-5次");
        }
        int sum = passTimeConf.getUppercase() + passTimeConf.getLowercase()
                + passTimeConf.getSpecialChart() + passTimeConf.getNumbers();
        if (sum < 2) {
            return new Result("-1", "复杂度为大小写英文字母数字和特殊字符中两者以上组合");
        }
        PassTimeConf passTimeConfSrc = getPassTimeConfSrc();
        Integer count = systemConfigService.safetyConfig(passTimeConf);
        if (count == null || count == 0) {
            return new Result("-1", "更新账号安全性配置失败");
        }
        SyslogSenderUtils.sendUpdateAndTransferredField(passTimeConfSrc, passTimeConf, "更新账号安全性配置",transferMap);
        return Global.OK;
    }

    private PassTimeConf getPassTimeConfSrc() {
        PassTimeConf passTimeConf = new PassTimeConf();
        passTimeConf.setMaxFailNumber(Integer.parseInt(systemConfigService.findByConfId("maxFailNumber").getConfValue()));
        passTimeConf.setCheckTimeSet(Integer.parseInt(systemConfigService.findByConfId("checkTimeSet").getConfValue()));
        passTimeConf.setUppercase(Short.valueOf(systemConfigService.findByConfId("uppercase").getConfEnable()));
        passTimeConf.setLowercase(Short.valueOf(systemConfigService.findByConfId("lowercase").getConfEnable()));
        passTimeConf.setSpecialChart(Short.valueOf(systemConfigService.findByConfId("specialChart").getConfEnable()));
        passTimeConf.setNumbers(Short.valueOf(systemConfigService.findByConfId("numbers").getConfEnable()));
        passTimeConf.setMaxlength(Integer.parseInt(systemConfigService.findByConfId("maxlength").getConfValue()));
        passTimeConf.setMinlength(Integer.parseInt(systemConfigService.findByConfId("minlength").getConfValue()));
        return passTimeConf;
    }

    @ApiOperation(value = "获取账号安全性配置`")
    @SysRequestLog(description = "获取账号安全性配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("safety")
    public Result getSafetyConfig() {
        PassTimeConf safetyConfig = systemConfigService.getSafetyConfig();
        if (safetyConfig == null) {
            return new Result("-1", "获取账号安全性配置失败");
        }
        return this.vData(safetyConfig);
    }

    @ApiOperation(value = "更新密码时效性配置`")
    @SysRequestLog(description="更新密码时效性配置", actionType = ActionType.UPDATE)
    @PutMapping("expiretime")
    public Result setExpiretime(@RequestBody Map<String, Short> param) {
        if (param == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Short expiretime = param.get("expiretime");
        if (expiretime > 7) {
            return new Result("-1", "密码的失效时间最大为7天");
        }
        SystemConfig configSrc = systemConfigService.findByConfId("expiretime");
        Integer count = systemConfigService.setExpiretime(param);
        if (count == null || count == 0) {
            return new Result("-1", "更新密码时效性配置失败");
        }
        SystemConfig config = systemConfigService.findByConfId("expiretime");
        SyslogSenderUtils.sendUpdateSyslog(configSrc, config, "更新密码时效性配置");
        return Global.OK;
    }


    @ApiOperation(value = "获取密码时效性配置`")
    @SysRequestLog(description = "获取密码时效性配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("expiretime")
    public Result getExpiretime() {
        Map<String, Short> expireConfig = systemConfigService.getExpiretime();
        if (expireConfig == null) {
            return new Result("-1", "获取密码时效性配置失败");
        }
        return this.vData(expireConfig);
    }



    @ApiOperation(value = "更新三权是否开启配置`")
    @SysRequestLog(description="更新三权是否开启配置", actionType = ActionType.UPDATE)
    @PutMapping("tripartite")
    public Result threePowerConfig(@RequestBody Map<String, Short> param) {
        if (param == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Integer count = systemConfigService.threePowerConfig(param);
        SystemConfig configSrc = systemConfigService.findByConfId("THREE_POWER_ON");
        if (count == null || count == 0) {
            return new Result("-1", "更新三权是否开启配置失败");
        }
        SystemConfig config = systemConfigService.findByConfId("THREE_POWER_ON");
        SyslogSenderUtils.sendUpdateSyslog(configSrc, config, "更新三权是否开启配置");
        return Global.OK;
    }


    @ApiOperation(value = "获取三权是否开启配置`")
    @SysRequestLog(description = "获取三权是否开启配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("tripartite")
    public Result getThreePowerConfig() {
        Map<String, Short> threePowerConfig = systemConfigService.getThreePowerConfig();
        if (threePowerConfig == null) {
            return new Result("-1", "获取三权是否开启配置失败");
        }
        return this.vData(threePowerConfig);
    }

    @ApiOperation(value = "更新级联是否开启配置`")
    @SysRequestLog(description = "更新级联是否开启配置",actionType = ActionType.SELECT)
    @PutMapping("cascade")
    public Result cascadeConfig(@RequestBody Map<String, Byte> param) {
        SystemConfig configSrc = systemConfigService.findByConfId("cascade");
        if (param == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Integer count = systemConfigService.cascadeConfig(param);
        SystemConfig config = systemConfigService.findByConfId("cascade");
        SyslogSenderUtils.sendUpdateSyslog(configSrc, config, "更新级联是否开启配置");
        return this.result(count == 1);
    }

    @ApiOperation(value = "获取级联是否开启配置`")
    @SysRequestLog(description = "获取级联是否开启配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("cascade")
    public Result getCascadeConfig() {
        Map<String, Byte> cascadeConfig = systemConfigService.getCascadeConfig();
        if (cascadeConfig == null) {
            return new Result("-1", "获取级联是否开启配置失败");
        }
        return this.vData(cascadeConfig);
    }
    @ApiOperation(value = "获取垂直越权是否开启配置`")
    @SysRequestLog(description = "获取垂直越权是否开启配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("/exceed/switch")
    public Result getExceedSwitchConfig() {
        Map<String, Short> exceedSwitchConfig = systemConfigService.getExceedSwitchConfig();
        if (exceedSwitchConfig == null) {
            return new Result("-1","获取垂直越权是否开启配置失败");
        }
        return this.vData(exceedSwitchConfig);
    }

    @ApiOperation(value = "更新垂直越权是否开启配置`")
    @SysRequestLog(description = "更新垂直越权是否开启配置",actionType = ActionType.UPDATE)
    @PutMapping("/exceed/switch")
    public Result exceedSwitchConfig(@RequestBody Map<String, Short> param) {
        SystemConfig configSrc = systemConfigService.findByConfId("authority_exceed_strategy_switch");
        if (param == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Integer count = systemConfigService.exceedSwitchConfig(param);
        if (count == null || count == 0) {
            return new Result("-1", "更新垂直越权是否开启配置失败");
        }
        SystemConfig config = systemConfigService.findByConfId("authority_exceed_strategy_switch");
        Map transferMap = new HashMap();
        transferMap.put("confEnable", "{\"0\":\"关闭\",\"1\":\"开启\"}");
        SyslogSenderUtils.sendUpdateAndTransferredField(configSrc, config, "更新垂直越权是否开启配置",transferMap);
        return Global.OK;
    }

    @ApiOperation(value = "获取垂直越权未知接口是否开启配置`")
    @SysRequestLog(description = "获取垂直越权未知接口是否开启配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("/exceed/unknown")
    public Result getExceedUnknownConfig() {
        Map<String, Short> exceedSwitchConfig = systemConfigService.getExceedUnknownConfig();
        if (exceedSwitchConfig == null) {
            return new Result("-1","获取垂直越权未知接口是否开启配置失败");
        }
        return this.vData(exceedSwitchConfig);
    }

    @ApiOperation(value = "更新垂直越权未知接口是否开启配置`")
    @SysRequestLog(description = "更新垂直越权未知接口是否开启配置",actionType = ActionType.UPDATE)
    @PutMapping("/exceed/unknown")
    public Result exceedUnknownConfig(@RequestBody Map<String, Short> param) {
        SystemConfig configSrc = systemConfigService.findByConfId("authority_exceed_strategy_unknown");
        if (param == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Integer count = systemConfigService.exceedUnknownConfig(param);
        if (count == null || count == 0) {
            return new Result("-1", "更新垂直越权未知接口是否开启配置失败");
        }
        SystemConfig config = systemConfigService.findByConfId("authority_exceed_strategy_unknown");
        Map transferMap = new HashMap();
        transferMap.put("confEnable", "{\"0\":\"关闭\",\"1\":\"开启\"}");
        SyslogSenderUtils.sendUpdateAndTransferredField(configSrc, config, "更新垂直越权未知接口是否开启配置",transferMap);
        return Global.OK;
    }

    @ApiOperation(value = "获取定时清理审计日志是否开启配置`")
    @SysRequestLog(description = "获取定时清理审计日志是否开启配置",actionType = ActionType.SELECT,manually = false)
    @GetMapping("/syslog/clean")
    public Result getSyslogCleanConfig() {
        Map<String,Object> syslogCleanConfig = systemConfigService.getSyslogCleanConfig();
        if (syslogCleanConfig == null) {
            return new Result("-1","获取定时清理审计日志是否开启配置失败");
        }
        return this.vData(syslogCleanConfig);
    }

    @ApiOperation(value = "更新定时清理审计日志配置`")
    @SysRequestLog(description = "更新定时清理审计日志配置",actionType = ActionType.UPDATE)
    @PutMapping("/syslog/clean")
    public Result syslogCleanConfig(@RequestBody Map<String, Short> param) {
        SystemConfig configSrc = systemConfigService.findByConfId("sys_log_clean");
        if (param == null) {
            return new Result("-1", PARAM_ERROR);
        }
        Integer count = systemConfigService.syslogCleanConfig(param);
        if (count == null || count == 0) {
            return new Result("-1", "更新定时清理审计日志配置失败");
        }
        SystemConfig config = systemConfigService.findByConfId("sys_log_clean");
        Map<String, Object> transferMap = new HashMap<>();
        transferMap.put("confEnable", "{\"0\":\"关闭\",\"1\":\"开启\"}");
        SyslogSenderUtils.sendUpdateAndTransferredField(configSrc, config, "更新定时清理审计日志配置",transferMap);
        return Global.OK;
    }
}
