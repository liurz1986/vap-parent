package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ReportParam;

import java.util.Map;

public interface BehaviorReportService {
    /**
     * 人员登录本地终端次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> userLoginCount(ReportParam model,String userType);

    /**
     * 用户访问其他终端次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> userVisitOtherDev(ReportParam model,String userType);


    /**
     * 被访问终端top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> beVisitDev(ReportParam model);

    /**
     * 用户访问应用系统次数top10 9.16 8.13
     * @param model
     * @return
     */
    VList<Map<String,Object>> userVisitSys(ReportParam model,String userType);

    /**
     * 应用系统被访问次数统计top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> sysBeVisit(ReportParam model);

    /**
     * 用户访问互联单位应用次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> userVisitSameOrg(ReportParam model);

    /**
     * 互联单位应用被访问次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> sameOrgBeVisit(ReportParam model);
    /**
     * 其他设备访问次数按人员统计top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> otherDevVisitByUser(ReportParam model);

    /**
     * 其他设备被访问次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> otherDevBeVisit(ReportParam model);

    /**
     * 本地文件导入按人员统计top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> fileImportByUser(ReportParam model);

    /**
     * 本地文件按密级划分
     * @param model
     * @return
     */
    VList<Map<String,Object>> fileImportByLevel(ReportParam model,String userType);

    /**
     * 本地文件输出按人员统计top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileExportByUser(ReportParam model,String userType);

    /**
     * 本地文件输出按人员统计top10列表
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileExportByUser2List(ReportParam model,String userType);

    /**
     * 本地文件输出情况按密级划分
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileCountByLevel(ReportParam model,String userType);

    /**
     * 本地文件导入情况按密级划分
     * @param model
     * @return
     */
    VList<Map<String,Object>> importFileCountByLevel(ReportParam model,String userType);

    /**
     * 运维方式分布统计 0-终端；1-服务器；3-应用系统；2-保密安全设备；4-网络设备
     * @param model
     * @return
     */
    VList<Map<String,Object>> operationType(ReportParam model,String deviceType);

    /**
     * 管理员访问服务器次数按人员统计
     * @param model
     * @return
     */
    VList<Map<String,Object>> adminVisitServerByUser(ReportParam model);

    /**
     * 各类安全保密产品被访问次数统计
     * @param model
     * @return
     */
    VList<Map<String,Object>> safeDevBeVisit(ReportParam model,String userType);

    /**
     * 管理员访问w网络设备次数按人员统计
     * @param model
     * @return
     */
    VList<Map<String,Object>> adminVisitNetDevByUser(ReportParam model);

    /**
     * 应用被访问次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> appBeVisit(ReportParam model);

    /**
     * 应用后台服务通信次数top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> signalNum(ReportParam model);

    /**
     * 文件处理情况按密级统计
     * @param model
     * @return
     */
    VList<Map<String,Object>> appFileByLevel(ReportParam model);

    /**
     * 应用内文件传输情况
     * @param model
     * @return
     */
    VList<Map<String,Object>> appFileInfo(ReportParam model);

     /**
     * 应用内文件传输情况列表
     * @param model
     * @return
     */
    VList<Map<String,Object>> appFileInfo2List(ReportParam model);

    /**
     * 应用内文件传输情况列表
     * @param model
     * @return
     */
    VList<Map<String,Object>> appFileCountByOrg(ReportParam model);

    /**
     * 应用中涉密信息数量top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> appFileCountBySys(ReportParam model);

    /**
     * 互联访问情况
     * @param model
     * @return
     */
    VData<Map<String,Object>> sameinfo(ReportParam model);

    /**
     * 内部ip访问次数统计top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> inIpCount(ReportParam model);

    /**
     * 内部ip被访问次数统计top10
     * @param model
     * @return
     */
    VList<Map<String,Object>> inIpBeVisitCount(ReportParam model);

    /**
     * 文件输出情况
     */
    VData<Map<String,Object>> fileOutInfo(ReportParam model);

    /**
     * 文件输出密级统计
     * @param model
     * @return
     */
    VList<Map<String,Object>> sameFileCountByLevel(ReportParam model,String fileDir);

    /**
     * 涉密文件流转情况汇总
     */
    VData<Map<String,Object>> secretFileOutInfo(ReportParam model);

    /**
     * 涉密文件数目按类别统计
     * @param model
     * @param fileDir
     * @return
     */
    VList<Map<String,Object>> secretFileCountByType(ReportParam model,String fileDir);
}
