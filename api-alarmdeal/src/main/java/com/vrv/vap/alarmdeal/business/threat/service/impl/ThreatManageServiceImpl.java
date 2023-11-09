package com.vrv.vap.alarmdeal.business.threat.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.threat.bean.ThreatManage;
import com.vrv.vap.alarmdeal.business.threat.bean.VulInfoVo;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.ThreatTimeReq;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.ThreatVulRes;
import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskInfoRes;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskRes;
import com.vrv.vap.alarmdeal.business.threat.service.ThreatManageService;
import com.vrv.vap.alarmdeal.frameworks.feign.WeakFegin;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Administrator
 * @since: 2022/8/29 16:06
 * @description:
 */
@Service
public class ThreatManageServiceImpl implements ThreatManageService {

    @Autowired
    private AssetService assetService;

    @Autowired
    private WeakFegin weakFegin;

    @Autowired
    private ElasticSearchRestClientService threatManageServiceEs;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;

    @Override
    public AssetRiskRes getAssetRisk(ThreatReq param) {
        AssetRiskRes result = new AssetRiskRes();
        List<QueryCondition> conditionsAsset = new ArrayList<>();
        conditionsAsset.add(QueryCondition.notNull("ip"));
        List<Asset> assets = assetService.findAll(conditionsAsset);
        List<String> ips = assets.stream().map(Asset::getIp).collect(Collectors.toList());
        // 查询威胁值
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.ge("insertTime", param.getStartTime()));
        conditions.add(QueryCondition_ES.le("insertTime", param.getEndTime()));
        if(StringUtils.isNotBlank(param.getOrgCode())){
            ips.clear();
            ips = assets.stream().filter(item->param.getOrgCode().equals(item.getOrgCode())).map(Asset::getIp).collect(Collectors.toList());
            conditions.add(QueryCondition_ES.eq("orgCode",param.getOrgCode()));
        }
        conditions.add(QueryCondition_ES.in("ip",ips));
        Map<String,Long> threatGroupByIp = getCountGroupByFieldSum(threatManageServiceEs.getIndexName(),"ip","threatValue",conditions);

        int threatSums = 0;
        for(Map.Entry<String,Long> entry : threatGroupByIp.entrySet()){
            threatSums += entry.getValue();
        }

        result.setThreatTotal(getAvg(threatSums, ips.size()));

        // 通过时间与IP，获取脆弱性
        Map<String,Object> vulParam = new HashMap<>();
//        vulParam.put("startTime",param.getStartTime());
//        vulParam.put("endTime",param.getEndTime());
        vulParam.put("ips",ips);

        Result<VulInfoVo> vuls =weakFegin.getAssetsTotalVulValue(vulParam);
        VulInfoVo vulInfoVo = vuls.getData();
        result.setVulTotal(getAvg(vulInfoVo.getTotalVulValue(), ips.size()));
        // 通过IP获取权重值
        Long weights = assetService.getAssetWeightByIp(ips);
        result.setAssetWorth(getAvg(Integer.valueOf(String.valueOf(weights)),ips.size()));

        // 计算最大值范围
        // 计算资产权重最大
        result.setMaxAssetWorth(5);

        // 计算威胁最大范围
        result.setMaxThreatTotal(5);

