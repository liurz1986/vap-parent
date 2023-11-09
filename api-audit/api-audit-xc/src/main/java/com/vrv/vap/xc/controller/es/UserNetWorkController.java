package com.vrv.vap.xc.controller.es;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.model.ObjectAnalyseModel;
import com.vrv.vap.xc.service.ObjectAnalyseService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户网络访问情况
 * Created by lilei on 2021/08/05.
 */
@RestController
public class UserNetWorkController {

    @Autowired
    private ObjectAnalyseService objectAnalyseService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("/visit/app/ip_acount")
    @ApiOperation("访问应用系统使用的IP、账号 参数start_time、end_time、count_")
    public VData<List<Map<String, Object>>> queryVisitAppIpAndAcount(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryVisitAppIpAndAcount(param);
    }
    
    @PostMapping("/history/ip_acount")
    @ApiOperation("历史登录IP、用户名 参数start_time、end_time、count_")
    public VData<Map<String, List<String>>> queryUseIpAndAccount(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryUseIpAndAccount(param);
    }
    
    @PostMapping("/file/up_down_count")
    @ApiOperation("上传或者下载文件数量 参数start_time、end_time、fileDir")
    public VData<Map<String, Object>> queryFileUpOrDown(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileUpOrDown(param);
    }
    
    @PostMapping("/login/avg")
    @ApiOperation("用户每周/月口令尝试平均次数 参数start_time、end_time")
    public VData<Map<String, Object>> queryLoginAvg(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryLoginAvg(param);
    }
    
    @PostMapping("/login/detail")
    @ApiOperation("用户每周/月登录时间和退出时间 参数start_time、end_time、count_")
    public VData<List<Map<String, String>>> queryLoginDetailInfo(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryLoginDetailInfo(param);
    }
    
    @PostMapping("/history/visit/proto_port")
    @ApiOperation("历史访问协议名和端口号 参数start_time、end_time")
    public VData<List<Map<String, Object>>> queryHistoryVisitProtoAndPort(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryHistoryVisitProtoAndPort(param);
    }
    
    @PostMapping("/history/visit/address")
    @ApiOperation("历史访问入口地址列表 参数start_time、end_time、count_")
    public VData<List<Map<String, String>>> queryHistoryVisitAddress(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryHistoryVisitAddress(param);
    }
    
    @PostMapping("/visit/count")
    @ApiOperation("一周/月访问次数 参数start_time、end_time")
    public VData<Map<String, Object>> queryVisitCount(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryVisitCount(param);
    }
    
    @PostMapping("/visit/app/name_ip_secret")
    @ApiOperation("系统名称、IP、密级 参数start_time、end_time、count_")
    public VData<List<Map<String, Object>>> queryVisitAppNameIpSecret(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryVisitAppNameIpSecret(param);
    }
    
    @PostMapping("/bevisit/device/name_ip_secret")
    @ApiOperation("被访问设备名称、ip、密级 参数start_time、end_time、count_")
    public VData<List<Map<String, Object>>> queryVisitDeviceNameIpSecret(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryVisitDeviceNameIpSecret(param);
    }
    
    @PostMapping("/file/local/business")
    @ApiOperation("本地处理文件最高密级、业务类别列表 参数start_time、end_time")
    public VData<List<Map<String, Object>>> queryFileLocalBusiness(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileLocalBusiness(param);
    }
    
    @PostMapping("/file/import/list")
    @ApiOperation("导入文件列表 参数start_time、end_time、start_、count_")
    public VData<List<Map<String, String>>> queryFileImportList(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileImportList(param);
    }
    
    @PostMapping("/file/export/count")
    @ApiOperation("打印、刻录输出文件数量统计 参数start_time、end_time、printType(0-打印、1-刻录)")
    public VData<Map<String, Object>> queryFileExportCount(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileExportCount(param);
    }
    
    @PostMapping("/file/import/trend")
    @ApiOperation("导入文件趋势 参数start_time、end_time")
    public VData<List<Map<String, Object>>> queryFileImportTrend(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileImportTrend(param);
    }
    
    @PostMapping("/file/export/trend")
    @ApiOperation("输出文件趋势 文件数统计 参数start_time、end_time、start_、count_")
    public VData<Map<String, List<Map<String, Object>>>> queryFileExportTrend(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileExportTrend(param);
    }
    
    @PostMapping("/file/export/list")
    @ApiOperation("打印、刻录输出文件列表 参数start_time、end_time、start_、count_")
    public VData<List<Map<String, String>>> queryFileExportList(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileExportList(param);
    }

    @PostMapping("/operation/method/count")
    @ApiOperation("运维方式分布 参数start_time、end_time、deviceType(0-终端 1-服务器 2-应用系统 3-安全保密设备 4-网络设备)")
    public VData<Map<String, Object>> queryOperationMethodCount(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryOperationMethodCount(param);
    }

    @PostMapping("/login/avg/trend")
    @ApiOperation("用户每周/月口令尝试平均次数趋势 参数start_time、end_time")
    public VData<List<Map<String, Object>>> loginAvgTrend(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.loginAvgTrend(param);
    }

    @PostMapping("/visit/count/trend")
    @ApiOperation("一周/月访问次数趋势 参数start_time、end_time")
    public VData<Map<String, Object>> queryVisitCountTrend(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.visitTrend(param);
    }

    @PostMapping("/file/up_down_count/trend")
    @ApiOperation("上传或者下载文件数量趋势 参数start_time、end_time、fileDir")
    public VData<Map<String, Object>> queryFileUpOrDownTrend(@RequestBody ObjectAnalyseModel param) {
        return objectAnalyseService.queryFileUpOrDownTrend(param);
    }


}
