package com.vrv.vap.alarmdeal.business.baseauth.controller.authV2;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.baseauth.enums.BaseAuthEnum;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthOperation;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthPrintBurn;
import com.vrv.vap.alarmdeal.business.baseauth.service.*;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthAppVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthInternetVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthPrintBurnVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthoOperationVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthAppQueryVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthPrintBurnQueryVo;
import com.vrv.vap.alarmdeal.frameworks.util.FileTemplateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;


@Api(description = "审批信息")
@RequestMapping("/baseAuth")
@RestController
public class BaseAuthPrintBurnController {

    private static Logger logger= LoggerFactory.getLogger(BaseAuthPrintBurnController.class);
    @Autowired
    private BaseAuthPrintBurnService baseAuthPrintBurnService;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private BaseAuthService baseAuthService;
    @Resource
    private BaseAuthAppService baseAuthAppService;
    @Autowired
    private BaseAuthInternetService baseAuthInternetService;
    @Autowired
    private BaseAuthOperationService baseAuthOperationService;
    @Autowired
    private AssetService assetService;


    @PostMapping(value="/printBurnPage")
    @SysRequestLog(description="打印刻录审批信息分页查询", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="打印刻录审批信息分页查询",notes="")
    public PageRes<BaseAuthPrintBurnVo> getPager(@RequestBody BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo){
        PageRes<BaseAuthPrintBurnVo> pageRes = baseAuthPrintBurnService.getPager(baseAuthPrintBurnQueryVo);
        return pageRes;
    }
    @PutMapping(value="addPrintBurn")
    @SysRequestLog(description="打印刻录审批信息保存", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="打印刻录审批信息保存",notes="")
    public Result<Map<String,List<String>>> save(@RequestBody BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo){
        try{
            return baseAuthPrintBurnService.saveData(baseAuthPrintBurnQueryVo);
        }catch (Exception e){
            logger.error("打印刻录审批信息保存异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "打印刻录审批信息保存异常");
        }
    }
    @PostMapping(value="updatePrintBurn")
    @SysRequestLog(description="打印刻录审批信息编辑", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="打印刻录审批信息编辑",notes="")
    public Result<List<BaseAuthPrintBurn>> updatePrintBurn(@RequestBody BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo){
        try{
            return baseAuthPrintBurnService.updatePrintBurn(baseAuthPrintBurnQueryVo);
        }catch (Exception e){
            logger.error("打印刻录审批信息编辑异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "打印刻录审批信息编辑异常");
        }
    }
    @PostMapping(value="delPrintBurn")
    @SysRequestLog(description="删除打印刻录审批信息", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="删除打印刻录审批信息",notes="")
    public Result<String> delPrintBurn(@RequestBody BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo){
        try{
            return baseAuthPrintBurnService.delPrintBurn(baseAuthPrintBurnQueryVo);
        }catch (Exception e){
            logger.error("删除打印刻录审批信息异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "删除打印刻录审批信息异常");
        }
    }
    @PostMapping(value="getIpsByAssetType")
    @SysRequestLog(description="通过资产类型名称获取ip列表", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="通过类型名称获取ip列表",notes="")
    public Result<List<String>> getIpsByAssetType(@RequestBody AssetTypeGroup assetTypeGroup){
            return baseAuthPrintBurnService.getIpsByAssetType(assetTypeGroup);
    }
    @GetMapping(value="getPrintBrunAssetType")
    @SysRequestLog(description="打印刻录审批获取可选择的资产类型", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="打印刻录审批获取可选择的资产类型",notes="")
    public Result<List<AssetTypeGroup>> getPrintBrunAssetType(){
        return baseAuthPrintBurnService.getPrintBrunAssetType();
    }
    @GetMapping(value="getMaintenAssetType")
    @SysRequestLog(description="运维审批获取可选择的资产类型", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="运维审批获取可选择的资产类型",notes="")
    public Result<List<AssetTypeGroup>> getMaintenAssetType(){
        return baseAuthPrintBurnService.getMaintenAssetType();
    }
    @PostMapping(value="/checkImportData")
    @ApiOperation(value="数据导入校验",notes="")
    @SysRequestLog(description="数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file,@RequestParam("code") Integer code){
        Map<String, List<Map<String, Object>>> map = baseAuthService.checkImportData(file,code);
        if(map==null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "导入文件解析异常");
        }
        Result<Map<String, List<Map<String, Object>>>> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        result.setData(map);
        return result;
    }
    @PostMapping(value="/saveList")
    @ApiOperation(value="导入数据入库",notes="")
    @SysRequestLog(description="导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody Map<String,Object> map){
        baseAuthService.saveList(map);
        // 数据变更消息推送 2022-06-01
        return ResultUtil.success(true);
    }
    @GetMapping(value="/downloadExportTemplate/{code}")
    @SysRequestLog(description="下载审批模板", actionType = ActionType.EXPORT,manually=false)
    @ApiOperation(value="下载审批模板",notes="")
    public  void downloadExportTemplate(HttpServletResponse response, @PathVariable("code") Integer code){
        String nameByCode = BaseAuthEnum.getNameByCode(code);
        FileTemplateUtil.downloadExportTemplate(response,nameByCode+"导入模板");
    }
    @GetMapping(value="/getBaseAuthList")
    @SysRequestLog(description="获取审批类型列表", actionType = ActionType.EXPORT,manually=false)
    @ApiOperation(value="获取审批类型列表",notes="")
    public  Result<List<Map<String,Object>>> getBaseAuthList(){
        List<Map<String,Object>> mapList=new ArrayList<>();
        List<BaseAuthEnum> list = BaseAuthEnum.list();
        for (BaseAuthEnum baseAuthEnum:list){
            Map<String,Object> map=new HashMap<>();
            map.put("name",baseAuthEnum.getName());
            map.put("code",baseAuthEnum.getCode());
            mapList.add(map);
        }
       return ResultUtil.successList(mapList);
    }
    @PostMapping(value="/appPage")
    @SysRequestLog(description="应用系统审批信息分页查询", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="应用系统审批信息分页查询",notes="")
    public PageRes<BaseAuthAppVo> getPager(@RequestBody BaseAuthAppQueryVo baseAuthAppQueryVo){
        PageRes<BaseAuthAppVo> pageRes = baseAuthAppService.getPager(baseAuthAppQueryVo);
        return pageRes;
    }
    @PutMapping(value="addAuthApp")
    @SysRequestLog(description="应用访问审批信息保存", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="应用访问审批信息保存",notes="")
    public Result<BaseAuthAppVo> addAuthApp(@RequestBody BaseAuthAppVo baseAuthAppVo){
        try{
            return baseAuthAppService.addAuthApp(baseAuthAppVo);
        }catch (Exception e){
            logger.error("应用访问审批信息保存异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用访问审批信息保存异常");
        }
    }
    @PostMapping(value="updateAuthApp")
    @SysRequestLog(description="应用访问审批信息编辑", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="应用访问审批信息编辑",notes="")
    public Result<BaseAuthAppVo> updateAuthApp(@RequestBody BaseAuthAppVo baseAuthAppVo){
        try{
            return baseAuthAppService.updateAuthApp(baseAuthAppVo);
        }catch (Exception e){
            logger.error("应用访问审批信息编辑异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用访问审批信息编辑异常");
        }
    }

    @PostMapping(value="delAuthApp")
    @SysRequestLog(description="删除应用访问审批信息", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="删除应用访问审批信息",notes="")
    public Result<String> delAuthApp(@RequestBody BaseAuthAppVo baseAuthAppVo){
        try{
            return baseAuthAppService.delAuthApp(baseAuthAppVo);
        }catch (Exception e){
            logger.error("删除应用访问审批信息异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "删除应用访问审批信息异常");
        }
    }
    @PutMapping(value="addAuthInt")
    @SysRequestLog(description="网络互联审批信息保存", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="网络互联审批信息保存",notes="")
    public Result<BaseAuthInternetVo> addAuthInt(@RequestBody BaseAuthInternetQueryVo baseAuthInternet){
        try{
            return baseAuthInternetService.addAuthInt(baseAuthInternet);
        }catch (Exception e){
            logger.error("网络互联审批信息保存异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络互联审批信息保存保存异常");
        }
    }
    @PostMapping(value="delAuthInt")
    @SysRequestLog(description="删除网络互联审批信息", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="删除网络互联审批信息",notes="")
    public Result<String> delAuthInt(@RequestBody BaseAuthInternetQueryVo baseAuthInternet){
        try{
            return baseAuthInternetService.delAuthInt(baseAuthInternet);
        }catch (Exception e){
            logger.error("删除应用访问审批信息异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "删除网络互联审批信息");
        }
    }
    @PostMapping(value="updateAuthInt")
    @SysRequestLog(description="网络互联审批信息编辑", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="网络互联审批信息编辑",notes="")
    public Result<BaseAuthInternetVo> updateAuthInt(@RequestBody BaseAuthInternetQueryVo baseAuthInternetQueryVo){
        try{
            return baseAuthInternetService.updateAuthInt(baseAuthInternetQueryVo);
        }catch (Exception e){
            logger.error("应用访问审批信息编辑异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络互联审批信息编辑异常");
        }
    }
    @PostMapping(value="/intPage")
    @SysRequestLog(description="网络互联审批信息分页查询", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="网络互联审批信息分页查询",notes="")
    public PageRes<BaseAuthInternetVo> intPage(@RequestBody BaseAuthInternetQueryVo baseAuthInternetQueryVo){
        PageRes<BaseAuthInternetVo> pageRes = baseAuthInternetService.intPage(baseAuthInternetQueryVo);
        return pageRes;
    }
    @GetMapping("getOperationIps")
    @ApiOperation(value="获取全部运维终端ip",notes="")
    @SysRequestLog(description="获取全部运维终端ip", actionType = ActionType.SELECT,manually=false)
    public Result<List<String>> getOperationIps(){
        AssetTypeGroup assetTypeGroup=new AssetTypeGroup();
        assetTypeGroup.setTreeCode("asset-MaintenHost");
        return baseAuthPrintBurnService.getIpsByAssetType(assetTypeGroup);
    }
    @PutMapping(value="addAuthOperation")
    @SysRequestLog(description="运维审批信息保存", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="运维审批信息保存",notes="")
    public Result<BaseAuthoOperationVo> addAuthOperation(@RequestBody BaseAuthoOperationVo baseAuthOperation){
        try{
            return baseAuthOperationService.addAuthOperation(baseAuthOperation);
        }catch (Exception e){
            logger.error("运维审批信息保存异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "网络互联审批信息保存保存异常");
        }
    }
    @PostMapping(value="updateAuthOperation")
    @SysRequestLog(description="运维审批信息编辑", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="运维审批信息编辑",notes="")
    public Result<BaseAuthoOperationVo> updateAuthOperation(@RequestBody BaseAuthoOperationVo baseAuthOperation){
        try{
            return baseAuthOperationService.updateAuthOperation(baseAuthOperation);
        }catch (Exception e){
            logger.error("运维审批信息编辑异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "运维审批信息编辑异常");
        }
    }
    @PostMapping(value="delAuthOperation")
    @SysRequestLog(description="删除运维审批信息", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="删除运维审批信息",notes="")
    public Result<String> delAuthOperation(@RequestBody BaseAuthOperation baseAuthOperation){
        try{
            return baseAuthOperationService.delAuthOperation(baseAuthOperation);
        }catch (Exception e){
            logger.error("删除运维审批信息异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "删除运维审批信息异常");
        }
    }
    @PostMapping(value="/operationPage")
    @SysRequestLog(description="运维审批信息分页查询", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="运维审批信息分页查询",notes="")
    public PageRes<BaseAuthoOperationVo> operationPage(@RequestBody BaseAuthInternetQueryVo baseAuthInternetQueryVo){
        PageRes<BaseAuthoOperationVo> pageRes = baseAuthOperationService.operationPage(baseAuthInternetQueryVo);
        return pageRes;
    }
    @PostMapping(value="/exportInfo")
    @ApiOperation(value="审批信息文件导出",notes="")
    @SysRequestLog(description="审批信息文件导出", actionType = ActionType.EXPORT,manually=false)
    public  Result<String> exportInfo(@RequestBody Map<String,Object> map){
        return baseAuthService.exportInfo(map);
    }

}
