package com.vrv.vap.xc.controller.es;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.constant.Common;
import com.vrv.vap.toolkit.excel.ExcelInfo;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.PrintStatisticsService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
public class PrintStatisticsController {
    private static final Log log = LogFactory.getLog(PrintStatisticsController.class);
    @Autowired
    private PrintStatisticsService printStatisticsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("/print/printDevByTime")
    @ApiOperation("按时间统计打印设备数量")
    public VData<List<Map<String,Object>>> printDevByTime(@RequestBody PrintTimeModel model){
        return printStatisticsService.printDevByTime(model);
    }

    @PostMapping("/print/printUserNonWorkTime")
    @ApiOperation("非工作时间打印人员分析")
    public VData<List<Map<String,Object>>> printUserNonWorkTime(@RequestBody PageModel model){
        return printStatisticsService.printUserNonWorkTime(model);
    }

    @PostMapping("/print/printNumByTime")
    @ApiOperation("按时间统计打印数量")
    public VData<List<Map<String,Object>>> printNumByTime(@RequestBody PrintTimeModel model){
        return printStatisticsService.printNumByTime(model);
    }


    /**
     * 按打印数量统计用户排行
     * @param model
     * @return
     */
    @PostMapping("/print/printOrderByUser")
    @ApiOperation("按打印数量统计用户排行")
    public VData<List<Map<String,Object>>> printOrderByUser(@RequestBody PageModel model){
        return printStatisticsService.printOrderByUser(model);
    }

    /**
     * 按部门统计打印数量
     * @param model
     * @return
     */
    @PostMapping("/print/printOrderByOrg")
    @ApiOperation("按部门统计打印数量")
    public VData<List<Map<String,Object>>> printOrderByOrg(@RequestBody PageModel model){
        return printStatisticsService.printOrderByOrg(model);
    }

    /**
     * 按设备统计打印数量
     * @param model
     * @return
     */
    @PostMapping("/print/printOrderByDev")
    @ApiOperation("按设备统计打印数量")
    public VData<List<Map<String,Object>>> printOrderByDev(@RequestBody PageModel model){
        return printStatisticsService.printOrderByDev(model);
    }

    /**
     * 打印文件类型分布
     * @param model
     * @return
     */
    @PostMapping("/print/printFileType")
    @ApiOperation("打印文件类型分布")
    public VData<List<Map<String,Object>>> printFileType(@RequestBody PageModel model){
        return printStatisticsService.printFileType(model);
    }

    /**
     * 打印文件类型doc数量排名
     * @param model
     * @return
     */
    @PostMapping("/print/printFileDoc")
    @ApiOperation("打印文件类型doc数量排名")
    public VData<List<Map<String,Object>>> printFileDoc(@RequestBody PageModel model){
        return printStatisticsService.printFileDoc(model);
    }

    /**
     * 打印文件类型pdf数量排名
     * @param model
     * @return
     */
    @PostMapping("/print/printFilePdf")
    @ApiOperation("打印文件类型pdf数量排名")
    public VData<List<Map<String,Object>>> printFilePdf(@RequestBody PageModel model){
        return printStatisticsService.printFilePdf(model);
    }

    /**
     * 打印文件类型总数量排名
     * @param model
     * @return
     */
    @PostMapping("/print/printFileTypeTotal")
    @ApiOperation("打印文件类型总数量排名")
    public VData<List<Map<String,Object>>> printFileTypeTotal(@RequestBody PageModel model){
        return printStatisticsService.printFileTypeTotal(model);
    }
    /**
     * 打印文件类型数量趋势分析
     * @param model
     * @return
     */
    @PostMapping("/print/printFileTypeCountTrend")
    @ApiOperation("打印文件类型数量趋势分析")
    public VData<List<Map<String,Object>>> printFileTypeCountTrend(@RequestBody PrintTimeModel model){
        return printStatisticsService.printFileTypeCountTrend(model);
    }
    /**
     * 打印文件密级分布
     * @param model
     * @return
     */
    @PostMapping("/print/printFileLevel")
    @ApiOperation("打印文件密级分布")
    public VData<List<Map<String,Object>>> printFileLevel(@RequestBody PageModel model){
        return printStatisticsService.printFileLevel(model);
    }

