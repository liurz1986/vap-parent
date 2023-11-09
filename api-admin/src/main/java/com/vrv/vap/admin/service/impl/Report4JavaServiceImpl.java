package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.*;
import com.vrv.vap.admin.model.VisualReportJava;
import com.vrv.vap.admin.model.VisualReportModel;
import com.vrv.vap.admin.service.Report4JavaService;
import com.vrv.vap.admin.service.VisualReportModelService;
import com.vrv.vap.admin.vo.QueryModel;
import com.vrv.vap.admin.vo.VisualReportJavaVO;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

//import com.vrv.vap.search.service.BaseDictAll2Service;

/**
 * Created by lizj on 2020/12/12
 */
@Service
@Transactional
public class Report4JavaServiceImpl implements Report4JavaService {

    private static final Logger logger = LoggerFactory.getLogger(Report4JavaServiceImpl.class);

    @Autowired
    private VisualReportModelService visualReportModelService;

//    @Autowired
//    private BaseDictAll2Service baseDictAllService;

    private static final String INDEX_NETFLOW = "netflow-*";
    private static final String INDEX_SEC_AUDIT = "sec_audit-*";
    private static final String INDEX_SEC_ALERT_CAMPAIGN = "sec_alert_campaign-*";

    private static final String DICT_AUDIT_KIND = "2ea4f08e-6f24-74fc-f30b-7ddfe55995ba";
    private static final String DICT_AUDIT_LEVEL = "cfe341a5-eca9-9cef-76c5-83cc1f5bcc2a";

    /**
     * 文件生成路径
     */
    @Value("${screen-capturer.file-path}")
    private String filePath;
    /**
     * 文件生成路径
     */
    @Value("${screen-capturer.fonts-path:/opt/vap/fonts/simhei.ttf}")
    private String fontsPath;

    @Override
    public String preview(VisualReportJava param) {
        return this.buildReportContent(param);
    }

