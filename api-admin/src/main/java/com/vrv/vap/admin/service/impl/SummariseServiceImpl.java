package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.model.BaseSecurityDomain;
import com.vrv.vap.admin.service.BaseSecurityDomainService;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.common.properties.SummariseConfig;
import com.vrv.vap.admin.mapper.SummariseMapper;
import com.vrv.vap.admin.model.DiscoverSummarise;
import com.vrv.vap.admin.service.SearchService;
import com.vrv.vap.admin.service.SummariseService;
import com.vrv.vap.admin.vo.QueryModel;
import com.vrv.vap.admin.vo.SummariseSearchQuery;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lilang
 * @date 2020/7/21
 * @description
 */
@Service
@Transactional
public class SummariseServiceImpl extends BaseServiceImpl<DiscoverSummarise> implements SummariseService {

    private static final Logger log = LoggerFactory.getLogger(SummariseServiceImpl.class);

    @Resource
    SummariseMapper summariseMapper;

    @Resource
    SearchService searchService;

    @Resource
    SummariseConfig summariseConfig;

    @Autowired
    private BaseSecurityDomainService baseSecurityDomainService;

    @Override
    public void saveSummarise(DiscoverSummarise discoverSummarise) {
        String fields = discoverSummarise.getIndexFields();
        if (StringUtils.isNotEmpty(fields)) {
            JSONArray jArray = new JSONArray();
            JSONArray jsonArray = JSONArray.fromObject(fields);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                JSONObject field = jsonArray.getJSONObject(i);
                jsonObject.put("name",field.get("name"));
                jsonObject.put("nameDesc",field.get("nameDesc"));
                jsonObject.put("type",field.get("type"));
                jsonObject.put("format",field.get("format"));
                jsonObject.put("displayed",field.get("displayed"));
                jsonObject.put("sort",field.get("sort") != null ? field.get("sort") : 0);
                jsonObject.put("size",field.get("size") != null ? field.get("size") : 0);
                jArray.add(jsonObject);
            }
            discoverSummarise.setIndexFields(jArray.toString());
        }
        summariseMapper.insert(discoverSummarise);
    }

    @Override
    public Map<String,Object> searchContent(SummariseSearchQuery summariseSearchQuery) {
        Map<String,Object> result = new HashMap<>();
        List<Map<String, Object>> datas = new ArrayList<>(10);
        String indexId = summariseSearchQuery.getIndexId();
        String date = TimeTools.formatDate(new Date(),"yyyy-MM-dd");
        String startTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        List<String> indexList = searchService.queryIndexListByTime(indexId,startTime,endTime);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        QueryModel queryModel = new QueryModel();
        queryModel.setStart_(0);
        queryModel.setCount_(summariseConfig.getTotal());
        queryModel.setTimeField(summariseSearchQuery.getTimeFieldName());
        queryModel.setStartTime(TimeTools.toDate(startTime,TimeTools.GMT_PTN));
        queryModel.setEndTime(TimeTools.toDate(endTime,TimeTools.GMT_PTN));
        queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{summariseSearchQuery.getTimeFieldName()});
        queryModel.setSortOrder(SortOrder.DESC);
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(true);
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (response != null && response.getHits() != null) {
            SearchHits searchHits = response.getHits();
            if (searchHits != null) {
                for (SearchHit hit : searchHits) {
                    Map<String, Object> tmpData = addUnitAndApp(hit.getSourceAsString());
//                    tmpData.put("_source", addUnitAndApp(hit.getSourceAsString()));
//                    tmpData.put("_index", hit.getIndex());
                    datas.add(tmpData);
                }
            }
            result.put("list",datas);
            result.put("total",datas.size());
            return result;
        }
        return null;
    }

    /**
     * 处理单位及应用
     * @param source
     * @return
     */
    public Map<String,Object> addUnitAndApp(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map sourceMap = objectMapper.readValue(source,Map.class);
            String netWay = (String) sourceMap.get("net_way");
            if (StringUtils.isEmpty(netWay) || "-1".equals(netWay) || "1".equals(netWay)) {
                sourceMap.put("unitName","");
                sourceMap.put("appName","");
            } else if ("0".equals(netWay)) {
                String srcArea = sourceMap.get("src_area") != null ? (String) sourceMap.get("src_area") : "";
                String srcAppId = sourceMap.get("src_app_id") != null ? (String) sourceMap.get("src_app_id") : "";
                sourceMap.put("unitName",getUnitName(srcArea));
                sourceMap.put("appName",srcAppId);
            } else if ("2".equals(netWay) || "3".equals(netWay)) {
                String dstArea = sourceMap.get("dst_area") != null ? (String) sourceMap.get("dst_area") : "";
                String dstAppId = sourceMap.get("dst_app_id") != null ? (String) sourceMap.get("dst_app_id") : "";
                sourceMap.put("unitName",getUnitName(dstArea));
                sourceMap.put("appName",dstAppId);
            } else {
                sourceMap.put("unitName","");
                sourceMap.put("appName","");
            }
            return sourceMap;
        } catch (Exception e) {
            log.error("",e);
        }
        return null;
    }

    /**
     * 获取单位名称
     * @param code
     * @return
     */
    private String getUnitName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        BaseSecurityDomain baseSecurityDomain = new BaseSecurityDomain();
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("code",code);
        List<BaseSecurityDomain> securityDomainList = baseSecurityDomainService.findByExample(example);
        if (CollectionUtils.isNotEmpty(securityDomainList)) {
            baseSecurityDomain = securityDomainList.get(0);
        }
        return baseSecurityDomain.getDomainName();
    }
}
