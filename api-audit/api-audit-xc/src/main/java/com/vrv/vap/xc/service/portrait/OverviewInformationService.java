package com.vrv.vap.xc.service.portrait;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.DevModel;
import com.vrv.vap.xc.model.ObjectPortraitModel;

import java.util.List;
import java.util.Map;

public interface OverviewInformationService {
    /**
     * 软件安装情况
     *
     * @return
     */
    VData<List<Map<String, String>>> softwareInstallation(DevModel model);

    /**
     * 病毒感染情况
     */
    VData<List<Map<String, String>>> virusInfection(DevModel model);

    /**
     * 文件密级分布
     */
    VData<List<Map<String, Object>>> fileLevel(DevModel model);

    /**
     * 文件密级分布详情
     */
    VList<Map<String, String>> fileLevelDetail(ObjectPortraitModel model);

    /**
     * CPU占用率
     *
     * @param model
     * @return
     */
    VData<Map<String, Object>> cpuInfo(DevModel model);

    /**
     * 内存占用率
     *
     * @param model
     * @return
     */
    VData<Map<String, Object>> memoryInfo(DevModel model);

    /**
     * 磁盘占用率
     *
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> diskInfo(DevModel model);

    /**
     * 行为轨迹分析
     *
     * @param model
     * @return
     */
    VList<Map<String, String>> trajectoryAnalysis(DevModel model);
}
