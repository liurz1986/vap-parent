package com.vrv.vap.amonitor.es.compatible;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.joda.time.DateTimeZone;

import java.io.IOException;

/**
 * es2.0使用
 *
 * @author xw
 * @date 2018年7月24日
 */
public class DateHistogramAggregationBuilderEs2 extends DateHistogramAggregationBuilder {

    private long interval;
    private DateHistogramInterval dateHistogramInterval;
    private ExtendedBounds extendedBounds;
    private BucketOrder order = BucketOrder.key(true);
    private boolean keyed = true;
    private long minDocCount = 0;

    public DateHistogramAggregationBuilderEs2(String name) {
        super("date_histogram#" + name);
    }

    @Override
    public DateHistogramAggregationBuilder interval(long interval) {
        this.interval = interval;
        return this;
    }

    @Override
    public DateHistogramAggregationBuilder dateHistogramInterval(DateHistogramInterval dateHistogramInterval) {
        this.dateHistogramInterval = dateHistogramInterval;
        return this;
    }

    @Override
    public DateHistogramAggregationBuilder timeZone(DateTimeZone timeZone) {
        super.timeZone(timeZone);
        return this;
    }

    @Override
    protected XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        if (dateHistogramInterval == null) {
            builder.field(Histogram.INTERVAL_FIELD.getPreferredName(), interval);
        } else {
            builder.field(Histogram.INTERVAL_FIELD.getPreferredName(), dateHistogramInterval.toString());
        }

        if (order != null) {
            builder.field(Histogram.ORDER_FIELD.getPreferredName());
            order.toXContent(builder, params);
        }

        builder.field(Histogram.KEYED_FIELD.getPreferredName(), keyed);

        builder.field(Histogram.MIN_DOC_COUNT_FIELD.getPreferredName(), minDocCount);

        if (extendedBounds != null) {
            extendedBounds.toXContent(builder, params);
        }

        return builder;
    }

}