    @Override
    public String export(VisualReportJavaVO param) {
        String result = "";
        FileOutputStream out = null;
        // 获取报表html
        String reportHtml = this.buildReportContent(param);
//        logger.info("reportHtml:" + reportHtml);
        // 生成文件名称
        String fileName = UUID.randomUUID().toString().toLowerCase().replace("-", "");
        try {
            // 导出类型 1-pdf 2-word 3-html
            if (param.getExportType() == 1) {
                logger.info("开始生成pdf，字体路径为：" + fontsPath);
                out = new FileOutputStream(filePath + File.separator + fileName + ".pdf");
                ReportUtils.writeStringToOutputStreamAsPDF2(reportHtml, out, fontsPath);
            } else if (param.getExportType() == 2) {
                out = new FileOutputStream(filePath + File.separator + fileName + ".doc");
                ReportUtils.writeStringToOutputStreamAsWord(reportHtml, out);
            } else if (param.getExportType() == 3) {
                out = new FileOutputStream(filePath + File.separator + fileName + ".html");
                ReportUtils.writeStringToOutputStreamAsHtml(reportHtml, out);
            } else {
                out = new FileOutputStream(filePath + File.separator + fileName + ".pdf");
                ReportUtils.writeStringToOutputStreamAsPDF(reportHtml, out);
            }
            result = fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 构建报表内容
     *
     * @param param
     * @return
     */
    private String buildReportContent(VisualReportJava param) {
        // 获取索引模块
        List<VisualReportModel> allModelList = visualReportModelService.findAll();
        // 构造模块id与模块内容映射
        Map<String, VisualReportModel> allModelMap = new HashMap<>();
        if (allModelList.size() > 0) {
            for (VisualReportModel model : allModelList) {
                allModelMap.put(model.getId() + "", model);
            }
        }

        StringBuffer html = new StringBuffer();
        StringBuffer desc = new StringBuffer();
        StringBuffer htmlBody = new StringBuffer();
        StringWriter writer = null;
        try {
            // html头
            writer = new StringWriter();
            if (StringUtils.isNotEmpty(param.getSubtitle()) && param.getSubtitle().indexOf("#generateTime#") > -1) {
                param.setSubtitle(param.getSubtitle().replace("#generateTime#", TimeTools.format2(new Date())));
            }
            html.append(writer.getBuffer());
            writer.flush();
            Date startTime = getStartTime(param);
            desc.append("<p><i>" + TimeTools.format4(startTime) + "</i>至<i>" + TimeTools.format4(TimeTools.getNow()) + "</i>，");
            // 根据模块组按顺序拼装模块
            String[] modelList = param.getModels().split(",");
            Map<String, Object> data = new HashMap<>();
            // 标题序号，总体概览默认都是一，故从二开始
            int titleTag = 2;
            for (int i = 0; i < modelList.length; i++) {
                VisualReportModel model = allModelMap.get(modelList[i]);
                // 模块类型 1-图表  2-文本  3-表格
                int modelType = model.getTemplateType();
                data = new HashMap<>();
                switch (modelType) {
                    // 图表
                    case 1:
                        String imageStr = this.getChartImageByType(model, param);
                        data.put("imageStr", imageStr);
                        break;
                    // 文本
                    case 2:
                        data = this.getTextByType(model, param);
                        break;
                    // 表格
                    case 3:
                        data = this.getTableByType(model, param);
                        break;
                    default:
                }
                data.put("title", CommonTools.int2chineseNum(titleTag) + "、" + model.getTitle());
                titleTag += 1;
                data.put("secondaryTitle", model.getSecondaryTitle().replace("#startTime#", TimeTools.format4(startTime)).replace("#endTime#", TimeTools.format4(TimeTools.getNow())));
                writer = new StringWriter();
                htmlBody.append(writer.getBuffer());
                writer.flush();
                if (StringUtils.isNotEmpty(model.getTemplateDesc())) {
                    desc.append(model.getTemplateDesc());
                }

            }
            data.put("desc", desc);
            //summary
            writer = new StringWriter();
            html.append(writer.getBuffer());
            writer.flush();

            //body
            html.append(htmlBody);

            // footer
            writer = new StringWriter();
            html.append(writer.getBuffer());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html.toString();
    }

    /**
     * 获取表格数据
     *
     * @param visualReportModel
     * @param param
     * @return
     */
    private Map<String, Object> getTableByType(VisualReportModel visualReportModel, VisualReportJava param) {
        Map<String, Object> result = new HashMap<>();
        QueryModel queryModel = null;
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        List<Map<String, Object>> dataList = null;
        Map<String, Object> data = null;
        List<String> headList = null;
        switch (visualReportModel.getDataId()) {
            // 源IP TOP5（会话数）
            case "netflow_3":
                queryModel = this.buildQueryModel(param, INDEX_NETFLOW, "event_time");
                dataList = QueryTools.simpleCardinalityAgg(queryModel, wrapper, "src_ip", "dst_ip", 5, "content", "count", "count2");
                headList = Arrays.asList(new String[]{"排行", "源IP", "会话数", "目标IP个数"});
                // 描述
                if (dataList != null && dataList.size() > 0) {
                    data = dataList.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                break;
            // 目标IP TOP5（会话数）
            case "netflow_4":
                queryModel = this.buildQueryModel(param, INDEX_NETFLOW, "event_time");
                dataList = QueryTools.simpleCardinalityAgg(queryModel, wrapper, "dst_ip", "src_ip", 5, "content", "count", "count2");
                headList = Arrays.asList(new String[]{"排行", "目标IP", "会话数", "源IP个数"});
                // 描述
                if (dataList != null && dataList.size() > 0) {
                    data = dataList.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                break;
            // 源端⼝ TOP5（会话数）
            case "netflow_5":
                queryModel = this.buildQueryModel(param, INDEX_NETFLOW, "event_time");
                dataList = QueryTools.simpleCardinalityAgg(queryModel, wrapper, "src_port", "dst_ip", 5, "content", "count", "count2");
                headList = Arrays.asList(new String[]{"排行", "源端口", "会话数", "目标IP个数"});
                // 描述
                if (dataList != null && dataList.size() > 0) {
                    data = dataList.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                break;
            // ⽬标端⼝ TOP5（会话数）
            case "netflow_6":
                queryModel = this.buildQueryModel(param, INDEX_NETFLOW, "event_time");
                dataList = QueryTools.simpleCardinalityAgg(queryModel, wrapper, "dst_port", "src_ip", 5, "content", "count", "count2");
                headList = Arrays.asList(new String[]{"排行", "目标端口", "会话数", "源IP个数"});
                // 描述
                if (dataList != null && dataList.size() > 0) {
                    data = dataList.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                break;
            // 按产品上报数量排行
//            case "sec_audit_4":
//                queryModel = this.buildQueryModel(param, INDEX_SEC_AUDIT, "event_time");
//                dataList = QueryTools.simpleAgg2(queryModel, wrapper, "name", 5, "content", "count");
//                headList = Arrays.asList(new String[]{"排行", "名称", "数量"});
//                if (dataList != null && dataList.size() > 0) {
//                    data = dataList.get(0);
//                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
//                } else {
//                    visualReportModel.setTemplateDesc("");
//                }
//                break;
            // 源IP TOP5（会话数）
            case "sec_alert_campaign_4":
                queryModel = this.buildQueryModel(param, INDEX_SEC_ALERT_CAMPAIGN, "event_time");
                dataList = QueryTools.simpleCardinalityAgg(queryModel, wrapper, "src_ip", "dst_ip", 5, "content", "count", "count2");
                headList = Arrays.asList(new String[]{"排行", "源IP", "攻击次数", "目标IP个数"});
                if (dataList != null && dataList.size() > 0) {
                    data = dataList.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                break;
            // 目标IP TOP5（会话数）
            case "sec_alert_campaign_5":
                queryModel = this.buildQueryModel(param, INDEX_SEC_ALERT_CAMPAIGN, "event_time");
                dataList = QueryTools.simpleCardinalityAgg(queryModel, wrapper, "dst_ip", "src_ip", 5, "content", "count", "count2");
                headList = Arrays.asList(new String[]{"排行", "目标IP", "被攻击次数", "源IP个数"});
                if (dataList != null && dataList.size() > 0) {
                    data = dataList.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) data.get("content")).replace("#count#", (int) data.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                break;
        }
        result.put("headList", headList);
        result.put("dataList", dataList);
        return result;
    }

    /**
     * 根据类型生成需加入报表的文本
     *
     * @param visualReportModel
     * @param param
     * @return
     */
    private Map<String, Object> getTextByType(VisualReportModel visualReportModel, VisualReportJava param) {
        Map<String, Object> result = new HashMap<>();

        switch (visualReportModel.getDataId()) {
            //
            case "sec_audit_0":
                result.put("text", "数据源数量统计：12");
                break;
        }
        return result;
    }


    /**
     * 根据类型判断获取图表
     *
     * @param
     * @param param
     * @return
     */
    private String getChartImageByType(VisualReportModel visualReportModel, VisualReportJava param) {
        String result = "";
        QueryModel queryModel = null;
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        List<Map<String, Object>> data = null;
        DefaultCategoryDataset dataSet = null;
        DefaultPieDataset pieDataSet = null;
        // 描述
        long total = 0;
        String maxDate = "";
        long maxCount = 0;
        switch (visualReportModel.getDataId()) {
            // 流量⽇志⼊库量趋势
            case "netflow_1":
                queryModel = this.buildQueryModel(param, INDEX_NETFLOW, "event_time");
                data = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "MM-dd", 8, "date", "count");

                for (Map<String, Object> map : data) {
                    int count = (int) map.get("count");
                    total += count;
                    if (maxCount == 0 || maxCount < count) {
                        maxCount = count;
                        maxDate = (String) map.get("date");
                    }
                }
                visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#total#", total + "").replace("#name#", maxDate).replace("#count#", maxCount + ""));
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "date", "count");
                result = this.createLineChart(dataSet, "", "", "", PlotOrientation.VERTICAL, true, true, false, 600, 400);
                break;
            // 协议占比统计
            case "netflow_2":
                queryModel = this.buildQueryModel(param, INDEX_NETFLOW, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "transport_protocol", 999, "protocol", "count");
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "protocol", "count");
                result = this.createBarChart(dataSet, "", "", "", PlotOrientation.VERTICAL, false, true, false, 600, 400);
                break;
            // 审计日志入库趋势
            case "sec_audit_1":
                queryModel = this.buildQueryModel(param, INDEX_SEC_AUDIT, "event_time");
                data = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "MM-dd", 8, "date", "count");

                for (Map<String, Object> map : data) {
                    int count = (int) map.get("count");
                    total += count;
                    if (maxCount == 0 || maxCount < count) {
                        maxCount = count;
                        maxDate = (String) map.get("date");
                    }
                }
                visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#total#", total + "").replace("#name#", maxDate).replace("#count#", maxCount + ""));
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "date", "count");
                result = this.createLineChart(dataSet, "", "", "", PlotOrientation.VERTICAL, true, true, false, 600, 400);
                break;
            // 行为类别占比
            case "sec_audit_2":
                queryModel = this.buildQueryModel(param, INDEX_SEC_AUDIT, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "kind", 999, "kind", "count");
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "kind", "count", DICT_AUDIT_KIND);
                result = this.createBarChart(dataSet, "", "", "", PlotOrientation.HORIZONTAL, false, true, false, 600, 400);
                break;
            // 风险级别排行
            case "sec_audit_3":
                queryModel = this.buildQueryModel(param, INDEX_SEC_AUDIT, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "level", 999, "level", "count");
                if (data == null || data.size() ==0) {
                    break;
                }
                pieDataSet = createPieDataSet(data, "level", "count", DICT_AUDIT_LEVEL);
                result = this.createPieChart(pieDataSet, "", "", "", PlotOrientation.HORIZONTAL, false, true, false, 600, 400);
                break;
            // 产品上报操作日志数量统计
            case "sec_audit_4":
                queryModel = this.buildQueryModel(param, INDEX_SEC_AUDIT, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "name", 999, "name", "count");

                if (data != null && data.size() > 0) {
                    Map<String, Object> map = data.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) map.get("name")).replace("#count#", (int) map.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "name", "count");
                result = this.createBarChart(dataSet, "", "", "", PlotOrientation.HORIZONTAL, false, true, false, 600, 400);
                break;

            // 网络攻击日志入库量趋势
            case "sec_alert_campaign_1":
                queryModel = this.buildQueryModel(param, INDEX_SEC_ALERT_CAMPAIGN, "event_time");
                data = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "MM-dd", 8, "date", "count");

                for (Map<String, Object> map : data) {
                    int count = (int) map.get("count");
                    total += count;
                    if (maxCount == 0 || maxCount < count) {
                        maxCount = count;
                        maxDate = (String) map.get("date");
                    }
                }
                visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#total#", total + "").replace("#name#", maxDate).replace("#count#", maxCount + ""));
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "date", "count");
                result = this.createLineChart(dataSet, "", "", "", PlotOrientation.VERTICAL, true, true, false, 600, 400);
                break;
            // 网络攻击日志风险级别统计
            case "sec_alert_campaign_2":
                queryModel = this.buildQueryModel(param, INDEX_SEC_ALERT_CAMPAIGN, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "level", 999, "level", "count");
                if (data == null || data.size() ==0) {
                    break;
                }
                pieDataSet = createPieDataSet(data, "level", "count", DICT_AUDIT_LEVEL);
                result = this.createPieChart(pieDataSet, "", "", "", PlotOrientation.VERTICAL, false, true, false, 600, 400);
                break;
            // 网络攻击日志事件类别统计
            case "sec_alert_campaign_3":
                queryModel = this.buildQueryModel(param, INDEX_SEC_ALERT_CAMPAIGN, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "event_name", 999, "event_name", "count");

                if (data != null && data.size() > 0) {
                    Map<String, Object> datainfo = data.get(0);
                    visualReportModel.setTemplateDesc(visualReportModel.getTemplateDesc().replace("#name#", (String) datainfo.get("event_name")).replace("#count#", (int) datainfo.get("count") + ""));
                } else {
                    visualReportModel.setTemplateDesc("");
                }
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "event_name", "count");
                result = this.createBarChart(dataSet, "", "", "", PlotOrientation.HORIZONTAL, false, true, false, 600, 400);
                break;
            // 网络攻击日志行为类别排行
            case "sec_alert_campaign_6":
                queryModel = this.buildQueryModel(param, INDEX_SEC_ALERT_CAMPAIGN, "event_time");
                data = QueryTools.simpleAgg(queryModel, wrapper, "kind", 999, "kind", "count");
                if (data == null || data.size() ==0) {
                    break;
                }
                dataSet = createDataSet(data, "kind", "count", DICT_AUDIT_KIND);
                result = this.createBarChart(dataSet, "", "", "", PlotOrientation.HORIZONTAL, false, true, false, 600, 400);
                break;
        }
        return result;
    }


    private Date getStartTime(VisualReportJava param) {
        Date startTime = null;
        String timeRange = param.getTimeRange();
        int num = Integer.parseInt(timeRange.substring(0, 1));
        // 天
        if (timeRange.endsWith("d")) {
            startTime = TimeTools.getNowBeforeByDay(num - 1);
        } else if (timeRange.endsWith("m")) {
            // 月
            startTime = TimeTools.getNowBeforeByMonth(num);
        } else {
            startTime = TimeTools.getNowBeforeByDay(num - 1);
        }
        return startTime;
    }

    /**
     * 构造netflow查询
     *
     * @param param
     * @param index
     * @param timeField
     * @return
     */
    private QueryModel buildQueryModel(VisualReportJava param, String index, String timeField) {
        String timeRange = param.getTimeRange();
        Date startTime = null;
        Date endTime = TimeTools.getNowBeforeByDay2(0);
        int num = Integer.parseInt(timeRange.substring(0, 1));
        // 天
        if (timeRange.endsWith("d")) {
            startTime = TimeTools.getNowBeforeByDay(num - 1);
        } else if (timeRange.endsWith("m")) {
            // 月
            startTime = TimeTools.getNowBeforeByMonth(num);
        } else {
            startTime = TimeTools.getNowBeforeByDay(num - 1);
        }
        QueryModel model = QueryTools.buildCommonQueryModel(startTime, endTime, index, timeField);
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        model.setQueryBuilder(boolQuery);
        return model;
    }

    /**
     * 生成线型图
     *
     * @param dataSet       数据源
     * @param title         标题
     * @param xLabel        x轴描述
     * @param yLabel        y轴描述
     * @param orientation   方向
     * @param includeLegend
     * @param tooltips
     * @param urls
     * @param width         生成图片的宽度
     * @param height        生成图片的高度
     * @return
     */
    public String createLineChart(DefaultCategoryDataset dataSet, String title, String xLabel, String yLabel, PlotOrientation orientation, boolean includeLegend, boolean tooltips, boolean urls, int width, int height) {
        JFreeChart chart = ChartFactory.createLineChart(
                title, // chart title
                xLabel, // domain axis label
                yLabel, // range axis label
                dataSet, // data
                orientation, // orientation
                includeLegend, // include legend
                tooltips, // tooltips
                urls // urls

        );

        CategoryPlot plot = chart.getCategoryPlot();
        // 设置图示字体
        chart.getTitle().setFont(new Font("宋体", Font.BOLD, 22));
        //设置横轴的字体
        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setLabelFont(new Font("宋体", Font.BOLD, 10));//x轴标题字体
        categoryAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 10));//x轴刻度字体

        //以下两行 设置图例的字体
