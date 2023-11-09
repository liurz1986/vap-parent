package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface OperationService {
    /**
     * 运维次数排名
     */
    VData<List<Map<String, Object>>> number(ObjectPortraitModel model);

    /**
     * 运维协议分布
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> protocol(ObjectPortraitModel model);

    /**
     * 运维端口分布
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> port(ObjectPortraitModel model);

    /**
     * 运维次数趋势统计
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> trend(ObjectPortraitModel model);

    /**
     * 非工作时间运维
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> nonWorkTime(ObjectPortraitModel model);

    /**
     * 运维指令统计
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> instruct(ObjectPortraitModel model);

    /**
     * 运维详情
     *
     * @param model
     * @return
     */
    VList<Map<String, String>> detail(ObjectPortraitModel model);

    /**
     * 运维详情导出
     *
     * @param model
     * @return
     */
    VData<Export.Progress> export(ObjectPortraitModel model);
}

