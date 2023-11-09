package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.SecurityModel;
import com.vrv.vap.xc.model.SysRelationModel;
import com.vrv.vap.xc.service.AppDevService;
import com.vrv.vap.xc.service.NetworkDevService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class NetworkDevController {
    @Autowired
    NetworkDevService networkDevService;
    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }
    

    /**
     *运维详情
     * @param model
     * @return
     */
    @PostMapping("/netdev/operationDetail")
    @ApiOperation("运维详情")
    VList<Map<String,String>> operationDetail(@RequestBody SecurityModel model){
        return networkDevService.operationDetail(model) ;
    }

    /**
     *运维详情导出
     * @param model
     * @return
     */
    @PostMapping("/netdev/operationDetailExport")
    @ApiOperation("运维详情导出")
    VData<Export.Progress> operationDetailExport(@RequestBody SecurityModel model){
        return networkDevService.operationDetailExport(model) ;
    }

    /**
     *互联边界流量情况
     * @param model
     * @return
     */
    @PostMapping("/netdev/interconnectionNetInfo")
    @ApiOperation("互联边界流量情况")
    VList<Map<String,String>> interconnectionNetInfo(@RequestBody SecurityModel model){
        return networkDevService.interconnectionNetInfo(model) ;
    }

    /**
     *互联边界流量情况导出
     * @param model
     * @return
     */
    @PostMapping("/netdev/interconnectionNetInfoExport")
    @ApiOperation("互联边界流量情况导出")
    VData<Export.Progress> interconnectionNetInfoExport(@RequestBody SecurityModel model){
        return networkDevService.interconnectionNetInfoExport(model) ;
    }

    

}
