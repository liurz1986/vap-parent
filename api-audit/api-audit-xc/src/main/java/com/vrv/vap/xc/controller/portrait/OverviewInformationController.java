package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.DevModel;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.OverviewInformationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 概况信息
 */
@RestController
@RequestMapping("/overView")
public class OverviewInformationController {

    @Resource
    private OverviewInformationService overviewInformationService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder){
        binder.setDisallowedFields(new String[]{});
    }

    /**
     * 软件安装情况
     *
     * @return
     */
    @PostMapping("/softwareInstallation")
    @ApiOperation("软件安装情况")
    public VData<List<Map<String, String>>> softwareInstallation(@RequestBody DevModel model) {
        return overviewInformationService.softwareInstallation(model);
    }

    /**
     * 病毒感染情况
     */
    @PostMapping("/virusInfection")
    @ApiOperation("病毒感染情况")
    public VData<List<Map<String, String>>> virusInfection(@RequestBody DevModel model) {
        return overviewInformationService.virusInfection(model);
    }

    /**
     * 文件密级分布
     */
    @PostMapping("/fileLevel")
    @ApiOperation("文件密级分布")
    public VData<List<Map<String, Object>>> fileLevel(@RequestBody DevModel model) {
        return overviewInformationService.fileLevel(model);
    }

    /**
     * 文件密级分布详情
     */
    @PostMapping("/fileLevelDetail")
    @ApiOperation("文件密级分布详情(参数 fileLevel)")
    public VList<Map<String, String>> fileLevelDetail(@RequestBody ObjectPortraitModel model) {
        return overviewInformationService.fileLevelDetail(model);
    }

    /**
     * CPU占用率
     *
     * @param model
     * @return
     */
    @PostMapping("/cpuInfo")
    @ApiOperation("CPU占用率")
    public VData<Map<String, Object>> cpuInfo(@RequestBody DevModel model) {
        return overviewInformationService.cpuInfo(model);
    }

    /**
     * 内存占用率
     *
     * @param model
     * @return
     */
    @PostMapping("/memoryInfo")
    @ApiOperation("内存占用率")
    public VData<Map<String, Object>> memoryInfo(@RequestBody DevModel model) {
        return overviewInformationService.memoryInfo(model);
    }

    /**
     * 磁盘占用率
     *
     * @param model
     * @return
     */
    @PostMapping("/diskInfo")
    @ApiOperation("磁盘占用率")
    public VData<List<Map<String, Object>>> diskInfo(@RequestBody DevModel model) {
        return overviewInformationService.diskInfo(model);
    }


    @PostMapping("/trajectoryAnalysis")
    @ApiOperation("行为轨迹分析")
    public VList<Map<String, String>> trajectoryAnalysis(@RequestBody DevModel model) {
        return overviewInformationService.trajectoryAnalysis(model);
    }
}