    /**
     * 打印各个密级文件数量排行
     * @param model
     * @return
     */
    @PostMapping("/print/printCountByLevel")
    @ApiOperation("打印各个密级文件数量排行")
    public VData<List<Map<String,Object>>> printCountByLevel(@RequestBody FileLevelModel model){
        return printStatisticsService.printCountByLevel(model);
    }

    /**
     * 打印文件密级数量趋势分析
     * @param model
     * @return
     */
    @PostMapping("/print/printCountByLevelTrend")
    @ApiOperation("打印文件密级数量趋势分析")
    public VData<List<Map<String,Object>>> printCountByLevelTrend(@RequestBody PrintTimeModel model){
        return printStatisticsService.printCountByLevelTrend(model);
    }

    /**
     * 打印文件密级数量总次数排行
     * @param model
     * @return
     */
    @PostMapping("/print/printLevelByUser")
    @ApiOperation("打印文件密级数量总次数排行")
    public VData<List<Map<String,Object>>> printLevelByUser(@RequestBody PageModel model){
        return printStatisticsService.printLevelByUser(model);
    }

    /**
     * 按时间统计打印频次
     * @param model
     * @return
     */
    @PostMapping("/print/printCountByTime")
    @ApiOperation("按时间统计打印频次")
    public VData<List<Map<String,Object>>> printCountByTime(@RequestBody PrintTimeModel model){
        return printStatisticsService.printCountByTime(model);
    }

    /**
     * 按打印频次统计用户排名
     * @param model
     * @return
     */
    @PostMapping("/print/printCountByUser")
    @ApiOperation("按打印频次统计用户排名")
    public VData<List<Map<String,Object>>> printCountByUser(@RequestBody PageModel model){
        return printStatisticsService.printCountByUser(model);
    }

    /**
     * 按部门统计打印频次
     * @param model
     * @return
     */
    @PostMapping("/print/printCountByOrg")
    @ApiOperation("按部门统计打印频次")
    public VData<List<Map<String,Object>>> printCountByOrg(@RequestBody PageModel model){
        return printStatisticsService.printCountByOrg(model);
    }

    /**
     * 打印操作结果分布情况
     * @param model
     * @return
     */
    @PostMapping("/print/printResultInfo")
    @ApiOperation("打印操作结果分布情况")
    public VData<List<Map<String,Object>>> printResultInfo(@RequestBody PageModel model){
        return printStatisticsService.printResultInfo(model);
    }

    /**
     * 打印成功或失败次数统计用户排名
     * @param model
     * @return
     */
    @PostMapping("/print/printResultUser")
    @ApiOperation("打印成功或失败次数统计用户排名")
    public VData<List<Map<String,Object>>> printResultUser(@RequestBody PrintResultModel model){
        return printStatisticsService.printResultUser(model);
    }
    /**
     * 按时间统计操作结果发生趋势
     * @param model
     * @return
     */
    @PostMapping("/print/printResultTrend")
    @ApiOperation("按时间统计操作结果发生趋势")
    public VData<List<Map<String,Object>>> printResultTrend(@RequestBody PrintTimeModel model){
        return printStatisticsService.printResultTrend(model);
    }
    /**
     * 按部门统计操作结果
     * @param model
     * @return
     */
    @PostMapping("/print/printResultByOrg")
    @ApiOperation("按部门统计操作结果")
    public VData<List<Map<String,Object>>> printResultByOrg(@RequestBody PageModel model){
        return printStatisticsService.printResultByOrg(model);
    }

