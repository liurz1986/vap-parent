package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.constant.ReportConstant;
import com.vrv.vap.admin.common.pdf.PdfExport;
import com.vrv.vap.admin.common.util.QueryTools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.BaseReportMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.util.ReportEngine;
import com.vrv.vap.admin.util.ReportRunnable;
import com.vrv.vap.admin.vo.BaseReportVo;
import com.vrv.vap.admin.vo.EsSearchQuery;
import com.vrv.vap.admin.vo.QueryModel;
import com.vrv.vap.admin.vo.ReportConfig;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping(path = "/baseReport")
public class BaseReportController extends ApiController {

    @Autowired
    private BaseReportService baseReportService;
    @Autowired
    private BaseReportModelService baseReportModelService;
    @Autowired
    private BaseReportInterfaceService baseReportInterfaceService;
    @Autowired
    BaseReportMapper baseReportMapper;
    @Autowired
    private BasePersonZjgService basePersonZjgService;
    @Autowired
    private NetworkMonitorService networkMonitorService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    private static final Logger log = LoggerFactory.getLogger(BaseReportController.class);

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("menuEnable","{\"false\":\"否\",\"true\":\"是\"}");
    }

    /**
     * 获取所有报表
     * @return
     */
    @ApiOperation(value = "获取所有报表")
    @GetMapping(value = "/all")
    @SysRequestLog(description="获取所有报表", actionType = ActionType.SELECT)
    public VData<List<BaseReport>> queryAllreport() {
        return this.vData(baseReportService.findAll());
    }


    /**
     * 条件查询报表
     * 支持分页查询、条件查询 、任意字段排序
     */
    @ApiOperation(value = "条件查询报表")
    @PostMapping
    @SysRequestLog(description="查询报表", actionType = ActionType.SELECT)
    public VList<BaseReport> queryAreas(@RequestBody BaseReportVo baseReportVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(baseReportVo, BaseReport.class);
        return this.vList(baseReportService.findByExample(example));
    }

    /**
     * 添加报表
     */
    @ApiOperation(value = "添加报表")
    @PutMapping
    @SysRequestLog(description="添加报表", actionType = ActionType.ADD)
    public Result add(@RequestBody BaseReport baseReport){
        baseReport.setCreateTime(new Date());
        int result = baseReportService.save(baseReport);
        if(result == 1){
            SyslogSenderUtils.sendAddSyslogAndTransferredField(baseReport,"添加报表",transferMap);
            return this.vData(baseReport);
        }
        return this.result( false);
    }

    /**
     * 修改报表
     */
    @ApiOperation(value = "修改报表")
    @PatchMapping
    @SysRequestLog(description="修改报表", actionType = ActionType.UPDATE)
    public Result edit(@RequestBody BaseReport baseReport){
        BaseReport baseReportSec = baseReportService.findById(baseReport.getId());
        int result = baseReportService.updateSelective(baseReport);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(baseReportSec,baseReport,"修改报表",transferMap);
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "报表详情")
    @GetMapping(value = "/{reportId}")
    public VData<BaseReport> detail(@PathVariable("reportId") Integer reportId){
        return this.vData( baseReportService.findById(reportId));
    }



    @ApiOperation(value = "报表导出")
    @PostMapping(value = "/report")
    @SysRequestLog(description="报表导出", actionType = ActionType.EXPORT)
    public Result report(@RequestBody Map map,HttpServletResponse response){
        SyslogSenderUtils.sendExportSyslog();
        log.info("#########报表导出开始：" + LogForgingUtil.validLog(map.toString()));
        BaseReport report = baseReportService.findById(Integer.parseInt(map.get("reportId").toString()));
        if(map.get("p")!=null && StringUtils.isNotEmpty(map.get("p").toString())){
            Map<String,Object> params = (Map) JSON.parse(map.get("p").toString());
            report.setBindParam(params);
        }
        PdfExport.PdfProgress progress = ReportEngine.report(report, map.get("type").toString());
        return this.vData(progress);
    }

    @ResponseBody
    @GetMapping("/progress/{workId}")
    @ApiOperation("根据workid获取导出进度")
    public Result getProgress(@ApiParam("workId") @PathVariable("workId") String workId) {
        PdfExport.PdfProgress progress = PdfExport.getProcess(workId);
        return this.vData(progress);
    }

    @GetMapping("/download/{workId}")
    @ApiOperation("根据workid下载报表文件")
    @SysRequestLog(description="根据workid下载报表文件", actionType = ActionType.DOWNLOAD)
    public void download( @ApiParam("workId") @PathVariable("workId") String workId, HttpServletResponse response, HttpServletRequest req) {
        SyslogSenderUtils.sendDownLosdSyslog();
        PdfExport.PdfProgress progress = PdfExport.getProcess(workId);
        if (null == progress) {
            log.error("未查询到指定workid文件: " + LogForgingUtil.validLog(workId));
            return;
        }

        File reportFile = new File(CleanUtil.cleanString(ReportConstant.TEM_DIR.DEFAULT+File.separator+progress.getFileName()));
        String fileName = progress.getFileName();


        try (FileInputStream fileIn = new FileInputStream(reportFile);
            ServletOutputStream out = response.getOutputStream()) {
            //String fileName = new String(fileNameString.getBytes("ISO8859-1"), "UTF-8");
            response.setContentType("application/octet-stream");
            // URLEncoder.encode(fileNameString, "UTF-8") 下载文件名为中文的，文件名需要经过url编码
            response.setHeader("Content-Disposition", "attachment;filename=" + CleanUtil.cleanString(URLEncoder.encode(fileName, "UTF-8")));

            byte[] outputByte = new byte[1024];
            int readTmp = 0;
            while ((readTmp = fileIn.read(outputByte)) != -1) {
                out.write(outputByte, 0, readTmp); //并不是每次都能读到1024个字节，所有用readTmp作为每次读取数据的长度，否则会出现文件损坏的错误
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        //删除临时文件
        if(reportFile.exists()){
            reportFile.delete();
        }
    }

    @ApiOperation(value = "获取报表预览标识")
    @GetMapping(value = "/toView")
    public Result toView(@RequestParam(required=false)Map<String,Object> params) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        log.info("#############toView start with:" + LogForgingUtil.validLog(params.toString()));
        BaseReport report = new BaseReport();
        if(params.get("reportId") != null && StringUtils.isNotEmpty(params.get("reportId").toString())){
            report = baseReportService.findById(Integer.parseInt(params.get("reportId").toString()));
        }else{
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                BeanUtils.setProperty(report, entry.getKey(), entry.getValue());
            }
        }
        if(params.get("p") != null && StringUtils.isNotEmpty(params.get("p").toString())){
            Map<String,Object> map = (Map) JSON.parse(params.get("p").toString());
            report.setBindParam(map);
        }
        String s = ReportEngine.renderReport(report);
        return this.vData(s);
    }

    @ApiOperation(value = "报表预览状态")
    @GetMapping(value = "/view/status/{uuid}")
    public Boolean getStatus(@PathVariable("uuid") String uuid) {
        String status = ReportRunnable.getStatus(uuid);
        return (StringUtils.isNotEmpty(status));
    }

    @ApiOperation(value = "报表预览")
    @GetMapping(value = "/view/{uuid}")
    public String view(@PathVariable("uuid") String uuid,HttpServletResponse response) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        String html = ReportRunnable.getStatus(uuid);
        if(StringUtils.isNotEmpty(html)){
            ReportRunnable.removeStatus(uuid);
            File srchtml = new File(html);
            String result = FileUtils.readFileToString(srchtml, Charset.forName("UTF-8"));
            //预览完成后删除临时文件
            try{
                srchtml.delete();
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            return result;
        }else{
            return "预览正在生成中...，请稍后再试！";
        }
    }


    @ApiOperation(value = "删除报表")
    @DeleteMapping
    @SysRequestLog(description="删除报表", actionType = ActionType.DELETE)
    public Result deleteModel(@RequestBody DeleteQuery param) {
        List<BaseReport> reportList = baseReportService.findByids(param.getIds());
        int result = baseReportService.deleteByIds(param.getIds());
        if (result > 0) {
            reportList.forEach(baseReport -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(baseReport,"删除报表",transferMap);
            });
        }
        return this.result(result >= 1);
    }

    @ApiOperation(value = "报表预览")
    @GetMapping(value = "/view")
    public String view(@RequestParam(required=false)Map<String,Object> params,HttpServletResponse response) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        BaseReport report = new BaseReport();
        if(params.get("reportId") != null && StringUtils.isNotEmpty(params.get("reportId").toString())){
            report = baseReportService.findById(Integer.parseInt(params.get("reportId").toString()));
        }else{
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                BeanUtils.setProperty(report, entry.getKey(), entry.getValue());
            }
        }
        if(params.get("p") != null && StringUtils.isNotEmpty(params.get("p").toString())){
            Map<String,Object> map = (Map) JSON.parse(params.get("p").toString());
            report.setBindParam(map);
        }
        return ReportEngine.renderReportOld(report);
    }

    /**
     * 2023-10-13
     * 涂爷前端反馈以前接口get请求参数太长，改为post请求，历史接口保留
     * @param params
     * @param response
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "报表预览")
    @PostMapping(value = "/view")
    public String postView(BaseReportParam params,HttpServletResponse response) throws IOException, InvocationTargetException, IllegalAccessException, SQLException, ClassNotFoundException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        BaseReport report = new BaseReport();
        if(null!= params.getReportId() && StringUtils.isNotEmpty(params.getReportId())){
            report = baseReportService.findById(Integer.parseInt(params.getReportId()));
        }else{
            BeanUtils.copyProperties(report,params);
        }
        if(params != null && StringUtils.isNotEmpty(params.getP())){
            Map<String,Object> map = (Map)JSON.parse(params.getP());
            report.setBindParam(map);
        }
        return ReportEngine.renderReportOld(report);
    }

    @GetMapping("/config")
    @ApiOperation("导出报表配置文件")
    @SysRequestLog(description="导出报表配置文件", actionType = ActionType.EXPORT)
    public void config(@ApiParam(value = "报表ID") String reportIds,HttpServletResponse response) throws IOException {
        SyslogSenderUtils.sendExportSyslog();
        if(StringUtils.isEmpty(reportIds)){
            log.error("报表导出reportIds为空");
            return;
        }
        String reg = "^[,0-9]+$";
        if(!reportIds.matches(reg)){
            log.info("reportIds：" + LogForgingUtil.validLog(reportIds) + " 参数错误！");
            return;
        }
        List<BaseReport> reports = baseReportService.findByids(reportIds);
        //取所有模型
        Set<String> models = new HashSet<>();
        List<BaseReportModel> reportmodels = new ArrayList<>();
        for(BaseReport report : reports){
            if(StringUtils.isNotEmpty(report.getModels())){
                cycleListModel(JSONArray.parseArray(report.getModels()),models);
            }
        }
        if(CollectionUtils.isNotEmpty(models)){
            reportmodels = this.baseReportModelService.selectByIds(models);
        }
        //取所有指标
        Set<String> ins = new HashSet<>();
        List<BaseReportInterface> interfaces = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(reportmodels)){
            reportmodels.forEach(m ->{
                if(StringUtils.isNotEmpty(m.getInterfaceId())){
                    ins.add(m.getInterfaceId());
                }
            });
        }
        if(CollectionUtils.isNotEmpty(ins)){
            interfaces = this.baseReportInterfaceService.selectByIds(ins);
        }
        /*
        if(CollectionUtils.isNotEmpty(reportmodels)){
            encodeModel(reportmodels);
        }*/
        ReportConfig config = new ReportConfig(reports,reportmodels,interfaces);
        File file = File.createTempFile(UUID.randomUUID().toString().replaceAll("-",""),".json");
        try ( FileOutputStream fos = new FileOutputStream(file);
              Writer write = new OutputStreamWriter(fos, "UTF-8")) {
            write.write(JSONObject.toJSONString(config));
            write.flush();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        //String name = StringUtils.isNotEmpty(report.getName()) ? URLEncoder.encode(report.getName())+".json" : file.getName();
        file.setReadOnly();
        String name = file.getName();
        try (FileInputStream fileIn = new FileInputStream(file);
             ServletOutputStream out = response.getOutputStream();) {
            //String fileName = new String(fileNameString.getBytes("ISO8859-1"), "UTF-8");
            response.setContentType("text/html");
            // URLEncoder.encode(fileNameString, "UTF-8") 下载文件名为中文的，文件名需要经过url编码
            response.setHeader("Content-Disposition", "attachment;filename=" + name/*URLEncoder.encode(report.getName(), "UTF-8")*/);
            byte[] outputByte = new byte[1024];
            int readTmp = 0;
            while ((readTmp = fileIn.read(outputByte)) != -1) {
                out.write(outputByte, 0, readTmp); //并不是每次都能读到1024个字节，所有用readTmp作为每次读取数据的长度，否则会出现文件损坏的错误
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }

    private void encodeModel(List<BaseReportModel> models){
        BASE64Encoder be = new BASE64Encoder();
        models.forEach(e ->{
            if(StringUtils.isNotEmpty(e.getSql())) {
                e.setSql(be.encode(e.getSql().getBytes(StandardCharsets.UTF_8)));
            }
            if(StringUtils.isNotEmpty(e.getContent())) {
                e.setContent(be.encode(e.getContent().getBytes(StandardCharsets.UTF_8)));
            }
        });
    }

    public void cycleListModel(JSONArray jsonArray,Set<String> models){
        jsonArray.forEach( e ->{
            JSONObject obj = (JSONObject)e;
            Object id = obj.get("id");
            JSONArray children = (JSONArray)obj.get("children");
            if(id != null && StringUtils.isNotEmpty(id.toString())){
                try {
                    models.add(id.toString());
                    if(children != null && children.size() > 0){
                        cycleListModel(children,models);
                    }
                } catch (Exception throwables) {
                    log.error(throwables.getMessage(),throwables);
                }
            }
        });
    }

    @PostMapping("/importConfig")
    @ApiOperation("导入报表配置")
    @SysRequestLog(description="导入报表配置", actionType = ActionType.IMPORT)
    public Result importConfig(@RequestBody ReportConfig config){
        ReportConfig reportConfig = new ReportConfig();
        org.springframework.beans.BeanUtils.copyProperties(config, reportConfig);
        boolean flag = this.baseReportService.importConfig(reportConfig);
        if(flag){
            SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
            syslogSender.sendSysLog(ActionType.IMPORT, null, null, "1");
            return this.result(flag);
        }else{
            return new Result("1","sql中存在非法字符串");
        }
    }

    /*
    * 人员统计
    *
    */
    @PostMapping("/person")
    @ApiOperation("人员统计")
    public VData queryPersonData(){
        Map<String, Object> personDataMap = new HashMap<>();
        List<BasePersonZjg> personList = basePersonZjgService.findAll();
        if (CollectionUtils.isNotEmpty(personList)) {
            Long personSecret = personList.stream().filter(p -> p.getSecretLevel() < 4).count();
            Long personNotSecret = personList.stream().filter(p -> p.getSecretLevel() == 4).count();
            personDataMap.put("personAll", personList.size());
            personDataMap.put("personSecret", personSecret);
            personDataMap.put("personNotSecret", personNotSecret);
        }
        return this.vData(personDataMap);
    }

    /*
     * 人员数量按部门统计
     *
     */
    @PostMapping("/person/org")
    @ApiOperation("人员数量按部门统计")
    public VList queryPersonByOrg(){
        return this.vList(baseReportMapper.queryPersonByOrg());
    }

    /*
     * 人员数量按密级统计
     *
     */
    @PostMapping("/person/secret")
    @ApiOperation("人员数量按密级统计")
    public VList queryPersonBySecret(){
        return this.vList(baseReportMapper.queryPersonBySecret());
    }

    /*
     * 监测器数据接入情况统计
     *
     */
    @PostMapping("/monitor")
    @ApiOperation("监测器数据接入情况统计")
    public VData queryMonitorData(@RequestBody EsSearchQuery esSearchQuery){
        List<NetworkMonitor> networkMonitorList = networkMonitorService.findByProperty(NetworkMonitor.class,"status", 0);
        Long monitorCount = networkMonitorList.stream().map(p -> p.getDeviceId()).distinct().count();
        Map<String, Object> monitorDataMap = new HashMap<>();
        monitorDataMap.put("total", monitorCount);


        List<String> indexList = Arrays.asList("netflow-*");
        String queryJsonStr = "{\"from\": 0,\"size\": 0,\"query\": {\"bool\": {\"must\": [{\"range\": " +
                "{\"event_time\": {\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\": \"yyyy-MM-dd HH:mm:ss\",\"time_zone\": \"+08:00\"}}}]," +
                "\"must_not\": [{\"term\": {\"report_log_type\": \"\"}}]}}," +
                "\"aggregations\": {\"term_field\": {\"terms\": {\"field\": \"report_log_type\"," +
                "\"size\": 3000,\"min_doc_count\": 1,\"shard_min_doc_count\": 0," +
                "\"show_term_doc_count_error\": false,\"order\": [{\"_count\": \"desc\"}]}}}}";

        List<String> typeList = Arrays.asList("DT012","DT013","DT014","DT015","DT016","DT017","DT018","DT019","DT020");
        typeList.forEach(type -> {
            monitorDataMap.put(transLogType(type), 0);
        });

        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> logTypeList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("term_field")).get("buckets");

            logTypeList.forEach(p -> {
                monitorDataMap.put(transLogType(p.get("key").toString()), p.get("doc_count"));
            });
        } catch (Exception e){
            log.error("监测器数据接入查询异常", e);
        }

        return this.vData(monitorDataMap);
    }

    /*
     * 监测器信息统计
     *
     */
    @PostMapping("/monitor/info")
    @ApiOperation("监测器信息统计")
    public VList queryMonitorInfo(){
        return this.vList(baseReportMapper.queryMonitorInfo());
    }

    @PostMapping("/monitor/status")
    @ApiOperation("监测器状态统计")
    public VData queryMonitorStatus(){
        Map<String, Object> resultMap = new HashMap();
        Page<Map<String, Object>> list = baseReportMapper.queryMonitorInfo();
        if (CollectionUtils.isNotEmpty(list)) {
            int totalCount = list.size();
            Long onlineCount = list.stream().filter(p -> p.get("monitor_status").equals("在线")).count();
            resultMap.put("onlineCount", onlineCount);
            resultMap.put("offlineCount", (long)totalCount - onlineCount);
            resultMap.put("totalCount", totalCount);
        } else {
            resultMap.put("onlineCount",0);
            resultMap.put("offlineCount",0);
            resultMap.put("totalCount",0);
        }
        return this.vData(resultMap);
    }

    /*
     * 监测器数据接入趋势
     *
     */
    @PostMapping("/monitor/trend")
    @ApiOperation("监测器数据接入趋势")
    public VList queryMonitorTrend(@RequestBody EsSearchQuery esSearchQuery){
        List<String> indexList = Arrays.asList("netflow-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"from\":\""+ esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\": \"+08:00\"}}}]," +
                "\"must_not\": [{\"term\": {\"report_log_type\": \"\"}}]}}," +
                "\"aggregations\":{\"dateAgg\":{\"date_histogram\":{\"field\":\"event_time\"," +
                "\"format\":\"yyyy-MM-dd\",\"time_zone\":\"+08:00\",\"interval\":\"1d\",\"offset\":0," +
                "\"order\":{\"_key\":\"asc\"},\"keyed\":false,\"min_doc_count\":0}}}}";

        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> dataTrendList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            dataTrendList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("dateAgg")).get("buckets");
        } catch (Exception e){
            log.error("监测器数据接入趋势查询异常", e);
        }

        Page page = new Page();
        page.addAll(dataTrendList);
        return this.vList(page);
    }

    /*
     * 监测器数据按日志类型统计
     *
     */
    @PostMapping("/monitor/logtype")
    @ApiOperation("监测器数据按日志类型统计")
    public VList queryMonitorByLogtype(@RequestBody EsSearchQuery esSearchQuery){

//        QueryModel model = this.buildNetflowQueryModel(esSearchQuery);
//        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
//        List<Map<String, Object>>  aggList = QueryTools.simpleAgg(model, wrapper,"report_log_type",9999, "key", "count");
//        log.info(aggList.size()+"");
//
//        List<Map<String, Object>> logTypeList = new ArrayList<>();
//        try {
//
//            aggList.forEach(p -> {
//                log.info(p.get("key").toString());
//                log.info(p.get("count")+"");
//                Map<String, Object> map = new HashMap<>();
//                map.put(transLogTypeDesc(p.get("key").toString()), p.get("count"));
//                logTypeList.add(map);
//            });
//        } catch (Exception e){
//            log.error("监测器数据按日志类型统计查询异常", e);
//        }
        List<String> indexList = Arrays.asList("netflow-*");
        String queryJsonStr = "{\"from\": 0,\"size\": 0,\"query\": {\"bool\": {\"must\": [{\"range\": " +
                "{\"event_time\": {\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\": \"yyyy-MM-dd HH:mm:ss\",\"time_zone\": \"+08:00\"}}}]," +
                "\"must_not\": [{\"term\": {\"report_log_type\": \"\"}}]}}," +
                "\"aggregations\": {\"term_field\": {\"terms\": {\"field\": \"report_log_type\"," +
                "\"size\": 3000,\"min_doc_count\": 1,\"shard_min_doc_count\": 0," +
                "\"show_term_doc_count_error\": false,\"order\": [{\"_count\": \"desc\"}]}}}}";

        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> logTypeList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("term_field")).get("buckets");
            dataList.forEach(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("key",transLogTypeDesc(p.get("key").toString()));
                map.put("doc_count", p.get("doc_count"));
                logTypeList.add(map);
            });
        } catch (Exception e){
            log.error("监测器数据按日志类型统计查询异常", e);
        }


        Page page = new Page();
        page.addAll(logTypeList);
        return this.vList(page);
    }

    @PostMapping("/print/person")
    @ApiOperation("打印文件次数按人TOP10")
    public VList queryPersonPrint(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryPersonPrint(esSearchQuery));
    }

    @PostMapping("/print/org")
    @ApiOperation("打印文件次数按部门TOP10")
    public VList queryOrgPrint(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryOrgPrint(esSearchQuery));
    }

    @PostMapping("/imprint/person")
    @ApiOperation("刻录文件次数按人TOP10")
    public VList queryPersonImPrint(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryPersonImPrint(esSearchQuery));
    }

    @PostMapping("/imprint/org")
    @ApiOperation("刻录文件次数按部门TOP10")
    public VList queryOrgImPrint(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryOrgImPrint(esSearchQuery));
    }

    @PostMapping("/safeLog/asset")
    @ApiOperation("安全日志数据入库按资产统计")
    public VList queryAsset(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryAsset(esSearchQuery));
    }

    @PostMapping("/safeLog/logType")
    @ApiOperation("安全日志数据入库按日志类型统计")
    public VList queryLogType(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryByLogType(esSearchQuery));
    }

    @PostMapping("/safeLog/trend")
    @ApiOperation("安全日志数据入库趋势统计")
    public VList queryAssetTrend(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryTrend(esSearchQuery));
    }

    @PostMapping("/virusSys/status")
    @ApiOperation("统计防病毒系统运行情况")
    public VList queryVirusSysStatus(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryVirusSysStatus(esSearchQuery));
    }

    @PostMapping("/virus/top")
    @ApiOperation("统计病毒上报次数前十")
    public VList queryVirusTop(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryVirusTop(esSearchQuery));
    }

    @PostMapping("/virus/deal")
    @ApiOperation("统计病毒处理情况")
    public VList queryVirusDeal(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryVirusDeal(esSearchQuery));
    }

    @PostMapping("/virus/detail")
    @ApiOperation("统计病毒处理详情")
    public VList queryVirusDetail(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vList(baseReportService.queryVirusDetail(esSearchQuery));
    }

    @PostMapping("/strategy/change")
    @ApiOperation("统计终端策略变更情况")
    public Map<String, Object> queryStrategyChangeInfo(@RequestBody EsSearchQuery esSearchQuery) {
        esSearchQuery.setFieldName("RD02");
        return baseReportService.queryChangeInfo(esSearchQuery);
    }

    @PostMapping("/audit/change")
    @ApiOperation("统计审计系统变更情况")
    public Map<String, Object> queryAuditChangeInfo(@RequestBody EsSearchQuery esSearchQuery) {
        esSearchQuery.setFieldName("RD04");
        return baseReportService.queryChangeInfo(esSearchQuery);
    }

    @PostMapping("/org/secret/count")
    @ApiOperation("按密级统计部门")
    public VList queryOrgBySecret() {
        return this.vList(baseReportMapper.queryOrgBySecret());
    }

    @PostMapping("/org/secret/info")
    @ApiOperation("统计部门涉密信息")
    public VData queryOrgSecretInfo() {
        List<BaseKoalOrg> orgList = baseKoalOrgService.findAll();
        Map<String, Object> dataMap = new HashMap<>();
        int orgTotalNum = orgList.size();
        Long notSecretNum = orgList.stream().filter(p -> p.getSecretLevel() == 1).count();
        dataMap.put("totalNum", orgTotalNum);
        dataMap.put("secretNum", (long)orgTotalNum - notSecretNum);
        dataMap.put("notSecretNum", notSecretNum);
        return this.vData(dataMap);
    }

    private QueryModel buildNetflowQueryModel(EsSearchQuery esSearchQuery){
        QueryModel model = QueryTools.buildCommonQueryModel(TimeTools.parseDate5(esSearchQuery.getStartTime()), TimeTools.parseDate5(esSearchQuery.getEndTime()),"netflow-*", "event_time");
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        model.setQueryBuilder(boolQuery);
        return model;
    }

    private String transLogType(String logType) {
        switch (logType) {
            case "DT012":
                return "data1_netflow-tcp";
            case "DT013":
                return "data2_netflow-udp";
            case "DT014":
                return "data3_netflow-http";
            case "DT015":
                return "data4_netflow-dns";
            case "DT016":
                return "data5_netflow-email";
            case "DT017":
                return "data6_netflow-db";
            case "DT018":
                return "data7_netflow-ssl";
            case "DT019":
                return "data8_netflow-file";
            case "DT020":
                return "data9_netflow-login";
            default:
                return logType;
        }
    }

    private String transLogTypeDesc(String logType) {
        switch (logType) {
            case "DT012":
                return "流量-TCP协议数据";
            case "DT013":
                return "流量-UDP协议数据";
            case "DT014":
                return "流量-HTTP协议数据";
            case "DT015":
                return "流量-DNS协议数据";
            case "DT016":
                return "流量-邮件协议数据";
            case "DT017":
                return "流量-数据库协议数据";
            case "DT018":
                return "流量-SSL解密协议数据";
            case "DT019":
                return "流量-文件传输协议数据";
            case "DT020":
                return "流量-登录行为数据";
            default:
                return logType;
        }
    }
}