        // 计算脆弱性最大范围
        result.setMaxVulTotal(5);
        return result;
    }

    public double getAvg(int nums,int size){
        BigDecimal a1 = new BigDecimal(nums);
        BigDecimal b1 = new BigDecimal(size);
        BigDecimal bd = a1.divide(b1,BigDecimal.ROUND_CEILING).setScale(2, RoundingMode.HALF_UP);
        return  bd.doubleValue();
    }

    /**
     * 威胁资产
     * @param threatReq
     * @return
     */
    @Override
    public List<AssetRiskInfoRes> getAssetThreatInfo(ThreatReq threatReq, Integer top) {
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.ge("insertTime", threatReq.getStartTime()));
        conditions.add(QueryCondition_ES.le("insertTime", threatReq.getEndTime()));
        if(StringUtils.isNotBlank(threatReq.getOrgCode())){
            conditions.add(QueryCondition_ES.eq("orgCode",threatReq.getOrgCode()));
        }
        Map<String,Long> threatGroupByIp = getCountGroupByFieldSum(threatManageServiceEs.getIndexName(),"ip","threatValue",conditions);
        Map<String, Long> result1 = threatGroupByIp.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        List<AssetRiskInfoRes> result = new ArrayList<>();
        result1.forEach((ip,riskConut)->{
            AssetRiskInfoRes assetRiskInfoRes = new AssetRiskInfoRes();
            assetRiskInfoRes.setIp(ip);
            List<QueryCondition> assetConditions = new ArrayList<>();
            assetConditions.add(QueryCondition.eq("ip",ip));
            List<Asset> assets = assetService.findAll(assetConditions);
            if(CollectionUtils.isNotEmpty(assets)){
                Asset asset = assets.get(0);
                assetRiskInfoRes.setUserName(asset.getResponsibleName());
                assetRiskInfoRes.setDeptName(asset.getOrgName());
            }
            assetRiskInfoRes.setWeight(String.valueOf(riskConut));
            result.add(assetRiskInfoRes);
        });

        return result;
    }

    @Override
    public List<ThreatManage> getThreatData(Map<String, Object> param) {
        String ip = String.valueOf(param.get("ip"));
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.eq("ip",ip));

        // 获取最大的数据时间
        Map<String,Long> map = alarmEventManagementForESService.getCountGroupByField(threatManageServiceEs.getIndexName(),"insertTime",new ArrayList<>());
        List<String> timeList = new ArrayList<>();
        map.forEach((key,value)->{
            timeList.add(key);
        });

        if(CollectionUtils.isNotEmpty(timeList)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Collections.sort(timeList, (s1, s2) -> LocalDateTime.parse(String.valueOf(s1), formatter).
                    compareTo(LocalDateTime.parse(String.valueOf(s2), formatter)));
            String time = DateUtil.parseUTC(timeList.get(0));
            conditions.add(QueryCondition_ES.eq("insertTime", time));
        }
        List<ThreatManage> result = threatManageServiceEs.findAll(conditions);
        return result;
    }

    @Override
    public List<ThreatManage> getThreatDataByIp(Map<String, Object> param) {
        List<QueryCondition_ES> conditionEs = new ArrayList<>();
        conditionEs.add(QueryCondition_ES.eq("ip",String.valueOf(param.get("ip"))));
        conditionEs.add(QueryCondition_ES.ge("insertTime",String.valueOf(param.get("startTime"))));
        conditionEs.add(QueryCondition_ES.le("insertTime",String.valueOf(param.get("endTime"))));
        return threatManageServiceEs.findAll(conditionEs);
    }

    /**
     *通过时间和ip查询威胁与漏洞
     * @param threatTimeReq
     * @return
     */
    @Override
    public ThreatVulRes getThreatDataByIpTimes(ThreatTimeReq threatTimeReq) {
        ThreatVulRes result = new ThreatVulRes();
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.ge("insertTime",threatTimeReq.getStartTime()));
        conditions.add(QueryCondition_ES.le("insertTime",threatTimeReq.getEndTime()));
        conditions.add(QueryCondition_ES.in("ip",threatTimeReq.getIps()));
        if(StringUtils.isNotBlank(threatTimeReq.getOrgCode())){
            conditions.add(QueryCondition_ES.eq("orgCode",threatTimeReq.getOrgCode()));
        }
        Map<String,Long> threatMap = alarmEventManagementForESService.getCountGroupByField(threatManageServiceEs.getIndexName(),"threatGuid",conditions);
        result.setThreatCount(threatMap.size());

        Map<String,Long> vulMap = alarmEventManagementForESService.getCountGroupByField(threatManageServiceEs.getIndexName(),"vulGuid",conditions);
        result.setVulCount(vulMap.size());

        Map<String,Long> ipMap = alarmEventManagementForESService.getCountGroupByField(threatManageServiceEs.getIndexName(),"ip",conditions);
        result.setIpCount(ipMap.size());
        return result;
    }

    @Override
    public List<String> getAssetThreatMsg(ThreatTimeReq threatTimeReq) {
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.ge("insertTime",threatTimeReq.getStartTime()));
        conditions.add(QueryCondition_ES.le("insertTime",threatTimeReq.getEndTime()));
        if(StringUtils.isNotBlank(threatTimeReq.getOrgCode())){
            conditions.add(QueryCondition_ES.eq("orgCode",threatTimeReq.getOrgCode()));
        }
        Map<String,Long> threatMap = getCountGroupByFieldSum(threatManageServiceEs.getIndexName(),"threatName","threatValue",conditions);
        Map<String, Long> threatSortValue = threatMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        List<String> result = new ArrayList<>();
        threatSortValue.forEach((name,value)->{
            result.add(name);
        });
        return result;
    }

    public Map<String, Long> getCountGroupByFieldSum(String indexName, String fieldName,String sumFieldName, List<QueryCondition_ES> conditions) {
        Map<String,Long> result = new HashMap<>();
        // 设置分组字段
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.notNull(fieldName));
        querys.addAll(conditions);
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        result.putAll(queryEsForAgg(indexName, fieldName,sumFieldName, queryBuilder));
        return result;
    }

    private Map<String, Long> queryEsForAgg(String indexName, String fieldName, String sumField,QueryBuilder queryBuilder) {
        Map<String, Long> result = new HashMap<>();
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field(fieldName);
        Script queryScript = AlarmDealUtil.initScript(new String[]{fieldName});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(1000);
        if(StringUtils.isNotBlank(sumField)){
            AggregationBuilder sum_riskValue = AggregationBuilders.sum("sum_field")
                    .field(sumField);
            groupByFieldAgg.subAggregation(sum_riskValue);
        }
        groupByFieldAgg.subAggregation(fieldCounts);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    String id = terms.getBuckets().get(i).getKey().toString();
                    Aggregation childtermsLevel = terms.getBuckets().get(i).getAggregations().get("sum_field");
                    String sumRiskValue = ((org.elasticsearch.search.aggregations.metrics.ParsedSum) childtermsLevel)
                            .getValueAsString();
                    BigDecimal bg = new BigDecimal(sumRiskValue);
                    long nums = bg.setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
                    result.put(id, Long.valueOf(nums));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }
}
