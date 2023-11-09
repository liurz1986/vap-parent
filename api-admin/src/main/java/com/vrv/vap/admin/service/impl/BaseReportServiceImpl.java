package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.BaseReportInterfaceMapper;
import com.vrv.vap.admin.mapper.BaseReportMapper;
import com.vrv.vap.admin.mapper.BaseReportModelMapper;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BaseReportService;
import com.vrv.vap.admin.service.SearchService;
import com.vrv.vap.admin.util.ModelUtil;
import com.vrv.vap.admin.vo.EsSearchQuery;
import com.vrv.vap.admin.vo.ReportConfig;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Main on 2019/07/24.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseReportServiceImpl extends BaseServiceImpl<BaseReport> implements BaseReportService {

    private static final Logger log = LoggerFactory.getLogger(BaseReportServiceImpl.class);

    @Resource
    private BaseReportMapper baseReportMapper;
    @Resource
    private BaseReportModelMapper baseReportModelMapper;
    @Resource
    private BaseReportInterfaceMapper baseReportInterfaceMapper;
    @Autowired
    private BaseKoalOrgService baseKoalOrgService;
    @Autowired
    private SearchService searchService;

    private final static Integer GROUP_NUM_CONST = 50;


    @Override
    public boolean importConfig(ReportConfig reportConfig) {
        if(reportConfig == null || reportConfig.getBaseReports() == null){
            return false;
        }
        List<BaseReport> reports = reportConfig.getBaseReports();
        for(BaseReport r : reports){
            r.setId(null);
        }
        if(CollectionUtils.isNotEmpty(reportConfig.getModels())){
            for(BaseReportModel model : reportConfig.getModels()){
                if(!ModelUtil.checkSql(model.getSql())){
                    return false;
                }
            }
            /*
            BASE64Decoder de = new BASE64Decoder();
            reportConfig.getModels().forEach(e ->{
                if(StringUtils.isNotEmpty(e.getSql())) {
                    try {
                        e.setSql(new String(de.decodeBuffer(e.getSql()),"utf-8"));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                if(StringUtils.isNotEmpty(e.getContent())) {
                    try {
                        e.setContent(new String(de.decodeBuffer(e.getContent()),"utf-8"));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });*/
            baseReportModelMapper.batchReplaceInto(reportConfig.getModels());
        }
        if(CollectionUtils.isNotEmpty(reportConfig.getInterfaces())){
            this.baseReportInterfaceMapper.batchReplaceInto(reportConfig.getInterfaces());
        }
        baseReportMapper.insertList(reports);
        return true;
    }

    @Override
    public Page queryPersonPrint(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("print-audit-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}},{\"term\":{\"op_type\":\"0\"}}]}}," +
                "\"aggs\":{\"aggs_person\":{\"terms\":{\"field\":\"username\",\"size\":10}," +
                "\"aggs\":{\"aggs_level\":{\"terms\":{\"field\":\"file_level\",\"order\":{\"_key\":\"asc\"}}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> personList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("aggs_person")).get("buckets");
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key",p.get("key"));
                    map.put("doc_count", p.get("doc_count"));
                    Map<String,Object> levelMap = (Map<String, Object>) p.get("aggs_level");
                    List<Map<String, Object>> levelList = (List<Map<String, Object>>) levelMap.get("buckets");
                    if (CollectionUtils.isNotEmpty(levelList)) {
                        levelList.forEach(item -> {
                            item.put("key",transSecretLevel(item.get("key").toString()));
                        });
                    }
                    map.put("buckets",levelList);
                    personList.add(map);
                });
            }
        } catch (Exception e){
            log.error("打印文件次数按人统计查询异常", e);
        }
        page.addAll(personList);
        return page;
    }

    @Override
    public Page queryOrgPrint(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("print-audit-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}},{\"term\":{\"op_type\":\"0\"}}]}}," +
                "\"aggs\":{\"aggs_org\":{\"terms\":{\"field\":\"std_org_code\",\"size\":10}," +
                "\"aggs\":{\"aggs_level\":{\"terms\":{\"field\":\"file_level\",\"order\":{\"_key\":\"asc\"}}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        List<BaseKoalOrg> baseKoalOrgs = baseKoalOrgService.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> orgList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("aggs_org")).get("buckets");
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(p -> {
                    Map<String, Object> map = new HashMap<>();
                    String orgCode = p.get("key").toString();
                    String orgName = "";
                    Optional<BaseKoalOrg> optional = baseKoalOrgs.stream().filter(org -> orgCode.equals(org.getCode())).findFirst();
                    if (optional.isPresent()) {
                        BaseKoalOrg baseKoalOrg = optional.get();
                        orgName = baseKoalOrg.getName();
                    }
                    map.put("key",orgName);
                    map.put("doc_count", p.get("doc_count"));
                    Map<String,Object> levelMap = (Map<String, Object>) p.get("aggs_level");
                    List<Map<String, Object>> levelList = (List<Map<String, Object>>) levelMap.get("buckets");
                    if (CollectionUtils.isNotEmpty(levelList)) {
                        levelList.forEach(item -> {
                            item.put("key",transSecretLevel(item.get("key").toString()));
                        });
                    }
                    map.put("buckets",levelList);
                    orgList.add(map);
                });
            }
        } catch (Exception e){
            log.error("打印文件次数按部门统计查询异常", e);
        }
        page.addAll(orgList);
        return page;
    }

    @Override
    public Page queryPersonImPrint(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("print-audit-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}},{\"term\":{\"op_type\":\"1\"}}]}}," +
                "\"aggs\":{\"aggs_person\":{\"terms\":{\"field\":\"username\",\"size\":10}," +
                "\"aggs\":{\"aggs_level\":{\"terms\":{\"field\":\"file_level\",\"order\":{\"_key\":\"asc\"}}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> personList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("aggs_person")).get("buckets");
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key",p.get("key"));
                    map.put("doc_count", p.get("doc_count"));
                    Map<String,Object> levelMap = (Map<String, Object>) p.get("aggs_level");
                    List<Map<String, Object>> levelList = (List<Map<String, Object>>) levelMap.get("buckets");
                    if (CollectionUtils.isNotEmpty(levelList)) {
                        levelList.forEach(item -> {
                            item.put("key",transSecretLevel(item.get("key").toString()));
                        });
                    }
                    map.put("buckets",levelList);
                    personList.add(map);
                });
            }
        } catch (Exception e){
            log.error("打印文件次数按人统计查询异常", e);
        }
        page.addAll(personList);
        return page;
    }

    @Override
    public Page queryOrgImPrint(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("print-audit-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}},{\"term\":{\"op_type\":\"1\"}}]}}," +
                "\"aggs\":{\"aggs_org\":{\"terms\":{\"field\":\"std_org_code\",\"size\":10}," +
                "\"aggs\":{\"aggs_level\":{\"terms\":{\"field\":\"file_level\",\"order\":{\"_key\":\"asc\"}}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        List<BaseKoalOrg> baseKoalOrgs = baseKoalOrgService.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> orgList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("aggs_org")).get("buckets");
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(p -> {
                    Map<String, Object> map = new HashMap<>();
                    String orgCode = p.get("key").toString();
                    String orgName = "";
                    Optional<BaseKoalOrg> optional = baseKoalOrgs.stream().filter(org -> orgCode.equals(org.getCode())).findFirst();
                    if (optional.isPresent()) {
                        BaseKoalOrg baseKoalOrg = optional.get();
                        orgName = baseKoalOrg.getName();
                    }
                    map.put("key",orgName);
                    map.put("doc_count", p.get("doc_count"));
                    Map<String,Object> levelMap = (Map<String, Object>) p.get("aggs_level");
                    List<Map<String, Object>> levelList = (List<Map<String, Object>>) levelMap.get("buckets");
                    if (CollectionUtils.isNotEmpty(levelList)) {
                        levelList.forEach(item -> {
                            item.put("key",transSecretLevel(item.get("key").toString()));
                        });
                    }
                    map.put("buckets",levelList);
                    orgList.add(map);
                });
            }
        } catch (Exception e){
            log.error("打印文件次数按部门统计查询异常", e);
        }
        page.addAll(orgList);
        return page;
    }

    @Override
    public Page queryAsset(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("weblogin-audit-*,client-logininout-*,terminal-login-*,adm-operate-*,specialudisk-use-*," +
                "changestrategy-audit-*,print-audit-*,file-audit-*,mb-log-*,process-audit-*,service-audit-*,software-audit-*,share-audit-*," +
                "hardware-audit-*,client-offline-*,omms-log-*,performance-audit-*,device-startorshut-*,net-virus-*,attack-audit-*,sem-log-*," +
                "violationoutreach-log-*,sa-log-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}},{\"term\":{\"op_type\":\"0\"}}]}}," +
                "\"aggs\":{\"aggs_company\":{\"terms\":{\"field\":\"report_company_name\"}," +
                "\"aggs\":{\"aggs_type\":{\"terms\":{\"field\":\"report_dev_type\"}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> productCompanyList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("aggs_company")).get("buckets");
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(p -> {
                    Map<String, Object> map = new HashMap<>();
                    String companyName = transCompany(p.get("key").toString());
                    Map<String,Object> typeMap = (Map<String, Object>) p.get("aggs_type");
                    List<Map<String, Object>> typeList = (List<Map<String, Object>>) typeMap.get("buckets");
                    if (CollectionUtils.isNotEmpty(typeList)) {
                        for (Map<String,Object> type : typeList) {
                            String typeName = transProduct(type.get("key").toString());
                            String name = companyName + typeName;
                            if (StringUtils.isEmpty(name)) {
                                name = "未知";
                            }
                            map.put("key",name);
                            map.put("doc_count",type.get("doc_count"));
                        }
                    }
                    productCompanyList.add(map);
                });
            }
        } catch (Exception e){
            log.error("数据入库按资产统计查询异常", e);
        }
        page.addAll(productCompanyList);
        return page;
    }

    @Override
    public Page queryByLogType(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("weblogin-audit-*,client-logininout-*,terminal-login-*,adm-operate-*,specialudisk-use-*," +
                "changestrategy-audit-*,print-audit-*,file-audit-*,mb-log-*,process-audit-*,service-audit-*,software-audit-*,share-audit-*," +
                "hardware-audit-*,client-offline-*,omms-log-*,performance-audit-*,device-startorshut-*,net-virus-*,attack-audit-*,sem-log-*," +
                "violationoutreach-log-*,sa-log-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}]}},\"aggs\":{\"aggs_type\":" +
                "{\"terms\":{\"field\":\"report_log_type\",\"size\":\"100\"}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> logTypeList = new ArrayList<>();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("aggs_type")).get("buckets");
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key",transLogType(p.get("key").toString()));
                    map.put("doc_count",p.get("doc_count"));
                    logTypeList.add(map);
                });
            }
        } catch (Exception e){
            log.error("数据入库按日志类型统计查询异常", e);
        }
        page.addAll(logTypeList);
        return page;
    }

    @Override
    public Page queryTrend(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<String> indexList = Arrays.asList("weblogin-audit-*,client-logininout-*,terminal-login-*,adm-operate-*,specialudisk-use-*," +
                "changestrategy-audit-*,print-audit-*,file-audit-*,mb-log-*,process-audit-*,service-audit-*,software-audit-*,share-audit-*," +
                "hardware-audit-*,client-offline-*,omms-log-*,performance-audit-*,device-startorshut-*,net-virus-*,attack-audit-*,sem-log-*," +
                "violationoutreach-log-*,sa-log-*");
        String interval = this.timeRangeCompute(esSearchQuery.getStartTime(),esSearchQuery.getEndTime());
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}]}},\"aggs\":{\"data\"" +
                ":{\"date_histogram\":{\"field\":\"event_time\",\"interval\":\"" + interval + "\"}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data")).get("buckets");
            page.addAll(dataList);
        } catch (Exception e){
            log.error("数据入库趋势统计查询异常", e);
        }
        return page;
    }

    @Override
    public Page queryVirusSysStatus(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        Map<String, Object> resultMap = new HashMap<>();

        List<String> indexOperateList = Arrays.asList("adm-operate-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}," +
                "{\"match\":{\"data_source\":\"瑞星防病毒\"}},{\"match\":{\"user_type\":\"升级\"}},{\"wildcard\":{\"op_description\":\"病毒库版本*\"}}]}}}";
        String res = searchService.searchGlobalContent(indexOperateList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            resultMap.put("upgradeNum", ((Map<String, Object>)((Map<String, Object>)resMap.get("hits")).get("total")).get("value"));
        } catch (Exception e){
            log.error("病毒库升级次数统计异常", e);
        }

        List<String> indexVirusList = Arrays.asList("net-virus-*");
        queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}]}}," +
                "\"aggregations\":{\"dev_num\":{\"cardinality\":{\"field\":\"dev_ip\"}}}}";
        res = searchService.searchGlobalContent(indexVirusList, queryJsonStr);
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            resultMap.put("infectNum", ((Map<String, Object>)((Map<String, Object>)resMap.get("hits")).get("total")).get("value"));
            resultMap.put("infectDevNum", ((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("dev_num")).get("value"));
        } catch (Exception e){
            log.error("病毒上报次数统计异常", e);
        }

        List<Map<String, Object>> resList = new ArrayList<>();
        resList.add(resultMap);
        page.addAll(resList);
        return page;
    }

    @Override
    public Page queryVirusTop(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<Map<String, Object>> resList = new ArrayList<>();

        List<String> indexList = Arrays.asList("net-virus-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}]}}," +
                "\"aggregations\":{\"virus_top\":{\"terms\":{\"field\":\"virus_name\",\"size\":10," +
                "\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false," +
                "\"order\":[{\"_count\":\"desc\"}]}," +
                "\"aggregations\":{\"dev_num\":{\"cardinality\":{\"field\":\"dev_ip\"}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("virus_top")).get("buckets");
            Map<String, Object> resultMap;
            for (Map<String, Object> dataMap : dataList) {
                resultMap = new HashMap<>();
                resultMap.put("name", dataMap.get("key"));
                resultMap.put("reportNum", dataMap.get("doc_count"));
                resultMap.put("devNum", ((Map<String, Object>)dataMap.get("dev_num")).get("value"));
                resList.add(resultMap);
            }
        } catch (Exception e){
            log.error("病毒上报次数前十统计异常", e);
        }

        page.addAll(resList);
        return page;
    }

    @Override
    public Page queryVirusDeal(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<Map<String, Object>> resList = new ArrayList<>();

        List<String> indexList = Arrays.asList("net-virus-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}]}}," +
                "\"aggregations\":{\"virus_deal\":{\"terms\":{\"field\":\"virus_result\",\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("virus_deal")).get("buckets");
            Map<String, Object> resultMap;
            for (Map<String, Object> dataMap : dataList) {
                resultMap = new HashMap<>();
                resultMap.put("dealType", dataMap.get("key"));
                resultMap.put("dealNum", dataMap.get("doc_count"));
                resList.add(resultMap);
            }

        } catch (Exception e){
            log.error("病毒处理情况统计异常", e);
        }

        page.addAll(resList);
        return page;
    }

    @Override
    public Page queryVirusDetail(EsSearchQuery esSearchQuery) {
        Page page = new Page();
        List<Map<String, Object>> resList = new ArrayList<>();

        List<String> indexList = Arrays.asList("net-virus-*");
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}]}}," +
                "\"aggregations\":{\"virus_detail\":{\"terms\":{\"field\":\"virus_name\"," +
                "\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false}," +
                "\"aggregations\":{\"first_data\":{\"top_hits\":{\"size\":1,\"_source\":[\"virus_type\",\"virus_result\"]}}}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("virus_detail")).get("buckets");
            Map<String, Object> resultMap;
            for (Map<String, Object> dataMap : dataList) {
                resultMap = new HashMap<>();
                resultMap.put("name", dataMap.get("key"));
                resultMap.put("reportNum", dataMap.get("doc_count"));

                List<Map<String, Object>> firstData = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)dataMap.get("first_data")).get("hits")).get("hits");
                if (firstData != null && firstData.size() > 0) {
                    Map<String, Object> data = (Map<String, Object>)firstData.get(0).get("_source");
                    resultMap.put("type", data.get("virus_type"));
                    resultMap.put("deal", data.get("virus_result"));
                }

                resList.add(resultMap);
            }

        } catch (Exception e){
            log.error("病毒处理详情统计异常", e);
        }

        page.addAll(resList);
        return page;
    }

    @Override
    public Map<String, Object> queryChangeInfo(EsSearchQuery esSearchQuery) {
        List<Map<String, Object>> resList = new ArrayList<>();
        Map<String, Object> dataSumMap = new HashMap<>();

        List<String> indexList = Arrays.asList("changestrategy-audit-*");
        String queryJsonStr = "{\"from\":0,\"size\":10000,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"from\":\"" + esSearchQuery.getStartTime() + "\",\"to\":\"" + esSearchQuery.getEndTime() + "\"," +
                "\"format\":\"yyyy-MM-dd HH:mm:ss\",\"time_zone\":\"+08:00\"}}}," +
                "{\"match\":{\"report_dev_type\":\"" + esSearchQuery.getFieldName() + "\"}}]}}," +
                "\"aggregations\":{\"dev_num\":{\"cardinality\":{\"field\":\"dev_ip\"}}}}";
        String res = searchService.searchGlobalContent(indexList, queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>)(((Map<String, Object>)resMap.get("hits")).get("hits"));

            dataSumMap.put("changeNum", ((Map<String, Object>)((Map<String, Object>)resMap.get("hits")).get("total")).get("value"));
            dataSumMap.put("devNum", ((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("dev_num")).get("value"));

            Map<String, String> codeToNameMap = baseKoalOrgService.findAll().stream().collect(Collectors.toMap(BaseKoalOrg::getCode, p -> p.getName(),(k1, k2) -> k1));
            Map<String, Object> resultMap;
            for (Map<String, Object> dataMap : dataList) {
                resultMap = new HashMap<>();
                Map<String, Object> sourceMap = (Map<String, Object>)dataMap.get("_source");
                resultMap.put("time", TimeTools.utc2Local(sourceMap.get("event_time")));
                resultMap.put("type", sourceMap.get("op_description"));
                resultMap.put("ip", sourceMap.get("dev_ip"));
                resultMap.put("org", codeToNameMap.get(sourceMap.get("std_org_code").toString()));
                resList.add(resultMap);
            }
        } catch (Exception e){
            log.error("统计变更信息异常", e);
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("code","0");
        resMap.put("message","成功");
        resMap.put("data", dataSumMap);
        resMap.put("list", resList);

        return resMap;
    }

    /**
     * 时间区间计算
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String timeRangeCompute(String startTime, String endTime) {
        long seconds = Math.abs((TimeTools.toDate(endTime, TimeTools.GMT_PTN).getTime() - TimeTools.toDate(startTime, TimeTools.GMT_PTN).getTime()) / 1000);
        if (seconds < GROUP_NUM_CONST) {
            return (int) seconds + "s";
        }
        long minutes = seconds / 60;
        if (minutes < GROUP_NUM_CONST) {
            return (int) (seconds / GROUP_NUM_CONST) + "s";
        }
        long hours = minutes / 60;
        if (hours < GROUP_NUM_CONST) {
            return (int) (minutes / GROUP_NUM_CONST) + "m";
        }
        long days = hours / 24;
        if (days < GROUP_NUM_CONST) {
            return (int) (hours / GROUP_NUM_CONST) + "h";
        }
        if (days > 366) {
            return "1M";
        }
        return (int) (days / GROUP_NUM_CONST) + "d";
    }

    private String transSecretLevel(String secretLevel) {
        switch (secretLevel) {
            case "0":
                return "绝密";
            case "1":
                return "机密";
            case "2":
                return "秘密";
            case "3":
                return "内部";
            case "4":
                return "非密";
            default:
                return "未知";
        }
    }

    private String transCompany(String companyCode) {
        switch (companyCode) {
            case "RC01":
                return "北信源";
            case "RC02":
                return "中孚";
            case "RC03":
                return "卫士通";
            case "RC04":
                return "东软";
            case "RC05":
                return "天融信";
            case "RC06":
                return "启明星辰";
            case "RC07":
                return "深信服";
            case "RC08":
                return "绿盟";
            default:
                return companyCode;
        }
    }

    private String transProduct(String type) {
        switch (type) {
            case "RD01":
                return "流量探针";
            case "RD02":
                return "主审";
            case "RD03":
                return "运管";
            case "RD04":
                return "三合一";
            case "RD05":
                return "打刻";
            case "RD06":
                return "mb";
            case "RD07":
                return "终端登录";
            case "RD08":
                return "防火墙";
            case "RD09":
                return "入侵检测";
            case "RD10":
                return "准入";
            case "RD11":
                return "防病毒";
            case "RD12":
                return "漏扫";
            case "RD13":
                return "OA";
            case "RD14":
                return "邮件系统";
            case "RD15":
                return "其它应用系统";
            case "RD16":
                return "服审";
            case "RD17":
                return "电子文档";
            case "RD18":
                return "隐写";
            default:
                return type;
        }
    }

    private String transLogType(String logType) {
        switch (logType) {
            case "DT022":
                return "web登录";
            case "DT023":
                return "客户端登录日志";
            case "DT008":
                return "终端登录日志";
            case "DT024":
                return "用户操作日志";
            case "DT025":
                return "U盘使用日志";
            case "DT026":
                return "策略变更";
            case "DT005":
                return "打印、刻录日志";
            case "DT002":
                return "文件监控";
            case "DT007":
                return "文件密级操作日志";
            case "DT003":
                return "进程日志";
            case "DT027":
                return "服务审计";
            case "DT004":
                return "软件日志";
            case "DT028":
                return "共享审计";
            case "DT029":
                return "硬件变更审计";
            case "DT030":
                return "客户端不在线";
            case "DT001":
                return "服务器不在线日志";
            case "DT035":
                return "系统性能审计";
            case "DT031":
                return "设备开关机日志";
            case "DT009":
                return "病毒上报日志";
            case "DT034":
                return "网络攻击日志";
            case "DT010":
                return "违规介质操作日志";
            case "DT032":
                return "违规外联";
            case "DT033":
                return "主机异常告警日志";
            case "DT012":
                return "流量-TCP协议";
            case "DT013":
                return "流量-UDP协议";
            case "DT014":
                return "流量-HTTP协议";
            case "DT015":
                return "流量-DNS协议";
            case "DT016":
                return "流量-邮件协议";
            case "DT017":
                return "流量-数据库协议";
            case "DT018":
                return "流量-SSL解密协议";
            case "DT019":
                return "流量-文件传输协议";
            case "DT020":
                return "流量-登录行为";
            case "DT021":
                return "流量-应用文件";
            default:
                return logType;
        }
    }
}
