package com.vrv.vap.xc.service.behavior;

import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;

import java.util.Map;

/**
 * 行为分析-打印、刻录行为分析
 */
public interface PrintStatisticsService {

    /**
     * 按时间统计打印数量
     * @param model
     * @return
     */
    VList<Map<String,Object>> printNumByTime(PrintBurnModel model);


    /**
     * 按打印数量统计用户排行
     * @param model
     * @return
     */
    VList<Map<String,Object>> printOrderByUser(PrintBurnModel model);

    /**
     * 按部门统计打印数量
     * @param model
     * @return
     */
    VList<Map<String,Object>> printOrderByOrg(PrintBurnModel model);

    /**
     * 按设备统计打印数量
     * @param model
     * @return
     */
    VList<Map<String,Object>> printOrderByDev(PrintBurnModel model);

    /**
     * 按时间统计打印设备数量
     * @param model
     * @return
     */
    VList<Map<String,Object>> printDevByTime(PrintBurnModel model);

    /**
     * 非工作时间打印人员分析
     * @param model
     * @return
     */
    VList<Map<String,Object>> printUserNonWorkTime(PrintBurnModel model);

    /**
     * 打印文件类型分布
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileType(PrintBurnModel model);

    /**
     * 打印文件类型doc数量排名
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileDoc(PrintBurnModel model);

    /**
     * 打印文件类型pdf数量排名
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFilePdf(PrintBurnModel model);

    /**
     * 打印文件类型总数量排名
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileTypeTotal(PrintBurnModel model);
    /**
     * 打印文件类型数量趋势分析
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileTypeCountTrend(PrintBurnModel model);
    /**
     * 打印文件密级分布
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileLevel(PrintBurnModel model);

    /**
     * 打印各个密级文件数量排行
     * @param model
     * @return
     */
    VList<Map<String,Object>> printCountByLevel(PrintBurnModel model);

    /**
     * 打印文件密级数量趋势分析
     * @param model
     * @return
     */
    VList<Map<String,Object>> printCountByLevelTrend(PrintBurnModel model);

    /**
     * 打印文件密级数量总次数排行
     * @param model
     * @return
     */
    VList<Map<String,Object>> printLevelByUser(PrintBurnModel model);

    /**
     * 按时间统计打印频次
     * @param model
     * @return
     */
    VList<Map<String,Object>> printCountByTime(PrintBurnModel model);

    /**
     * 按打印频次统计用户排名
     * @param model
     * @return
     */
    VList<Map<String,Object>> printCountByUser(PrintBurnModel model);

    /**
     * 按部门统计打印频次
     * @param model
     * @return
     */
    VList<Map<String,Object>> printCountByOrg(PrintBurnModel model);

    /**
     * 打印操作结果分布情况
     * @param model
     * @return
     */
    VList<Map<String,Object>> printResultInfo(PrintBurnModel model);

    /**
     * 打印成功或失败次数统计用户排名
     * @param model
     * @return
     */
    VList<Map<String,Object>> printResultUser(PrintBurnModel model);
    /**
     * 按时间统计操作结果发生趋势
     * @param model
     * @return
     */
    VList<Map<String,Object>> printResultTrend(PrintBurnModel model);
    /**
     * 按部门统计操作结果
     * @param model
     * @return
     */
    VList<Map<String,Object>> printResultByOrg(PrintBurnModel model);

    /**
     * 按时间统计打刻行为趋势
     * @param model
     * @return
     */
    VList<Map<String,Object>> printOrburnTrend(PrintBurnModel model);

    /**
     * 打印时间集中度分析
     * @param model
     * @return
     */
    VList<Map<String,Object>> printCountByHour(PrintBurnModel model);

    /**
     * 文件大小分布情况
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileSizeInfo(PrintBurnModel model);

    /**
     * 根据文件区间大小统计数量
     * @param model
     * @return
     */
    VList<Map<String,Object>> printFileSize(PrintBurnModel model);

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
