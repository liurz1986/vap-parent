package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("索引查询对象")
public class IndexQuery extends Query {
    @ApiModelProperty("索引")
    public String index;
    @ApiModelProperty("类型")
    public String type;
    @ApiModelProperty("主键")
    public Integer id;
    @ApiModelProperty("索引id")
    public String indexid;
    @QueryLike
    @ApiModelProperty("标题")
    public String title;
    @QueryLike
    @ApiModelProperty("标题描述")
    public String titledesc;
    @QueryLike
    @ApiModelProperty("时间字段名称")
    public String timefieldname;
    @ApiModelProperty("是否默认索引")
    public Integer defaultindex;
    @ApiModelProperty("索引字段")
    public String indexfields;
    @ApiModelProperty("过滤类型")
    public String filterType;
    @ApiModelProperty("索引类型")
    public String category;
    @ApiModelProperty("组类别")
    public Integer grouptype;
    @ApiModelProperty("组id")
    public Integer parentid;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndexid() {
        return indexid;
    }

    public void setIndexid(String id) {
        this.indexid = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitledesc() { return titledesc; }

    public void setTitledesc(String titledesc) { this.titledesc = titledesc; }

    public String getTimefieldname() { return timefieldname; }

    public void setTimefieldname(String timefieldname) { this.timefieldname = timefieldname; }

    public Integer getDefaultindex() { return defaultindex; }

    public void setDefaultindex(int defaultindex) { this.defaultindex = defaultindex; }

    public String getIndexfields() { return indexfields; }

    public void setIndexfields(String indexfields) { this.indexfields = indexfields; }

    public String getFilterType() { return filterType; }

    public void setFilterType(String filterType) { this.filterType = filterType; }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public void setDefaultindex(Integer defaultindex) {
        this.defaultindex = defaultindex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getGrouptype() {
        return grouptype;
    }

    public void setGrouptype(Integer grouptype) {
        this.grouptype = grouptype;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }
}
