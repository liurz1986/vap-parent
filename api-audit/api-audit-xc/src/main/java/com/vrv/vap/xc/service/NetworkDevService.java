package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.SecurityModel;

import java.util.List;
import java.util.Map;

public interface NetworkDevService {

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
     *互联边界流量情况
     * @param model
     * @return
     */
    VList<Map<String,String>> interconnectionNetInfo(SecurityModel model);

    /**
     *互联边界流量情况导出
     * @param model
     * @return
     */
    VData<Export.Progress> interconnectionNetInfoExport(SecurityModel model);

}
