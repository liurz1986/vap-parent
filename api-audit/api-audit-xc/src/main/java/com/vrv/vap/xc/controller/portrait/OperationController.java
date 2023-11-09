package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.OperationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 运维管理
 */
@RestController
@RequestMapping("/operation")
public class OperationController {
    @Autowired
    private OperationService operationService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    /**
     * 运维次数排名
     */
    @PostMapping("/number")
    @ApiOperation("运维次数排名")
    public VData<List<Map<String, Object>>> number(@RequestBody ObjectPortraitModel model) {
        return operationService.number(model);
    }

    /**
     * 运维协议分布
     *
     * @param model
     * @return
     */
    @PostMapping("/protocol")
    @ApiOperation("运维协议分布")
    public VData<List<Map<String, Object>>> protocol(@RequestBody ObjectPortraitModel model) {
        return operationService.protocol(model);
    }

    /**
     * 运维端口分布
     *
     * @param model
     * @return
     */
    @PostMapping("/port")
    @ApiOperation("运维端口分布")
    public VData<List<Map<String, Object>>> port(@RequestBody ObjectPortraitModel model) {
        return operationService.port(model);
    }

    /**
     * 运维次数趋势统计
     *
     * @param model
     * @return
     */
    @PostMapping("/trend")
    @ApiOperation("运维次数趋势统计")
    public VData<List<Map<String, Object>>> trend(@RequestBody ObjectPortraitModel model) {
        return operationService.trend(model);
    }

    /**
     * 非工作时间运维次数统计
     *
     * @param model
     * @return
     */
    @PostMapping("/nonWorkTime")
    @ApiOperation("非工作时间运维次数统计")
    public VData<List<Map<String, Object>>> nonWorkTime(@RequestBody ObjectPortraitModel model) {
        return operationService.nonWorkTime(model);
    }

    /**
     * 运维指令统计
     *
     * @param model
     * @return
     */
    @PostMapping("/instruct")
    @ApiOperation("运维指令统计")
    public VData<List<Map<String, Object>>> instruct(@RequestBody ObjectPortraitModel model) {
        return operationService.instruct(model);
    }

    /**
     * 运维详情
     *
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("运维详情")
    public VList<Map<String, String>> detail(@RequestBody ObjectPortraitModel model) {
        return operationService.detail(model);
    }

    /**
     * 运维详情导出
     *
     * @param model
     * @return
     */
    @PostMapping("/export")
    @ApiOperation("运维详情导出")
    public VData<Export.Progress> export(@RequestBody ObjectPortraitModel model) {
        return operationService.export(model);
    }

}
