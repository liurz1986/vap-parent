package com.vrv.vap.alarmdeal.business.model.controller;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.model.constant.ModelManageConstant;
import com.vrv.vap.alarmdeal.business.model.model.ModelParamConfig;
import com.vrv.vap.alarmdeal.business.model.service.ModelManageService;
import com.vrv.vap.alarmdeal.business.model.service.ModelParamConfigService;
import com.vrv.vap.alarmdeal.business.model.util.ShellExcUtil;
import com.vrv.vap.alarmdeal.business.model.vo.*;
import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模型管理
 */
@RestController
@RequestMapping("modelmanage")
public class ModelManageController {
    private static Logger logger = LoggerFactory.getLogger(ModelManageController.class);
    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private ModelParamConfigService modelParamConfigService;




    /**
     * 资源导入(新增场景)
     * 1.zip格式
     * 2.zip中包括两个文件：.json、。zip
     * 3.json格式符合规范要求
     * 4.。json、.zip文件名称一致
     * 5.文件名称格式：模型名称_版本号
     * 6.编辑时不允许导入不同模型
     */
    @PostMapping("getImportFile")
    @SysRequestLog(description="模型管理新增资源导入", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型管理新增资源导入",notes="")
    public Result<ImportFileResultVO> getImportFile(@RequestParam("file") MultipartFile file){
        try {
            return modelManageService.parseImportFile(file,null, ModelManageConstant.OperationStatus.ADD);
        } catch (Exception e) {
            logger.error("模型管理资源导入",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 资源导入(编辑场景)
     * 1.zip格式
     * 2.zip中包括两个文件：.json、。zip
     * 3.json格式符合规范要求
     * 4.。json、.zip文件名称一致
     * 5.文件名称格式：模型名称_版本号
     * 6.编辑时不允许导入不同模型
     */
    @PostMapping("getImportFileUpdate")
    @SysRequestLog(description="模型管理编辑资源导入", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型管理编辑资源导入",notes="")
    public Result<ImportFileResultVO> getImportFileUpdate(@RequestParam("file") MultipartFile file, @RequestParam("guid") String guid){
        try {
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型配置guid不能为空！");
            }
            return modelManageService.parseImportFile(file,guid,ModelManageConstant.OperationStatus.EDIT);
        } catch (Exception e) {
            logger.error("模型管理资源导入",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }
    /**
     * 新增
     */
    @PostMapping("save")
    @SysRequestLog(description="模型管理新增", actionType = ActionType.ADD,manually = false)
    @ApiOperation(value="模型管理新增",notes="")
    public Result<String> save(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "参数不能为空");
            }
            return modelManageService.saveModleManage(modelManageVO);

        } catch (Exception e) {
            logger.error("模型管理新增失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 编辑
     */
    @PostMapping("update")
    @SysRequestLog(description="模型管理编辑", actionType = ActionType.UPDATE,manually = false)
    @ApiOperation(value="模型管理编辑",notes="")
    public Result<String> edit(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "参数不能为空");
            }
            if(StringUtils.isEmpty(modelManageVO.getGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空");
            }
            return modelManageService.editModleManage(modelManageVO);
        } catch (Exception e) {
            logger.error("模型管理编辑失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 根据guid删除
     */
    @DeleteMapping("modelDelete/{guid}")
    @SysRequestLog(description="模型管理删除", actionType = ActionType.DELETE,manually = false)
    @ApiOperation(value="模型管理删除",notes="")
    public Result<String> deleteByGuids(@PathVariable("guid") String guid){
        logger.info("模型管理删除,guid:"+guid);
        try {
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "参数不能为空");
            }
            List<String> guids = new ArrayList<>();
            guids.add(guid);
            modelManageService.deleteByGuids(guids);
            return ResultUtil.success("删除成功");
        } catch (Exception e) {
            logger.error("模型管理删除失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 模型发布
     */
    @PostMapping("publish")
    @SysRequestLog(description="模型发布", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型发布",notes="")
    public Result<String> publish(@RequestBody ModelPublishVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            return modelManageService.publish(modelManageVO);
        } catch (Exception e) {
            logger.error("模型发布失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }
    /**
     * 模型启动
     */
    @PostMapping("start")
    @SysRequestLog(description="模型启动", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型启动",notes="")
    public Result<String> start(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelManageService.start(guid);
        } catch (Exception e) {
            logger.error("模型启动失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 模型停用
     */
    @PostMapping("stop")
    @SysRequestLog(description="模型停用", actionType = ActionType.UPDATE,manually = false)
    @ApiOperation(value="模型停用",notes="")
    public Result<String> stop(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelManageService.stop(guid);
        } catch (Exception e) {
            logger.error("模型停用失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }
    /**
     * 测试
     */
    @PostMapping("testShell")
    @SysRequestLog(description="测试", actionType = ActionType.SELECT)
    @ApiOperation(value="测试",notes="")
    public Result<String> testShell(@RequestBody Map<String,String> excParams){
        try {
            String type = excParams.get("type");
            String exccmd=excParams.get("cmd");
          if("1".equalsIgnoreCase(type)){
              return ResultUtil.success(ShellExcUtil.excShellResult(exccmd)+"");
          }else {
              List<String> result = ShellExcUtil.excShellList(exccmd);
              return ResultUtil.success(JSONObject.toJSONString(result));
          }
        } catch (Exception e) {
            logger.error("模型停用失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }
    /**
     * 模型下架
     */
    @PostMapping("downShelf")
    @SysRequestLog(description="模型下架", actionType = ActionType.UPDATE,manually = false)
    @ApiOperation(value="模型下架",notes="")
    public Result<String> downShelf(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelManageService.downShelf(guid);

        } catch (Exception e) {
            logger.error("模型下架失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 获取具体模型全部参数
     */
    @PostMapping("queryParamList")
    @SysRequestLog(description="获取模型全部参数", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="获取模型全部参数",notes="")
    public Result<List<ModelParamConfig>> queryModelParamList(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelParamConfigService.queryModelParamList(guid);
        } catch (Exception e) {
            logger.error("获取模型全部参数失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }


    /**
     * 模型发布：以默认值加载全部参数
     * @param modelManageVO
     * @return
     */
    @PostMapping("queryParamsByGuid")
    @SysRequestLog(description="获取模型全部参数", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="获取模型全部参数",notes="")
    public Result<Map<String,Object>> queryParamConfigByGuid(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelParamConfigService.queryParamConfigByGuid(guid);
        } catch (Exception e) {
            logger.error("获取模型全部参数失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 模型发布：参数名称下拉列表
     * @param modelManageVO
     * @return
     */
    @PostMapping("queryParamNamesByGuid")
    @SysRequestLog(description="获取模型全部参数", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="获取模型全部参数",notes="")
    public Result<List<String>> queryParamNamesByGuid(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelParamConfigService.queryParamNamesByGuid(guid);
        } catch (Exception e) {
            logger.error("获取模型全部参数失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 获取模型版本信息
     */
    @PostMapping("getVersions")
    @SysRequestLog(description="获取模型版本信息", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="获取模型版本信息",notes="")
    public Result<List<ModelVersionVO>> queryModelVersions(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String modelId = modelManageVO.getModelId();
            if(StringUtils.isEmpty(modelId)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型id不能为空！");
            }
            return modelManageService.queryModelVersions(modelId);
        } catch (Exception e) {
            logger.error("获取模型版本信息失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }


    /**
     * 根据模型配置id获取模型配置信息
     */
    @PostMapping("getModelMessage")
    @SysRequestLog(description="根据模型配置id获取模型配置信息", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="根据模型配置id获取模型配置信息",notes="")
    public Result<ModelManageVO> getModelManageByGuid(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型配置id不能为空！");
            }
            return modelManageService.getModelManageByGuid(guid);
        } catch (Exception e) {
            logger.error("根据模型配置id获取模型配置信息失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
        }
    }

    /**
     * 版本切换
     */
    @PostMapping("changeVersion")
    @SysRequestLog(description="版本切换", actionType = ActionType.UPDATE,manually = false)
    @ApiOperation(value="版本切换",notes="")
    public Result<String> changeVersion(@RequestBody ModelVersionChangeVO modelVersionChangeVO){
        try {
            if(null == modelVersionChangeVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            if(StringUtils.isEmpty(modelVersionChangeVO.getOldGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "切换前模型guid为空！");
            }
            if(StringUtils.isEmpty(modelVersionChangeVO.getNewGuid())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "切换后模型guid为空！");
            }
            String oldGuid = modelVersionChangeVO.getOldGuid();
            String newGuid = modelVersionChangeVO.getNewGuid();
            if(oldGuid.equalsIgnoreCase(newGuid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "同一个版本不用切换！");
            }
            modelManageService.changeVersion(modelVersionChangeVO);
            return ResultUtil.success("版本切换成功");
        } catch (Exception e) {
            logger.error("版本切换异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 模型管理分页查询
     */
    @PostMapping("getModelManagePage")
    @SysRequestLog(description="模型管理分页查询", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型管理分页查询",notes="")
    public PageRes<ModelManageVO> getModelManagePage(@RequestBody ModelManageSearchVO search){
        PageRes<ModelManageVO> errorMsg = new PageRes<ModelManageVO>();
        if(search==null) {
            search=new ModelManageSearchVO();
        }
        if(search.getCount_()==null||search.getCount_()<0) {
            search.setCount_(10);
        }

        if(search.getStart_()==null||search.getStart_()<0) {
            search.setStart_(0);
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(search.getType())) {
            errorMsg.setCode(String.valueOf(ResultCodeEnum.UNKNOW_FAILED.getCode()));
            errorMsg.setMessage("type值不能为空");
            return errorMsg;
        }
        PageReq pageReq=new PageReq();
        pageReq.setCount(search.getCount_());
        pageReq.setBy("desc");
        pageReq.setOrder("createTime");
        pageReq.setStart(search.getStart_());
        try {
            return modelManageService.getModelManagePage(search,pageReq.getPageable());
        } catch (Exception e) {
            errorMsg.setCode(String.valueOf(ResultCodeEnum.UNKNOW_FAILED.getCode()));
            errorMsg.setMessage("模型管理分页查询异常："+e.getMessage());
            logger.error("模型管理分页查询失败",e);
            return errorMsg;
        }
    }

    /**
     * 模型可用性测试（一期不需要了）
     */
    @PostMapping("test")
    @SysRequestLog(description="模型可用性测试", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型可用性测试",notes="")
    public Result<Map<String,Object>> modelTest(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型管理guid不能为空！");
            }
            return modelManageService.modelTest(guid);
        } catch (Exception e) {
            logger.error("模型可用性测试失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 模型可用性测试状态（一期不需要了）
     */
    @PostMapping("testStatus")
    @SysRequestLog(description="模型可用性测试状态", actionType = ActionType.SELECT,manually = false)
    @ApiOperation(value="模型可用性测试状态",notes="")
    public Result<Map<String,Object>> testStatus(@RequestBody ModelManageVO modelManageVO){
        try {
            if(null == modelManageVO){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有参数！");
            }
            String guid = modelManageVO.getGuid();
            if(StringUtils.isEmpty(guid)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "模型可用性测试guid不能为空！");
            }
            return modelManageService.modelTestStatus(guid);
        } catch (Exception e) {
            logger.error("模型可用性测试状态失败",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }


}
