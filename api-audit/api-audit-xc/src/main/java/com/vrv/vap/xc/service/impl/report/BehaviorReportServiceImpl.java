package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.BehaviorReportService;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BehaviorReportServiceImpl implements BehaviorReportService {
    private PageModel parseModel(ReportParam model){
        PageModel pageModel = new PageModel();
        pageModel.setMyStartTime(model.getStartTime());
        pageModel.setMyEndTime(model.getEndTime());
        return pageModel;
    }
    
    @Override
    public VList<Map<String,Object>> userLoginCount(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "terminal-login", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //判断数据为登录的
        query.must(QueryBuilders.termQuery("op_type", 1));
        query.must(QueryBuilders.termsQuery("std_user_type",userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "std_user_no", 10, "userNo","loginNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> userVisitOtherDev(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", "1,4".split(",")));
        query.must(QueryBuilders.termsQuery("src_std_user_type",userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_user_no", 10, "userNo","visitNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> beVisitDev(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("dst_std_dev_type_group", "0"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "dst_std_dev_ip", 10, "devIp","visitNum" , null);
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> userVisitSys(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("src_std_user_type", userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_user_no", 10, "userNo","visitNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> sysBeVisit(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), new String[]{"netflow-http"}, "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.mustNot(QueryBuilders.termQuery("dst_std_sys_name", ""));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "dst_std_sys_id", 10, "sysId","visitNum" , new String[]{"dst_std_sys_name"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> userVisitSameOrg(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("std_is_same_unit", "0"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_user_no", 10, "userNo","visitNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> sameOrgBeVisit(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), new String[]{"netflow-http"}, "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.mustNot(QueryBuilders.termQuery("dst_std_sys_name", ""));
        query.must(QueryBuilders.termQuery("std_is_same_unit", "0"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "dst_std_sys_id", 10, "sysId","visitNum" , new String[]{"dst_std_sys_name"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> otherDevVisitByUser(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", "1,4".split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_user_no", 10, "userNo","visitNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> otherDevBeVisit(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", "1,4".split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "dst_std_dev_ip", 10, "devIp","visitNum" , null);
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> fileImportByUser(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "file-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("file_dir", 2));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_user_no", "file_level", 10, 10, "count", "username".split(","));
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> fileImportByLevel(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "file-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("file_dir", 2));
        query.must(QueryBuilders.termsQuery("std_user_type", userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "file_level", 10, "fileLevel","num" , null);
        translateFileLevel(list,"fileLevel");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> printFileExportByUser(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "print-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("std_user_type", userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_user_no", "op_type", 10, 10, "count", "username".split(","));
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> printFileExportByUser2List(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "print-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("std_user_type", userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_user_no", "op_type", 10, 10, "count", "username,file_level".split(","));
        LinkedHashMap<String,Map<String,Object>> maps = new LinkedHashMap<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(l ->{
                String userNo = l.get("std_user_no").toString();
                String opType = l.get("op_type").toString();
                Object count = l.get("count");
                Map<String,Object> data = new HashMap<>();
                data.putAll(l);
                data.remove("count");
                if(maps.containsKey(userNo)){
                    data = maps.get(userNo);
                }
                if("0".equals(opType)){
                    data.put("printNum",count);
                }else{
                    data.put("burnNum",count);
                }
                maps.put(userNo,data);
            });
        }
        List<Map<String, Object>> collect = maps.values().stream().collect(Collectors.toList());
        translateFileLevel(collect,"file_level");
        return VoBuilder.vl(collect.size(),collect);
    }

    @Override
    public VList<Map<String, Object>> printFileCountByLevel(ReportParam model,String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "print-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("std_user_type", userType.split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "file_level", 10, "fileLevel","num" , null);
        translateFileLevel(list,"fileLevel");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> importFileCountByLevel(ReportParam model, String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-file", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termsQuery("src_std_user_type", userType.split(",")));
        query.must(QueryBuilders.termQuery("file_dir", "2"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "file_level", 10, "fileLevel","num" , null);
        translateFileLevel(list,"fileLevel");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> operationType(ReportParam model, String deviceType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //主机登录日志统计
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "operation-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("std_dev_type_group", deviceType));
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"operation_type",50,"type","count");
        translateOperationType(list,"type");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> adminVisitServerByUser(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("dst_std_dev_type_group", "1"));
        query.must(QueryBuilders.termsQuery("src_std_user_type", "1,2".split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_user_no", 10, "userNo","visitNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> safeDevBeVisit(ReportParam model, String userType) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("dst_std_dev_type_group", "2"));
        query.must(QueryBuilders.termsQuery("src_std_user_type", userType.split(",")));
        query.mustNot(QueryBuilders.termsQuery("dst_std_dev_type", ""));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "dst_std_dev_type", "src_std_user_no", 10000, 10000, "count", "username,src_std_org_name".split(","));
        Map<String,Map<String,Object>> datas = new HashMap<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(m ->{
                String devType = m.get("dst_std_dev_type").toString();
                String userNo = m.get("src_std_user_no").toString();
                String username = m.get("username").toString();
                String orgName = m.get("src_std_org_name").toString();
                int count = Integer.parseInt(m.get("count").toString());
                if(datas.containsKey(devType)){
                    Map<String, Object> item = datas.get(devType);
                    item.put("count",count+Integer.parseInt(item.get("count").toString()));
                    int max = Integer.parseInt(item.get("max").toString());
                    if(count > max){
                        item.put("max",count);
                        item.put("userName",username);
                        item.put("orgName",orgName);
                    }
                }else{
                    Map<String, Object> item = new HashMap<>();
                    item.put("devType",devType);
                    item.put("count",count);
                    item.put("max",count);
                    item.put("userName",username);
                    item.put("orgName",orgName);
                }
            });
        }
        List<Map<String, Object>> collect = datas.values().stream().collect(Collectors.toList());
        return VoBuilder.vl(collect.size(),collect);
    }

    @Override
    public VList<Map<String, Object>> adminVisitNetDevByUser(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, parseModel(model));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("dst_std_dev_type_group", "4"));
        query.must(QueryBuilders.termsQuery("src_std_user_type", "1,2".split(",")));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_user_no", 10, "userNo","visitNum" , new String[]{"username"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> appBeVisit(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-http", "event_time");
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "dst_std_sys_id", 10, "sysId","visitNum" , new String[]{"dst_std_sys_name","dst_std_sys_secret_level"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> signalNum(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-http", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.filter(QueryBuilders.scriptQuery(new Script("doc['src_std_sys_id'].value == doc['dst_std_sys_id'].value")));
        query.mustNot(QueryBuilders.termQuery("src_std_sys_id", ""));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_sys_id", 10, "sysId","visitNum" , new String[]{"src_std_sys_name,sip,sport,app_protocol"});
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> appFileByLevel(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"classification_level",50,"type","count");
        translateFileLevel(list,"type");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> appFileInfo(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_sys_id", "file_dir", 100, 10, "count", "std_sys_name".split(","));
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> appFileInfo2List(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_sys_id", "file_dir", 100, 10, "count", "std_sys_name".split(","));
        LinkedHashMap<String,Map<String,Object>> maps = new LinkedHashMap<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(l ->{
                String sysId = l.get("std_sys_id").toString();
                String fileDir = l.get("file_dir").toString();
                Object count = l.get("count");
                Map<String,Object> data = new HashMap<>();
                data.putAll(l);
                data.remove("count");
                if(maps.containsKey(sysId)){
                    data = maps.get(sysId);
                }
                if("1".equals(fileDir)){
                    data.put("importNum",count);
                }else{
                    data.put("downNum",count);
                }
                maps.put(sysId,data);
            });
        }
        List<Map<String, Object>> collect = maps.values().stream().collect(Collectors.toList());
        return VoBuilder.vl(collect.size(),collect);
    }

    @Override
    public VList<Map<String, Object>> appFileCountByOrg(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "dst_std_org_code", 10, "orgCode","num" , new String[]{"dst_std_org_name,classification_level"});
        translateFileLevel(list,"classificationLevel");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VList<Map<String, Object>> appFileCountBySys(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        List<Map<String, Object>> list = simpleAggAndTopHitByOrder(queryModel, wrapper, "src_std_sys_id", 10, "sysId","num" , new String[]{"src_std_sys_name,classification_level"});
        translateFileLevel(list,"classificationLevel");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VData<Map<String, Object>> sameinfo(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-http", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termQuery("visit_type", "2"));
        sd.should(QueryBuilders.termQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"dport",10000,"port","count");
        List<Map<String,Object>> list2 = QueryTools.simpleAgg(queryModel, wrapper,"app_protocol",10000,"pro","count");
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel,wrapper,"dip","sip",100,100,"count",new String[]{"src_std_org_code","dst_std_org_code"});
        Set<String> inips = new HashSet<>();
        Set<String> outips = new HashSet<>();
        for(Map<String, Object> m : maps){
            if(m.get("src_std_org_code") != null && StringUtils.isNotEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    inips.add(m.get("sip").toString());
                }
            }
            if(m.get("dst_std_org_code") != null && StringUtils.isNotEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    inips.add(m.get("dip").toString());
                }
            }
            //外部ip统计
            if(m.get("src_std_org_code") == null || StringUtils.isEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    outips.add(m.get("sip").toString());
                }
            }
            if(m.get("dst_std_org_code") == null || StringUtils.isEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    outips.add(m.get("dip").toString());
                }
            }
        }
        result.put("inips",inips.size());
        result.put("outips",outips.size());
        result.put("port",list.size());
        result.put("protocol",list2.size());
        return VoBuilder.vd(result);
    }

    @Override
    public VList<Map<String, Object>> inIpCount(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-http", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termQuery("visit_type", "2"));
        sd.should(QueryBuilders.termQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel,wrapper,"dip","sip",100,100,"count",new String[]{"src_std_org_code","dst_std_org_code"});
        Map<String,Map<String,Object>> datas = new HashMap<>();
        Set<String> outips = new HashSet<>();
        for(Map<String, Object> m : maps){
            //外部ip统计
            if(m.get("src_std_org_code") == null || StringUtils.isEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    outips.add(m.get("sip").toString());
                }
            }
            if(m.get("dst_std_org_code") == null || StringUtils.isEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    outips.add(m.get("dip").toString());
                }
            }
        }
        for(Map<String, Object> m : maps){
            if(m.get("src_std_org_code") != null && StringUtils.isNotEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    String ip = m.get("sip").toString();
                    String sip = m.get("dip").toString();
                    if(datas.containsKey(ip)){
                        Map<String, Object> d = datas.get(ip);
                        d.put("count",Integer.parseInt(datas.get(ip).get("count").toString())+1);
                    }else{
                        Map<String, Object> map = new HashMap<>();
                        map.put("ip",ip);
                        map.put("count",1);
                        datas.put(ip,map);
                    }
                    if(outips.contains(sip)){
                        int count = Integer.parseInt(m.get("count").toString());
                        Map<String, Object> d = datas.get(ip);
                        if(d.containsKey("maxOutCount")){
                            int max = Integer.parseInt(d.get("maxOutCount").toString());
                            if(count > max){
                               d.put("outIp",sip);
                               d.put("maxOutCount",count);
                            }
                        }else{
                            d.put("outIp",sip);
                            d.put("maxOutCount",count);
                        }
                    }
                }
            }
        }
        List<Map<String, Object>> collect = datas.values().stream().sorted(Comparator.comparing(r -> Integer.parseInt(r.get("count").toString())))
                .collect(Collectors.toList());
        Collections.reverse(collect);
        return VoBuilder.vl(collect.size(),collect);
    }

    @Override
    public VList<Map<String, Object>> inIpBeVisitCount(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-http", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termQuery("visit_type", "2"));
        sd.should(QueryBuilders.termQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel,wrapper,"dip","sip",100,100,"count",new String[]{"src_std_org_code","dst_std_org_code"});
        Map<String,Map<String,Object>> datas = new HashMap<>();
        Set<String> outips = new HashSet<>();
        for(Map<String, Object> m : maps){
            //外部ip统计
            if(m.get("src_std_org_code") == null || StringUtils.isEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    outips.add(m.get("sip").toString());
                }
            }
            if(m.get("dst_std_org_code") == null || StringUtils.isEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    outips.add(m.get("dip").toString());
                }
            }
        }
        for(Map<String, Object> m : maps){
            if(m.get("dst_std_org_code") != null && StringUtils.isNotEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    String ip = m.get("dip").toString();
                    String sip = m.get("sip").toString();
                    if(datas.containsKey(ip)){
                        Map<String, Object> d = datas.get(ip);
                        d.put("count",Integer.parseInt(datas.get(ip).get("count").toString())+1);
                    }else{
                        Map<String, Object> map = new HashMap<>();
                        map.put("ip",ip);
                        map.put("count",1);
                        datas.put(ip,map);
                    }
                    if(outips.contains(sip)){
                        int count = Integer.parseInt(m.get("count").toString());
                        Map<String, Object> d = datas.get(ip);
                        if(d.containsKey("maxOutCount")){
                            int max = Integer.parseInt(d.get("maxOutCount").toString());
                            if(count > max){
                               d.put("outIp",sip);
                               d.put("maxOutCount",count);
                            }
                        }else{
                            d.put("outIp",sip);
                            d.put("maxOutCount",count);
                        }
                    }
                }
            }
        }
        List<Map<String, Object>> collect = datas.values().stream().sorted(Comparator.comparing(r -> Integer.parseInt(r.get("count").toString())))
                .collect(Collectors.toList());
        Collections.reverse(collect);
        return VoBuilder.vl(collect.size(),collect);
    }

    @Override
    public VData<Map<String, Object>> fileOutInfo(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termQuery("visit_type", "2"));
        sd.should(QueryBuilders.termQuery("visit_type", "3"));
        inquery.must(sd);
        inquery.must(QueryBuilders.termsQuery("file_dir", "1"));
        queryModel.setQueryBuilder(inquery);
        //输入数量
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        long intotal = 0;
        if (response != null && response.getHits() != null) {
            intotal = response.getHits().getTotalHits().value;
        }
        BoolQueryBuilder outquery = new BoolQueryBuilder();
        outquery.must(sd);
        outquery.must(QueryBuilders.termsQuery("file_dir", "2"));
        queryModel.setQueryBuilder(outquery);
        SearchResponse response2 = wrapper.getSearchResponse(queryModel);
        long outtotal = 0;
        if (response2 != null && response2.getHits() != null) {
            outtotal = response2.getHits().getTotalHits().value;
        }
        Map<String,Object> data = new HashMap<>();
        data.put("intotal",intotal);
        data.put("outtotal",outtotal);
        return VoBuilder.vd(data);
    }

    @Override
    public VList<Map<String, Object>> sameFileCountByLevel(ReportParam model,String fileDir) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termQuery("visit_type", "2"));
        sd.should(QueryBuilders.termQuery("visit_type", "3"));
        inquery.must(sd);
        inquery.must(QueryBuilders.termsQuery("file_dir", fileDir));
        queryModel.setQueryBuilder(inquery);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"classification_level",50,"type","count");
        translateFileLevel(list,"type");
        return VoBuilder.vl(list.size(),list);
    }

    @Override
    public VData<Map<String, Object>> secretFileOutInfo(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        inquery.must(QueryBuilders.termsQuery("file_dir", "1"));
        queryModel.setQueryBuilder(inquery);
        //输入数量
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        long intotal = 0;
        if (response != null && response.getHits() != null) {
            intotal = response.getHits().getTotalHits().value;
        }
        BoolQueryBuilder outquery = new BoolQueryBuilder();
        outquery.must(QueryBuilders.termsQuery("file_dir", "2"));
        queryModel.setQueryBuilder(outquery);
        SearchResponse response2 = wrapper.getSearchResponse(queryModel);
        long outtotal = 0;
        if (response2 != null && response2.getHits() != null) {
            outtotal = response2.getHits().getTotalHits().value;
        }
        Map<String,Object> data = new HashMap<>();
        data.put("intotal",intotal);
        data.put("outtotal",outtotal);
        data.put("total",outtotal+intotal);
        return VoBuilder.vd(data);
    }

    @Override
    public VList<Map<String, Object>> secretFileCountByType(ReportParam model, String fileDir) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), "netflow-app-file", "event_time");
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        inquery.must(QueryBuilders.termsQuery("file_dir", fileDir));
        queryModel.setQueryBuilder(inquery);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"file_type",100,"type","count");
        return VoBuilder.vl(list.size(),list);
    }


    public List<Map<String, Object>> simpleAggAndTopHitByOrder(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                               String aggField, int size, String keyField, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.order(BucketOrder.count(false));
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("agg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
        if (!bucketsMap.containsKey("buckets")) return result;
        List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
        aggItems.forEach(aggItem -> {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(keyField, aggItem.get("key"));
            tmp.put(valueField, aggItem.get("doc_count"));
            if (topHitFields != null) {
                List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                for (String topHitField : topHitFields) {
                    tmp.put(CommonTools.underLineToCamel(topHitField), row.get(topHitField));
                }
            }
            result.add(tmp);
        });
        return result;
    }

    public static EsQueryModel buildQueryModelByNetflow(QueryTools.QueryWrapper wrapper, PageModel model) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(new String[]{"netflow-db","netflow-dns","netflow-email","netflow-http","netflow-tcp","netflow-udp"}

            , queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField("event_time");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public void translateFileLevel(List<Map<String, Object>> list,String key){
        Map<String,String> fileLevelMap = new HashMap<>();
        fileLevelMap.put("0","绝密");
        fileLevelMap.put("1","机密");
        fileLevelMap.put("2","秘密");
        fileLevelMap.put("3","内部");
        fileLevelMap.put("4","公开");
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(m ->{
                String levelkey = m.get(key).toString();
                String level = "未知";
                if(fileLevelMap.containsKey(levelkey)){
                    level = fileLevelMap.get(levelkey);
                }
                m.put("level",level);
            });
        }
    }

    public void translateOperationType(List<Map<String, Object>> list,String key){
        Map<String,String> fileLevelMap = new HashMap<>();
        fileLevelMap.put("0","本地");
        fileLevelMap.put("1","堡垒机");
        fileLevelMap.put("2","域运维");
        fileLevelMap.put("3","winscp");
        fileLevelMap.put("4","融一");
        fileLevelMap.put("5","融二");
        fileLevelMap.put("6","web");
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(m ->{
                String levelkey = m.get(key).toString();
                String level = "未知";
                if(fileLevelMap.containsKey(levelkey)){
                    level = fileLevelMap.get(levelkey);
                }
                m.put("operationType",level);
            });
        }
    }
}
