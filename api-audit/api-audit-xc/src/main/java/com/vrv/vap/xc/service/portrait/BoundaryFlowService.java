package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.CommunicationModel;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface BoundaryFlowService {

    /**
     * 边界流量-近一月通信总包数
     *
     * @param model 请求参数
     * @return 边界流量结果
     */
    VData<List<Map<String, Object>>> communicationTotalPkt(ObjectPortraitModel model);

    /**
     * 边界流量-发送和接收流量大小趋势
     *
     * @param model 请求参数
     * @return 边界流量结果
     */
    VData<List<Map<String, Object>>> sendReceiveFlowTrend(ObjectPortraitModel model);

    /**
     * 边界流量-通信总包数统计趋势
     *
     * @param model 请求参数
     * @return 边界流量结果
     */
    VData<List<Map<String, Object>>> communicationTotalTrend(ObjectPortraitModel model);

    /**
     * 边界流量-详情导出
     *
     * @param model 请求参数
     * @return 边界流量结果
     */
    VData<Export.Progress> export(ObjectPortraitModel model);

    /**
     * 边界流量-访问详情
     *
     * @param model 请求参数
     * @return 边界流量结果
     */
    VList<Map<String, String>> detail(ObjectPortraitModel model);
}
