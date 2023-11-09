package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.vo.QueryModel;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.time.ZoneId;
import java.util.*;

/**
 * 查询工具
 * Created by lizj on 2020/5/20.
 */
public class QueryTools {
    /**
     * 简单聚合
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> simpleAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null);
        agg.field(aggField).size(size);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合（结果带排行）
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> simpleAgg2(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null);
        agg.field(aggField).size(size);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        int id = 1;
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    for (Map<String, Object> aggItem : aggItems) {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put("id", id);
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        result.add(tmp);
                        id += 1;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+去重（结果带排行）
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param cardinalityAggField
     * @param size
     * @param keyField
     * @param valueField
     * @param cardinalityValueField
     * @return
     */
    public static List<Map<String, Object>> simpleCardinalityAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, String cardinalityAggField, int size, String keyField, String valueField, String cardinalityValueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null);
        agg.field(aggField).size(size);
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(2000);
        agg.subAggregation(cardinalityAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        int id = 1;
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    for (Map<String, Object> aggItem : aggItems) {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put("id", id);
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        tmp.put(cardinalityValueField, ((Map<String, Double>) aggItem.get("cardinalityAgg")).get("value"));
                        result.add(tmp);
                        id += 1;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合,返回map
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyField
     * @param valueField
     * @return
     */
    public static Map<String, Object> simpleAggToMap(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField) {
        Map<String, Object> result = new HashMap<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null);
        agg.field(aggField).size(size);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        result.put((String) aggItem.get("key"), aggItem.get("doc_count"));
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合2
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param codeField
     * @param nameField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> simpleAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String codeField, String nameField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null);
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>)((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(nameField, row.get(nameField));
                        tmp.put(codeField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+加和
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param sumField
     * @param codeField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> simpleSumAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String sumField, String codeField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null).order(BucketOrder.aggregation("sumAgg",false));
        agg.field(aggField).size(size);
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumField);
        queryModel.setAggregationBuilder(agg.subAggregation(sumAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(codeField, aggItem.get("key"));
                        Map<String, Object> sumMap = (Map<String, Object>) aggItem.get("sumAgg");
                        tmp.put(valueField, sumMap.get("value"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 聚合+平均数+排序
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param avgField
     * @param codeField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> simpleAvgAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String avgField, String codeField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null).order(BucketOrder.aggregation("avgAgg",false));
        agg.field(aggField).size(size);
        AvgAggregationBuilder avgAgg = AggregationBuilders.avg("avgAgg").field(avgField);
        queryModel.setAggregationBuilder(agg.subAggregation(avgAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(codeField, aggItem.get("key"));
                        Map<String, Object> avgMap = (Map<String, Object>) aggItem.get("avgAgg");
                        tmp.put(valueField, avgMap.get("value"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 聚合+Max
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param maxField
     * @param codeField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> simpleMaxAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String aggField, int size, String maxField, String codeField, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg", null).order(BucketOrder.aggregation("maxAgg",false));
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1).sort(maxField, SortOrder.DESC));
        }
        MaxAggregationBuilder maxAgg = AggregationBuilders.max("maxAgg").field(maxField);
        queryModel.setAggregationBuilder(agg.subAggregation(maxAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(codeField, aggItem.get("key"));
                        Map<String, Object> avgMap = (Map<String, Object>) aggItem.get("maxAgg");
                        tmp.put(valueField, avgMap.get("value"));
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                            tmp.put(topHitFields[i], row.get(topHitFields[i]));
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单时间分桶统计
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField
     * @param interval
     * @param dateFormat
     * @param offset       时区偏移量 8代表数据里是utc时间  0代表数据里是北京数据
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> dateAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(ZoneId.of("Asia/Shanghai"));
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        String date = aggItem.get("key_as_string").toString();
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, date);
                        tmp.put(valueField, aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单时间分桶统计+加和
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField
     * @param interval
     * @param dateFormat
     * @param offset       时区偏移量 8代表数据里是utc时间  0代表数据里是北京数据
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> dateSumAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String sumField, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumField);
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(ZoneId.of("Asia/Shanghai"));
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg.subAggregation(sumAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        String date = aggItem.get("key_as_string").toString();
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, date);
                        Map<String, Object> sumMap = (Map<String, Object>) aggItem.get("sumAgg");
                        tmp.put(valueField, sumMap.get("value"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单时间分桶统计
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField
     * @param interval
     * @param dateFormat
     * @param offset       时区偏移量 8代表数据里是utc时间  0代表数据里是北京数据
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> dateAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String keyField, String valueField, int size, BucketOrder order) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(ZoneId.of("Asia/Shanghai"));
        dateAgg.format(dateFormat);
        dateAgg.order(order);
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    for (Map<String, Object> aggItem : aggItems) {
                        String date = aggItem.get("key_as_string").toString();
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, date);
                        tmp.put(valueField, aggItem.get("doc_count"));
                        result.add(tmp);
                        if (result.size() == size) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 两级聚合
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> twoLevelAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg", null);
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg", null);
        secondAgg.field(twoAggField).size(secondAggSize);
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(oneAggField, aggItem.get("key"));
                                    tmp.put(twoAggField, item.get("key"));
                                    tmp.put(valueField, item.get("doc_count"));
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 时间分桶+去重个数统计
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField        时间分桶字段
     * @param interval            时间间隔
     * @param dateFormat          时间格式
     * @param offset              时区偏移量 8代表数据里是utc时间  0代表数据里是北京数据
     * @param cardinalityAggField 去重统计字段
     * @param keyField            返回的keyField
     * @param valueField          返回的valueField
     * @return
     */

    public static List<Map<String, Object>> cardinalityAndDateAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String cardinalityAggField, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(ZoneId.of("Asia/Shanghai"));
        dateAgg.format(dateFormat);
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(2000);
        dateAgg.subAggregation(cardinalityAgg);
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        String date = aggItem.get("key_as_string").toString();
                        Map ca = (Map) aggItem.get("cardinalityAgg");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, date);
                        tmp.put(valueField, ca.get("value"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }


    /**
     * 简单去重统计
     *
     * @param queryModel
     * @param wrapper
     * @param cardinalityAggField 去重统计的字段
     * @param threshold           精度
     * @param valueField          返回的key
     * @return
     */
    public static Map<String, Object> simpleCardinalityAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String cardinalityAggField, int threshold, String valueField) {
        Map<String, Object> result = new HashMap<>();
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(threshold);
        queryModel.setAggregationBuilder(cardinalityAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> response = wrapper.getAggResponse4Map(queryModel);
        if (response != null && response.containsKey("aggregations")) {
            Map<String, Object> aggMap = (Map<String, Object>) response.get("aggregations");
            if (aggMap != null && aggMap.containsKey("cardinalityAgg")) {
                Map ca = (Map) aggMap.get("cardinalityAgg");
                result.put(valueField, ca.get("value"));
            }
        }
        return result;
    }

    /**
     * 简单去重统计
     *
     * @param queryModel
     * @param wrapper
     * @param cardinalityAggField 去重统计的字段
     * @param threshold           精度
     * @return
     */
    public static int simpleCardinalityAgg(QueryModel queryModel, ES7Tools.QueryWrapper wrapper, String cardinalityAggField, int threshold) {
        int result = 0;
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(threshold);
        queryModel.setAggregationBuilder(cardinalityAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> response = wrapper.getAggResponse4Map(queryModel);
        if (response != null && response.containsKey("aggregations")) {
            Map<String, Object> aggMap = (Map<String, Object>) response.get("aggregations");
            if (aggMap != null && aggMap.containsKey("cardinalityAgg")) {
                Map ca = (Map) aggMap.get("cardinalityAgg");
                result = (int) ca.get("value");
            }
        }
        return result;
    }

    /**
     * 获取总数
     * @param model
     * @param wrapper
     */
    public static long getCount(QueryModel model, ES7Tools.QueryWrapper wrapper) {
        long result = 0;
        SearchResponse response = wrapper.getSearchResponse(model);
        if (response != null) {
            result = response.getHits().getTotalHits().value;
        }
        return result;
    }

    /**
     * 简单加和
     * @param model
     * @param wrapper
     */
    public static long getSimpleSum(QueryModel model, String sumField, ES7Tools.QueryWrapper wrapper) {
        long result = 0;
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumField);
        model.setAggregationBuilder(sumAgg);
        model.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse4Map(model);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("sumAgg")) {
                Map<String, Object> sumMap = (Map<String, Object>) dataAggMap.get("sumAgg");
                if (sumMap != null && sumMap.get("value") != null) {
                    double sumValue = (double) sumMap.get("value");
                    result = new Double(sumValue).longValue();
                }
            }
        }
        return result;
    }

    /**
     * 构建通用model
     * @param startTime
     * @param endTime
     * @param index
     * @param timeField
     * @return
     */
    public static QueryModel buildCommonQueryModel(Date startTime, Date endTime, String index, String timeField) {
        QueryModel model = new QueryModel();
        if (null == startTime) {
            startTime = TimeTools.getNowBeforeByDay(6);
        }
        if (null == endTime) {
            endTime = TimeTools.getNowBeforeByDay2(0);
        }
        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setIndexName(index);
        model.setIndexNames(ES7Tools.getIndexNames(model));
        model.setTimeField(timeField);
        model.setQueryBuilder(new BoolQueryBuilder());
        model.setUseTimeRange(true);
        return model;
    }

    /**
     * 构建通用model
     * @param startTime
     * @param endTime
     * @param index
     * @param timeField
     * @return
     */
    public static QueryModel buildCommonQueryModel2(Date startTime, Date endTime, String index, String timeField) {
        QueryModel model = new QueryModel();
        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setIndexName(index);
        model.setIndexNames(ES7Tools.getIndexNames(model));
        model.setTimeField(timeField);
        model.setQueryBuilder(new BoolQueryBuilder());
        model.setUseTimeRange(true);
        return model;
    }


}
