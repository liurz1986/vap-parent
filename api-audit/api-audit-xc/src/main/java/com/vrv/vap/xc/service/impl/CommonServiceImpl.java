package com.vrv.vap.xc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vrv.vap.toolkit.config.PathConfig;
import com.vrv.vap.toolkit.tools.*;
import com.vrv.vap.toolkit.tools.jsch.FileProgressMonitor;
import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.client.ElasticSearchManager;
import com.vrv.vap.xc.config.AutoCreateAliasConfig;
import com.vrv.vap.xc.config.AutoCreateAliasConfig.AliasConfig;
import com.vrv.vap.xc.config.IndexConfig;
import com.vrv.vap.xc.mapper.core.custom.DataCleanMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.EsTemplate;
import com.vrv.vap.xc.model.PreparedCleanIndex;
import com.vrv.vap.xc.pojo.DataCleanLog;
import com.vrv.vap.xc.pojo.DataDumpLog;
import com.vrv.vap.xc.pojo.DataDumpStrategy;
import com.vrv.vap.xc.service.CommonService;
import com.vrv.vap.xc.service.DataDumpService;
import com.vrv.vap.xc.tools.EsCurdTools;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.xc.vo.DataDumpStrategyQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * es公共服务实现
 * Created by lizj on 2019/9/16.
 */
@Service
public class CommonServiceImpl implements CommonService {

    private static final Log log = LogFactory.getLog(CommonServiceImpl.class);

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    @Autowired
    private DataDumpService businessClient;
    @Autowired
    private ElasticSearchManager esClient;
    @Autowired
    private IndexConfig indexConfig;
    @Autowired
    private PathConfig pathConfig;
    @Autowired
    private AutoCreateAliasConfig createAliasConfig;
    @Autowired
    private DataCleanMapper dataCleanDao;

    private Map<String, AliasConfig> aliasConfig;

    /**
     * 匹配xxx-yyyy.MM 和 xxx-yyyy.MM.dd
     */
    private static String regexStr = "(((.*)-\\d\\d\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01]))|(((.*)-\\d\\d\\d\\d)(0[1-9]|1[012])|((.*)_\\d\\d\\d\\d)(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01]))|(((.*)_\\d\\d\\d\\d)(0[1-9]|1[012]))";

    private static String regexCsvStr = "((((.*)-\\d\\d\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01]))|(((.*)-\\d\\d\\d\\d)\\.(0[1-9]|1[012])))(.csv)";

    private static Pattern patternDay = Pattern.compile("[1-9]\\d{3}(.)(((0[13578]|1[02])(.)([0-2]\\d|3[01]))|((0[469]|11)(.)([0-2]\\d|30))|(02([01]\\d|2[0-8])))");


    // 推送相关配置
    @Value("${push.url:https://localhost:8780/push/user}")
    private String pushUrl;

    @Value("${push.userId:31}")
    private Integer pushUserId;


    @PostConstruct
    public void initAliasConfig() {
        aliasConfig = createAliasConfig.getAliasConfig();
    }

