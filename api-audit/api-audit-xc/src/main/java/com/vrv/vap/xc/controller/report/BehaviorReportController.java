package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.BehaviorReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class BehaviorReportController {
    @Autowired
    BehaviorReportService behaviorReportService;
    
    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }
    
    @PostMapping("/report/userLoginCount")
    public VList<Map<String,Object>> userLoginCount(@RequestBody ReportParam model,@RequestParam  String userType){
        return behaviorReportService.userLoginCount(model, userType);
    }

    /**
     * 用户访问其他终端次数top10
     * @param model
     * @return
     */
    @PostMapping("/report/userVisitOtherDev")
    public VList<Map<String,Object>> userVisitOtherDev(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.userVisitOtherDev(model, userType);
    }


    /**
     * 被访问终端top10
     * @param model
     * @return
     */
    @PostMapping("/report/beVisitDev")
    public VList<Map<String,Object>> beVisitDev(@RequestBody ReportParam model){
        return behaviorReportService.beVisitDev(model);
    }

    /**
     * 用户访问应用系统次数top10 9.16 8.13
     * @param model
     * @return
     */
    @PostMapping("/report/userVisitSys")
    public VList<Map<String,Object>> userVisitSys(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.userVisitSys(model, userType);
    }

    /**
     * 应用系统被访问次数统计top10
     * @param model
     * @return
     */
    @PostMapping("/report/sysBeVisit")
    public VList<Map<String,Object>> sysBeVisit(@RequestBody ReportParam model){
        return behaviorReportService.sysBeVisit(model);
    }

    /**
     * 用户访问互联单位应用次数top10
     * @param model
     * @return
     */
    @PostMapping("/report/userVisitSameOrg")
    public VList<Map<String,Object>> userVisitSameOrg(@RequestBody ReportParam model){
        return behaviorReportService.userVisitSameOrg(model);
    }

    /**
     * 互联单位应用被访问次数top10
     * @param model
     * @return
     */
    @PostMapping("/report/sameOrgBeVisit")
    public VList<Map<String,Object>> sameOrgBeVisit(@RequestBody ReportParam model){
        return behaviorReportService.sameOrgBeVisit(model);
    }
    /**
     * 其他设备访问次数按人员统计top10
     * @param model
     * @return
     */
    @PostMapping("/report/otherDevVisitByUser")
    public VList<Map<String,Object>> otherDevVisitByUser(@RequestBody ReportParam model){
        return behaviorReportService.otherDevVisitByUser(model);
    }

    /**
     * 其他设备被访问次数top10
     * @param model
     * @return
     */
    @PostMapping("/report/otherDevBeVisit")
    public VList<Map<String,Object>> otherDevBeVisit(@RequestBody ReportParam model){
        return behaviorReportService.otherDevBeVisit(model);
    }

    /**
     * 本地文件导入按人员统计top10
     * @param model
     * @return
     */
    @PostMapping("/report/fileImportByUser")
    public VList<Map<String,Object>> fileImportByUser(@RequestBody ReportParam model){
        return behaviorReportService.fileImportByUser(model);
    }

    /**
     * 本地文件按密级划分
     * @param model
     * @return
     */
    @PostMapping("/report/fileImportByLevel")
    public VList<Map<String,Object>> fileImportByLevel(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.fileImportByLevel(model, userType);
    }

    /**
     * 本地文件输出按人员统计top10
     * @param model
     * @return
     */
    @PostMapping("/report/printFileExportByUser")
    public VList<Map<String,Object>> printFileExportByUser(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.printFileExportByUser(model, userType);
    }

    /**
     * 本地文件输出按人员统计top10列表
     * @param model
     * @return
     */
    @PostMapping("/report/printFileExportByUser2List")
    public VList<Map<String,Object>> printFileExportByUser2List(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.printFileExportByUser2List(model, userType);
    }

    /**
     * 本地文件输出情况按密级划分
     * @param model
     * @return
     */
    @PostMapping("/report/printFileCountByLevel")
    public VList<Map<String,Object>> printFileCountByLevel(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.printFileCountByLevel(model, userType);
    }

    /**
     * 本地文件导入情况按密级划分
     * @param model
     * @return
     */
    @PostMapping("/report/importFileCountByLevel")
    public VList<Map<String,Object>> importFileCountByLevel(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.importFileCountByLevel(model, userType);
    }

    /**
     * 运维方式分布统计 0-终端；1-服务器；3-应用系统；2-保密安全设备；4-网络设备
     * @param model
     * @return
     */
    @PostMapping("/report/operationType")
    public VList<Map<String,Object>> operationType(@RequestBody ReportParam model,@RequestParam String deviceType){
        return behaviorReportService.operationType(model, deviceType);
    }

    /**
     * 管理员访问服务器次数按人员统计
     * @param model
     * @return
     */
    @PostMapping("/report/adminVisitServerByUser")
    public VList<Map<String,Object>> adminVisitServerByUser(@RequestBody ReportParam model){
        return behaviorReportService.adminVisitServerByUser(model);
    }

    /**
     * 各类安全保密产品被访问次数统计
     * @param model
     * @return
     */
    @PostMapping("/report/safeDevBeVisit")
    public VList<Map<String,Object>> safeDevBeVisit(@RequestBody ReportParam model,@RequestParam String userType){
        return behaviorReportService.safeDevBeVisit(model, userType);
    }

    /**
     * 管理员访问w网络设备次数按人员统计
     * @param model
     * @return
     */
    @PostMapping("/report/adminVisitNetDevByUser")
    public VList<Map<String,Object>> adminVisitNetDevByUser(@RequestBody ReportParam model){
        return behaviorReportService.adminVisitNetDevByUser(model);
    }

    /**
     * 应用被访问次数top10
     * @param model
     * @return
     */
    @PostMapping("/report/appBeVisit")
    public VList<Map<String,Object>> appBeVisit(@RequestBody ReportParam model){
        return behaviorReportService.appBeVisit(model);
    }

    /**
     * 应用后台服务通信次数top10
     * @param model
     * @return
     */
    @PostMapping("/report/signalNum")
    public VList<Map<String,Object>> signalNum(@RequestBody ReportParam model){
        return behaviorReportService.signalNum(model);
    }

    /**
     * 文件处理情况按密级统计
     * @param model
     * @return
     */
    @PostMapping("/report/appFileByLevel")
    public VList<Map<String,Object>> appFileByLevel(@RequestBody ReportParam model){
        return behaviorReportService.appFileByLevel(model);
    }

    /**
     * 应用内文件传输情况
     * @param model
     * @return
     */
    @PostMapping("/report/appFileInfo")
    public VList<Map<String,Object>> appFileInfo(@RequestBody ReportParam model){
        return behaviorReportService.appFileInfo(model);
    }

     /**
     * 应用内文件传输情况列表
     * @param model
     * @return
     */
    @PostMapping("/report/appFileInfo2List")
    public VList<Map<String,Object>> appFileInfo2List(@RequestBody ReportParam model){
        return behaviorReportService.appFileInfo2List(model);
    }

    /**
     * 应用内文件传输情况列表
     * @param model
     * @return
     */
    @PostMapping("/report/appFileCountByOrg")
    public VList<Map<String,Object>> appFileCountByOrg(@RequestBody ReportParam model){
        return behaviorReportService.appFileCountByOrg(model);
    }

    /**
     * 应用中涉密信息数量top10
     * @param model
     * @return
     */
    @PostMapping("/report/appFileCountBySys")
    public VList<Map<String,Object>> appFileCountBySys(@RequestBody ReportParam model){
        return behaviorReportService.appFileCountBySys(model);
    }

    /**
     * 互联访问情况
     * @param model
     * @return
     */
    @PostMapping("/report/sameinfo")
    public VData<Map<String,Object>> sameinfo(@RequestBody ReportParam model){
        return behaviorReportService.sameinfo(model);
    }

    /**
     * 内部ip访问次数统计top10
     * @param model
     * @return
     */
    @PostMapping("/report/inIpCount")
    public VList<Map<String,Object>> inIpCount(@RequestBody ReportParam model){
        return behaviorReportService.inIpCount(model);
    }

    /**
     * 内部ip被访问次数统计top10
     * @param model
     * @return
     */
    @PostMapping("/report/inIpBeVisitCount")
    public VList<Map<String,Object>> inIpBeVisitCount(@RequestBody ReportParam model){
        return behaviorReportService.inIpBeVisitCount(model);
    }

    /**
     * 文件输出情况
     */
    @PostMapping("/report/fileOutInfo")
    public VData<Map<String,Object>> fileOutInfo(@RequestBody ReportParam model){
        return behaviorReportService.fileOutInfo(model);
    }

    /**
     * 文件输出密级统计
     * @param model
     * @return
     */
    @PostMapping("/report/sameFileCountByLevel")
    public VList<Map<String,Object>> sameFileCountByLevel(@RequestBody ReportParam model,@RequestParam String fileDir){
        return behaviorReportService.sameFileCountByLevel(model, fileDir);
    }

    /**
     * 涉密文件流转情况汇总
     */
    @PostMapping("/report/secretFileOutInfo")
    public VData<Map<String,Object>> secretFileOutInfo(@RequestBody ReportParam model){
        return behaviorReportService.secretFileOutInfo(model);
    }

    /**
     * 涉密文件数目按类别统计
     * @param model
     * @param fileDir
     * @return
     */
    @PostMapping("/report/secretFileCountByType")
    public VList<Map<String,Object>> secretFileCountByType(@RequestBody ReportParam model,@RequestParam String fileDir){
        return behaviorReportService.secretFileCountByType(model, fileDir);
    }

}


