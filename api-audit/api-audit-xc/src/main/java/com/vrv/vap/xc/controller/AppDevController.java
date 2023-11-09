package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.AppDevService;
import com.vrv.vap.xc.service.portrait.FileTransferService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class AppDevController {
    @Autowired
    AppDevService appDevService;

    @Resource
    private FileTransferService fileTransferService;
    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }
    
    /**
     *被攻击ip个数
     * @param 
     * @return
     */
    @PostMapping("/appdev/visitDetail")
    @ApiOperation("业务访问-访问列表详情")
    public VList<Map<String,Object>> visitDetail(@RequestBody SysRelationModel model){
        return appDevService.visitDetail(model) ;
    }

    /**
     *运维详情
     * @param model
     * @return
     */
    @PostMapping("/appdev/operationDetail")
    @ApiOperation("运维详情")
    public VList<Map<String,String>> operationDetail(@RequestBody SecurityModel model){
        return appDevService.operationDetail(model) ;
    }

    /**
     *运维详情导出
     * @param model
     * @return
     */
    @PostMapping("/appdev/operationDetailExport")
    @ApiOperation("运维详情导出")
    public VData<Export.Progress> operationDetailExport(@RequestBody SecurityModel model){
        return appDevService.operationDetailExport(model) ;
    }

    /**
     * 文件传输关系图
     * @param model
     * @return
     */
    @PostMapping("/appdev/fileRelationGap")
    @ApiOperation("文件传输关系图")
    public VData<List<Map<String,Object>>> fileRelationGap(@RequestBody SecurityModel model){
        return appDevService.fileRelationGap(model) ;
    }
    /**
     * 上传/下载趋势
     * @param model
     * @return
     */
    @PostMapping("/appdev/fileUpDownTrend")
    @ApiOperation("上传/下载趋势")
    public VData<List<Map<String,Object>>> fileUpDownTrend(@RequestBody ObjectPortraitModel model){
        return fileTransferService.fileUpDownTrend(model);
    }
    /**
     * 上传/下载 文件密级分布
     * @param model
     * @return
     */
    @PostMapping("/appdev/fileLevelCount")
    @ApiOperation("上传/下载 文件密级分布 fileDir 文件传输方向 上传/发送 1 ,下载/接收 2")
    public VData<List<Map<String,Object>>> fileLevelCount(@RequestBody SecurityModel model){
        return appDevService.fileLevelCount(model) ;
    }
    /**
     * 上传/下载 文件类型分布
     * @param model
     * @return
     */
    @PostMapping("/appdev/fileTypeCount")
    @ApiOperation("上传/下载 文件类型分布 fileDir 文件传输方向 上传/发送 1 ,下载/接收 2")
    public VData<List<Map<String,Object>>> fileTypeCount(@RequestBody SecurityModel model){
        return appDevService.fileTypeCount(model) ;
    }
    /**
     *文件列表详情
     * @param model
     * @return
     */
    @PostMapping("/appdev/fileDetail")
    @ApiOperation("文件列表详情")
    public VList<Map<String,String>> fileDetail(@RequestBody SecurityModel model){
        return appDevService.fileDetail(model) ;
    }

    /**
     *文件列表详情导出
     * @param model
     * @return
     */
    @PostMapping("/appdev/fileDetailExport")
    @ApiOperation("文件列表详情导出")
    public VData<Export.Progress> fileDetailExport(@RequestBody SecurityModel model){
        return appDevService.fileDetailExport(model) ;
    }

    /**
     * 交互关系图（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    @PostMapping("/appdev/interactiveRelationGap")
    @ApiOperation("交互关系图（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）")
    public VData<List<Map<String,Object>>> interactiveRelationGap(@RequestBody SysRelationModel model){
        return appDevService.interactiveRelationGap(model) ;
    }
    /**
     * 交互协议分布（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    @PostMapping("/appdev/interactiveProtocol")
    @ApiOperation("交互协议分布（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）")
    public VData<List<Map<String,Object>>> interactiveProtocol(@RequestBody SysRelationModel model){
        return appDevService.interactiveProtocol(model) ;
    }
    /**
     * 流量大小排行（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    @PostMapping("/appdev/netflowBytesCount")
    @ApiOperation("流量大小排行（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）")
    public VData<List<Map<String,Object>>> netflowBytesCount(@RequestBody SysRelationModel model){
        return appDevService.netflowBytesCount(model) ;
    }
    /**
     * 交互趋势分析（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    @PostMapping("/appdev/interactiveTrend")
    @ApiOperation("交互趋势分析（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）")
    public VData<List<Map<String,Object>>> interactiveTrend(@RequestBody SysRelationModel model){
        return appDevService.interactiveTrend(model) ;
    }
    /**
     * 交互列表详情（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    @PostMapping("/appdev/interactiveDetail")
    @ApiOperation("交互列表详情（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）")
    public VList<Map<String,String>> interactiveDetail(@RequestBody SysRelationModel model){
        return appDevService.interactiveDetail(model) ;
    }
    /**
     *交互列表详情导出（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    @PostMapping("/appdev/interactiveDetailExport")
    @ApiOperation("交互列表详情导出（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）")
    public VData<Export.Progress> interactiveDetailExport(@RequestBody SysRelationModel model){
        return appDevService.interactiveDetailExport(model) ;
    }

    /**
     * 业务访问-访问关系图
     * @param model
     * @return
     */
    @PostMapping("/appdev/visitRelationGap")
    @ApiOperation("业务访问-访问关系图")
    public VData<List<Map<String,Object>>> visitRelationGap(@RequestBody SysRelationModel model){
        return appDevService.visitRelationGap(model) ;
    }
    /**
     * 业务访问-访问趋势
     * @param model
     * @return
     */
    @PostMapping("/appdev/visitTrend")
    @ApiOperation("业务访问-访问趋势")
    public VData<List<Map<String,Object>>> visitTrend(@RequestBody SysRelationModel model){
        return appDevService.visitTrend(model) ;
    }

}
