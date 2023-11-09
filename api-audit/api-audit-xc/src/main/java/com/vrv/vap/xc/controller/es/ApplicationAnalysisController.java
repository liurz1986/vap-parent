package com.vrv.vap.xc.controller.es;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.ApplicationAnalysisService;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
public class ApplicationAnalysisController {

    @Autowired
    private ApplicationAnalysisService applicationAnalysisService;


    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("/analysis/aggLoginPwd")
    @ApiOperation("账户登陆口令尝试次数")
    public VList<Map<String, String>> aggLoginPwd(@RequestBody ApplicationModel pageModel){
        return applicationAnalysisService.aggLoginPwd(pageModel);
    }

    @PostMapping("/analysis/hisVisitsUrl")
    @ApiOperation("历史访问入口地址")
    public VList<Map<String, Object>> hisVisitsUrl(@RequestBody ApplicationModel pageModel){
        return applicationAnalysisService.hisVisitsUrl(pageModel);
    }

    @PostMapping("/analysis/hisVisitAgreePort")
    @ApiOperation("用户历史通信使用的入口地址,ip,协议名,及端口号")
    public VList<Map<String, Object>> hisVisitAgreePort(@RequestBody ApplicationModel pageModel){
        return applicationAnalysisService.hisVisitAgreePort(pageModel);
    }

    @PostMapping("/analysis/userFileCount")
    @ApiOperation("用户周月文件上传下载数量")
    public VData<Long> userFileCount(@RequestBody FileModel fileModel){
        return applicationAnalysisService.userFileCount(fileModel);
    }

    @PostMapping("/analysis/userVisitCount")
    @ApiOperation("用户月周访问次数")
    public VData<Long> userVisitCount(@RequestBody TimeModel modle){
        return applicationAnalysisService.userVisitCount(modle);
    }

    @PostMapping("/analysis/userVisitRecently")
    @ApiOperation("用户最近一次访问记录")
    public VData<Map<String, Object>> userVisitRecently(@RequestBody ApplicationModel pageModel){
        return applicationAnalysisService.userVisitRecently(pageModel);
    }

    @PostMapping("/analysis/serverList")
    @ApiOperation("后台服务列表")
    public VList<Map<String, Object>> serverList(@RequestBody ApplicationModel pageModel){
        return applicationAnalysisService.serverList(pageModel);
    }

    @PostMapping("/analysis/serverVisitRecently")
    @ApiOperation("服务最近一次被访问记录")
    public VData <Map<String, Object>> serverVisitRecently(@RequestBody ApplicationModel model){
        return applicationAnalysisService.serverVisitRecently(model);
    }

    @PostMapping("/analysis/otherServerList")
    @ApiOperation("其他应用列表")
    public VList <Map<String, Object>> otherServerList(@RequestBody ApplicationModel pageModel){
        return applicationAnalysisService.otherServerList(pageModel);
    }

    @PostMapping("/analysis/appVisitTrend")
    @ApiOperation("应用访问趋势")
    public VList <Map<String, Object>> appVisitTrend(@RequestBody TimeModel model){
        return applicationAnalysisService.appVisitTrend(model);
    }

    @PostMapping("/analysis/fileTrend")
    @ApiOperation("文件下载上传趋势")
    public VData <Map<String, Object>> fileTrend(@RequestBody TimeModel model){
        return applicationAnalysisService.fileTrend(model);
    }

    @PostMapping("/analysis/inAndOutFileDate")
    @ApiOperation("输入/输出、密级-文件分布饼图")
    public VData <Map<String, Object>> inAndOutFileDate(@RequestBody ApplicationModel model){
        return applicationAnalysisService.inAndOutFileDate(model);
    }

    @PostMapping("/analysis/fileListLevel")
    @ApiOperation("文件业务列表密级")
    public VData<List<Map<String, Object>>> fileListLevel(@RequestBody ApplicationModel model){
        return applicationAnalysisService.fileListLevel(model);
    }

    @PostMapping("/analysis/interconTrend")
    @ApiOperation("互联访问趋势")
    public VData<List<Map<String, Object>>> interconTrend(@RequestBody TimeModel model){
        return applicationAnalysisService.interconTrend(model);
    }

    @PostMapping("/analysis/interconProPortList")
    @ApiOperation("每对联通IP采用的协议及端口号")
    public EsResult interconProPortList(@RequestBody ApplicationModel model){
        return applicationAnalysisService.interconProPortList(model);
    }

