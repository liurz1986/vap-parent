package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface LocalOperationService {
    /**
     * 本地登录-登录次数
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> loginCount(ObjectPortraitModel model, boolean work);

    /**
     *本地登录-登录次数详情
     * @param model
     * @return
     */
    VList<Map<String, String>> loginCountDetail(ObjectPortraitModel model, boolean work);

    /**
     * 登录次数详情导出
     * @param model
     * @return
     */
    VData<Export.Progress> loginCountDetailExport(ObjectPortraitModel model);

    /**
     * 使用专用介质数量
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> mediumUse(ObjectPortraitModel model);

    /**
     * 使用专用介质 频次分析
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> mediumUseFrequency(ObjectPortraitModel model);

    /**
     * 使用介质频次详情
     * @param model
     * @return
     */
    VList<Map<String,String>> mediumUseDetail(ObjectPortraitModel model);
    /**
     * 使用介质数量详情
     * @param model
     * @return
     */
    VList<Map<String,String>> mediumUseNumDetail(ObjectPortraitModel model);

    /**
     * 专用介质使用数量详情导出
     * @param model
     * @return
     */
    VData<Export.Progress> mediumUseNumDetailExport(ObjectPortraitModel model);

    /**
     * 介质使用详情导出
     * @param model
     * @return
     */
    VData<Export.Progress> mediumUseDetailExport(ObjectPortraitModel model);

    /**
     * 打印/刻录 文件次数
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrBurnCount(ObjectPortraitModel model);

    /**
     * 打印/刻录 频次分析
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrBurnFrequency(ObjectPortraitModel model);

    /**
     *打印/刻录-详情
     * @param model
     * @return
     */
    VList<Map<String,String>> printOrBurnCountDetail(ObjectPortraitModel model);

    /**
     *打印详情/刻录详情-导出
     * @param model
     * @return
     */
    VData<Export.Progress> printOrBurnCountDetailExport(ObjectPortraitModel model);

    /**
     * 打印/刻录 密级分布
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrBurnLevelCount(ObjectPortraitModel model);
}
