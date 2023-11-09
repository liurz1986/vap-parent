package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface SecurityService {
    /**
     * 服务次数分析
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> timesAnalysis(ObjectPortraitModel model);

    /**
     * 服务次数排名
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> timesTop(ObjectPortraitModel model);

    /**
     * 服务协议、端口分布
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> serverProtocolAndPort(ObjectPortraitModel model, String aggField, String keyField);

    /**
     * 安全服务详情列表
     *
     * @param model
     * @return
     */
    VList<Map<String, String>> serviceDetail(ObjectPortraitModel model);

    /**
     * 安全服务详情列表-导出
     *
     * @param model
     * @return
     */
    VData<Export.Progress> export(ObjectPortraitModel model);
}
