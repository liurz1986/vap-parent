package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.LocalOperationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 本地操作
 */
@RestController
@RequestMapping("/local/operation")
public class LocalOperationController {
    @Autowired
    private LocalOperationService localOperationService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    /**
     * 本地登录-登录次数
     *
     * @param model
     * @return
     */
    @PostMapping("/loginCount")
    @ApiOperation("本地登录-登录次数")
    public VData<List<Map<String, Object>>> loginCount(@RequestBody ObjectPortraitModel model) {
        return localOperationService.loginCount(model, model.isWork());
    }

    /**
     * 本地登录-登录次数详情
     *
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("本地登录-登录次数详情")
    public VList<Map<String, String>> loginCountDetail(@RequestBody ObjectPortraitModel model) {
        return localOperationService.loginCountDetail(model, model.isWork());
    }

    /**
     * 登录次数详情导出
     *
     * @param model
     * @return
     */
    @PostMapping("/loginCountDetailExport")
    @ApiOperation("登录次数详情导出")
    public VData<Export.Progress> loginCountDetailExport(@RequestBody ObjectPortraitModel model) {
        return localOperationService.loginCountDetailExport(model);
    }

    /**
     * 使用专用介质数量
     *
     * @param model
     * @return
     */
    @PostMapping("/mediumUse")
    @ApiOperation("使用专用介质数量")
    public VData<List<Map<String, Object>>> mediumUse(@RequestBody ObjectPortraitModel model) {
        return localOperationService.mediumUse(model);
    }

    /**
     * 使用专用介质 频次分析
     *
     * @param model
     * @return
     */
    @PostMapping("/mediumUseFrequency")
    @ApiOperation("使用专用介质 频次分析")
    public VData<List<Map<String, Object>>> mediumUseFrequency(@RequestBody ObjectPortraitModel model) {
        return localOperationService.mediumUseFrequency(model);
    }

    /**
     * 专用介质使用频次详情
     *
     * @param model
     * @return
     */
    @PostMapping("/mediumUseDetail")
    @ApiOperation("专用介质使用频次详情")
    public VList<Map<String, String>> mediumUseDetail(@RequestBody ObjectPortraitModel model) {
        return localOperationService.mediumUseDetail(model);
    }

    /**
     * 专用介质使用数量详情
     *
     * @param model
     * @return
     */
    @PostMapping("/mediumUseNumDetail")
    @ApiOperation("专用介质使用数量详情")
    public VList<Map<String, String>> mediumUseNumDetail(@RequestBody ObjectPortraitModel model) {
        return localOperationService.mediumUseNumDetail(model);
    }

    /**
     * 专用介质使用数量详情导出
     *
     * @param model
     * @return
     */
    @PostMapping("/mediumUseNumDetailExport")
    @ApiOperation("专用介质使用数量详情导出")
    public VData<Export.Progress> mediumUseNumDetailExport(@RequestBody ObjectPortraitModel model) {
        return localOperationService.mediumUseNumDetailExport(model);
    }

    /**
     * 介质使用详情导出
     *
     * @param model
     * @return
     */
    @PostMapping("/mediumUseDetailExport")
    @ApiOperation("介质使用详情导出")
    public VData<Export.Progress> mediumUseDetailExport(@RequestBody ObjectPortraitModel model) {
        return localOperationService.mediumUseDetailExport(model);
    }

    /**
     * 打印/刻录 文件数量分析
     *
     * @param model
     * @return
     */
    @PostMapping("/printOrBurnCount")
    @ApiOperation("打印/刻录 文件数量分析")
    public VData<List<Map<String, Object>>> printOrBurnCount(@RequestBody ObjectPortraitModel model) {
        return localOperationService.printOrBurnCount(model);
    }

    /**
     * 打印/刻录 频次分析
     *
     * @param model
     * @return
     */
    @PostMapping("/printOrBurnFrequency")
    @ApiOperation("打印/刻录 频次分析")
    public VData<List<Map<String, Object>>> printOrBurnFrequency(@RequestBody ObjectPortraitModel model) {
        return localOperationService.printOrBurnFrequency(model);
    }

    /**
     * 打印/刻录-详情
     *
     * @param model
     * @return
     */
    @PostMapping("/printOrBurnCountDetail")
    @ApiOperation("打印/刻录-详情")
    public VList<Map<String, String>> printOrBurnCountDetail(@RequestBody ObjectPortraitModel model) {
        return localOperationService.printOrBurnCountDetail(model);
    }

    /**
     * 打印/刻录详情-导出
     *
     * @param model
     * @return
     */
    @PostMapping("/printOrBurnCountDetailExport")
    @ApiOperation("打印详情/刻录详情-导出")
    public VData<Export.Progress> printOrBurnCountDetailExport(@RequestBody ObjectPortraitModel model) {
        return localOperationService.printOrBurnCountDetailExport(model);
    }

    /**
     * 打印/刻录 涉密文件数量分析
     *
     * @param model
     * @return
     */
    @PostMapping("/printOrBurnLevelCount")
    @ApiOperation("打印/刻录 涉密文件数量分析")
    public VData<List<Map<String, Object>>> printOrBurnLevelCount(@RequestBody ObjectPortraitModel model) {
        return localOperationService.printOrBurnLevelCount(model);
    }

}
