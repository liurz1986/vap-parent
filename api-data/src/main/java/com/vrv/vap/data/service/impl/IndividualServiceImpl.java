package com.vrv.vap.data.service.impl;

import cn.hutool.json.JSONUtil;
import com.vrv.vap.data.component.ConvertElastic;
import com.vrv.vap.data.component.ESTools;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.service.ContentService;
import com.vrv.vap.data.service.IndividualService;
import com.vrv.vap.data.service.SourceService;
import com.vrv.vap.data.vo.CommonRequest;
import com.vrv.vap.data.vo.CommonResponse;
import com.vrv.vap.data.vo.ElasticParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class IndividualServiceImpl implements IndividualService {

    @Autowired
    SourceService sourceService;

    @Autowired
    private ESTools esTools;

    @Autowired
    private ConvertElastic convertElastic;

    @Autowired
    ContentService contentService;

    @Override
    public Long query24Total(CommonRequest query) {
        long total = 0;
        List<Source> sourceList = sourceService.findAll();
        for (Source source : sourceList) {
            if (StringUtils.isEmpty(source.getTimeField())) continue;
            String indexName = source.getName();
            if (indexName.endsWith("-*")) {
                indexName = indexName.substring(0, indexName.length() - 2);
            } else if (indexName.endsWith("*")) {
                indexName = indexName.substring(0, indexName.length() - 1);
            }

            LinkedHashSet<String> indexes = esTools.getIndexesByTime(indexName, query.getStartTime(), query.getEndTime());

            CommonResponse param = new CommonResponse();
            param.setSegment(indexes);
            param.setQuery(convertElastic.buildQueryParam(query, source.getTimeField(), source.getDomainField()));

            CommonResponse result = contentService.elasticTotal(param);
            total += result.getTotalAcc();
        }
        return total;
    }

    @Override
    public Map query24Trend(CommonRequest query) {
        Map<String, Object> resMap = new HashMap<>();
        long total = 0;
        List<Source> sourceList = sourceService.findAll();
        for (Source source : sourceList) {
            if (StringUtils.isEmpty(source.getTimeField())) continue;
            String indexName = source.getName();
            if (indexName.endsWith("-*")) {
                indexName = indexName.substring(0, indexName.length() - 2);
            } else if (indexName.endsWith("*")) {
                indexName = indexName.substring(0, indexName.length() - 1);
            }

            LinkedHashSet<String> indexes = esTools.getIndexesByTime(indexName, query.getStartTime(), query.getEndTime());

            ElasticParam param = new ElasticParam();
            param.setIndex(indexes.toArray(new String[indexes.size()]));
            String queryJson = "{\"from\":0,\"size\":0,\"query\":"
                    + convertElastic.buildQueryParam(query, source.getTimeField(), source.getDomainField())
                    + ",\"aggs\":{\"data\":" + convertElastic.aggTimeField(source.getTimeField(), query.getStartTime(), query.getEndTime(), "1h")
                    + "}}";
            param.setQuery(queryJson);

            String result = contentService.elasticSearch(param);
            Map<String, Object> resultMap = JSONUtil.parseObj(result);
            if (resultMap.containsKey("code")) continue;

            total += Long.valueOf(((Map<String, Object>)((Map<String, Object>)resultMap.get("hits")).get("total")).get("value").toString());

            List<Map<String, Object>> bucketMap = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resultMap.get("aggregations")).get("data")).get("buckets");
            bucketMap.stream().forEach(p -> {
                String key = p.get("key_as_string").toString();
                if (resMap.containsKey(key)) {
                    resMap.put(key, Long.valueOf(resMap.get(key).toString()) + Long.valueOf(p.get("doc_count").toString()));
                } else {
                    resMap.put(key, p.get("doc_count"));
                }
            });
        }

        resMap.put("totalCount", total);
        return resMap;
    }
}
