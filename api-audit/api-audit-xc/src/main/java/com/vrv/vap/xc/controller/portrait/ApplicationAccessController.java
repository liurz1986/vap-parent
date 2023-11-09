package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.ApplicationAccessService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 应用访问
 */
@RestController
@RequestMapping("/application/access")
public class ApplicationAccessController {
    @Resource
    private ApplicationAccessService applicationAccessService;

    /**
     * 应用访问-访问关系图
     * @param model
     * @return
     */
    @PostMapping("/diagram")
    @ApiOperation("应用访问-访问关系图")
    public VData<List<Map<String,Object>>> diagram(@RequestBody ObjectPortraitModel model){
        return applicationAccessService.diagram(model);
    }

    /**
     * 应用访问-应用url访问次数
     * @param model
     * @return
     */
    @PostMapping("/urlTimes")
    @ApiOperation("应用访问-应用url访问次数")
    public VData<List<Map<String,Object>>> urlTimes(@RequestBody ObjectPortraitModel model){
        return applicationAccessService.urlTimes(model);
    }

    /**
     * 应用访问-访问趋势
     * @param model
     * @return
     */
    @PostMapping("/trend")
    @ApiOperation("应用访问-访问趋势")
    public VData<List<Map<String,Object>>> trend(@RequestBody ObjectPortraitModel model){
        return applicationAccessService.trend(model);
    }

    /**
     * 应用访问-访问时长趋势
     * @param model
     * @return
     */
    @PostMapping("/duration/trend")
    @ApiOperation("应用访问-访问时长趋势")
    public VData<List<Map<String,Object>>> durationTrend(@RequestBody ObjectPortraitModel model){
        return applicationAccessService.durationTrend(model);
    }

    /**
     *应用访问-访问详情
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("应用访问-访问详情")
    public VList<Map<String,String>> detail(@RequestBody ObjectPortraitModel model){
        return applicationAccessService.detail(model);
    }

    /**
     * 应用访问-访问详情-导出
     * @param model
     * @return
     */
    @PostMapping("/export")
    @ApiOperation("应用访问-访问详情-导出")
    public VData<Export.Progress> export(@RequestBody ObjectPortraitModel model){
        return applicationAccessService.export(model);
    }
}
