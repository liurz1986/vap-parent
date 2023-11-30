package com.vrv.vap.xc.controller.behavior;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.behavior.PrintStatisticsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 行为分析-打印、刻录行为分析
 */
@RestController
@RequestMapping("/printBrun/analysis")
public class PrintStatisticsController {
    @Autowired
    private PrintStatisticsService printStatisticsService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("/devByTime")
    @ApiOperation("按时间统计打印、刻录设备打印、刻录数量")
    public VList<Map<String, Object>> printDevByTime(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printDevByTime(model);
    }

    @PostMapping("/userNonWorkTime")
    @ApiOperation("非工作时间打印、刻录人员分析")
    public VList<Map<String, Object>> printUserNonWorkTime(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printUserNonWorkTime(model);
    }

    @PostMapping("/numByTime")
    @ApiOperation("按时间统计打印、刻录数量")
    public VList<Map<String, Object>> printNumByTime(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printNumByTime(model);
    }


    /**
     * 按打印、刻录数量统计用户排行
     *
     * @param model
     * @return
     */
    @PostMapping("/orderByUser")
    @ApiOperation("按打印、刻录数量统计用户排行")
    public VList<Map<String, Object>> printOrderByUser(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printOrderByUser(model);
    }

    /**
     * 按部门统计打印、刻录数量
     *
     * @param model
     * @return
     */
    @PostMapping("/orderByOrg")
    @ApiOperation("按部门统计打印、刻录数量")
    public VList<Map<String, Object>> printOrderByOrg(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printOrderByOrg(model);
    }

    /**
     * 按打印、刻录设备统计打印数量
     *
     * @param model
     * @return
     */
    @PostMapping("/orderByDev")
    @ApiOperation("按打印、刻录设备统计打印数量")
    public VList<Map<String, Object>> printOrderByDev(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printOrderByDev(model);
    }

    /**
     * 打印、刻录文件类型分布
     *
     * @param model
     * @return
     */
    @PostMapping("/fileType")
    @ApiOperation("打印、刻录文件类型分布")
    public VList<Map<String, Object>> printFileType(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileType(model);
    }

    /**
     * 打印、刻录文件类型doc数量排名
     *
     * @param model
     * @return
     */
    @PostMapping("/fileDoc")
    @ApiOperation("打印、刻录文件类型doc数量排名")
    public VList<Map<String, Object>> printFileDoc(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileDoc(model);
    }

    /**
     * 打印、刻录文件类型pdf数量排名
     *
     * @param model
     * @return
     */
    @PostMapping("/filePdf")
    @ApiOperation("打印、刻录文件类型pdf数量排名")
    public VList<Map<String, Object>> printFilePdf(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFilePdf(model);
    }

    /**
     * 打印、刻录文件类型总数量排名
     *
     * @param model
     * @return
     */
    @PostMapping("/fileTypeTotal")
    @ApiOperation("打印、刻录文件类型总数量排名")
    public VList<Map<String, Object>> printFileTypeTotal(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileTypeTotal(model);
    }

    /**
     * 打印、刻录文件类型数量趋势分析
     *
     * @param model
     * @return
     */
    @PostMapping("/fileTypeCountTrend")
    @ApiOperation("打印、刻录文件类型数量趋势分析")
    public VList<Map<String, Object>> printFileTypeCountTrend(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileTypeCountTrend(model);
    }

    /**
     * 打印、刻录文件密级分布
     *
     * @param model
     * @return
     */
    @PostMapping("/fileLevel")
    @ApiOperation("打印、刻录文件密级分布")
    public VList<Map<String, Object>> printFileLevel(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileLevel(model);
    }

    /**
     * 打印、刻录各个密级文件数量排行
     *
     * @param model
     * @return
     */
    @PostMapping("/countByLevel")
    @ApiOperation("打印、刻录各个密级文件数量排行")
    public VList<Map<String, Object>> printCountByLevel(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printCountByLevel(model);
    }

