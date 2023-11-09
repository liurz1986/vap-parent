package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface NetworkBoundaryService {

    /**
     * 网络通联-访问关系图
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> relationshipDiagram(ObjectPortraitModel model);

    /**
     * 网络通联-访问详情
     *
     * @param model
     * @return
     */
    VList<Map<String, String>> networkVisitDetail(ObjectPortraitModel model);

    /**
     * 网络通联-访问详情-导出
     *
     * @param model
     * @return
     */
    VData<Export.Progress> networkVisitDetailExport(ObjectPortraitModel model);

    /**
     * 网络通联-访问协议、目的端口分布
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> visitProtocolAndPort(ObjectPortraitModel model, String aggField, String keyField);

    /**
     * 网络通联-流量大小排行
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> pckSizeRanking(ObjectPortraitModel model);

    /**
     * 网络通联-通联趋势分析
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> visitTrend(ObjectPortraitModel model);

    /**
     * 网络通联-会话次数统计
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> sessionTimes(ObjectPortraitModel model);
}