//        LegendTitle legend = chart.getLegend(0);
//        legend.setItemFont(new Font("宋体", Font.BOLD, 14));
        //设置竖轴的字体
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 10)); //设置竖轴的字体
        rangeAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 10));

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());//去掉竖轴字体显示不全
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setUpperMargin(0.20);
        rangeAxis.setLabelAngle(Math.PI / 2.0);
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartRenderTools.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        ChartRenderTools.setLineRender(chart.getCategoryPlot(), false, true);//
        // 5:对其他部分进行渲染
        ChartRenderTools.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartRenderTools.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(swapStream, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Tools.encodeBase64(swapStream.toByteArray().toString()));
    }

    /**
     * 生成柱型图
     *
     * @param dataSet       数据源
     * @param title         标题
     * @param xLabel        x轴描述
     * @param yLabel        y轴描述
     * @param orientation   方向
     * @param includeLegend
     * @param tooltips
     * @param urls
     * @param width         生成图片的宽度
     * @param height        生成图片的高度
     * @return
     */
    public String createBarChart(DefaultCategoryDataset dataSet, String title, String xLabel, String yLabel, PlotOrientation orientation, boolean includeLegend, boolean tooltips, boolean urls, int width, int height) {
        JFreeChart chart = ChartFactory.createBarChart(
                title, // chart title
                xLabel, // domain axis label
                yLabel, // range axis label
                dataSet, // data
                orientation, // orientation
                includeLegend, // include legend
                tooltips, // tooltips
                urls // urls
        );
        CategoryPlot plot = chart.getCategoryPlot();
        // 设置图示字体
        chart.getTitle().setFont(new Font("宋体", Font.BOLD, 22));
        //设置横轴的字体
        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setLabelFont(new Font("宋体", Font.BOLD, 10));//x轴标题字体
        categoryAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 10));//x轴刻度字体

        //以下两行 设置图例的字体
