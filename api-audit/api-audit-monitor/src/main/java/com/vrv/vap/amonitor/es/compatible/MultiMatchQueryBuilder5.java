package com.vrv.vap.amonitor.es.compatible;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * Created by lil on 2019/4/25.
 */
public class MultiMatchQueryBuilder5 extends MultiMatchQueryBuilder {

    public MultiMatchQueryBuilder5(Object value, String... fields) {
        super(value, fields);
    }

    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {

        builder.startObject(NAME);
        builder.field("query", super.value());
        builder.startArray("fields");
        for (Map.Entry<String, Float> fieldEntry : super.fields().entrySet()) {
            builder.value(fieldEntry.getKey() + "^" + fieldEntry.getValue());
        }
        builder.endArray();
        builder.field("type", "best_fields");
        builder.field("operator", "or");
        builder.endObject();

    }

}
