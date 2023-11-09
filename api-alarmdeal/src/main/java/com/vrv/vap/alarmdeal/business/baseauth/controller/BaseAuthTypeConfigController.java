package com.vrv.vap.alarmdeal.business.baseauth.controller;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthTypeConfig;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthTypeConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthTypeConfigSearchVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthTypeConfigVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 审批类型配置
 *  2023-08
 * @author liurz
 */
@RestController
@RequestMapping(value="/baseAuthTypeConfig")
@ApiOperation(value= "审批类型配置")
public class BaseAuthTypeConfigController {

    private static Logger logger = LoggerFactory.getLogger(BaseAuthTypeConfigController.class);

    @Autowired
    private BaseAuthTypeConfigService baseAuthTypeConfigService;


    @PostMapping(value="")
    @SysRequestLog(description="分页查询", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="分页查询",notes="")
    public PageRes<BaseAuthTypeConfig> getPager(@RequestBody BaseAuthTypeConfigSearchVO baseAuthTypeConfigSearchVO){
        PageRes<BaseAuthTypeConfig> pageRes = baseAuthTypeConfigService.getPager(baseAuthTypeConfigSearchVO);
        return pageRes;
    }
    @PutMapping(value="")
    @SysRequestLog(description="审批类型配置保存", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="审批类型配置保存",notes="")
    public Result<String> save(@RequestBody BaseAuthTypeConfigVO baseAuthTypeConfigVO){
        try{
            return baseAuthTypeConfigService.saveData(baseAuthTypeConfigVO);
        }catch (Exception e){
            logger.error("审批类型配置保存异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型配置保存异常");
        }
    }

    /**
     * 删除
     * @param param
     * @return
     */
    @DeleteMapping(value="")
    @SysRequestLog(description="审批类型配置删除", actionType = ActionType.DELETE,manually=false)
    @ApiOperation(value="审批类型配置删除",notes="")
    public Result<String> delete(@RequestBody Map<String,Object> param){
        try{
            logger.debug("审批类型配置删除："+ JSON.toJSONString(param));
            String ids = String.valueOf(param.get("ids"));
            if(StringUtils.isEmpty(ids)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "ids的值不为空");
            }
            List<String> idStr = Arrays.asList(ids.split(","));
            return baseAuthTypeConfigService.deleteByIds(idStr);
        }catch (Exception e){
            logger.error("审批类型配置删除异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型配置保存异常");
        }
    }

    /**
     * 文件导入
     * @param file
     * @return
     */
    @PostMapping(value="/importFile")
    @SysRequestLog(description="审批类型配置文件导入", actionType = ActionType.IMPORT,manually=false)
    @ApiOperation(value="审批类型配置文件导入",notes="")
    public Result<String> importFile(@RequestParam("file") MultipartFile file) {
        try {
            return baseAuthTypeConfigService.importFile(file);
        } catch (Exception e) {
            logger.error("审批类型配置文件导入异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型配置文件导入异常");
        }
    }

    @GetMapping(value="/downloadExportTemplate")
    @SysRequestLog(description="下载审批类型配置模板", actionType = ActionType.EXPORT,manually=false)
    @ApiOperation(value="下载审批类型配置模板",notes="")
    public  void downloadExportTemplate(HttpServletResponse response){
        baseAuthTypeConfigService.downloadExportTemplate(response);
    }

    @GetMapping(value="/getTypeConfigTree")
    @SysRequestLog(description="审批信息左边树结构数据", actionType = ActionType.EXPORT,manually=false)
    @ApiOperation(value="审批信息左边树结构数据",notes="")
    public  Result<List<BaseAuthTypeConfig>> getTypeConfigTree(){
        try {
            return ResultUtil.successList(baseAuthTypeConfigService.findAll());
        } catch (Exception e) {
            logger.error("审批信息左边树结构数据异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批信息左边树结构数据异常");
        }
    }

}
