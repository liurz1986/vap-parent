package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.BusinessAccessService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 业务访问
 */
@RestController
@RequestMapping("/business/access")
public class BusinessAccessController {
    @Resource
    private BusinessAccessService businessAccessService;

    /**
     * 业务访问-访问关系图
     * @param model
     * @return
     */
    @PostMapping("/diagram")
    @ApiOperation("业务访问-访问关系图")
    public VData<List<Map<String,Object>>> diagram(@RequestBody ObjectPortraitModel model){
        return businessAccessService.diagram(model);
    }

    /**
     * 业务访问-被访问URL TOP10
     * @param model
     * @return
     */
    @PostMapping("/urlTop")
    @ApiOperation("业务访问-被访问URL TOP10")
    public VData<List<Map<String,Object>>> urlTop(@RequestBody ObjectPortraitModel model){
        return businessAccessService.urlTop(model);
    }

    /**
     * 业务访问-访问趋势
     * @param model
     * @return
     */
    @PostMapping("/trend")
    @ApiOperation("业务访问-访问趋势")
    public VData<List<Map<String,Object>>> trend(@RequestBody ObjectPortraitModel model){
        return businessAccessService.trend(model);
    }

    /**
     * 业务访问-访问详情
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("业务访问-访问详情")
    public VList<Map<String,String>> detail(@RequestBody ObjectPortraitModel model){
        return businessAccessService.detail(model);
    }

    /**
     * 业务访问-访问详情-导出
     * @param model
     * @return
     */
    @PostMapping("/export")
    @ApiOperation("业务访问-访问详情-导出")
    public VData<Export.Progress> export(@RequestBody ObjectPortraitModel model){
        return businessAccessService.export(model);
    }
}
