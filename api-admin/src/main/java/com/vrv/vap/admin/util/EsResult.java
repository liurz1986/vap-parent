package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONObject;
import org.github.iamxwaa.elasticsearch.core.entry.SearchHit;

import java.util.Arrays;

public class EsResult {
    private long total;
    private SearchHit[] hits;
    private String scrollId;
    private boolean empty;
    private boolean timeOut;
    private long took;
    private JSONObject aggregations;

    public EsResult() {
    }

    public JSONObject getAggregations() {
        return aggregations;
    }

    public void setAggregations(JSONObject aggregations) {
        this.aggregations = aggregations;
    }

    public long getTotal() {
        return this.total;
    }

    public SearchHit[] getHits() {
        return this.hits;
    }

    public String getScrollId() {
        return this.scrollId;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean isTimeOut() {
        return this.timeOut;
    }

    public long getTook() {
        return this.took;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setHits(SearchHit[] hits) {
        this.hits = hits;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }

    public void setTook(long took) {
        this.took = took;
    }

}