    /**
     * 按时间统计打刻行为趋势
     * @param model
     * @return
     */
    @PostMapping("/print/printOrburnTrend")
    @ApiOperation("按时间统计打刻行为趋势")
    public VData<List<Map<String,Object>>> printOrburnTrend(@RequestBody PrintBurnModel model){
        return printStatisticsService.printOrburnTrend(model);
    }

    @PostMapping("/print/printCountByHour")
    @ApiOperation("打印时间集中度分析")
    public VData<List<Map<String,Object>>> printCountByHour(@RequestBody PrintTimeModel model){
        return printStatisticsService.printCountByHour(model);
    }

    @PostMapping("/print/printFileSize")
    @ApiOperation("根据文件区间大小统计数量")
    public VData<List<Map<String,Object>>> printFileSize(@RequestBody PrintSizeModel model){
        return printStatisticsService.printFileSize(model);
    }

    @PostMapping("/print/printFileSizeInfo")
    @ApiOperation("文件大小分布情况")
    public VData<List<Map<String,Object>>> printFileSizeInfo(@RequestBody PageModel model){
        return printStatisticsService.printFileSizeInfo(model);
    }

    @PostMapping("/print/printDetail")
    @ApiOperation("详情")
    public VList<Map<String, String>> printDetail(@RequestBody  PrintDetailModel model){
        return printStatisticsService.printDetail(model);
    }

    @PostMapping("/print/exportDetail")
    @ApiOperation("详情导出")
    public VData<Export.Progress> exportDetail(@RequestBody  PrintDetailModel model){
        return printStatisticsService.exportDetail(model);
    }

    @GetMapping("/download/excel/{workId}")
    @ApiOperation("根据workid下载文件")
    public void download(@PathVariable("workId") String workId, HttpServletRequest req, HttpServletResponse resp) {
        Export.Progress progress = Export.getProcess(workId);
        String downloadName;
        String filePath;
        String channle = "local";
        if (null == progress) {
            String proStr = (String) redisTemplate.opsForValue().get(Common.EXPORT_REDIS_PRO_PATH + workId);
            JSONObject jsonObject = JSONObject.parseObject(proStr);
            if (jsonObject == null) {
                log.error("未查询到指定workid文件: " + workId);
                return;
            }
            JSONObject subObject = (JSONObject) jsonObject.get("firstExcelInfo");
            downloadName = subObject.getString("filename");
            filePath = subObject.getString("filePath");
            channle = "redis";
        } else {
            ExcelInfo info = progress.getFirstExcelInfo();
            downloadName = info.getFilename();
            filePath = info.getFilePath();
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");
        // 兼容火狐浏览器导出文件名乱码问题
        String agent = req.getHeader("USER-AGENT");
        try {
            if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
                downloadName = "=?UTF-8?B?" + (new String(Base64Utils.encodeToString(downloadName.getBytes("UTF-8")))) + "?=";
            } else {
                downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setHeader("Content-Disposition", "attachment;filename=" + downloadName + ".xls");
        OutputStream out = null;
        InputStream in = null;
        try {
            out = resp.getOutputStream();
            if ("local".equals(channle)) {
                in = FileUtils.openInputStream(new File(filePath));
                IOUtils.copy(in, out);
            } else {
                List blist = redisTemplate.opsForList().range(Common.EXPORT_REDIS_FILE_PATH + workId + "_file", 0, -1);
                int lengthTotal = 0;
                for (Object item : blist) {
                    byte[] tmp = (byte[]) item;
                    lengthTotal += tmp.length;
                }
                byte[] totalByte = new byte[lengthTotal];
                int begin = 0;
                for (Object item : blist) {
                    byte[] tmp = (byte[]) item;
                    System.arraycopy(tmp, 0, totalByte, begin, tmp.length);
                    begin += tmp.length;
                }
                out.write(totalByte);
            }
        } catch (IOException e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}
