package com.vrv.vap.xc.controller.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.SecurityService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 安全服务
 */
@RestController
@RequestMapping("/security/service")
public class SecurityServiceController {

    @Resource
    private SecurityService securityService;

    /**
     * 服务次数分析
     *
     * @param model
     * @return
     */
    @PostMapping("/timesAnalysis")
    @ApiOperation("服务次数分析")
    public VData<List<Map<String, Object>>> timesAnalysis(@RequestBody ObjectPortraitModel model) {
        return securityService.timesAnalysis(model);
    }

    /**
     * 服务次数排名
     *
     * @param model
     * @return
     */
    @PostMapping("/timesTop")
    @ApiOperation("服务次数排名")
    public VData<List<Map<String, Object>>> timesTop(@RequestBody ObjectPortraitModel model) {
        return securityService.timesTop(model);
    }

    /**
     * 服务协议分布
     *
     * @param model
     * @return
     */
    @PostMapping("/protocol")
    @ApiOperation("服务协议分布")
    public VData<List<Map<String, Object>>> serverProtocol(@RequestBody ObjectPortraitModel model) {
        return securityService.serverProtocolAndPort(model, "app_protocol", "protocol");
    }

    /**
     * 服务源端口分布
     *
     * @param model
     * @return
     */
    @PostMapping("/sport")
    @ApiOperation("服务源端口分布")
    public VData<List<Map<String, Object>>> sport(@RequestBody ObjectPortraitModel model) {
        return securityService.serverProtocolAndPort(model, "sport", "port");
    }

    /**
     * 服务目的端口分布
     *
     * @param model
     * @return
     */
    @PostMapping("/dport")
    @ApiOperation("服务目的端口分布")
    public VData<List<Map<String, Object>>> dport(@RequestBody ObjectPortraitModel model) {
        return securityService.serverProtocolAndPort(model, "dport", "port");
    }

    /**
     * 安全服务详情
     *
     * @param model
     * @return
     */
    @PostMapping("/detail")
    @ApiOperation("安全服务详情")
    public VList<Map<String, String>> serviceDetail(@RequestBody ObjectPortraitModel model) {
        return securityService.serviceDetail(model);
    }

    /**
     * 安全服务详情列表-导出
     *
     * @param model
     * @return
     */
    @PostMapping("/export")
    @ApiOperation("安全服务详情列表-导出")
    public VData<Export.Progress> export(@RequestBody ObjectPortraitModel model) {
        return securityService.export(model);
    }
}
