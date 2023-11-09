package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface FileTransferService {
    /**
     * 文件传输-文件传输关系图
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> fileDiagram(ObjectPortraitModel model);

    /**
     * 文件传输-上传/下载
     *
     * @param model 请求参数
     * @param model 文件大小、密级、类型
     * @return
     */
    VData<List<Map<String, Object>>> fileInfo(ObjectPortraitModel model, String aggField, String keyField);

    /**
     * 文件传输-文件上传下载趋势
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> fileUpDownTrend(ObjectPortraitModel model);

    /**
     * 文件传输-详情
     *
     * @param model
     * @return
     */
    VList<Map<String, String>> fileTransferDetail(ObjectPortraitModel model);

    /**
     * 文件传输-详情导出
     *
     * @param model
     * @return
     */
    VData<Export.Progress> fileTransferDetailExport(ObjectPortraitModel model);
}
