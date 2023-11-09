package com.vrv.vap.amonitor.es.compatible;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;

import java.io.IOException;

/**
 * Created by lil on 2020/11/11.
 */
public class MatchQueryBuilder5 extends MatchQueryBuilder {

    public MatchQueryBuilder5(String fieldName, Object value) {
        super(fieldName, value);
    }

    public MatchQueryBuilder5(StreamInput in) throws IOException {
        super(in);
    }

    @Override
    public void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        builder.startObject(super.fieldName());

        builder.field(QUERY_FIELD.getPreferredName(), super.value());
        builder.field(OPERATOR_FIELD.getPreferredName(), super.operator().toString());
        printBoostAndQueryName(builder);
        builder.endObject();
        builder.endObject();
    }
}
