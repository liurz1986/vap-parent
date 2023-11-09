package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;

import java.util.List;
import java.util.Map;

public interface PrintStatisticsService {

    /**
     * 按时间统计打印数量
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printNumByTime(PrintTimeModel model);


    /**
     * 按打印数量统计用户排行
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrderByUser(PageModel model);

    /**
     * 按部门统计打印数量
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrderByOrg(PageModel model);

    /**
     * 按设备统计打印数量
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrderByDev(PageModel model);

    /**
     * 按时间统计打印设备数量
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printDevByTime(PrintTimeModel model);

    /**
     * 非工作时间打印人员分析
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printUserNonWorkTime(PageModel model);

    /**
     * 打印文件类型分布
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileType(PageModel model);

    /**
     * 打印文件类型doc数量排名
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileDoc(PageModel model);

    /**
     * 打印文件类型pdf数量排名
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFilePdf(PageModel model);

    /**
     * 打印文件类型总数量排名
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileTypeTotal(PageModel model);
    /**
     * 打印文件类型数量趋势分析
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileTypeCountTrend(PrintTimeModel model);
    /**
     * 打印文件密级分布
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileLevel(PageModel model);

    /**
     * 打印各个密级文件数量排行
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printCountByLevel(FileLevelModel model);

    /**
     * 打印文件密级数量趋势分析
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printCountByLevelTrend(PrintTimeModel model);

    /**
     * 打印文件密级数量总次数排行
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printLevelByUser(PageModel model);

    /**
     * 按时间统计打印频次
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printCountByTime(PrintTimeModel model);

    /**
     * 按打印频次统计用户排名
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printCountByUser(PageModel model);

    /**
     * 按部门统计打印频次
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printCountByOrg(PageModel model);

    /**
     * 打印操作结果分布情况
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printResultInfo(PageModel model);

    /**
     * 打印成功或失败次数统计用户排名
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printResultUser(PrintResultModel model);
    /**
     * 按时间统计操作结果发生趋势
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printResultTrend(PrintTimeModel model);
    /**
     * 按部门统计操作结果
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printResultByOrg(PageModel model);

    /**
     * 按部门统计操作结果
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printOrburnTrend(PrintBurnModel model);

    /**
     * 打印时间集中度分析
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printCountByHour(PrintTimeModel model);

    /**
     * 文件大小分布情况
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileSizeInfo(PageModel model);

    /**
     * 根据文件区间大小统计数量
     * @param model
     * @return
     */
    VData<List<Map<String,Object>>> printFileSize(PrintSizeModel model);

    /**
     * 详情
     * @param model
     * @return
     */
    VList<Map<String, String>> printDetail(PrintDetailModel model);
    /**
     * 详情
     * @param model
     * @return
     */
    VData<Export.Progress> exportDetail(PrintDetailModel model);
}