    @Override
    public Result createAlias() {
        QueryTools.QueryWrapper queryWrapper = QueryTools.build(esClient, indexConfig);
        try {
            log.info("start checking elasticsearch index alias...");
//            String tailUrl = "_aliases";
            //高版本是_alias
            String tailUrl = "_alias";
            if (createAliasConfig.isCurrentMonth()) {
                tailUrl = "*" + TimeTools.format(new Date(), "yyyy.MM") + "/" + tailUrl;
            }
            //Optional<JSONObject> opt = queryWrapper.lowLevelResponseValue("", "_aliases");
            Optional<JSONObject> opt = EsCurdTools.simpleGetQueryHttp(tailUrl);
            if (opt.isPresent()) {
//                String regDay = "[1-9]\\d{3}(((0[13578]|1[02])([0-2]\\d|3[01]))|((0[469]|11)([0-2]\\d|30))|(02([01]\\d|2[0-8])))$";
                String regMonth = "[1-9]\\d{3}(.)((0[13578]|1[02])|(0[469]|11)|(02))$";
//                Pattern patternDay = Pattern.compile(regDay);
                Pattern patternMonth = Pattern.compile(regMonth);

                Map<String, String> indexCreate = new HashMap<>();
//                List<String> indexCreate = new ArrayList<>();
                opt.get().entrySet().forEach(e -> {
                    String index = e.getKey();
                    Matcher matcher = patternMonth.matcher(index);
                    //未匹配到月索引
                    if (index.startsWith("searchguard") || !matcher.find()) {
                        return;
                    }
                    String month = matcher.group();
                    String indexPrefix = index.replace(month, "");
                    if (!aliasConfig.containsKey(indexPrefix.substring(0, indexPrefix.length() - 1))) {
                        //根据配置的索引来创建别名
                        return;
                    }

                    log.info(index + " : setting max_result_window=1000000000...");
                    queryWrapper.setWindowMaxResult(index);

                    Date date = TimeTools.parseDate(month, "yyyy.MM");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    JSONObject tmp = (JSONObject) e.getValue();
                    if (null != tmp) {
                        JSONObject aliases = tmp.getJSONObject("aliases");
                        if (null != aliases && !aliases.isEmpty()) {
                            //已有别名则退出此次操作
                            return;
                        }
                    }
                    for (int i = firstDay; i <= lastDay; i++) {
                        String day = month + (i < 10 ? (".0" + i) : ("." + i));
                        indexCreate.put(indexPrefix + day, day);
                    }

                });

                log.info("需要创建的索引别名" + indexCreate.keySet().size());
                postMultiAlias(queryWrapper, indexCreate);

            }
            log.info("finish refresh elasticsearch index alias ...");
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

    @Override
    public Map create365Alias(String index, String indexPrefix, String timeField, String timeFormat, String year, boolean force) {
        Map resEntity = new HashMap<String, Object>();
        QueryTools.QueryWrapper queryWrapper = QueryTools.build(esClient, indexConfig);
        try {
            Map aliasRes = queryAliias(queryWrapper, index);

            if (aliasRes != null) {
                Map<String, String> indexCreate = new HashMap<>();

                Date date = TimeTools.parseDate(year, "yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                if (aliasRes.containsKey(index)) {
                    Map object = (Map) aliasRes.get(index);
                    if (!((Map) object.get("aliases")).isEmpty()) {
                        // 已有别名则退出此次操作
                        resEntity.put("code", "100");
                        resEntity.put("warn", "已有别名, 请检查已有的是否正确");
                        resEntity.putAll(aliasRes);
                        return resEntity;
                    }
                } else {
                    resEntity.put("warn", index + "索引不存在");
                    resEntity.put("code", "404");
                    if (force) {
                        Request createRequest = new Request("put", index);
                        //request.setEntity(entity);
                        Response response = queryWrapper.getClient().getLowLevelClient().performRequest(createRequest);
                        log.info(index + "索引不存在, 已新建");
                        resEntity.put("warn", index + "索引不存在, 已自动创建");
                    } else {
                        return resEntity;
                    }
                }

                for (int m = 0; m < 12; m++) {
                    calendar.set(Calendar.MONTH, m);
                    int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                    int mon = m + 1;
                    String month = year + (mon < 10 ? (".0" + mon) : ("." + mon));
                    indexCreate.put(indexPrefix + month, month);
                    for (int i = firstDay; i <= lastDay; i++) {
                        String day = month + (i < 10 ? (".0" + i) : ("." + i));
                        indexCreate.put(indexPrefix + day, day);
                    }
                }
//                log.info(index + "需要创建的索引别名" + indexCreate.keySet().size());
                log.debug(index + "需要创建的索引别名" + indexCreate.keySet());
                resEntity.put("message", postMultiAlias(queryWrapper, timeField, timeFormat, indexCreate, index));
                resEntity.put("code", "200");
            }
            log.info("finish refresh elasticsearch index alias ...");
        } catch (Exception e) {
            log.error("", e);
        }
        return resEntity;
    }

    private Map queryAliias(QueryTools.QueryWrapper queryWrapper, String index) {
        log.info("start to check elasticsearch index alias...");
        String tailUrl = "_alias";
        tailUrl = index + "/" + tailUrl;

        try {
            Request request = new Request("get", tailUrl);
            //request.setEntity(entity);
            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request);
            return (Map) JSON.parse(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error("", e);
        }
        return Collections.emptyMap();
    }

    private String postMultiAlias(QueryTools.QueryWrapper queryWrapper, String defaultField, String timeFormat, Map<String, String> indexCreate, String targetIndex) {
        if (indexCreate.isEmpty()) {
            return "";
        }
        String defaultTimeField = defaultField;
        try {
            HttpEntity entity = new NStringEntity(createMultiAliasJson(indexCreate, defaultTimeField, timeFormat, targetIndex), ContentType.APPLICATION_JSON);
            Request request = new Request("post", "/_aliases");
            request.setEntity(entity);
            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request);
            log.info(response);
            return response.toString();
        } catch (IOException e) {
            log.error("", e);
        }
        return "";
    }

    private String createMultiAliasJson(Map<String, String> indexCreate, final String defaultField, final String timeFormat, String targetIndex) {

        IndexRequest indexRequest = new IndexRequest();
        String source = null;
        try {
            final XContentBuilder builder = JsonXContent.contentBuilder().startObject().startArray("actions");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            indexCreate.entrySet().forEach(r -> {
                try {
                    String indexAlias = r.getKey();
                    String index = targetIndex;
                    String indexPrefix = indexAlias.substring(0, indexAlias.length() - 11);

                    String timeField = defaultField;
                    String day = r.getValue();

                    if (day.length() > 8) {
                        // 给每个别名添加当天的时间过滤
                        Date startDate = sdf.parse(day + " 00:00:00");
                        Date endDate = sdf.parse(day + " 23:59:59");
                        String start = null;
                        String end = null;
                        String timeFormat2 = timeFormat;
                        if (TimeTools.TIME_FMT_1.equals(timeFormat2)) {
                            start = TimeTools.gmtToUtcTimeAsString2(startDate);
                            end = TimeTools.gmtToUtcTimeAsString2(endDate);
                        } else {
                            start = TimeTools.format(startDate, timeFormat2);
                            end = TimeTools.format(endDate, timeFormat2);
                        }

                        builder.startObject().startObject("add").field("index", index).field("alias", indexAlias).startObject("filter").startObject("range")
                                .startObject(timeField).field("from", start).field("to", end).field("include_lower", "true").field("include_upper", "true")
                                .field("boost", "1").endObject().endObject().endObject().endObject().endObject();
                    } else {
                        //按月的索引别名
                        // 给每个别名月索引添加当月的时间过滤
                        Date startDate = sdf.parse(day + ".01" + " 00:00:00");
                        Date endDate = TimeTools.getLastDayOfMonth(startDate);
                        String start = null;
                        String end = null;
                        String timeFormat2 = timeFormat;
                        if (TimeTools.TIME_FMT_1.equals(timeFormat2)) {
                            start = TimeTools.gmtToUtcTimeAsString2(startDate);
                            end = TimeTools.gmtToUtcTimeAsString2(endDate);
                        } else {
                            start = TimeTools.format(startDate, timeFormat2);
                            end = TimeTools.format(endDate, timeFormat2);
                        }

                        builder.startObject().startObject("add").field("index", index).field("alias", indexAlias).startObject("filter").startObject("range")
                                .startObject(timeField).field("from", start).field("to", end).field("include_lower", "true").field("include_upper", "true")
                                .field("boost", "1").endObject().endObject().endObject().endObject().endObject();

                    }

                } catch (Exception e) {
                    log.error("构建别名的语句时错误", e);
                }
            });
            builder.endArray().endObject();

            indexRequest.source(builder);
            // 生成json字符串
            source = indexRequest.source().utf8ToString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return source;
    }

    @Override
    public Result setWindowMaxResult() {
        try {
            QueryTools.QueryWrapper queryWrapper = QueryTools.build(esClient, indexConfig);
            String tailUrl = "*" + TimeTools.format(new Date(), "yyyy.MM") + "*/" + "_alias";
            //Optional<JSONObject> opt = queryWrapper.lowLevelResponseValue("", "_alias");
            Optional<JSONObject> opt = EsCurdTools.simpleGetQueryHttp(tailUrl);
            if (opt.isPresent()) {
                opt.get().entrySet().forEach(e -> {
                    String index = e.getKey();
                    if (index.startsWith("searchguard")) {
                        return;
                    }
                    log.info(index + " : setting max_result_window=1000000000...");
                    queryWrapper.setWindowMaxResult(index);
                });
            }
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.error();
        }
        return VoBuilder.success();
    }

    @Override
    public Result dataClean(Map<String, Object> paramModel) {
        log.info("es数据清理开始");
        String tailUrl = "_cat/indices";
        String result = EsCurdTools.simpleGetQueryHttp2(tailUrl);
        /*
        green  open   task-detail-3fdcc8c3525f4f38b0233dc79387ff6e HbcYiNIDQqCYFaT7WfSCbw   1   0         35            0     60.4kb         60.4kb
        green  open   probe-fileinfo-2020.10                       07MSaa7ZRc-b_aRc_Y108w   1   0         30            8       28kb           28kb
        green  open   threat-indicator                             Q-Jky8nnReWr1eawU4QudA   1   0    3123834            0    707.6mb        707.6mb
         */
        List<String> indexList = new ArrayList<>();
        if (result != null) {
            String[] lines = result.split("\n");
            if (lines.length > 0) {
                for (String line : lines) {
                    String[] fields = line.replaceAll(" +", " ").split(" ");
                    indexList.add(fields[2].trim());
                }
            }
        }
        // 获取数据保留时间（天数）
        int dataSaveDays = (Integer) paramModel.get("dataSaveDays");
        // 获取磁盘使用超过阈值时，数据保留时间（天数）
        int dataSaveDays2 = (Integer) paramModel.get("dataSaveDays2");
        // 保留时间，默认取dataSaveTime，磁盘使用率超过阈值，则取dataSaveTime2
        int saveDays = dataSaveDays;
        // 获取磁盘使用率阈值
        double diskUsedPercentThreshold = (Double) paramModel.get("diskUsedPercentThreshold");

        // 获取es磁盘使用率
        double diskUsedPercent = EsCurdTools.getDiskUsedPercent();
        if (diskUsedPercent > diskUsedPercentThreshold) {
            saveDays = dataSaveDays2;
        }
        // 获取需清理的索引列表
        List<String> cleanIndexList = new ArrayList<>();
        String cleanIndexStrs = (String) paramModel.get("dataCleanIndex");
        if (StringUtils.isNotEmpty(cleanIndexStrs)) {
            String[] indexStrs = cleanIndexStrs.split(",");
            cleanIndexList = Arrays.asList(indexStrs);
        }
        Date lastDate = TimeTools.getNowBeforeByDate(new Date(), saveDays);
        if (indexList.size() > 0) {
            for (String index : indexList) {
                // 匹配xxx-yyyy.MM 、 xxx-yyyy.MM.dd 、xxx_yy_yyyyMM 或 xxx_yy_yyyyMMdd
                if (index.matches(regexStr)) {
                    String indexDate = "";
                    String indexName = "";
                    // 索引包含_ 不包含.  则为xxx_yy_yyyyMM 或 xxx_yy_yyyyMMdd 格式
                    if (index.indexOf("_") > -1 && index.indexOf(".") <= -1) {
                        indexDate = index.substring(index.lastIndexOf("_") + 1);
                        indexName = index.substring(0, index.lastIndexOf("_"));
                    } else {
                        indexDate = index.substring(index.lastIndexOf("-") + 1);
                        indexName = index.substring(0, index.lastIndexOf("-"));
                    }

                    // 该索引是否需要清理数据
                    if (!cleanIndexList.contains(indexName)) {
                        continue;
                    }
                    if (indexDate.length() == 7) {
                        // yyyy.MM
                        int lastDateNum = Integer.parseInt(TimeTools.format(lastDate, "yyyyMM"));
                        int indexDateNum = Integer.parseInt(indexDate.replace(".", ""));
                        if (indexDateNum < lastDateNum) {
                            log.info("清理索引：" + index);
                            EsCurdTools.deleteIndex(index);
                        }
                    } else if (indexDate.length() == 6) {
                        // yyyyMM
                        int lastDateNum = Integer.parseInt(TimeTools.format(lastDate, "yyyyMM"));
                        int indexDateNum = Integer.parseInt(indexDate);
                        if (indexDateNum < lastDateNum) {
                            log.info("清理索引：" + index);
                            EsCurdTools.deleteIndex(index);
                        }
                    } else if (indexDate.length() == 10) {
                        // yyyy.MM.dd
                        int lastDateNum = Integer.parseInt(TimeTools.format(lastDate, "yyyyMMdd"));
                        int indexDateNum = Integer.parseInt(indexDate.replace(".", ""));
                        if (indexDateNum < lastDateNum) {
                            log.info("清理索引：" + index);
                            EsCurdTools.deleteIndex(index);
                        }
                    } else if (indexDate.length() == 8) {
                        // yyyyMMdd
                        int lastDateNum = Integer.parseInt(TimeTools.format(lastDate, "yyyyMMdd"));
                        int indexDateNum = Integer.parseInt(indexDate);
                        if (indexDateNum < lastDateNum) {
                            log.info("清理索引：" + index);
                            EsCurdTools.deleteIndex(index);
                        }
                    }
                }
            }
        }
        log.info("es数据清理结束");
        return VoBuilder.success();
    }

    @Override
    public Result dataTransfer(Map<String, Object> paramModel) {
        String host = String.valueOf(paramModel.get("host"));
        int port = (Integer) paramModel.get("port");
        String user = String.valueOf(paramModel.get("user"));
        String password = String.valueOf(paramModel.get("password"));

        String dstDir = String.valueOf(paramModel.get("dataTransferDst"));
        Integer model = (Integer) paramModel.get("dataTransferModel");

        if (model == 0) {
            //无操作
            log.error("清理模式设置为无, 本次数据转存无任何操作");
            return VoBuilder.success();
        }

        // 获取数据保留时间（天数）
        int dataSaveDays = (Integer) paramModel.get("dataSaveDays");
        // 获取磁盘使用超过阈值时，数据保留时间（天数）
        int dataSaveDays2 = (Integer) paramModel.get("dataSaveDays2");
        // 保留时间，默认取dataSaveTime，磁盘使用率超过阈值，则取dataSaveTime2
        int saveDays = dataSaveDays;
        // 获取磁盘使用率阈值
        double diskUsedPercentThreshold = (Double) paramModel.get("diskUsedPercentThreshold");

        // 获取es磁盘使用率
        double diskUsedPercent = EsCurdTools.getDiskUsedPercent();
        if (diskUsedPercent > diskUsedPercentThreshold) {
            saveDays = dataSaveDays2;
        }

        List<String> cleanIndexList = (List<String>) paramModel.get("dataCleanIndex");

        // 数据备份目录
        String dataBackupDir = pathConfig.getBase() + File.separator + pathConfig.getDataBackup();

        Date lastDate = TimeTools.getNowBeforeByDate(new Date(), saveDays);
        String transferLastDay = TimeTools.format(lastDate, "yyyy.MM.dd");
        List<File> cleanFiles = new ArrayList<>();
        RemoteSSHTools sshTools = null;
        try {
            sshTools = RemoteSSHTools.build(host, port, user, password);
            for (String index : cleanIndexList) {
                String baseDirPath = dataBackupDir + File.separator + index;
                File baseDir = new File(FilenameUtils.normalize(baseDirPath));
                if (baseDir.exists() && baseDir.isDirectory()) {
                    for (File file : baseDir.listFiles((d, n) -> patternDay.matcher(n).find())) {
                        String day = patternDay.matcher(file.getName()).group();
                        if (transferLastDay.compareTo(day) >= 0) {
                            cleanFiles.add(file);
                            if (model != 1) {
                                sshTools.uploadSftpFile(file.getAbsolutePath(), dstDir, file.getName());
                            }
                        }
                    }
                }
            }
            //转存后清理
            cleanFiles.forEach(f -> f.delete());
        } catch (Exception e) {
            log.error("数据转存失败", e);
            return VoBuilder.error();
        } finally {
            if (sshTools != null) {
                sshTools.close();
            }
        }
        return VoBuilder.success();
    }

    /**
     * es的索引备份操作
     *
     * @param paramModel
     * @return
     */
    @Override
    public Result dataBackup(Map<String, Object> paramModel) {
        log.warn("es数据备份开始");
        // 默认备份30天前的数据
        int dataBackupBeforeDays = (Integer) paramModel.get("dataBackupBeforeDays");
        // 默认磁盘使用率超过80，不执行备份
        double diskUsedPercentThreshold = (Double) paramModel.get("diskUsedPercentThreshold");
        // 获取需备份的索引列表
        String indexStr = (String) paramModel.get("dataBackupIndex");
        List<String> indexList = new ArrayList<>();
        if (StringUtils.isNotEmpty(indexStr)) {
            String[] indexStrs = indexStr.split(",");
            indexList = Arrays.asList(indexStrs);
        }
        // 分页条数
        int size = 500;
        // 获取本机磁盘使用情况
        double diskUsedPercent = this.getDiskUsedPercent();
        if (diskUsedPercent >= diskUsedPercentThreshold) {
            log.warn("当前磁盘空间已使用：" + diskUsedPercent + ", 超过阈值" + diskUsedPercentThreshold + "，暂不进行数据备份");
        } else {
            if (indexList != null && indexList.size() > 0) {
                QueryTools.QueryWrapper wrapper = QueryTools.build();
                for (String index : indexList) {
                    //log.info("备份索引：" + index);
//                this.dataBack4Json(index, wrapper, dataBackBeforeDays, size);
                    this.dataBackup4Csv(index, wrapper, dataBackupBeforeDays, size);
                }
            }
            log.warn("es数据备份结束");
        }

        return VoBuilder.success();
    }

    @Value("${dir.backup}")
    private String backUpBaseDir;

    @Override
    public Result dataBackupAndCLean(Map<String, Object> paramModel) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        String gzBaseDir = backUpBaseDir;
        log.warn(String.format("backUpBaseDir: %s, 开始时间：%s", backUpBaseDir, DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")));
        RemoteSSHTools sshTools = null;
        try {
            sshTools = getRemoteSSHTools();
        } catch (JSchException e) {
            log.error("连接失败", e);
            return VoBuilder.error();
        }

        String regDay = "[1-9]\\d{3}(.)*(((0[13578]|1[02])(.)*([0-2]\\d|3[01]))|((0[469]|11)(.)*([0-2]\\d|30))|(02(.)*([01]\\d|2[0-8])))$";
        String regMonth = "[1-9]\\d{3}(.?)((0[13578]|1[02])|(0[469]|11)|(02))$";
        //匹配年索引
        String regYear = "[1-9]\\d{3}$";
        Pattern patternDay = Pattern.compile(regDay);
        Pattern patternMonth = Pattern.compile(regMonth);
        Pattern patternYear = Pattern.compile(regYear);

        RestClient lowLevelClient = wrapper.getClient().getLowLevelClient();
        DataDumpStrategyQuery record = new DataDumpStrategyQuery();
        record.setState(1);
        record.setMyCount(1000);
        List<PreparedCleanIndex> allIndex = new ArrayList<>();
        List<PreparedCleanIndex> allCleanIndex = new ArrayList<>();
        List<DataDumpStrategy> dumpStrategies = businessClient.selectStrategyListByPage(record).getList();
        log.warn("dumpStrategies size:" + dumpStrategies.size());
        for (DataDumpStrategy dataDumpStrategy : dumpStrategies) {
            // indices 为 netflow-http-*
            String indices = dataDumpStrategy.getDataId();
            if (StringUtils.isEmpty(indices)) {
                continue;
            }
            String tailUrl = indices + "*/" + "_alias";
            List<PreparedCleanIndex> cleanIndices = new ArrayList<>();
            log.warn("simpleGetQueryHttp【GET请求】，别名请求url-----> " + tailUrl);
            //Optional<JSONObject> opt = wrapper.lowLevelResponseValue("", "_alias");
            Optional<JSONObject> opt = EsCurdTools.simpleGetQueryHttp(tailUrl);

            log.warn(String.format("根据别名获取所有索引 %s", ReflectionToStringBuilder.toString(opt, ToStringStyle.MULTI_LINE_STYLE)));
            opt.get().entrySet().forEach(e -> {
                String index = e.getKey();
                Object value = e.getValue();
                if (index.startsWith("searchguard") || index.startsWith(".")) {
                    return;
                }
                log.warn("数据elasticSearch index: " + index);
                Matcher matcher = patternDay.matcher(index);
                PreparedCleanIndex cleanIndex = null;
                if (matcher.find()) {
                    String day = matcher.group();
                    String indexPrefix = index.replace(day, "");
                    log.warn(String.format("别名按天，数据elasticSearch index: %s, indexPrefix: %s, day: %s", index, indexPrefix, day));
                    Date date = TimeTools.parseDate(day.replaceAll("\\.", ""), "yyyyMMdd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    cleanIndex = new PreparedCleanIndex(indexPrefix, index, date, day, false);
                    cleanIndex.setStrategy(dataDumpStrategy);
                    //cleanIndices.add(cleanIndex);
                } else {
                    matcher = patternMonth.matcher(index);
                    if (matcher.find()) {
                        String month = matcher.group();
                        String indexPrefix = index.replace(month, "");
                        log.warn(String.format("别名按月，数据elasticSearch index: %s, indexPrefix: %s, month: %s", index, indexPrefix, month));
                        Date date = TimeTools.parseDate(month.replaceAll("\\.", ""), "yyyyMM");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.MONTH, 1);
                        calendar.set(Calendar.DAY_OF_MONTH, 0);
                        cleanIndex = new PreparedCleanIndex(indexPrefix, index, calendar.getTime(), month, true);
                        cleanIndex.setStrategy(dataDumpStrategy);
                        //cleanIndices.add(cleanIndex);
                    } else {
                        //新加年份索引的判断逻辑
                        matcher = patternYear.matcher(index);
                        if (matcher.find()) {
                            String year = matcher.group();
                            String indexPrefix = index.replace(year, "");
                            log.warn(String.format("别名按年，数据elasticSearch index: %s, indexPrefix: %s, year: %s", index, indexPrefix, year));
                            Date date = TimeTools.parseDate(year.replaceAll("\\.", ""), "yyyy");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.YEAR, 1);
                            calendar.set(Calendar.DAY_OF_YEAR, 0);
                            cleanIndex = new PreparedCleanIndex(indexPrefix, index, calendar.getTime(), year, false);
                            cleanIndex.setStrategy(dataDumpStrategy);
                            //cleanIndices.add(cleanIndex);
                        }
                    }
                }
                if (cleanIndex != null) {
                    log.warn(String.format("数据清理备份结果请求打印日志[%s]", ReflectionToStringBuilder.toString(cleanIndex, ToStringStyle.MULTI_LINE_STYLE)));
                    //对索引前缀二次校验, 避免相同前缀的索引(如net-*, net-dns-*)导致备份了不相干的数据
                    if (indices.substring(0, indices.length() - 1).equals(cleanIndex.getIndexPrefi())) {
                        cleanIndices.add(cleanIndex);
                    }
                }
            });
            allIndex.addAll(cleanIndices);

            // TODO 从界面上获取的数据保留时间（天数），计算当前时间前多少天的数据需要清理
            final Date remainDate = TimeTools.getNowBeforeByDay(dataDumpStrategy.getSaveTime());
            log.warn(String.format("开始时间:%s, 结束时间:%s, 保存天数：%s", DateFormatUtils.format(remainDate, "yyyy-MM-dd HH:mm:ss"), DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"), String.valueOf(dataDumpStrategy.getSaveTime())));
            List<PreparedCleanIndex> cleanIndexList = cleanIndices.stream().filter(f -> f.getIndexDate().compareTo(remainDate) < 0).collect(Collectors.toList());
            log.warn("数据清理备份:待清理的所有索引!!!!!!!!!!!!!!!!!!!!!!" + cleanIndexList.size());
            log.warn(String.format("数据清理备份:待清理的所有索引!!! %s", ReflectionToStringBuilder.toString(cleanIndexList, ToStringStyle.MULTI_LINE_STYLE)));
            if (cleanIndexList.isEmpty()) {
                continue;
            }
            // TODO 清理并进行备份操作
            eachBackAndClean(backUpBaseDir, gzBaseDir, lowLevelClient, dataDumpStrategy, cleanIndexList, sshTools);
            allCleanIndex.addAll(cleanIndexList);
        }

        //阈值
        double diskUsedPercentThreshold = (Double) paramModel.getOrDefault("diskUsedPercentThreshold", 80d);
        // 去除已经清理的索引列表
        allIndex.removeAll(allCleanIndex);
        allIndex.sort((a, b) -> a.getIndexDate().compareTo(b.getIndexDate()));
        while (this.getDiskUsedPercent() >= diskUsedPercentThreshold && !allIndex.isEmpty()) {
            cleanAgain(backUpBaseDir, gzBaseDir, lowLevelClient, allIndex, diskUsedPercentThreshold, sshTools);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
            }
        }
        log.warn("数据清理备份:结束!!!!!!!!!!!!!!!!!!!!!!");
        sshTools.close();
        return VoBuilder.success();
    }

    protected boolean cleanAgain(String backUpBaseDir, String gzBaseDir, RestClient lowLevelClient, List<PreparedCleanIndex> allIndex, double diskUsedPercentThreshold, RemoteSSHTools sshTools) {
        // 判断磁盘 利用率
        // 获取本机磁盘使用情况
        double diskUsedPercent = this.getDiskUsedPercent();
        if (diskUsedPercent >= diskUsedPercentThreshold) {
            log.info("当前磁盘空间已使用：" + diskUsedPercent + ", 超过阈值" + diskUsedPercentThreshold + "，需要再次清理数据");

            List<PreparedCleanIndex> earlistCleanIndex = new ArrayList<>();
            PreparedCleanIndex cleanIndex = allIndex.get(0);
            earlistCleanIndex.add(cleanIndex);
            eachBackAndClean(backUpBaseDir, gzBaseDir, lowLevelClient, earlistCleanIndex.get(0).getStrategy(), earlistCleanIndex, sshTools);
            allIndex.remove(cleanIndex);
            return true;
        }
        return false;
    }

    /**
     * 创建备份操作，通过ELASTICSEARCH的方式进行备份 https://zhuanlan.zhihu.com/p/654858137
     *
     * @param backUpBaseDir
     * @param gzBaseDir
     * @param lowLevelClient
     * @param r
     * @param cleanIndexList
     * @param sshTools
     * @return
     */
    protected boolean eachBackAndClean(String backUpBaseDir, String gzBaseDir, RestClient lowLevelClient, DataDumpStrategy r, List<PreparedCleanIndex> cleanIndexList, RemoteSSHTools sshTools) {
        String currentDate = TimeTools.formatTimeStamp(TimeTools.getNow());
        String indicesBackup = cleanIndexList.stream().map(PreparedCleanIndex::getIndexName).collect(Collectors.joining(","));
        //判断是否需要备份
        if (r.getType() == 2 || r.getType() == 3) {
            //  是否创建仓库
            try {
                HttpEntity entity0 = new NStringEntity(String.format("{\"type\": \"fs\",\"settings\": {\"location\":\"%s\",\"max_snapshot_bytes_per_sec\" : \"30mb\", \"max_restore_bytes_per_sec\" :\"30mb\"}}", r.getId()), ContentType.APPLICATION_JSON);
                Request request = new Request("put", "/_snapshot/" + r.getId());
                request.setEntity(entity0);
//                Response put = lowLevelClient.performRequest("put", "/_snapshot/" + r.getId(), Collections.emptyMap(), entity0);
                Response put = lowLevelClient.performRequest(request);
                Object o = JSON.parseObject(put.getEntity().getContent(), Map.class);
                log.warn("数据清理备份:创建仓库!!!!!!!!!!!!!!!!!!!!!!" + o);
            } catch (IOException e) {
                log.error("", e);
            }

            //  备份
            try {
                HttpEntity entity = new NStringEntity(String.format("{\"indices\": \"%s\"}", indicesBackup), ContentType.APPLICATION_JSON);
                Map<String, String> params = new HashMap<>();
                params.put("wait_for_completion", "true");
                Request request = new Request("put", "/_snapshot/" + r.getId() + "/" + currentDate);
                request.setEntity(entity);
                request.addParameters(params);
//                Response response = lowLevelClient.performRequest("put", "/_snapshot/" + r.getId() + "/" + currentDate, params, entity);
                Response response = lowLevelClient.performRequest(request);
                Object o = JSON.parseObject(response.getEntity().getContent(), Map.class);
                log.info("数据清理备份:备份!!!!!!!!!!!!!!!!!!!!!!" + o);

                //压缩文件
                String fileName = String.format("%s.tar.gz", r.getDataDesc() + currentDate);
                String dstFile = gzBaseDir + "/" + fileName;
                String command = String.format("tar -zcvPf %s %s/*", dstFile, backUpBaseDir + "/" + r.getId());

                String res = sshTools.execute(command);
                log.warn("数据清理备份:执行压缩结果:!!!!!!!!!!!!!!!!!!!!!!" + res.substring(0, 5) + "...");
                log.debug("数据清理备份:执行压缩结果:!!!!!!!!!!!!!!!!!!!!!!" + res);

                TimeUnit.SECONDS.sleep(5);
                File file = new File(FilenameUtils.normalize(dstFile));
                command = String.format("md5sum %s", dstFile);
                String md5 = sshTools.isLocal() ? CommonTools.calcMD5(file) : sshTools.execute(command);
                if (md5 != null) {
                    md5 = md5.split(" ")[0].toUpperCase();
                    if (md5.length() < 16) {
                        md5 = "";
                    }
                }

                //记录
                DataDumpLog log1 = new DataDumpLog();
                log1.setStrategyId(r.getId());
                log1.setDataId(r.getDataId());
                log1.setDataDesc(r.getDataDesc());
                log1.setDumpFilePath(dstFile);
                log1.setDumpFileMd5(md5);
                log1.setDumpFileState(1);
                log1.setDumpTime(TimeTools.getNow());
                log1.setSnapshotName(r.getId() + "/" + currentDate);
                log1.setDataDetail(indicesBackup);
                businessClient.addDataDumpLog(log1);

                command = String.format("rm -rf %s/*", backUpBaseDir + "/" + r.getId());
                res = sshTools.execute(command);
                log.warn("数据清理备份:压缩后执行删除:!!!!!!!!!!!!!!!!!!!!!!" + res);

                if (r.getType() == 3) {
                    //转存
                    FileProgressMonitor downloadMonitor = new FileProgressMonitor();
                    InputStream downloadSftpFile = sshTools.downloadSftpFile(dstFile, downloadMonitor);
                    //                while(!downloadMonitor.isEnd()){
                    //                    //隔1秒获取下文件下载状态
                    //                    TimeUnit.SECONDS.sleep(1);
                    //                }
                    RemoteSSHTools transferRemoteSSHTools = getTransferRemoteSSHTools();
                    Map<String, Map<String, String>> cleanHostMap = dataCleanDao.getConfMapById("data_transfer_dst_audit");
                    String dstDir = cleanHostMap.get("data_transfer_dst_audit").get("val");

                    FileProgressMonitor uploadMonitor = new FileProgressMonitor();
                    transferRemoteSSHTools.uploadSftpFile(downloadSftpFile, dstDir, fileName, uploadMonitor);
                    //                while(!downloadMonitor.isEnd()){
                    //                    //隔1秒获取下文件上传状态
                    //                    TimeUnit.SECONDS.sleep(1);
                    //                }
                    log.warn("数据清理备份:文件转存结束!!!!!!!!!!!!!!!!!!!!!!");
                }
            } catch (Exception e1) {
                log.error("", e1);
                return true;
            }
        }

        //清理
        cleanIndexList.sort((a, b) -> a.getIndexDate().compareTo(b.getIndexDate()));
        cleanIndexList.forEach(i -> EsCurdTools.deleteIndex(i.getIndexName()));

        //记录
        DataCleanLog log2 = new DataCleanLog();
        log2.setDataId(r.getDataId());
        log2.setCleanTime(TimeTools.getNow());
        log2.setDataDesc(r.getDataDesc());
        log2.setDataDetail(indicesBackup);
        log2.setDataType(1);
        businessClient.addDataCleanLog(log2);

        // 发送告警
        sendWarn(log2);

        return false;
    }

    @Async
    void sendWarn(DataCleanLog log2) {
        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("userId", pushUserId);
        pushMap.put("content", "根据数据安全策略配置，已对" + log2.getDataDesc() + "数据进行转储清理。具体如下：" + log2.getDataDetail());
        pushMap.put("title", "es数据转储清理告警");
        pushMap.put("url", "");
        try {
            String response = HttpTools.doPutJson(pushUrl, JSONObject.toJSONString(pushMap));
            log.info("es数据转储清理，发送告警 " + response);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public Result datarollBack(DataDumpLog paramModel) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        RestClient lowLevelClient = wrapper.getClient().getLowLevelClient();

        //先解压
        RemoteSSHTools sshTools = null;
        try {
            sshTools = getRemoteSSHTools();
        } catch (JSchException e) {
            log.error("连接失败", e);
            return VoBuilder.error();
        }
        String command = String.format("tar -zxvf %s -C /", paramModel.getDumpFilePath(), backUpBaseDir + "/" + paramModel.getStrategyId());
        try {
            sshTools.execute(command);
            //localExecuteCmd(command);
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            log.error("", e);
        }

        if (paramModel.getDataDetail() != null) {
            for (String index : paramModel.getDataDetail().split(",")) {
                try {
                    HttpEntity entity = new NStringEntity("{}", ContentType.APPLICATION_JSON);
                    Map<String, String> params = new HashMap<>(1);
                    Request request = new Request("post", index + "/_close");
                    request.setEntity(entity);
                    request.addParameters(params);
//                    Response response = lowLevelClient.performRequest("post", index + "/_close", params, entity);
                    Response response = lowLevelClient.performRequest(request);
                    Object o = JSON.parseObject(response.getEntity().getContent(), Map.class);
                    log.info("数据清理备份:执行关闭索引:!!!!!!!!!!!!!!!!!!!!!!" + o);
                } catch (Exception e) {
                    log.error("404不影响还原操作", e);
                }
            }
        }

        try {
            HttpEntity entity = new NStringEntity("", ContentType.APPLICATION_JSON);
            Map<String, String> params = new HashMap<>();
            params.put("wait_for_completion", "true");
            Request request = new Request("post", "/_snapshot/" + paramModel.getSnapshotName() + "/_restore");
            request.setEntity(entity);
            request.addParameters(params);
//            Response response = lowLevelClient.performRequest("post", "/_snapshot/" + paramModel.getSnapshotName() + "/_restore", params, entity);
            Response response = lowLevelClient.performRequest(request);
            Object o = JSON.parseObject(response.getEntity().getContent(), Map.class);
            log.info(o);
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            log.error("", e);
        }

        //删除备份元文件
        try {
            command = String.format("rm -rf %s/*", backUpBaseDir + "/" + paramModel.getStrategyId());
            sshTools.execute(command);
            //localExecuteCmd(command);
        } catch (Exception e) {
            log.error("", e);
        }
        log.info("数据清理备份:还原结束!!!!!!!!!!!!!!!!!!!!!!");
        sshTools.close();
        return VoBuilder.success();
    }

    @Override
    public Result createTemplate(EsTemplate template) {
        Result re = new Result();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        RestClient lowLevelClient = wrapper.getClient().getLowLevelClient();
        String tailUrl = "/_template/" + template.getName();
        Request request = new Request("put", tailUrl);
        HttpEntity entity = new NStringEntity(template.toTemplateJson(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            lowLevelClient.performRequest(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return re;
    }

    @Override
    public boolean indexTemplateExists(String templateName) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        RestClient lowLevelClient = wrapper.getClient().getLowLevelClient();
        Request request = new Request(HttpHead.METHOD_NAME, "_template/" + templateName);
        try {
            Response response = lowLevelClient.performRequest(request);
            if (200 == response.getStatusLine().getStatusCode()) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 初始化es服务器的ssh会话
     *
     * @return
     * @throws JSchException
     */
    private RemoteSSHTools getRemoteSSHTools() throws JSchException {
        /*Map<String, Map<String, String>> cleanHostMap = dataCleanDao.getConfMapById("data_clean_host");
        String host = cleanHostMap.get("data_clean_host").get("val");
        String user = cleanHostMap.get("data_clean_host_user").get("val");
        String password = cleanHostMap.get("data_clean_host_pass").get("val");
        int port = Integer.parseInt(cleanHostMap.get("data_clean_host_port").get("val"));*/
        //本地连接模式
        return RemoteSSHTools.build("localhost", 22, "", "");
    }

    /**
     * 初始化转存服务器的ssh会话
     *
     * @return
     * @throws JSchException
     */
    private RemoteSSHTools getTransferRemoteSSHTools() throws JSchException {
        Map<String, Map<String, String>> cleanHostMap = dataCleanDao.getConfMapById("data_transfer");
        String host = cleanHostMap.get("data_transfer_host_audit").get("val");
        String user = cleanHostMap.get("data_transfer_user_audit").get("val");
        String password = cleanHostMap.get("data_transfer_pass_audit").get("val");
        int port = Integer.parseInt(cleanHostMap.get("data_transfer_port_audit").get("val"));
        return RemoteSSHTools.build(host, port, user, password);
    }

    protected Session openRemoteSession(String host, int port, String user, String password) {
        Session session = null;
        try {
            // 建立远程连接
            JSch jsch = new JSch();
            if (log.isDebugEnabled()) {
                log.debug("sftp > " + host + ":" + port);
            }
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(30 * 1000);
            session.setPassword(password);
            session.connect();

        } catch (JSchException e) {
            if (session != null) {
                session.disconnect();
            }
        }
        return session;
    }

    /**
     * 获取本机磁盘使用情况
     *
     * @return
     */
    private double getDiskUsedPercent() {
        File[] disks = File.listRoots();
        double totalSpace = 0;
        long totalUsedSpace = 0;
        for (File file : disks) {
            totalSpace += file.getTotalSpace();
            totalUsedSpace += file.getTotalSpace() - file.getUsableSpace();
            if (log.isDebugEnabled()) {
                log.debug(file.getPath());
                log.debug("空闲未使用 = " + file.getFreeSpace() / 1024 / 1024 + "M" + "    ");
                log.debug("已经使用 = " + (file.getTotalSpace() - file.getUsableSpace()) / 1024 / 1024 + "M" + "    ");
                log.debug("容量 = " + file.getTotalSpace() / 1024 / 1024 + "M" + "    ");
            }
        }
        log.info("总容量" + totalSpace);
        log.info("总已使用" + totalUsedSpace);
        double result = totalUsedSpace / totalSpace * 100;
        double diskUsedPercent = new BigDecimal(result).setScale(2, RoundingMode.UP).doubleValue();
        log.info("使用率" + diskUsedPercent);
        return diskUsedPercent;
    }


    /**
     * es数据备份(csv)
     *
     * @param index
     * @param wrapper
     * @param dataBackBeforeDays
     * @param size
     */
    private void dataBackup4Csv(String index, QueryTools.QueryWrapper wrapper, int dataBackBeforeDays, int size) {
        EsQueryModel queryModel = buildDataBackQueryModel(index, wrapper, dataBackBeforeDays, size);
        SearchResponse response = null;
        List<String> headList = new ArrayList<>();
        // 数据备份目录
        String dataBackupDir = pathConfig.getBase() + File.separator + pathConfig.getDataBackup();
        // 写入
        File file = null;
        BufferedWriter writer = null;
        FileOutputStream fs = null;
        OutputStreamWriter ow = null;
        String filePath = dataBackupDir + File.separator + index + File.separator + index + "-" + TimeTools.format(queryModel.getStartTime(), "yyyy.MM.dd") + ".csv";
        try {
            // 无目录则创建
            file = new File(FilenameUtils.normalize(filePath));
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
            fs = new FileOutputStream(file);
            ow = new OutputStreamWriter(fs, "UTF-8");
            writer = new BufferedWriter(ow, 1024);

            // 获取head
            response = QueryTools.build().scrollQueryNoPermission(queryModel, response == null ? null : response.getScrollId());
            if (response != null && response.getHits().getTotalHits().value > 0) {
                EsResult esResult = wrapper.wrapResult(response, queryModel);
                Map<String, Object> tmp = esResult.getList().get(0);
                if (tmp != null) {
                    for (String fieldName : tmp.keySet()) {
                        headList.add(fieldName);
                    }
                }
                // 写入head
                this.headWrite(writer, headList);
                // 写入data
                this.dataWrite(writer, esResult.getList(), headList);
            }

            while (true) {
                response = QueryTools.build().scrollQueryNoPermission(queryModel, response == null ? null : response.getScrollId());
                if (response != null && response.getHits().getTotalHits().value > 0) {
                    EsResult esResult = wrapper.wrapResult(response, queryModel);
                    // 写入data
                    this.dataWrite(writer, esResult.getList(), headList);

                    int currentSize = response.getHits().getHits().length;
                    if (currentSize < size) {
                        break;
                    }
                } else {
                    break;
                }
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ow != null) {
                    ow.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 压缩
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(FilenameUtils.normalize(filePath.replace(".csv", ".zip"))));
            ZipTools.toZip(filePath, os, true);

            // 压缩完成后，删除文件
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    /**
     * 表头写入
     *
     * @param writer
     * @param headList
     */
    private void headWrite(BufferedWriter writer, List<String> headList) throws IOException {
        String headStr = "";
        for (String h : headList) {
            headStr += h + ",";
        }
        writer.write(headStr.substring(0, headStr.length() - 1));
        writer.newLine();
    }

    /**
     * 数据写入
     *
     * @param writer
     * @param list
     * @param headList
     */
    private void dataWrite(BufferedWriter writer, List<Map<String, Object>> list, List<String> headList) throws IOException {
        for (Map<String, Object> tmp : list) {
            String lineData = "";
            for (String field : headList) {
                lineData += tmp.get(field).toString() + ",";
            }
            writer.write(lineData.substring(0, lineData.length() - 1));
            writer.newLine();
        }
    }

    /**
     * es数据备份(json)
     *
     * @param index
     * @param wrapper
     * @param dataBackBeforeDays
     * @param size
     */
    private void dataBackup4Json(String index, QueryTools.QueryWrapper wrapper, int dataBackBeforeDays, int size) {
        EsQueryModel queryModel = buildDataBackQueryModel(index, wrapper, dataBackBeforeDays, size);
        SearchResponse response = null;
        // 写入
        File file = null;
        BufferedWriter writer = null;
        FileOutputStream fs = null;
        try {
            String filePath = "F:/test/" + index + File.separator + index + "-" + TimeTools.format(queryModel.getStartTime(), "yyyy.MM.dd") + ".txt";
            // 无目录则创建
            file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
            fs = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fs, "UTF-8"), 1024);
            while (true) {
                response = QueryTools.build().scrollQueryNoPermission(queryModel, response == null ? null : response.getScrollId());
                if (response != null && response.getHits().getTotalHits().value > 0) {
                    // 写入json
                    for (SearchHit hit : response.getHits()) {
                        String dataJson = gson.toJson(hit.getSourceAsMap());
                        writer.write(dataJson);
                        writer.newLine();
                    }
                    int currentSize = response.getHits().getHits().length;
                    if (currentSize < size) {
                        break;
                    }
                } else {
                    break;
                }
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 构建数据备份查询
     *
     * @param index
     * @param wrapper
     * @param dataBackBeforeDays
     * @param size
     * @return
     */
    private EsQueryModel buildDataBackQueryModel(String index, QueryTools.QueryWrapper wrapper, int dataBackBeforeDays, int size) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setCount(size);
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(dataBackBeforeDays));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(dataBackBeforeDays));
        // 获取索引
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[0]));
        }
        queryModel.setTimeField("event_time");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(false);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }


    private void postSingleSpecialAlias(QueryTools.QueryWrapper queryWrapper, Map<String, String> indexCreate) {
        indexCreate.entrySet().forEach(r -> {
            try {
                String index = r.getKey();
                String day = r.getValue();
                String start = day.replaceAll("\\.", "-");
                String end = start;

                String endpoint = "/" + index.substring(0, index.length() - 3) + "/_alias/" + index;
//                HttpEntity entity = new NStringEntity(createSingleAliasJson("event_time", TimeTools.gmtToUtcTimeAsString2(start), TimeTools.gmtToUtcTimeAsString2(end)), ContentType.APPLICATION_JSON);
                HttpEntity entity = new NStringEntity(createSingleAliasJson("event_time", start, end), ContentType.APPLICATION_JSON);
                Request request = new Request("put", endpoint);
                request.setEntity(entity);
//                request.addParameters(params);
//                queryWrapper.getClient().getLowLevelClient().performRequest("put", endpoint, Collections.emptyMap(), entity);
                queryWrapper.getClient().getLowLevelClient().performRequest(request);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private String createSingleAliasJson(String field, String from, String to) {
        IndexRequest indexRequest = new IndexRequest();
        XContentBuilder builder = null;
        try {
            builder = JsonXContent.contentBuilder()
                    .startObject()
                    .startObject("filter")
                    .startObject("range")
                    .startObject("event_time")
                    .field("from", from)
                    .field("to", to)
                    .field("include_lower", "true")
                    .field("include_upper", "true")
                    .field("boost", "1")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        indexRequest.source(builder);
        // 生成json字符串
        String source = indexRequest.source().utf8ToString();
        return source;
    }

    private void postMultiAlias(QueryTools.QueryWrapper queryWrapper, Map<String, String> indexCreate) {
        if (indexCreate.isEmpty()) {
            return;
        }
        String defaultTimeField = indexConfig.getTimeField().getOrDefault("default", "event_time");
        try {
            HttpEntity entity = new NStringEntity(createMultiAliasJson(indexCreate, defaultTimeField), ContentType.APPLICATION_JSON);
            Request request = new Request("post", "/_aliases");
            request.setEntity(entity);
//                request.addParameters(params);
//            Response response = queryWrapper.getClient().getLowLevelClient().performRequest("post", "/_aliases", Collections.emptyMap(), entity);
            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request);
            log.info(response);
        } catch (IOException e) {
            log.error(e);
        }

    }

    private String createMultiAliasJson(Map<String, String> indexCreate, final String defaultField) {

        IndexRequest indexRequest = new IndexRequest();
        String source = null;
        try {
            final XContentBuilder builder = JsonXContent.contentBuilder()
                    .startObject()
                    .startArray("actions");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            indexCreate.entrySet().forEach(r -> {
                try {
                    String indexAlias = r.getKey();
                    String index = indexAlias.substring(0, indexAlias.length() - 3);
                    String indexPrefix = indexAlias.substring(0, indexAlias.length() - 11);
                    AliasConfig aliasConfig = this.aliasConfig.get(indexPrefix);

//                    String timeField = indexConfig.getTimeField().getOrDefault(indexAlias.substring(0, indexAlias.length() - 11),defaultField);
                    String timeField = aliasConfig.getTimeField();
                    String day = r.getValue();

                    // 给每个别名添加当天的时间过滤
                    Date startDate = sdf.parse(day + " 00:00:00");
                    Date endDate = sdf.parse(day + " 23:59:59");
                    String start = null;
                    String end = null;
                    if (TimeTools.TIME_FMT_1.equals(aliasConfig.getTimeFormat())) {
                        start = TimeTools.gmtToUtcTimeAsString2(startDate);
                        end = TimeTools.gmtToUtcTimeAsString2(endDate);
                    } else {
                        start = TimeTools.format(startDate, aliasConfig.getTimeFormat());
                        end = TimeTools.format(endDate, aliasConfig.getTimeFormat());
                    }

                    builder.startObject().startObject("add")
                            .field("index", index)
                            .field("alias", indexAlias)
                            .startObject("filter")
                            .startObject("range")
                            .startObject(timeField)
                            .field("from", start)
                            .field("to", end)
                            .field("include_lower", "true")
                            .field("include_upper", "true")
                            .field("boost", "1")
                            .endObject()
                            .endObject()
                            .endObject()
                            .endObject()
                            .endObject();

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    log.error(e);
                }
            });
            builder.endArray().endObject();

            indexRequest.source(builder);
            // 生成json字符串
            source = indexRequest.source().utf8ToString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return source;
    }
}
