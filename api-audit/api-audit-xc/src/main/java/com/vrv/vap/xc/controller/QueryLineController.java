package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.ObjectAnalyseModel;
import com.vrv.vap.xc.model.PortraitModel;
import com.vrv.vap.xc.service.ObjectAnalyseService;
import com.vrv.vap.xc.service.QueryLineService;
import com.vrv.vap.xc.tools.TrendTools;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/line")
public class QueryLineController {
    @Autowired
    private QueryLineService queryLineService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }


    @PostMapping("/history/username")
    @ApiOperation("历史登录用户名基线")
    public VData<Map<String, Object>> queryUseName(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryUseIp(param);
    }

    @PostMapping("/history/ip")
    @ApiOperation("历史登录ip基线")
    public VData<Map<String, Object>> queryUseIp(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryUseIp(param);
    }

    @PostMapping("/visit/app/ip_acount")
    @ApiOperation("访问应用系统使用的IP、账号基线")
    public VData<Map<String, Object>> queryVisitAppIpAndAcount(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryVisitAppIpAndAcount(param);
    }

    @PostMapping("/visit/app/name_ip_secret")
    @ApiOperation("系统名称、IP、密级基线")
    public VData<Map<String, Object>> queryVisitAppNameIpSecret(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryVisitAppNameIpSecret(param);
    }

    @PostMapping("/history/visit/address")
    @ApiOperation("历史访问入口地址基线")
    public VData<Map<String, Object>> queryHistoryVisitAddress(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryHistoryVisitAddress(param);
    }

    @PostMapping("/history/visit/proto_port")
    @ApiOperation("历史访问协议名和端口号基线")
    public VData<Map<String, Object>> queryHistoryVisitProtoAndPort(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryHistoryVisitProtoAndPort(param);
    }

    @PostMapping("/file/local/business")
    @ApiOperation("本地处理文件最高密级、业务类别基线")
    public VData<Map<String, Object>> queryFileLocalBusiness(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryFileLocalBusiness(param);
    }

    @PostMapping("/file/import/trend")
    @ApiOperation("导入文件趋势基线")
    public VData<Map<String, Object>> queryFileImportTrend(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryFileImportTrend(param);
    }

    @PostMapping("/file/export/trend")
    @ApiOperation("输出文件趋势 文件数统计基线")
    public VData<Map<String, Object>> queryFileExportTrend(@RequestBody ObjectAnalyseModel param) {
        return queryLineService.queryFileExportTrend(param);
    }
    @PostMapping("/generalAnalytics")
    @ApiOperation("通用模块解析接口")
    public VData<Map<String, Object>> generalAnalytics(@RequestBody PortraitModel param) {
        return queryLineService.generalAnalytics(param);
    }

    @GetMapping("/asd")
    public Result renderconfig(){
        new TrendTools().renderLineConfig();
        return VoBuilder.success();
    }

}
