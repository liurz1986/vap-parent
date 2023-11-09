package com.vrv.vap.alarmdeal.business.alaramevent.strategy.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.RiskRuleInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.RuleOperation;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleService;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterPagerVO;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月06日 16:18
 */
@RestController
@RequestMapping("/rule")
@Api(description="规则管理")
public class RuleController extends BaseController {

    @Autowired
    private RuleService ruleService;
    /**
     * 获取规则运行状态
     * @return Result<Object>
     */
    @RequestMapping("/getRuleOperation")
    @SysRequestLog(description = "规则管理-获取规则运行状态", actionType = ActionType.SELECT, manually = false)
    public Result<RuleOperation> getRuleOperation(){
        RuleOperation ruleOperation = ruleService.getRuleOperation();
        return ResultUtil.success(ruleOperation);
    }

    /**
     * 获取规则运行状态
     * @return Result<Object>
     */
    @RequestMapping("/getRiskRuleInfo")
    @SysRequestLog(description = "规则管理-获取策略运行状态", actionType = ActionType.SELECT, manually = false)
    public Result<RiskRuleInfo> getRiskRuleInfo(){
        RiskRuleInfo riskRuleInfo = ruleService.getRiskRuleInfo();
        return ResultUtil.success(riskRuleInfo);
    }

    /**
     * 通过规则ID删除规则
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    @SysRequestLog(description = "规则管理-删除规则", actionType = ActionType.DELETE, manually = false)
    public Result<String> deleteRuleForId(@PathVariable("id") String id){
        Boolean isDelete = ruleService.deleteRuleForId(id);
        if(!isDelete){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"规则被开启策略所引用，无法删除！");
        }
        return ResultUtil.success("规则删除成功！");
    }

    @PostMapping(value="/import")
    @SysRequestLog(description="规则信息-导入规则信息", actionType = ActionType.IMPORT, manually = false)
    @ApiOperation(value="规则管理-导入规则信息",notes="")
    public Result<List<String>> importRule(@RequestParam("file") MultipartFile file){
        List<String> importList = ruleService.importRule(file);
        if(CollectionUtils.isEmpty(importList)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导入规则文件为空或失败!");
        }
        return ResultUtil.successList(importList);
    }

    @PostMapping(value="/export")
    @SysRequestLog(description="导出规则信息", actionType = ActionType.EXPORT, manually = false)
    @ApiOperation(value="规则管理-导出规则信息",notes="")
    public Result<String> exportRule(@RequestBody FilterPagerVO filterPagerVO, HttpServletResponse httpServletResponse){
        String importName = ruleService.exportRule(filterPagerVO,httpServletResponse);
        if(StringUtils.isEmpty(importName)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导入规则文件为空或失败!");
        }
        return ResultUtil.success(importName);
    }

    @GetMapping("/downfilterZip/{fileName:.+}")
    @ApiOperation(value = "fileName", notes = "规则导出")
    @SysRequestLog(description = "规则导出", actionType = ActionType.SELECT, manually = false)
    public void test(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        String filePath = ruleService.getFilePath();
        FileUtil.downLoadFile(fileName, filePath, response);
    }

}
