package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.model.SecurityModel;
import com.vrv.vap.xc.model.SysRelationModel;

import java.util.List;
import java.util.Map;

public interface AppDevService {

    /**
     *运维详情
     * @param model
     * @return
     */
    VList<Map<String,String>> operationDetail(SecurityModel model);

    /**
     *运维详情导出
     * @param model
     * @return
     */
    VData<Export.Progress> operationDetailExport(SecurityModel model);

    /**
     * 文件传输关系图
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> fileRelationGap(SecurityModel model);
    /**
     * 上传/下载趋势
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> fileLevelCount(SecurityModel model);
    /**
     * 上传/下载 文件类型分布
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> fileTypeCount(SecurityModel model);
    /**
     *文件列表详情
     * @param model
     * @return
     */
    VList<Map<String,String>> fileDetail(SecurityModel model);

    /**
     *文件列表详情导出
     * @param model
     * @return
     */
    VData<Export.Progress> fileDetailExport(SecurityModel model);

    /**
     * 交互关系图（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> interactiveRelationGap(SysRelationModel model);
    /**
     * 交互协议分布（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> interactiveProtocol(SysRelationModel model);
    /**
     * 流量大小排行（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> netflowBytesCount(SysRelationModel model);
    /**
     * 交互趋势分析（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> interactiveTrend(SysRelationModel model);
    /**
     * 交互列表详情（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    VList<Map<String,String>> interactiveDetail(SysRelationModel model);
    /**
     *交互列表详情导出（用户devTypeGroup=0 其他应用服务devTypeGroup=3 ）
     * @param model
     * @return
     */
    VData<Export.Progress> interactiveDetailExport(SysRelationModel model);

    /**
     * 业务访问-访问关系图
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> visitRelationGap(SysRelationModel model);
    /**
     * 业务访问-访问趋势
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> visitTrend(SysRelationModel model);
    /**
     * 业务访问-访问列表详情
     * @param model
     * @return
     */
    VList<Map<String,Object>> visitDetail(SysRelationModel model);
}
