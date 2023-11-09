package com.vrv.vap.alarmdeal.business.baseauth.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthConfig;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthConfigSearchVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthConfigVO;
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
 * 审批信息
 * 2023-08
 * @author liurz
 */
@RestController
@RequestMapping(value="/baseAuthConfig")
@ApiOperation(value= "审批信息")
public class BaseAuthConfigController {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthConfigController.class);

    @Autowired
    private BaseAuthConfigService baseAuthConfigService;


    /**
     * 审批信息分页查询
     *  目前只支持分页，不支持条件查询
     *
     * @param baseAuthConfigSearchVO
     * @return
     */
    @PostMapping(value="")
    @SysRequestLog(description="审批信息分页查询", actionType = ActionType.SELECT,manually=false)
    @ApiOperation(value="审批信息分页查询",notes="")
    public PageRes<BaseAuthConfig> getPager(@RequestBody BaseAuthConfigSearchVO baseAuthConfigSearchVO){
        PageRes<BaseAuthConfig> pageRes = baseAuthConfigService.getPager(baseAuthConfigSearchVO);
        return pageRes;
    }

    /**
     * 审批信息新增
     * @param baseAuthConfigVO
     * @return
     */
    @PutMapping(value="")
    @SysRequestLog(description="审批信息新增", actionType = ActionType.ADD,manually=false)
    @ApiOperation(value="审批信息新增",notes="")
    public Result<String> saveDate(@RequestBody BaseAuthConfigVO baseAuthConfigVO){
        try{
            return  baseAuthConfigService.saveDate(baseAuthConfigVO);
        }catch (Exception e){
            logger.error("审批类型新增异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批信息新增异常");
        }
    }

    /**
     * 审批信息编辑
     * @param baseAuthConfigVO
     * @return
     */
    @PatchMapping(value="")
    @SysRequestLog(description="审批信息编辑", actionType = ActionType.UPDATE,manually=false)
    @ApiOperation(value="审批信息编辑",notes="")
    public Result<String> saveEdit(@RequestBody BaseAuthConfigVO baseAuthConfigVO){
        try{
            return  baseAuthConfigService.saveEdit(baseAuthConfigVO);
        }catch (Exception e){
            logger.error("审批信息编辑异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批信息编辑异常");
        }
    }

    /**
     * 审批信息删除
     * @param param
     * @return
     */
    @DeleteMapping(value="")
    @SysRequestLog(description="审批信息删除", actionType = ActionType.DELETE,manually=false)
    @ApiOperation(value="审批信息删除",notes="")
    public Result<String> delete(@RequestBody Map<String,String> param){
        try{
            logger.debug("审批信息删除："+ JSON.toJSONString(param));
            String ids = String.valueOf(param.get("ids"));
            if(StringUtils.isEmpty(ids)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "ids的值不为空");
            }
            List<String> idStr = Arrays.asList(ids.split(","));
            for(String id : idStr){
                baseAuthConfigService.delete(Integer.parseInt(id));
            }
            return ResultUtil.success("success");
        }catch (Exception e){
            logger.error("审批信息删除异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批信息删除异常");
        }
    }
    /**
     * 文件导入
     * @param file
     * @return
     */
    @PostMapping(value="/importFile")
    @SysRequestLog(description="审批信息文件导入", actionType = ActionType.IMPORT,manually=false)
    @ApiOperation(value="审批信息文件导入",notes="")
    public Result<String> importFile(@RequestParam("file") MultipartFile file) {
        try {
            return baseAuthConfigService.importFile(file);
        } catch (Exception e) {
            logger.error("审批信息文件导入异常:{}",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批信息文件导入异常");
        }
    }

    /**
     * 审批信息模板
     *
     * @param response
     */
    @GetMapping(value="/downloadExportTemplate")
    @SysRequestLog(description="下载审批信息模板", actionType = ActionType.EXPORT,manually=false)
    @ApiOperation(value="下载审批信息模板",notes="")
    public  void downloadExportTemplate(HttpServletResponse response){
        baseAuthConfigService.downloadExportTemplate(response);
    }
}
