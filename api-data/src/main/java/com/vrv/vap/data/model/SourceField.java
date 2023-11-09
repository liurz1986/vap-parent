package com.vrv.vap.data.model;

import com.vrv.vap.data.constant.FieldLibrary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

@Table(name = "data_source_field")
@ApiModel(value = "数据源字段")
public class SourceField {

    public SourceField() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "source_id")
    @ApiModelProperty("数据源ID")
    private Integer sourceId;

    @ApiModelProperty("字段名")
    private String field;

    @Column(name = "`name`")
    @ApiModelProperty("字段标题")
    private String name;

    /**
     * 支持：keyword text long double date object json
     */
    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("字段原始类型")
    private String origin;

    @ApiModelProperty("字典")
    private String dict;

    /**
     * person / device / app (单机版不支持)
     */
    @ApiModelProperty("链接类型")
    private String link;

    @ApiModelProperty("数量格式")
    private String unit;

    @Column(name = "`show`")
    @ApiModelProperty("是否显示")
    private Boolean show;

    @ApiModelProperty("是否可以按此字段排序")
    private Boolean sorter;

    @ApiModelProperty("是否过滤")
    private Boolean filter;

    @ApiModelProperty("是否标签")
    private Boolean tag;

    @ApiModelProperty("排序")
    private Short sort;

    @ApiModelProperty("拓展字段json")
    @Column(name = "extend_conf")
    private String extendConf;

    @ApiModelProperty("字段别名")
    private String alias;

    @Ignore
    @ApiModelProperty("分析事件排序")
    @Column(name = "analysis_sort")
    private Integer analysisSort;

    @ApiModelProperty("分析事件字段类型")
    @Column(name = "analysis_type")
    private String analysisType;

    @ApiModelProperty("分析事件字段类型长度")
    @Column(name = "analysis_type_length")
    private Integer analysisTypeLength;

    @ApiModelProperty("分析字段是否显示")
    @Column(name = "analysis_show")
    private Integer analysisShow;

    @ApiModelProperty("权限标记")
    @Column(name = "auth_mark")
    private int authMark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public Boolean getSorter() {
        return sorter;
    }

    public void setSorter(Boolean sorter) {
        this.sorter = sorter;
    }

    public Boolean getFilter() {
        return filter;
    }


    public void setFilter(Boolean filter) {
        this.filter = filter;
    }

    public Boolean getTag() {
        return tag;
    }


    public void setTag(Boolean tag) {
        this.tag = tag;
    }


    public Short getSort() {
        return sort;
    }

    public void setSort(Short sort) {
        this.sort = sort;
    }

    public String getExtendConf() {
        return extendConf;
    }

    public void setExtendConf(String extendConf) {
        this.extendConf = extendConf;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public Integer getAnalysisShow() {
        return analysisShow;
    }

    public void setAnalysisShow(Integer analysisShow) {
        this.analysisShow = analysisShow;
    }

    public int getAuthMark() {
        return authMark;
    }

    public void setAuthMark(int authMark) {
        this.authMark = authMark;
    }

    public SourceField(int sourceId, String field, String origin, String type, short sort, String name) {
        this.setSourceId(sourceId);
        this.setField(field);
        this.setOrigin(origin);
        this.setType(type);
        this.setSort(sort);
        this.setShow(true);
        this.setSorter(true);
        this.setTag(false);
        this.setFilter(false);
        if (name == null) {
            this.setName(FieldLibrary.getFieldName(field));
        } else {
            if (name.length() > 16) {
                this.setName(name.substring(0, 16));
            } else {
                this.setName(name);
            }
        }
        if ("text".equals(type) || "keyword".equals(type)) {
//            this.setType("keyword");
            this.setFilter(true);
            this.setTag(true);
            this.setSorter(false);
        }

    }
}