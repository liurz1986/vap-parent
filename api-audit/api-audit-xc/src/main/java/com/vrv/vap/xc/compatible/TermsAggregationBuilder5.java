//package com.vrv.vap.xc.compatible;
//
//import org.elasticsearch.search.aggregations.BucketOrder;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.aggregations.support.ValueType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Field;
//
///**
// * es5使用
// *
// * @author xw
// * @date 2018年10月11日
// */
//public class TermsAggregationBuilder5 extends TermsAggregationBuilder {
//    private static final Logger log = LoggerFactory.getLogger(TermsAggregationBuilder5.class);
//
//    public TermsAggregationBuilder5(String name, ValueType valueType) {
//        super(name, valueType);
//        try {
//            Field field = TermsAggregationBuilder.class.getDeclaredField("order");
//            field.setAccessible(true);
//            field.set(this, BucketOrder.count(false));
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//            log.error("TermsAggregationBuilder5", e);
//        }
//    }
//
//    public void setAggOrder(BucketOrder bucketOrder) {
//        try {
//            Field field = TermsAggregationBuilder.class.getDeclaredField("order");
//            field.setAccessible(true);
//            field.set(this, bucketOrder);
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//            log.error("setAggOrder", e);
//        }
//    }
//}
