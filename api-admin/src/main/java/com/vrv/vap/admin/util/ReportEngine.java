package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.common.constant.ConfigConstants;
import com.vrv.vap.admin.common.constant.ReportConstant;
import com.vrv.vap.admin.common.pdf.PdfData;
import com.vrv.vap.admin.common.pdf.PdfExport;
import com.vrv.vap.admin.common.pdf.PdfWriteHandler;
import com.vrv.vap.admin.common.util.AESUtil;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.BaseDataSourceService;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.BaseReportInterfaceService;
import com.vrv.vap.admin.service.BaseReportModelService;
import com.vrv.vap.admin.vo.ReportComUid;
import com.vrv.vap.report.ReportGenerater;
import com.vrv.vap.report.beetl.model.*;
import com.vrv.vap.report.chart.ChartGroupValue;
import com.vrv.vap.report.chart.ChartValue;
import com.vrv.vap.report.config.ComponentConfig;
import com.vrv.vap.report.config.HtmlItemType;
import com.vrv.vap.report.config.ReportConfig;
import com.vrv.vap.report.config.SourceType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.github.iamxwaa.elasticsearch.core.entry.ElasticsearchConfig;
import org.github.iamxwaa.elasticsearch.core.entry.Query;
import org.github.iamxwaa.elasticsearch.core.entry.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;
import tk.mybatis.mapper.entity.Example;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ReportEngine {


    //@Value("${elk.ip}")
    private static String IPS;
    //@Value("${elk.port}")
    private static int PORT;
    //@Value("${elk.xpack.security.user}")
    private static String USER;
    //@Value("${elk.xpack.security.password}")
    private static String PASSWORD;
    //@Value("${screen-capturer.file-path}")
    private static String filePath;

    private static ExecutorService exec = Executors.newFixedThreadPool(20);
    private static ExecutorService POOL = Executors.newFixedThreadPool(10);

    private static String sKey = "1234567887654321";

    private static BaseDataSourceService baseDataSourceService = SpringContextUtil.getApplicationContext().getBean(BaseDataSourceService.class);

    private static BaseReportModelService baseReportModelService = SpringContextUtil.getApplicationContext().getBean(BaseReportModelService.class);

    private static DataSource dataSource = SpringContextUtil.getApplicationContext().getBean(DataSource.class);

    private static Environment env = SpringContextUtil.getApplicationContext().getBean(Environment.class);

    private static BaseDictAllService dictAllService = SpringContextUtil.getApplicationContext().getBean(BaseDictAllService.class);

    private static RestTemplate restTemplate = SpringContextUtil.getApplicationContext().getBean(RestTemplate.class);

    private static final Logger log = LoggerFactory.getLogger(ReportEngine.class);

    private static final Map<String,ComponentConfig> CPTS = new HashMap<>();

    private static BaseReportInterfaceService interfaceService = SpringContextUtil.getApplicationContext().getBean(BaseReportInterfaceService.class);

    static {
        IPS = env.getProperty("elk.ip");
        PORT = Integer.parseInt(env.getProperty("elk.port"));
        USER = env.getProperty("elk.xpack.security.user");
        PASSWORD = env.getProperty("elk.xpack.security.ppp");
        filePath = env.getProperty("screen-capturer.file-path");
    }

    public static PdfExport.PdfProgress report(BaseReport report, String type){
        File target = null;
        try{
            File dir = new File(ReportConstant.TEM_DIR.DEFAULT);
            if (!dir.exists() && !dir.isDirectory()){
                dir.mkdirs();
            }
            switch(type) {
                case ReportConstant.REPORT_TYPE.PDF:
                    target = File.createTempFile(report.getName()+ReportConstant.TEM_PREFIX.DEFAULT, "."+ReportConstant.REPORT_EXT.PDF,dir);
                    break;
                case ReportConstant.REPORT_TYPE.WORD:
                    target = File.createTempFile(report.getName()+ReportConstant.TEM_PREFIX.DEFAULT, "."+ReportConstant.REPORT_EXT.DOC,dir);
                    break;
                case ReportConstant.REPORT_TYPE.HTML:
                    target = File.createTempFile(report.getName()+ReportConstant.TEM_PREFIX.DEFAULT, "."+ReportConstant.REPORT_EXT.HTML,dir);
                    break;
                case ReportConstant.REPORT_TYPE.WPS:
                    target = File.createTempFile(report.getName()+ReportConstant.TEM_PREFIX.DEFAULT, "."+ReportConstant.REPORT_EXT.WPS,dir);
                    break;
                default:
                    target = File.createTempFile(report.getName()+ReportConstant.TEM_PREFIX.DEFAULT, "."+ReportConstant.REPORT_EXT.PDF,dir);
                    break;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if (target == null) {
            return null;
        }
        PdfData data = new PdfData();
        data.setTaskId(CommonTools.generateId());
        data.setPdfName(target.getName());
        File finalTarget = target;
        return PdfExport.build(data).start(PdfWriteHandler.fun(f -> {
            List<ComponentConfig> parent = new ArrayList<ComponentConfig>();
            ReportConfig reportConfig = new ReportConfig();
            reportConfig.setTitle(report.getTitle());
            reportConfig.setMenuEnable(report.getMenuEnable());
            reportConfig.setComponentList(parent);
            reportConfig.setSubTitle(report.getSubTitle());
            threadReport(report,parent);
            try{
                switch(type) {
                    case ReportConstant.REPORT_TYPE.PDF:
                        ReportGenerater.renderPDF(finalTarget.getAbsolutePath(), reportConfig);
                        break;
                    case ReportConstant.REPORT_TYPE.WORD:
                        ReportGenerater.renderWord2007(finalTarget.getAbsolutePath(), reportConfig);
                        break;
                    case ReportConstant.REPORT_TYPE.WPS:
                        reportConfig.setMenuEnable(false);
                        ReportGenerater.renderWord2007(finalTarget.getAbsolutePath(), reportConfig);
                        break;
                    case ReportConstant.REPORT_TYPE.HTML:
                        //target = File.createTempFile(report.getName()+ReportConstant.TEM_PREFIX.DEFAULT, "."+ReportConstant.REPORT_EXT.HTML,dir);
                        String html = ReportGenerater.renderHtml(reportConfig);
                        FileUtils.copyFile(new File(html),finalTarget);
                        break;
                    default:
                        ReportGenerater.renderPDF(finalTarget.getAbsolutePath(), reportConfig);
                        break;
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            String filepath = finalTarget.getAbsolutePath();
            f.generatePdf(filepath);
        }));
    }

    public static String cycleReport(BaseReport report, String type){
        List<ComponentConfig> parent = new ArrayList<ComponentConfig>();
        ReportConfig reportConfig = new ReportConfig();
        reportConfig.setTitle(report.getTitle());
        reportConfig.setMenuEnable(report.getMenuEnable());
        reportConfig.setComponentList(parent);
        reportConfig.setSubTitle(report.getSubTitle());
        buildReportBody(JSONArray.parseArray(report.getModels()),parent,report.getBindParam());
        String fileName = UUID.randomUUID().toString().toLowerCase().replace("-", "");
        File target = new File(filePath+File.separator+fileName+"."+type);
        try{
            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()){
                dir.mkdirs();
            }
            if(!target.exists()){
                target.createNewFile();
            }
            switch(type) {
                case ReportConstant.REPORT_EXT.PDF:
                    ReportGenerater.renderPDF(target.getAbsolutePath(), reportConfig);
                    break;
                case ReportConstant.REPORT_EXT.DOC:
                    ReportGenerater.renderWord2007(target.getAbsolutePath(), reportConfig);
                    break;
                case ReportConstant.REPORT_EXT.WPS:
                    reportConfig.setMenuEnable(false);
                    ReportGenerater.renderWord2007(target.getAbsolutePath(), reportConfig);
                    break;
                case ReportConstant.REPORT_EXT.HTML:
                    String html = ReportGenerater.renderHtml(reportConfig);
                    FileUtils.copyFile(new File(html),target);
                    break;
                default:
                    ReportGenerater.renderPDF(target.getAbsolutePath(), reportConfig);
                    break;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return fileName;
    }

    public static void buildReportBody(JSONArray jsonArray,List<ComponentConfig> parent,Map<String,Object> params){
        jsonArray.forEach( e ->{
            JSONObject obj = (JSONObject)e;
            Object id = obj.get("id");
            JSONArray children = (JSONArray)obj.get("children");
            if(id != null && StringUtils.isNotEmpty(id.toString())){
                BaseReportModel model = baseReportModelService.findById(id.toString());
                model.setBindParam(params);
                try {
                    /*
                    ComponentConfig componentConfig = new ComponentConfig();
                    exec.execute(new ItemRunable(model,componentConfig,params));*/
                    ItemModel itemModel = buildItemModel(model);
                    ComponentConfig componentConfig = Item2Component(itemModel,params);
                    if(componentConfig != null){
                        List<ComponentConfig> cd = new ArrayList<ComponentConfig>();
                        componentConfig.setChildren(cd);
                        parent.add(componentConfig);
                        if(children != null && children.size() > 0){
                            buildReportBody(children,componentConfig.getChildren(),params);
                        }
                    }
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                    log.error(throwables.getMessage(),throwables);
                }
            }
        });
    }

    public static void parseModel(JSONArray jsonArray,StringBuffer ids){
        jsonArray.forEach( e ->{
            JSONObject obj = (JSONObject)e;
            Object id = obj.get("id");
            JSONArray children = (JSONArray)obj.get("children");
            if(id != null && StringUtils.isNotEmpty(id.toString())) {
                ids.append("'").append(id.toString()).append("'").append(",");
                if(children != null && children.size() > 0){
                    parseModel(children,ids);
                }
            }
        });
    }


    public static void buildReportBody2(JSONArray jsonArray, List<ReportComUid> parent, Map<String,Object> params,Map<String,BaseReportModel> modelMap){
        jsonArray.forEach( e ->{
            JSONObject obj = (JSONObject)e;
            Object id = obj.get("id");
            JSONArray children = (JSONArray)obj.get("children");
            if(id != null && StringUtils.isNotEmpty(id.toString())){
                BaseReportModel model = modelMap.get(id.toString());
                Map<String,Object> modelParam = new HashMap();
                modelParam.putAll(params);
                model.setBindParam(modelParam);
                try {
                    ReportComUid ucm = new ReportComUid();
                    ucm.setUid(UUID.randomUUID().toString().toLowerCase().replace("-", ""));
                    List<ReportComUid> cd = new ArrayList<ReportComUid>();
                    ucm.setChildren(cd);
                    parent.add(ucm);
                    exec.execute(new ItemRunable(model,ucm.getUid(),params));
                    if(children != null && children.size() > 0){
                        buildReportBody2(children,ucm.getChildren(),params,modelMap);
                    }
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                    log.error(throwables.getMessage(),throwables);
                }
            }
        });
    }

    public static String renderReport(BaseReport report) throws SQLException, ClassNotFoundException, IOException{
        String wid = UUID.randomUUID().toString().replaceAll("-","");
        POOL.execute(new ReportRunnable(report,wid));
        return wid;
    }

    public static void parseCmu2Com(List<ReportComUid> pa,List<ComponentConfig> parent){
        pa.forEach(e ->{
            if(CPTS.containsKey(e.getUid())){
                ComponentConfig componentConfig = CPTS.get(e.getUid());
                CPTS.remove(e.getUid());
                if(CollectionUtils.isNotEmpty(e.getChildren())){
                    componentConfig.setChildren(new ArrayList<>());
                    parseCmu2Com(e.getChildren(),componentConfig.getChildren());
                }
                parent.add(componentConfig);
            }
        });
    }

    public static String renderReportOld(BaseReport report) throws SQLException, ClassNotFoundException, IOException{
        String html = "";
        try{
            List<ComponentConfig> parent = new ArrayList<ComponentConfig>();
            ReportConfig reportConfig = new ReportConfig();
            reportConfig.setTitle(report.getTitle());
            reportConfig.setMenuEnable(report.getMenuEnable());
            reportConfig.setComponentList(parent);
            reportConfig.setSubTitle(report.getSubTitle());
            threadReport(report,parent);
            String srcHtml = ReportGenerater.renderHtml(reportConfig);
            File file = new File(srcHtml);
            html = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
            //预览完成后删除临时文件
            try{
                file.delete();
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return html;
    }

    public static String renderModel(BaseReportModel model) throws Exception{
        log.info("报表基础model预览开始");
        ItemModel itemModel =  buildItemModel(model);
        ComponentConfig componentConfig = Item2Component(itemModel,model.getBindParam());
        List<ComponentConfig> parent = new ArrayList<ComponentConfig>();
        parent.add(componentConfig);
        ReportConfig reportConfig = new ReportConfig();
        reportConfig.setTitle(model.getTitle());
        reportConfig.setMenuEnable(false);
        reportConfig.setComponentList(parent);
//        ChartConfig chart = new ChartConfig();
//        chart.setPieEnlarge(10);
//        reportConfig.setChartConfig(chart);
        String srcHtml = ReportGenerater.renderHtml(reportConfig);
        File file = new File(srcHtml);
        String html = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        //预览完成后删除临时文件
        try{
            file.delete();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        log.info("报表基础model预览结束");
        return html;
    }

    private static ItemModel buildItemModel(BaseReportModel model) throws Exception{
        log.info("######组装ItemModel开始");
        log.info("######model：" + JSONObject.toJSONString(model));
        if(ReportConstant.MODEL_TYPE.TITLE.equals(model.getType())){
            TitleModel tm = new TitleModel();
            tm.setTitle(model.getTitle());
            return tm;
        }
        Connection conn = null;
        RestClient esClient = null;
        ItemModel itemModel = new ItemModel();
        BaseDataSource baseDataSource = new BaseDataSource();
        String dataSourceType = "";
        if(ReportConstant.IS_INTERFACE.YES.equals(model.getIsInterface())){
            //数据源为指标
            ItemModel im = buildFromInterface(model);
            if(StringUtils.isNotEmpty(model.getDescription())){
                String t = PlaceholderUtils.resolvePlaceholders(model.getDescription(), model.getBindParam());
                im.setDescription(t);
            }
            return im;
        }
        try {
            if (ReportConstant.DATA_SOURCE_TYPE_ID.MYSQL.equals(model.getDataSourceId())) {
                //当前mysql
                conn = dataSource.getConnection();
                dataSourceType = ReportConstant.DATA_SOURCE_TYPE.MYSQL;
            } else if (ReportConstant.DATA_SOURCE_TYPE_ID.ELASTICSEARCH.equals(model.getDataSourceId())) {
                //当前es
                ElasticsearchConfig elasticsearchConfig = new ElasticsearchConfig();
                elasticsearchConfig.setClusterIp(IPS.split(","));
                elasticsearchConfig.setPort(PORT);
                elasticsearchConfig.setUserName(USER);
                elasticsearchConfig.setPassword(PASSWORD);
                esClient = buildRestClient(elasticsearchConfig);
                dataSourceType = ReportConstant.DATA_SOURCE_TYPE.ELASTICSEARCH;
            } else {
                baseDataSource = baseDataSourceService.findById(model.getDataSourceId());
                dataSourceType = baseDataSource.getType();
            }
        } catch (Exception e) {
            try {
                if (null != conn) {
                    log.info("close database connection");
                    conn.close();
                }
            } catch (Exception ex) {

            }
        }

        try{
            switch (dataSourceType) {
                case ReportConstant.DATA_SOURCE_TYPE.MYSQL:
                    if (null == conn) {
                        log.info("create database connection");
                        Class.forName(baseDataSource.getDriver());
                        conn = DriverManager.getConnection(baseDataSource.getUrl(), baseDataSource.getUsername(), AESUtil.Decrypt(baseDataSource.getPassword(), sKey));
                    }
                    itemModel = getDataFromDatabase(conn, model);
                    break;
                case ReportConstant.DATA_SOURCE_TYPE.ELASTICSEARCH:
                    if (null == esClient) {
                        log.info("create elasticsearch connection");
                        ElasticsearchConfig elasticsearchConfig = new ElasticsearchConfig();
                        elasticsearchConfig.setClusterIp(baseDataSource.getClusterIp().split(","));
                        /*if (null != elasticConfig.getHttps()) {
                            elasticsearchConfig.setHttpSslEnabled(elasticConfig.getHttps());
                        }*/
                        elasticsearchConfig.setPort(Integer.parseInt(baseDataSource.getPort()));
                        elasticsearchConfig.setUserName(baseDataSource.getUsername());
                        elasticsearchConfig.setPassword(AESUtil.Decrypt(baseDataSource.getPassword(), sKey));
                        esClient = buildRestClient(elasticsearchConfig);
                    }
                    itemModel = getDataFromElasticSearch(esClient, model, baseDataSource);
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (null != esClient) {
                    log.info("close elasticsearch connection");
                    esClient.close();
                }
            } catch (Exception e) {

            }
            try {
                if (null != conn) {
                    log.info("close database connection");
                    conn.close();
                }
            } catch (Exception e) {

            }
        }
        if(StringUtils.isNotEmpty(model.getDescription())){
            String t = PlaceholderUtils.resolvePlaceholders(model.getDescription(), model.getBindParam());
            itemModel.setDescription(t);
        }
        log.info("######组装ItemModel结束");
        return itemModel;
    }

    private static ItemModel getDataFromDatabase(Connection connection, BaseReportModel model) throws SQLException {
        log.info("####getDataFromDatabase start");
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        ElasticsearchConfig elasticsearchConfig = new ElasticsearchConfig();
        elasticsearchConfig.setClusterIp(IPS.split(","));
        elasticsearchConfig.setPort(PORT);
        elasticsearchConfig.setUserName(USER);
        elasticsearchConfig.setPassword(PASSWORD);
        RestClient esClient = buildRestClient(elasticsearchConfig);
        try{
            if(StringUtils.isNotEmpty(model.getSql())){
                JSONArray sqls = JSONArray.parseArray(model.getSql());
                for(Object e : sqls){
                    JSONObject o = (JSONObject)e;
                    if(o.get("dataSourceId") != null && ReportConstant.DATA_SOURCE_TYPE_ID.ELASTICSEARCH.equals(Integer.parseInt(o.get("dataSourceId").toString()))){
                        //es查询
                        dataList.addAll(getDataFromLocalEs(esClient,o,model.getBindParam()));
                    }else{
                        dataList.addAll(getDataFromLocalMysql(connection,o,model.getBindParam()));
                    }
                }
            }

        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try{
                if(esClient != null){
                    esClient.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            try{
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
        return buildModelByDataList(dataList,model);
    }


    private static ItemModel getDataFromElasticSearch(RestClient esClient, BaseReportModel model,BaseDataSource dataSources) {
        log.info("#######getDataFromElasticSearch start");
        Connection connection = null;
        List<Map<String, Object>> dataList = new ArrayList();
        try{
            connection = dataSource.getConnection();
            Map<String, Object> bindParam = model.getBindParam();
            if(StringUtils.isNotEmpty(model.getSql())){
                JSONArray sqls = JSONArray.parseArray(model.getSql());
                for(Object e : sqls){
                    JSONObject o = (JSONObject)e;
                    if(o.get("dataSourceId") != null && ReportConstant.DATA_SOURCE_TYPE_ID.MYSQL.equals(Integer.parseInt(o.get("dataSourceId").toString()))){
                        //es查询
                        dataList.addAll(getDataFromLocalMysql(connection,o,model.getBindParam()));
                    }else{
                        dataList.addAll(getDataFromLocalEs(esClient,o,model.getBindParam()));
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try{
                if(esClient != null){
                    esClient.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            try{
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
        return buildModelByDataList(dataList,model);
    }

    private static ItemModel buildModelByDataList(List<Map<String, Object>> srcdataList,BaseReportModel model){
        log.info("##############根据结果数据组装model开始##############");
        ItemModel itemModel = null;
        if(srcdataList == null){
            log.info("组装model dataList无数据");
            srcdataList = new ArrayList<>();
        }
        BaseReportInterface baseReportInterface = interfaceService.findById(model.getInterfaceId());
        final List<Map<String, Object>> dataList = srcdataList;
        switch(model.getType()) {
            case ReportConstant.MODEL_TYPE.TEXTAREA:
                TextAreaModel textAreaModel = new TextAreaModel();
                textAreaModel.setTitle(model.getTitle());
                JSONArray array = JSONArray.parseArray(model.getContent());
                if (array.size() > 1) {
                    List<String> contentList = new ArrayList<>(dataList.size());
                    array.forEach((s) -> {
                        JSONObject obj = (JSONObject)s;
                        String t = obj.get("text").toString();
                        t = PlaceholderUtils.resolvePlaceholders(t, ModelUtil.extendBindParam(dataList,model.getBindParam()));
                        contentList.add(HtmlUtils.htmlEscape(t));
                    });
                    textAreaModel.setContentList(contentList);
                } else {
                    JSONObject obj = (JSONObject)array.get(0);
                    String content = "-";
                    if (obj.get("text") != null) {
                        content = obj.get("text").toString();
                        content = PlaceholderUtils.resolvePlaceholders(content, ModelUtil.extendBindParam(dataList,model.getBindParam()));
                    }
                    textAreaModel.setContent(HtmlUtils.htmlEscape(content));
                }
                itemModel = textAreaModel;
                break;
            case ReportConstant.MODEL_TYPE.TABLE:
                TableModel tableModel = new TableModel();
                String[][] fn = ModelUtil.buildTableFiledAndName(model.getContent());
                tableModel.setColumnFiled(fn[0]);
                tableModel.setColumnName(fn[1]);
                if(ReportConstant.INTERFACE_TYPE.EMPTY.equals(baseReportInterface.getType())){
                    tableModel.setData(dataList);
                }else{
                    tableModel.setData(ModelUtil.safeList4html(dataList));
                }
                tableModel.setTitle(model.getTitle());
                if (StringUtils.isNotEmpty(model.getDescription())) {
                    tableModel.setDescription(model.getDescription());
                }
                itemModel = tableModel;
                break;
            case ReportConstant.MODEL_TYPE.LIST:
                ListModel listModel = new ListModel();
                listModel.setTitle(model.getTitle());
                List<String> stringList = new ArrayList();
                listModel.setList(stringList);
                JSONArray listarray = JSONArray.parseArray(model.getContent());
                listarray.forEach((m) -> {
                    JSONObject obj = (JSONObject)m;
                    String str = PlaceholderUtils.resolvePlaceholders(obj.get("text").toString(), ModelUtil.extendBindParam(dataList,model.getBindParam()));
                    stringList.add(HtmlUtils.htmlEscape(str));
                });
                itemModel = listModel;
                break;
            case ReportConstant.MODEL_TYPE.QUOTE:
                QuoteModel quoteModel = new QuoteModel();
                quoteModel.setTitle(model.getTitle());
                String quote = model.getContent();
                if (StringUtils.isNotEmpty(model.getContent())) {
                    quote = PlaceholderUtils.resolvePlaceholders(quote, ModelUtil.extendBindParam(dataList,model.getBindParam()));
                }
                quoteModel.setContent(HtmlUtils.htmlEscape(quote));
                itemModel = quoteModel;
                break;
            case ReportConstant.MODEL_TYPE.PIE:
                PieModel pieModel = new PieModel();
                pieModel.setTitle(model.getTitle());
                pieModel.setShowTitle(false);
                List<ChartValue> list = new ArrayList();
                pieModel.setData(list);
                JSONObject piecontent = (JSONObject)JSONObject.parse(model.getContent());
                dataList.forEach((m) -> {
                    ChartValue chartValue = new ChartValue();
                    String name = String.valueOf(m.get(piecontent.get("nameCol")));
                    String value = String.valueOf(m.get(piecontent.get("valueCol")));
                    chartValue.setName(name);
                    chartValue.setValue(EsDataUtil.parseStr2Int(value));
                    list.add(chartValue);
                });
                itemModel = pieModel;
                break;
            case ReportConstant.MODEL_TYPE.LINE:
                LineModel lineModel = new LineModel();
                JSONObject linecontent = (JSONObject)JSONObject.parse(model.getContent());
                if(StringUtils.isNotEmpty(linecontent.getString("xTitle"))){
                    lineModel.setXtitle(linecontent.getString("xTitle"));
                }
                if(StringUtils.isNotEmpty(linecontent.getString("yTitle"))){
                    lineModel.setYtitle(linecontent.getString("yTitle"));
                }
                lineModel.setTitle(model.getTitle());
                lineModel.setShowTitle(false);
                List<ChartGroupValue> list2 = new ArrayList();
                lineModel.setData(list2);
                dataList.forEach((m) -> {
                    ChartGroupValue chartGroupValue = new ChartGroupValue();
                    String name = String.valueOf(m.get(linecontent.get("nameCol")));
                    String value = String.valueOf(m.get(linecontent.get("valueCol")));
                    String group = String.valueOf(m.get(linecontent.get("groupCol")));
                    chartGroupValue.setName(name);
                    chartGroupValue.setValue((value == null || "null".equals(value)) ? 0 : Integer.parseInt(value));
                    chartGroupValue.setGroup(group);
                    list2.add(chartGroupValue);
                });
                itemModel = lineModel;
                break;
            case ReportConstant.MODEL_TYPE.BAR:
                BarModel barModel = new BarModel();
                JSONObject barcontent = (JSONObject)JSONObject.parse(model.getContent());
                if(StringUtils.isNotEmpty(barcontent.getString("xTitle"))){
                    barModel.setXtitle(barcontent.getString("xTitle"));
                }
                if(StringUtils.isNotEmpty(barcontent.getString("yTitle"))){
                    barModel.setYtitle(barcontent.getString("yTitle"));
                }
                barModel.setTitle(model.getTitle());
                barModel.setShowTitle(false);
                List<ChartGroupValue> list3 = new ArrayList<>();
                barModel.setData(list3);
                dataList.forEach((m) -> {
                    ChartGroupValue chartGroupValue = new ChartGroupValue();
                    String name = String.valueOf(m.get(barcontent.get("nameCol")));
                    String value = String.valueOf(m.get(barcontent.get("valueCol")));
                    String group = String.valueOf(m.get(barcontent.get("groupCol")));
                    chartGroupValue.setName(name);
                    chartGroupValue.setValue(EsDataUtil.parseStr2Int(value));
                    chartGroupValue.setGroup(group);
                    list3.add(chartGroupValue);
                });
                itemModel = barModel;
                break;
            case ReportConstant.MODEL_TYPE.STACK_BAR:
                StackedBarModel stackedBarModel = new StackedBarModel();
                stackedBarModel.setTitle(model.getTitle());
                stackedBarModel.setShowTitle(false);
                List<ChartGroupValue> stackedBarList = new ArrayList<>();
                stackedBarModel.setData(stackedBarList);
                String fieldInfo = baseReportInterface.getFieldInfo();
                JSONArray jsonArray = JSONArray.parseArray(fieldInfo);
                for (Map<String, Object> data : dataList) {
                    for (Object value : jsonArray) {
                        ChartGroupValue chartGroupValue = new ChartGroupValue();
                        String name = String.valueOf(data.get("name"));
                        chartGroupValue.setName(name);
                        JSONObject object = (JSONObject) value;
                        chartGroupValue.setGroup(object.getString("label"));
                        String field = object.getString("field");
                        chartGroupValue.setValue((Integer) data.get(field));
                        stackedBarList.add(chartGroupValue);
                    }
                }
                itemModel = stackedBarModel;
                break;
            case ReportConstant.MODEL_TYPE.TITLE:
                TitleModel titleModel = new TitleModel();
                titleModel.setTitle(model.getTitle());
                titleModel.setDescription(model.getDescription());
                itemModel = titleModel;
                break;
            default:
                itemModel = new NoTypeModel();
        }
        if(StringUtils.isNotEmpty(model.getDescription())){
            itemModel.setDescription(model.getDescription());
        }
        log.info("###数据组装model结束###：result=" + JSONObject.toJSONString(itemModel));
        return generateSupply(itemModel,model);
    }

    /*
    private static void prepareItem(int level, ItemModel parent, List<ItemModel> body, boolean useImageFile) {
        for(int i = 0; i < body.size(); ++i) {
            ItemModel itemModel = (ItemModel)body.get(i);
            itemModel.setLevel(level);
            switch(level) {
                case 0:
                    itemModel.setTitleIndex(i + 1 + "");
                    break;
                default:
                    itemModel.setTitleIndex(parent.getTitleIndex() + "." + (i + 1));
            }

            if (itemModel instanceof ImageModel) {
                ((ImageModel)itemModel).setUseImgFile(useImageFile);
            }

            if (null != itemModel.getBody()) {
                prepareItem(level + 1, itemModel, itemModel.getBody(), useImageFile);
            }
        }

    }*/

    //处理补全信息
    private static ItemModel generateSupply(ItemModel imodel,BaseReportModel bmodel){
        log.info("##############model自动补全开始##############");
        if(!(imodel instanceof ImageModel)){
            return imodel;
        }
        String sql = bmodel.getSql();
        if(StringUtils.isEmpty(sql)) {
            return imodel;
        }
        JSONArray array = JSONArray.parseArray(sql);
        JSONObject o = (JSONObject)array.get(0);
        if(o == null || o.get("type") == null){
            return imodel;
        }
        if("time".equals(o.get("type").toString())){
            //时间类型自动补全
            return TimeAutoSupply(imodel,o);
        }else{
            //非时间类型根据 supply 配置补全
            if(o == null || o.get("supply") == null){
                return imodel;
            }
            JSONArray sups = (JSONArray)o.get("supply");
            if(imodel instanceof PieModel){
                PieModel p = (PieModel)imodel;
                for(Object s : sups){
                    JSONObject item = (JSONObject)s;
                    if(item != null && item.get("name") != null && StringUtils.isNotEmpty(item.get("name").toString())){
                        ChartValue v = new ChartValue();
                        v.setName(item.get("name").toString());
                        v.setValue(0);
                        p.getData().add(v);
                    }
                }
                return p;
            }else if(imodel instanceof LineModel){
                LineModel p = (LineModel) imodel;
                for(Object s : sups){
                    JSONObject item = (JSONObject)s;
                    if(item != null && item.get("name") != null && StringUtils.isNotEmpty(item.get("name").toString())){
                        ChartGroupValue v = new ChartGroupValue();
                        v.setName(item.get("name").toString());
                        v.setValue(0);
                        if(item.get("group") != null){
                            v.setGroup(item.get("group").toString());
                        }
                        p.getData().add(v);
                    }
                }
                return p;
            }else if(imodel instanceof BarModel){
                BarModel p = (BarModel) imodel;
                for(Object s : sups){
                    JSONObject item = (JSONObject)s;
                    if(item != null && item.get("name") != null && StringUtils.isNotEmpty(item.get("name").toString())){
                        ChartGroupValue v = new ChartGroupValue();
                        v.setName(item.get("name").toString());
                        v.setValue(0);
                        if(item.get("group") != null){
                            v.setGroup(item.get("group").toString());
                        }
                        p.getData().add(v);
                    }
                }
                return p;
            }
        }
        return imodel;
    }

    private static ItemModel TimeAutoSupply(ItemModel imodel,JSONObject obj){
        log.info("##############根据时间自动补全开始##############");
        Object format = obj.get("format");
        Object interval = obj.get("interval");
        if(format == null || interval == null){
            log.info("时间自动补全：format或者interval为空");
            return imodel;
        }
        if(imodel instanceof PieModel){
            PieModel p = (PieModel)imodel;
            List<ChartValue> re = new ArrayList<>();
            for(int i = 0; i < p.getData().size()-1;i++){
                ChartValue s = p.getData().get(i);
                ChartValue e = p.getData().get(i+1);
                Date start = ModelUtil.formatData(s.getName(),format.toString());
                Date end = ModelUtil.formatData(e.getName(),format.toString());
                Calendar c = Calendar.getInstance();
                re.add(s);
                c.setTime(start);
                int t;
                if("h".equals(interval.toString())){
                    //间隔为小时
                    t = Calendar.HOUR;
                }else if("d".equals(interval.toString())){
                    //间隔为天
                    t = Calendar.DATE;
                }else if("m".equals(interval.toString())){
                    //时间间隔为月份
                    t = Calendar.MONTH;
                }else{
                    //默认为天
                    t = Calendar.DATE;
                }
                while (c.getTime().before(end)){
                    c.add(t,1);
                    if(c.getTime().before(end)){
                        ChartValue v = new ChartValue();
                        v.setName(ModelUtil.date2string(c.getTime(),format.toString()));
                        v.setValue(0);
                        re.add(v);
                    }
                }
                re.add(e);
            }
            p.setData(re);
            return p;
        }else if(imodel instanceof LineModel){
            LineModel p = (LineModel) imodel;
            p.setData(genChartData(p.getData(),format.toString(),interval.toString()));
            return p;
        }else if(imodel instanceof BarModel){
            BarModel p = (BarModel) imodel;
            p.setData(genChartData(p.getData(),format.toString(),interval.toString()));
            return p;
        }
        return imodel;
    }

    private static List<ChartGroupValue> genChartData(List<ChartGroupValue> datas,String format,String interval){
        Map<String,List<ChartGroupValue>> m = new HashMap<>();
        datas.forEach( e -> {
            if(m.containsKey(e.getGroup())){
                m.get(e.getGroup()).add(e);
            }else{
                List a = new ArrayList();
                a.add(e);
                m.put(e.getGroup(),a);
            }
        });
        List<ChartGroupValue> re = new ArrayList<>();
        m.keySet().forEach( item -> {
            List<ChartGroupValue> list = m.get(item);
            for(int i = 0; i < list.size()-1;i++){
                ChartGroupValue s = (ChartGroupValue)list.get(i);
                ChartGroupValue e = (ChartGroupValue)list.get(i+1);
                Date start = ModelUtil.formatData(s.getName(),format);
                Date end = ModelUtil.formatData(e.getName(),format);
                Calendar c = Calendar.getInstance();
                re.add(s);
                c.setTime(start);
                int t;
                if("h".equals(interval)){
                    //间隔为小时
                    t = Calendar.HOUR;
                }else if("d".equals(interval)){
                    //间隔为天
                    t = Calendar.DATE;
                }else if("m".equals(interval)){
                    //时间间隔为月份
                    t = Calendar.MONTH;
                }else{
                    //默认为天
                    t = Calendar.DATE;
                }

                while (c.getTime().before(end)){
                    c.add(t,1);
                    if(c.getTime().before(end)){
                        ChartGroupValue v = new ChartGroupValue();
                        v.setName(ModelUtil.date2string(c.getTime(),format.toString()));
                        v.setValue(0);
                        v.setGroup(item);
                        re.add(v);
                    }
                }
                re.add(e);
            }
        });
        return re;
    }

    public static Optional<EsResult> search(Query query,RestClient restClient) {
        String endpoint = getQueryEndpoint(query);
        Request request = new Request("POST", endpoint);
        //setDefaultParameter(request);
        StringBuilder queryBuilder = (new StringBuilder());//.append("\"from\":").append(query.getFrom()).append(",").append("\"size\":").append(query.getSize());
        String queryString = StringUtils.isEmpty(query.getQuery()) ? (new StringBuilder("{")).append(queryBuilder).append("}").toString() : (new StringBuilder(query.getQuery())).toString();/*.insert(1, queryBuilder.append(","))*/
        request.setJsonEntity(JSON.toJSONString(JSONObject.parse(queryString)));
        try {
            Response response = restClient.performRequest(request);
            return Optional.ofNullable(parseEntity(response.getEntity()));
        } catch (IOException var7) {
            log.error("Search request error.", var7);
            return Optional.empty();
        }
    }

    private static String getQueryEndpoint(Query query) {
        StringBuilder endpoint = new StringBuilder("/");
        String index = String.join(",", query.getIndecies());
        String type = null != query.getTypes() && query.getTypes().length != 0 ? String.join(",", query.getTypes()) : "";
        if (StringUtils.isEmpty(type)) {
            endpoint.append(index);
        } else {
            endpoint.append(index).append("/").append(type);
        }
        endpoint.append("/_search");
        return endpoint.toString();
    }

    private static EsResult parseEntity(HttpEntity httpEntity) throws IOException {
        String s = EntityUtils.toString(httpEntity, "utf-8");
        log.info("es 结果："+s);
        JSONObject jsonObject = JSON.parseObject(s);
        //JSONObject jsonObject = (JSONObject)JSON.parseObject(IOUtils.toByteArray(httpEntity.getContent()), JSONObject.class, new Feature[0]);
        EsResult result = new EsResult();
        result.setTimeOut(jsonObject.getBooleanValue("timed_out"));
        result.setTook(jsonObject.getLongValue("took"));
        result.setScrollId(jsonObject.getString("_scroll_id"));
        JSONObject hitObj = jsonObject.getJSONObject("hits");
        result.setAggregations(jsonObject.getJSONObject("aggregations"));
        //JSONObject hitObj = jsonObject.getJSONObject("hits");
        if (null == hitObj) {
            result.setEmpty(true);
        } else {
            JSONArray hits = hitObj.getJSONArray("hits");
            result.setEmpty(hits.isEmpty());
            Object totalObj = hitObj.get("total");
            if (totalObj instanceof JSONObject) {
                totalObj = ((JSONObject)totalObj).get("value");
            }

            if (totalObj instanceof Integer) {
                result.setTotal((long)(Integer)totalObj);
            } else {
                result.setTotal((Long)totalObj);
            }
            if (hits.isEmpty()) {
                result.setHits(new SearchHit[0]);
            } else {
                result.setHits((SearchHit[])hits.parallelStream().map((hit) -> {
                    JSONObject hit2 = (JSONObject)hit;
                    SearchHit searchHit = new SearchHit();
                    searchHit.setId(hit2.getString("_id"));
                    searchHit.setIndex(hit2.getString("_index"));
                    searchHit.setScore(hit2.getFloatValue("_score"));
                    searchHit.setType(hit2.getString("_type"));
                    searchHit.setSource(hit2.getJSONObject("_source"));
                    return searchHit;
                }).toArray((x$0) -> {
                    return new SearchHit[x$0];
                }));
            }
        }
        return result;
    }


    private static RestClient buildRestClient(final ElasticsearchConfig config) {
        Set<String> ipSet = new HashSet();
        if (StringUtils.isNotEmpty(config.getMaster())) {
            ipSet.add(config.getMaster());
        }
        String[] var3 = config.getClusterIp();
        int var4 = var3.length;
        for(int var5 = 0; var5 < var4; ++var5) {
            String ip = var3[var5];
            ipSet.add(ip);
        }
        String schema = config.isHttpSslEnabled() ? "https" : "http";
        RestClientBuilder builder = RestClient.builder((HttpHost[])((List)ipSet.stream().map((ipx) -> {
            return new HttpHost(ipx, config.getPort(), schema);
        }).collect(Collectors.toList())).toArray(new HttpHost[0]));
        if (StringUtils.isNotEmpty(config.getUserName()) && StringUtils.isNotEmpty(config.getPassword())) {
            log.info("Enable basic authorization.");
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getUserName(), config.getPassword()));
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    httpClientBuilder.disableAuthCaching();
                    if (config.isHttpSslEnabled()) {
                        log.info("Enable https client.");
                        try {
                            TrustStrategy trustStrategy = new TrustStrategy() {
                                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                                    return true;
                                }
                            };
                            SSLContext sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, trustStrategy).build();
                            httpClientBuilder.setSSLContext(sslContext);
                            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException var4) {
                            log.error("", var4);
                        }
                    }

                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        builder = builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setConnectTimeout(config.getConnectTimeout()).setSocketTimeout(config.getSocketTimeout()).setConnectionRequestTimeout(config.getConnectionRequestTimeout());
            }
        });
        builder.setFailureListener(new RestClient.FailureListener() {
            public void onFailure(Node node) {
                log.error("{}://{}:{} connect error.", new Object[]{node.getHost().getSchemeName(), node.getHost().getHostName(), node.getHost().getPort()});
            }
        });
        return builder.build();
    }

    public static void enmuDatas(List<Map<String, Object>> datas,JSONArray enmus){
        log.info("######枚举翻译开始");
        if(enmus == null){
            return;
        }
        for(Object o : enmus){
            JSONObject j = (JSONObject)o;
            String field = j.getString("field");
            String type = j.getString("type");
            if(StringUtils.isEmpty(field) || StringUtils.isEmpty(type)){
                continue;
            }
            for(Map<String, Object> map : datas){
                if(map.containsKey(field)){
                    String key = map.get(field).toString();
                    String value = parseDict(key, type);
                    if(StringUtils.isNotEmpty(value)){
                        map.put(field,value);
                    }
                }
            }
        }
    }

    public static String parseDict(String key,String type){
        Example e = new Example(BaseDictAll.class);
        e.createCriteria().andEqualTo("parentType",type);
        List<BaseDictAll> dicts = dictAllService.findByExample(e);
        if(dicts == null || dicts.size() == 0){
            return null;
        }
        for(BaseDictAll d : dicts){
            if(key.equals(d.getCode())){
                return d.getCodeValue();
            }
        }
        return null;
    }

    static class ItemRunable implements Runnable{
        private BaseReportModel model;
        private String uid;
        private Map<String,Object> params;

        public ItemRunable(){
        }
        public ItemRunable(BaseReportModel model,String uid,Map<String,Object> params){
            this.model = model;
            this.uid = uid;
            this.params = params;
        }
        @Override
        public void run() {
            try{
                log.info("#########ItemRunable run start uid:"+this.uid);
                ItemModel itemModel = buildItemModel(model);
                ComponentConfig componentConfig = Item2Component(itemModel, params);
                CPTS.put(uid,componentConfig);
                log.info("#########ItemRunable run end uid:"+this.uid);
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage(),e);
            }
        }
    }

    public static void threadReport(BaseReport report,List<ComponentConfig> parent){
        log.info("########threadReport start");
        List<ReportComUid> pa = new ArrayList<ReportComUid>();
        StringBuffer ids = new StringBuffer();
        parseModel(JSONArray.parseArray(report.getModels()),ids);
        if(ids.toString().endsWith(",")){
            ids.deleteCharAt(ids.length()-1);
        }
        List<BaseReportModel> models = baseReportModelService.findByids(ids.toString());
        Map<String,BaseReportModel> modelMap =  models.stream().collect(Collectors.toMap(e -> e.getId(), e -> e,(k1, k2) -> k1));
        log.info("########buildReportBody2 start");
        buildReportBody2(JSONArray.parseArray(report.getModels()),pa,report.getBindParam(),modelMap);
        exec.shutdown();
        while (true) {
            if (exec.isTerminated()) {
                break;
            }
        }
        exec = Executors.newFixedThreadPool(20);
        parseCmu2Com(pa,parent);
    }

    public static ComponentConfig Item2Component(ItemModel itemModel,Map<String,Object> params){
        log.info("##############model转ComponentConfig开始##############");
        log.info("####itemModel####：" + JSONObject.toJSONString(itemModel));
        if(itemModel == null){
            return null;
        }
        ComponentConfig c = new ComponentConfig();
        c.setTitle(itemModel.getTitle());
        c.setSourceType(SourceType.Static);
        c.setBindParam(params);
        List<Map<String, Object>> list = new ArrayList<>();
        if(itemModel instanceof QuoteModel){
            c.setType(HtmlItemType.Quote);
            QuoteModel q = (QuoteModel)itemModel;
            Map<String,Object> m = new HashMap<>();
            m.put(ConfigConstants.TEXT_CONTENT,q.getContent());
            list.add(m);
        }else if(itemModel instanceof TableModel){
            TableModel t = (TableModel)itemModel;
            c.setType(HtmlItemType.Table);
            c.setField(t.getColumnFiled());
            c.setFieldName(t.getColumnName());
            list = t.getData();
        }else if(itemModel instanceof ListModel){
            ListModel l = (ListModel)itemModel;
            c.setType(HtmlItemType.List);
            for(String s : l.getList()){
                Map<String,Object> m = new HashMap<>();
                m.put(ConfigConstants.TEXT_CONTENT,s);
                list.add(m);
            }
        }else if(itemModel instanceof LineModel){
            LineModel e = (LineModel)itemModel;
            c.setType(HtmlItemType.Line);
            for(ChartGroupValue cg : e.getData()){
                Map<String,Object> m = new HashMap<>();
                m.put(ConfigConstants.LINE_BAR_NAME,cg.getName());
                m.put(ConfigConstants.LINE_BAR_VALUE,cg.getValue());
                m.put(ConfigConstants.LINE_BAR_GROUP,cg.getGroup());
                list.add(m);
            }
            c.setFieldName(new String[]{e.getXtitle(),e.getYtitle()});
        }else if(itemModel instanceof BarModel){
            BarModel b = (BarModel)itemModel;
            c.setType(HtmlItemType.Bar);
            for(ChartGroupValue cg : b.getData()){
                Map<String,Object> m = new HashMap<>();
                m.put(ConfigConstants.LINE_BAR_NAME,cg.getName());
                m.put(ConfigConstants.LINE_BAR_VALUE,cg.getValue());
                m.put(ConfigConstants.LINE_BAR_GROUP,cg.getGroup());
                list.add(m);
            }
            c.setFieldName(new String[]{b.getXtitle(),b.getYtitle()});
        } else if (itemModel instanceof StackedBarModel) {
            StackedBarModel b = (StackedBarModel) itemModel;
            c.setType(HtmlItemType.StackedBar);
            for (ChartGroupValue cg : b.getData()) {
                Map<String, Object> m = new HashMap<>();
                m.put(ConfigConstants.LINE_BAR_NAME, cg.getName());
                m.put(ConfigConstants.LINE_BAR_VALUE, cg.getValue());
                m.put(ConfigConstants.LINE_BAR_GROUP, cg.getGroup());
                list.add(m);
            }
        } else if(itemModel instanceof PieModel){
            PieModel p = (PieModel)itemModel;
            c.setType(HtmlItemType.Pie);
            for(ChartValue cg : p.getData()){
                Map<String,Object> m = new HashMap<>();
                m.put(ConfigConstants.PIE_NAME,cg.getName());
                m.put(ConfigConstants.PIE_VALUE,cg.getValue());
                list.add(m);
            }
        }else if(itemModel instanceof TextAreaModel){
            TextAreaModel a = (TextAreaModel)itemModel;
            c.setType(HtmlItemType.TextArea);
            if(CollectionUtils.isNotEmpty(a.getContentList())){
                for(String s : a.getContentList()){
                    Map<String,Object> m = new HashMap<>();
                    m.put(ConfigConstants.TEXT_CONTENT,s);
                    list.add(m);
                }
            }
        }else if(itemModel instanceof TitleModel){
            c.setType(HtmlItemType.TitleOnly);
        }else{
            c.setType(HtmlItemType.NoType);
        }
        c.setDataList(list);
        c.setDescription(itemModel.getDescription());
        log.info("###model转ComponentConfig开始### result：" + JSONObject.toJSONString(c));
        return c;
    }

    public static List<Map<String,Object>> getDataFromLocalEs(RestClient esClient,JSONObject o,Map<String,Object> bindParam){
        log.info("#######获取本地es数据开始");
        if(esClient == null){
            ElasticsearchConfig elasticsearchConfig = new ElasticsearchConfig();
            elasticsearchConfig.setClusterIp(IPS.split(","));
            elasticsearchConfig.setPort(PORT);
            elasticsearchConfig.setUserName(USER);
            elasticsearchConfig.setPassword(PASSWORD);
            esClient = buildRestClient(elasticsearchConfig);
        }
        List<Map<String, Object>> datas = new ArrayList<>();
        Query query = new Query();
        Object type = o.get("type");//获取sql类型（时间 ，非时间）
        Object pre = o.get("pre");//获取sql数据前缀
        JSONArray enmu = o.getJSONArray("enmu");
        //解析索引
        if(o.get("indexs") != null && !o.get("indexs").toString().endsWith("*") && o.get("startTimeField") != null && o.get("endTimeField") != null && bindParam.get(o.get("startTimeField").toString()) != null && bindParam.get(o.get("endTimeField").toString()) != null ){
            //根据时间生成索引
            Date startTime = ModelUtil.formatData(bindParam.get(o.get("startTimeField").toString()).toString(),null);
            Date endTime = ModelUtil.formatData(bindParam.get(o.get("endTimeField").toString()).toString(),null);
            String[] indexs = ES7Tools.getIndexNames(o.get("indexs").toString(), startTime, endTime);
            if(indexs.length == 0){
                if(o.get("indexs") != null && StringUtils.isNotEmpty(o.get("indexs").toString()) ){
                    query.setIndecies(o.get("indexs").toString().split(","));
                }
            }else{
                query.setIndecies(indexs);
            }
        }else{
            if(o.get("indexs") != null && StringUtils.isNotEmpty(o.get("indexs").toString()) ){
                query.setIndecies(o.get("indexs").toString().split(","));
            }
        }
        query.setQuery(PlaceholderUtils.resolvePlaceholders(o.getString("sql"), bindParam != null ? bindParam : new HashMap<>()));
        Optional<EsResult> result = search(query,esClient);
        JSON.toJSONString(result);
        if (result.isPresent() && null != ((EsResult)result.get()).getHits()) {
            datas = EsDataUtil.gen((EsResult) result.get(), o.getJSONArray("agg"),pre);
            //解析枚举值
            enmuDatas(datas,enmu);
        }
        return datas;
    }

    public  static List<Map<String,Object>> getDataFromLocalMysql(Connection connection,JSONObject o,Map<String,Object> bindParam){
        log.info("#######获取本地mysql数据开始");
        String sql = PlaceholderUtils.resolvePlaceholders(o.get("sql").toString(),bindParam != null ? bindParam : new HashMap<>());
        List<Map<String,Object>> dataList = new ArrayList<>();
        try{
            if(connection == null){
                connection = dataSource.getConnection();
            }
            if(StringUtils.isNotEmpty(sql)){
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                while (rs.next()) {
                    Map<String,Object> rowData = new HashMap<String,Object>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnLabel(i), rs.getString(i));
                    }
                    dataList.add(rowData);
                }
                stmt.close();
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return dataList;
    }

    public static ItemModel buildFromInterface(BaseReportModel model){
        List<Map<String, Object>> list = ReportInterfaceEngine.getDataFromInterface(model);
        return buildModelByDataList(list,model);
    }
}