    /**
     * 打印、刻录文件密级数量趋势分析
     *
     * @param model
     * @return
     */
    @PostMapping("/countByLevelTrend")
    @ApiOperation("打印、刻录文件密级数量趋势分析")
    public VList<Map<String, Object>> printCountByLevelTrend(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printCountByLevelTrend(model);
    }

    /**
     * 打印、刻录文件密级数量总次数排行
     *
     * @param model
     * @return
     */
    @PostMapping("/levelByUser")
    @ApiOperation("打印、刻录文件密级数量总次数排行")
    public VList<Map<String, Object>> printLevelByUser(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printLevelByUser(model);
    }

    /**
     * 按时间统计打印、刻录频次
     *
     * @param model
     * @return
     */
    @PostMapping("/countByTime")
    @ApiOperation("按时间统计打印、刻录频次")
    public VList<Map<String, Object>> printCountByTime(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printCountByTime(model);
    }

    /**
     * 按打印、刻录频次统计用户排名
     *
     * @param model
     * @return
     */
    @PostMapping("/countByUser")
    @ApiOperation("按打印、刻录频次统计用户排名")
    public VList<Map<String, Object>> printCountByUser(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printCountByUser(model);
    }

    /**
     * 按部门统计打印、刻录频次
     *
     * @param model
     * @return
     */
    @PostMapping("/countByOrg")
    @ApiOperation("按部门统计打印、刻录频次")
    public VList<Map<String, Object>> printCountByOrg(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printCountByOrg(model);
    }

    /**
     * 打印、刻录操作结果分布情况
     *
     * @param model
     * @return
     */
    @PostMapping("/resultInfo")
    @ApiOperation("打印、刻录操作结果分布情况")
    public VList<Map<String, Object>> printResultInfo(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printResultInfo(model);
    }

    /**
     * 打印、刻录成功或失败次数统计用户排名
     *
     * @param model
     * @return
     */
    @PostMapping("/resultUser")
    @ApiOperation("打印、刻录成功或失败次数统计用户排名")
    public VList<Map<String, Object>> printResultUser(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printResultUser(model);
    }

    /**
     * 按时间统计操作结果发生趋势
     *
     * @param model
     * @return
     */
    @PostMapping("/resultTrend")
    @ApiOperation("按时间统计操作结果发生趋势")
    public VList<Map<String, Object>> printResultTrend(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printResultTrend(model);
    }

    /**
     * 按部门统计操作结果
     *
     * @param model
     * @return
     */
    @PostMapping("/resultByOrg")
    @ApiOperation("按部门统计操作结果")
    public VList<Map<String, Object>> printResultByOrg(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printResultByOrg(model);
    }

    /**
     * 按时间统计打刻行为趋势
     *
     * @param model
     * @return
     */
    @PostMapping("/timeTrend")
    @ApiOperation("按时间统计打刻行为趋势")
    public VList<Map<String, Object>> timeTrend(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printOrburnTrend(model);
    }

    @PostMapping("/countByHour")
    @ApiOperation("打印、刻录时间集中度分析")
    public VList<Map<String, Object>> printCountByHour(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printCountByHour(model);
    }

    @PostMapping("/fileSize")
    @ApiOperation("根据文件区间大小统计数量")
    public VList<Map<String, Object>> printFileSize(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileSize(model);
    }

    @PostMapping("/fileSizeInfo")
    @ApiOperation("文件大小分布情况")
    public VList<Map<String, Object>> printFileSizeInfo(@RequestBody PrintBurnModel model) {
        return printStatisticsService.printFileSizeInfo(model);
    }

    @PostMapping("/detail")
    @ApiOperation("详情")
    public VList<Map<String, String>> printDetail(@RequestBody PrintDetailModel model) {
        return printStatisticsService.printDetail(model);
    }

    @PostMapping("/exportDetail")
    @ApiOperation("详情导出")
    public VData<Export.Progress> exportDetail(@RequestBody PrintDetailModel model) {
        return printStatisticsService.exportDetail(model);
    }
}
