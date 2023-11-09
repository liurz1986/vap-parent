package com.vrv.vap.line.model;

public class SourceField {

    private int sourceId;
    private String field;
    private String name;
    private String type;
    private String origin;
    private String dict;
    private String alias;
    private Integer analysisSort;
    private String analysisType;
    private Integer analysisTypeLength;
    private boolean show;
    private boolean sorter;
    private boolean filter;
    private boolean tag;


    public SourceField() {
        this.show = true;
        this.sorter = false;
        this.filter = true;
        this.tag = false;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isSorter() {
        return sorter;
    }

    public void setSorter(boolean sorter) {
        this.sorter = sorter;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public Integer getAnalysisSort() {
        return analysisSort;
    }

    public void setAnalysisSort(Integer analysisSort) {
        this.analysisSort = analysisSort;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public Integer getAnalysisTypeLength() {
        return analysisTypeLength;
    }

    public void setAnalysisTypeLength(Integer analysisTypeLength) {
        this.analysisTypeLength = analysisTypeLength;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