    @PostMapping("/analysis/protocolCount")
    @ApiOperation("协议次数分布")
    public VData<List<Map<String, Object>>> protocolCount(@RequestBody ApplicationModel model){
        return applicationAnalysisService.protocolCount(model);
    }

    @PostMapping("/analysis/portTop")
    @ApiOperation("端口top排行")
    public VData<List<Map<String, Object>>> portTop(@RequestBody ApplicationModel model){
        return applicationAnalysisService.portTop(model);
    }

    @PostMapping("/analysis/inoutlevelcount")
    @ApiOperation("（输入/输出）每个密级的业务个数")
    public VData<Map<String, Object>> inoutlevelcount(@RequestBody ApplicationModel model){
        return applicationAnalysisService.inoutlevelcount(model);
    }

    @PostMapping("/analysis/userleveldata")
    @ApiOperation("密级-用户数量分布")
    public VData<Map<String, Integer>> userleveldata(@RequestBody ApplicationModel model){
        return applicationAnalysisService.userleveldata(model);
    }

    @PostMapping("/analysis/userFileTop")
    @ApiOperation("用户-涉密信息数量TOP")
    public VData<List<Map<String, Object>>> userFileTop(@RequestBody ApplicationModel model){
        return applicationAnalysisService.userFileTop(model);
    }

    @PostMapping("/analysis/userBusinessList")
    @ApiOperation("业务用户数列表")
    public VData<Map<String, Integer>> userBusinessList(@RequestBody ApplicationModel model){
        return applicationAnalysisService.userBusinessList(model);
    }

    @PostMapping("/analysis/appleveldata")
    @ApiOperation("密级-应用数量分布")
    public VData<Map<String, Integer>> appleveldata(@RequestBody ApplicationModel model){
        return applicationAnalysisService.appleveldata(model);
    }

    @PostMapping("/analysis/appFileTop")
    @ApiOperation("应用-涉密信息数量TOP")
    public VData<List<Map<String, Object>>> appFileTop(@RequestBody ApplicationModel model){
        return applicationAnalysisService.appFileTop(model);
    }

    @PostMapping("/analysis/appBusinessList")
    @ApiOperation("业务应用数列表")
    public VData<Map<String, Integer>> appBusinessList(@RequestBody ApplicationModel model){
        return applicationAnalysisService.appBusinessList(model);
    }

    @PostMapping("/analysis/appSignalNum")
    @ApiOperation("后台通信次数")
    public VData<Long> appSignalNum(@RequestBody ApplicationModel model){
        return applicationAnalysisService.appSignalNum(model);
    }

    @PostMapping("/analysis/appVisitCount")
    @ApiOperation("应用访问次数分布和应用个数")
    public VData<Map<String, Object>> appVisitCount(@RequestBody ApplicationModel model){
        return applicationAnalysisService.appVisitCount(model);
    }

    @PostMapping("/analysis/fileDownUploadData")
    @ApiOperation("文件上传下载数量分布")
    public VData<Map<String, Object>> fileDownUploadData(@RequestBody ApplicationModel model){
        return applicationAnalysisService.fileDownUploadData(model);
    }

    @PostMapping("/analysis/businessLevelData")
    @ApiOperation("业务类别、密级分布")
    public VData<Map<String, Object>> businessLevelData(@RequestBody ApplicationModel model){
        return applicationAnalysisService.businessLevelData(model);
    }

    @PostMapping("/analysis/businessLevelCount")
    @ApiOperation("每个业务的最高密级")
    public VData<Map<String, Integer>> businessLevelCount(@RequestBody ApplicationModel model){
        return applicationAnalysisService.businessLevelCount(model);
    }

    @PostMapping("/analysis/ipCount")
    @ApiOperation("内外ip数")
    public VData<Map<String, Object>> ipCount(@RequestBody ApplicationModel model){
        return applicationAnalysisService.ipCount(model);
    }

    @PostMapping("/analysis/departBaseInfo")
    @ApiOperation("部门基础信息")
    public VData<Map<String, Integer>> departBaseInfo(@RequestBody ApplicationModel model){
        return applicationAnalysisService.departBaseInfo(model);
    }

    @PostMapping("/analysis/visitDetail")
    @ApiOperation("通信详情")
    public VData<List<Map<String, Object>>> visitDetail(@RequestBody ApplicationModel model){
        return applicationAnalysisService.visitDetail(model);
    }

    @PostMapping("/analysis/otherSysVisitNum")
    @ApiOperation("其他应用访问本应用次数")
    public VData<Long> otherSysVisitNum(@RequestBody ApplicationModel model){
        return applicationAnalysisService.otherSysVisitNum(model);
    }
}
