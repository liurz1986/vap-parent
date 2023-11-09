package com.vrv.vap.monitor.es.compatible;

import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.InternalOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 适配es5.5.3 terms聚合
 */
public class TermsAggregationFit5 {
    private static final Logger log = LoggerFactory.getLogger(TermsAggregationFit5.class);

    /**
     * 去除默认的_key排序
     *
     * @param userid
     */
    public static void suit5(TermsAggregationBuilder userid) {
        InternalOrder.CompoundOrder order = (InternalOrder.CompoundOrder) userid.order();
        try {
            Field orderElements = order.getClass().getDeclaredField("orderElements");
            orderElements.setAccessible(true);
            List<BucketOrder> obj = (List<BucketOrder>) orderElements.get(order);
            obj.remove(1);
        } catch (Exception e) {
            log.error("TermsAggregationBuilder", e);
        }
    }

    public static void suitTime5(DateHistogramAggregationBuilder userid) {
        Field field = null;
        try {
            field = userid.getClass().getDeclaredField("order");
            field.setAccessible(true);
            field.set(userid, BucketOrder.count(false));
        } catch (Exception e) {
            log.error("suitTime5", e);
        }
    }
}
