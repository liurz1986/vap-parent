package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface BusinessAccessService {
    /**
     * 业务访问-访问关系图
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> diagram(ObjectPortraitModel model);

    /**
     * 业务访问-被访问URL TOP10
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> urlTop(ObjectPortraitModel model);

    /**
     * 业务访问-访问趋势
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> trend(ObjectPortraitModel model);

    /**
     * 业务访问-访问详情
     *
     * @param model
     * @return
     */
    VList<Map<String, String>> detail(ObjectPortraitModel model);

    /**
     * 业务访问-访问详情-导出
     *
     * @param model
     * @return
     */
    VData<Export.Progress> export(ObjectPortraitModel model);
}
