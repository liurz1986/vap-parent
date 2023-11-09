package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.NetworkBoundaryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 网络通联
 */
@RestController
@RequestMapping("/network/connectivity")
public class NetworkConnectivityController {

    @Resource
    private NetworkBoundaryService networkBoundaryService;

    /**
     * 网络通联-访问关系图
     * @param model
     * @return
     */
    @PostMapping("/diagram")
    @ApiOperation("网络通联-访问关系图")
    public VData<List<Map<String,Object>>> diagram(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.relationshipDiagram(model);
    }


    /**
     * 网络通联-访问协议分布
     * @param model
     * @return
     */
    @PostMapping("/protocol")
    @ApiOperation("网络通联-访问协议分布")
    public VData<List<Map<String,Object>>> visitProtocol(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.visitProtocolAndPort(model,"app_protocol","protocol");
    }

    /**
     * 网络通联-目的端口分布
     * @param model
     * @return
     */
    @PostMapping("/port")
    @ApiOperation("网络通联-目的端口分布")
    public VData<List<Map<String,Object>>> visitPort(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.visitProtocolAndPort(model,"dport","port");
    }


    /**
     * 网络通联-访问详情
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("网络通联-访问详情")
    public VList<Map<String,String>> detail(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.networkVisitDetail(model);
    }

    /**
     * 网络通联-访问详情-导出
     * @param model
     * @return
     */
    @PostMapping("/export")
    @ApiOperation("网络通联-访问详情-导出")
    public VData<Export.Progress> export(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.networkVisitDetailExport(model);
    }

    /**
     * 网络通联-流量大小排行
     * @param model
     * @return
     */
    @PostMapping("/pckSizeRanking")
    @ApiOperation("网络通联-流量大小排行")
    public VData<List<Map<String,Object>>> pckSizeRanking(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.pckSizeRanking(model);
    }
    /**
     * 网络通联-通联趋势分析
     * @param model
     * @return
     */
    @PostMapping("/trend")
    @ApiOperation("网络通联-通联趋势分析")
    public VData<List<Map<String,Object>>> trend(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.visitTrend(model);
    }

    /**
     * 网络通联-会话次数统计
     * @param model
     * @return
     */
    @PostMapping("/sessionTimes")
    @ApiOperation("网络通联-会话次数统计")
    public VData<List<Map<String,Object>>> sessionTimes(@RequestBody ObjectPortraitModel model){
        return networkBoundaryService.sessionTimes(model);
    }
}