//        LegendTitle legend = chart.getLegend(0);
//        legend.setItemFont(new Font("宋体", Font.BOLD, 14));
        //设置竖轴的字体
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 10)); //设置数轴的字体
        rangeAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 10));

        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());//去掉竖轴字体显示不全
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setUpperMargin(0.20);
        rangeAxis.setLabelAngle(Math.PI / 2.0);

        // 3:设置抗锯齿，防止字体显示不清楚
        ChartRenderTools.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染
        ChartRenderTools.setBarRenderer(chart.getCategoryPlot(), false);//
        // 5:对其他部分进行渲染
        ChartRenderTools.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartRenderTools.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
        // 设置标注无边框
//        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));


        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(swapStream, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Tools.encodeBase64(swapStream.toByteArray().toString()));
    }

    /**
     * 生成饼图
     *
     * @param dataSet       数据源
     * @param title         标题
     * @param includeLegend
     * @param tooltips
     * @param urls
     * @param width         生成图片的宽度
     * @param height        生成图片的高度
     * @return
     */
    public String createPieChart(PieDataset dataSet, String title, String xLabel, String yLabel, PlotOrientation orientation, boolean includeLegend, boolean tooltips, boolean urls, int width, int height) {
        JFreeChart chart = ChartFactory.createPieChart(
                title, // chart title
                dataSet, // data
                includeLegend, // include legend
                tooltips, // tooltips
                urls // urls
        );
        PiePlot pieplot = (PiePlot) chart.getPlot(); //通过JFreeChart 对象获得
        pieplot.setNoDataMessage("无数据可供显示！"); // 没有数据的时候显示的内容
        pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                ("{0}: ({2})"), NumberFormat.getNumberInstance(),
                new DecimalFormat("0.00%")));
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(swapStream, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Tools.encodeBase64(swapStream.toByteArray().toString()));
    }

    /**
     * 根据模块生成对应数据
     *
     * @param dataList
     * @param key
     * @param count
     * @return
     */
    public DefaultCategoryDataset createDataSet(List<Map<String, Object>> dataList, String key, String count) {
        DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                linedataset.addValue((int) data.get(count), "", (String) data.get(key));
            }
        }
        return linedataset;
    }

    /**
     * 根据模块生成对应数据（饼图数据）
     *
     * @param dataList
     * @param key
     * @param count
     * @return
     */
    public DefaultPieDataset createPieDataSet(List<Map<String, Object>> dataList, String key, String count) {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                pieDataset.setValue((String) data.get(key), (int) data.get(count));
            }
        }
        return pieDataset;
    }

    /**
     * 根据模块生成对应数据（饼图）
     *
     * @param dataList
     * @param date
     * @param count
     * @param dicId
     * @return
     */
    public DefaultPieDataset createPieDataSet(List<Map<String, Object>> dataList, String date, String count, String dicId) {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
//        List<BaseDictAll2> dicts = baseDictAllService.findByProperty(BaseDictAll2.class, "parentType", dicId);
        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                final String name = (String) data.get(date);
                String label = name;
//                if (dicts != null && dicts.size() > 0) {
//                    Optional<BaseDictAll2> optionalBaseDictAll = dicts.stream().filter(dic -> dic.getCode().equals(name)).findFirst();
//                    if (optionalBaseDictAll.isPresent()) {
//                        label = optionalBaseDictAll.get().getCodeValue();
//                    }
//                }
                pieDataset.setValue(label, (int) data.get(count));
            }
        }
        return pieDataset;
    }

    /**
     * 根据模块生成对应数据
     *
     * @param dataList
     * @param date
     * @param count
     * @param dicId
     * @return
     */
    public DefaultCategoryDataset createDataSet(List<Map<String, Object>> dataList, String date, String count, String dicId) {
        DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
//        List<BaseDictAll2> dicts = baseDictAllService.findByProperty(BaseDictAll2.class, "parentType", dicId);
        if (dataList.size() > 0) {
            for (Map<String, Object> data : dataList) {
                final String name = (String) data.get(date);
                String label = name;
//                if (dicts != null && dicts.size() > 0) {
//                    Optional<BaseDictAll2> optionalBaseDictAll = dicts.stream().filter(dic -> dic.getCode().equals(name)).findFirst();
//                    if (optionalBaseDictAll.isPresent()) {
//                        label = optionalBaseDictAll.get().getCodeValue();
//                    }
//                }
                linedataset.addValue((int) data.get(count), "", label);
            }
        }
        return linedataset;
    }


}
