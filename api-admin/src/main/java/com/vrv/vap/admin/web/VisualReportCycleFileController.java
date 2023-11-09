package com.vrv.vap.admin.web;


import com.vrv.vap.admin.vo.AlarmItemVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.VisualReportCycleFile;
import com.vrv.vap.admin.service.VisualReportCycleFileService;
import com.vrv.vap.admin.service.VisualReportCycleService;
import com.vrv.vap.admin.vo.VisualReportCatalogQuery;
import com.vrv.vap.admin.vo.VisualReportCycleFileQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
* @BelongsPackage com.sa.platform.bussiness.web
* @Author CodeGenerator
* @CreateTime 2020/09/10
* @Description (VisualReportCycleFile相关接口)
* @Version
*/
@RestController
@Api(value = "VisualReportCycleFile")
@RequestMapping("/report/file")
public class VisualReportCycleFileController extends ApiController {

    @Autowired
    private VisualReportCycleFileService visualReportCycleFileService;

    @Autowired
    private VisualReportCycleService visualReportCycleService;

    /**
    * 获取所有数据--VisualReportCycleFile
    */
    @ApiOperation(value = "获取所有VisualReportCycleFile")
    @SysRequestLog(description="获取所有周期报表文件", actionType = ActionType.SELECT)
    @GetMapping
    public VData< List<VisualReportCycleFile>> getAllVisualReportCycleFile() {
        List<VisualReportCycleFile> list = visualReportCycleFileService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加VisualReportCycleFile")
    @SysRequestLog(description="添加周期报表文件", actionType = ActionType.ADD)
    @PutMapping
    public Result addVisualReportCycleFile(@RequestBody VisualReportCycleFile visualReportCycleFile) {
        int result = visualReportCycleFileService.save(visualReportCycleFile);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(visualReportCycleFile,"添加周期报表文件");
        }
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改VisualReportCycleFile", hidden = false)
    @SysRequestLog(description="修改周期报表文件", actionType = ActionType.UPDATE)
    @PatchMapping
    public Result updateVisualReportCycleFile(@RequestBody VisualReportCycleFile  visualReportCycleFile) {
        VisualReportCycleFile reportCycleFileSec = visualReportCycleFileService.findById(visualReportCycleFile.getId());
        int result = visualReportCycleFileService.update(visualReportCycleFile);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(reportCycleFileSec,visualReportCycleFile,"修改周期报表文件");
        }
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除VisualReportCycleFile")
    @SysRequestLog(description="删除周期报表文件", actionType = ActionType.DELETE)
    @DeleteMapping
    public Result delVisualReportCycleFile(@RequestBody DeleteQuery deleteQuery) {
        List<VisualReportCycleFile> fileList = visualReportCycleFileService.findByids(deleteQuery.getIds());
        int result = visualReportCycleFileService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            fileList.forEach(visualReportCycleFile -> {
                SyslogSenderUtils.sendDeleteSyslog(visualReportCycleFile,"删除周期报表文件");
            });
        }
        return this.result(result == 1);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询VisualReportCycleFile（分页）")
    @SysRequestLog(description="查询周期报表文件", actionType = ActionType.SELECT)
    @PostMapping
    public VList<VisualReportCycleFile> queryVisualReportCycleFile(@RequestBody VisualReportCycleFileQuery queryVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(queryVo, VisualReportCycleFile.class);
        List<VisualReportCycleFile> list =  visualReportCycleFileService.findByExample(example);
        return this.vList(list);
    }

    /**
     * 最新生成的报表TOP
     */
    @ApiOperation(value = "最新生成的报表TOP")
    @SysRequestLog(description="最新生成的报表TOP", actionType = ActionType.SELECT)
    @PostMapping("/top")
    public Result queryFileTop(@RequestBody VisualReportCatalogQuery queryVo) {
        Example example = this.pageQuery(queryVo, VisualReportCycleFile.class);
        example.orderBy("createTime").desc();
        if(queryVo.getStartTime()!=null){
            example.getOredCriteria().get(0).andGreaterThan("createTime",queryVo.getStartTime());
        }
        if(queryVo.getEndTime()!=null){
            example.getOredCriteria().get(0).andLessThan("createTime",queryVo.getEndTime());
        }

        List<VisualReportCycleFile> list =  visualReportCycleFileService.findByExample(example);
        return this.vData(list);
    }



    /**
     * 周期报表生成情况
     */
    @ApiOperation(value = "周期报表生成情况")
    @SysRequestLog(description="周期报表生成情况", actionType = ActionType.SELECT)
    @PostMapping("/trend")
    public Result queryVisualReportCycle(@RequestBody VisualReportCatalogQuery queryVoOrg) {
        VisualReportCatalogQuery queryVo = new VisualReportCatalogQuery();
        BeanUtils.copyProperties(queryVoOrg,queryVo);
        List<Map> list =  visualReportCycleFileService.getReportTrend(queryVo);
        return this.vData(list);
    }


    @ApiOperation("下载文件")
    @SysRequestLog(description="下载周期报表文件", actionType = ActionType.DOWNLOAD)
    @GetMapping(path = "/download/{id}")
    public void fileDownload(@PathVariable("id")@ApiParam(value="ID") String id, HttpServletResponse response, HttpServletRequest req){
        SyslogSenderUtils.sendDownLosdSyslog();
        VisualReportCycleFile visualReportCycleFile = visualReportCycleFileService.findById(Integer.valueOf(id));
        if (visualReportCycleFile == null) {
            throw new RuntimeException("文件不存在");
        }
        String fileName = visualReportCycleFile.getFileName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if(visualReportCycleFile.getStatus()!=2 || !visualReportCycleService.checkExistFileWithSuffix(visualReportCycleFile.getFileId(),suffix)){
            throw new RuntimeException("文件不存在");
        }

        String filePath ;
        InputStream st = null;
        try {
            filePath = visualReportCycleService.getReportFilePath()+File.separator+visualReportCycleFile.getFileId()+"." + suffix;
            st = new FileInputStream(filePath);

            if(st == null){
                throw new RuntimeException("下载文件流缺失");
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            String agent = req.getHeader("USER-AGENT");
            if(agent != null && agent.toLowerCase(Locale.ENGLISH).indexOf("firefox") > 0)
            {
                fileName = "=?UTF-8?B?" + (new String(Base64Utils.encodeToString(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName =  java.net.URLEncoder.encode(fileName, "UTF-8");
            }
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            IOUtils.copy(st, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(st != null){
                    st.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}