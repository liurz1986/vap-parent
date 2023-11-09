package com.vrv.vap.data.vo;

import java.util.List;

public class EsQueryResult {
    private Integer took;

    private String  scrollId;

    private Boolean timeOut;

    private Long total;

    private List<EsSourceVo> hits;

    public Integer getTook() {
        return took;
    }

    public void setTook(Integer took) {
        this.took = took;
    }

    public Boolean getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        this.timeOut = timeOut;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<EsSourceVo> getHits() {
        return hits;
    }

    public void setHits(List<EsSourceVo> hits) {
        this.hits = hits;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }
}
