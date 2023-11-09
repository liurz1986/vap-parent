package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.BoundaryFlowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 边界流量
 */
@RestController
@RequestMapping("/boundary/flow")
public class BoundaryFlowController {

    @Resource
    private BoundaryFlowService boundaryFlowService;

    /**
     * 边界流量-近一月通信总包数
     *
     * @param model 请求参数
     * @return 近一月通信总包数
     */
    @PostMapping("/communicationTotalPkt")
    @ApiOperation("边界流量-近一月通信总包数")
    public VData<List<Map<String, Object>>> communicationTotalPkt(@RequestBody ObjectPortraitModel model) {
        return boundaryFlowService.communicationTotalPkt(model);
    }

    /**
     * 边界流量-发送和接收流量大小趋势
     *
     * @param model 请求参数
     * @return 发送或接收流量大小总和
     */
    @PostMapping("/sendReceiveFlowTrend")
    @ApiOperation("边界流量-发送和接收流量大小趋势")
    public VData<List<Map<String, Object>>> sendReceiveFlowTrend(@RequestBody ObjectPortraitModel model) {
        return boundaryFlowService.sendReceiveFlowTrend(model);
    }

    /**
     * 边界流量-通信总包数统计趋势
     *
     * @param model 请求参数
     * @return 通信总包数
     */
    @PostMapping("/communicationTotalTrend")
    @ApiOperation("边界流量-通信总包数统计趋势")
    public VData<List<Map<String, Object>>>  communicationTotalTrend(@RequestBody ObjectPortraitModel model) {
        return boundaryFlowService.communicationTotalTrend(model);
    }

    /**
     * 边界流量-访问详情
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("边界流量-访问详情")
    public VList<Map<String,String>> detail(@RequestBody ObjectPortraitModel model){
        return boundaryFlowService.detail(model);
    }

    /**
     * 边界流量-详情导出
     *
     * @param model 请求参数
     * @return
     */
    @PostMapping("/export")
    @ApiOperation("边界流量-详情导出")
    public VData<Export.Progress> export(@RequestBody ObjectPortraitModel model) {
        return boundaryFlowService.export(model);
    }
}
