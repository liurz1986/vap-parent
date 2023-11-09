package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * 索引实体类
 *
 * @author lilang
 * @date 2018年2月2日
 */

@Table(name = "discover_index")
@ApiModel("索引对象")
public class DiscoverIndex {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 索引id
     */
    @Column(name = "index_id")
    @ApiModelProperty("索引id")
    private String indexid;

    /**
     * 索引名称
     */
    @Column(name = "index_name")
    @ApiModelProperty("索引名称")
    private String indexname;

    /**
     * 索引类型
     */
    @Column(name = "type")
    @ApiModelProperty("索引类型")
    private String type;

    /**
     * 索引名称
     */
    @Column(name = "title")
    @ApiModelProperty("索引名称")
    private String title;

    /**
     * 索引描述
     */
    @Column(name = "title_desc")
    @ApiModelProperty("索引描述")
    private String titledesc;

    /**
     * 时间字段
     */
    @Column(name = "time_field_name")
    @ApiModelProperty("时间字段")
    private String timefieldname;

    /**
     * 是否默认索引 1：是，0：否
     */
    @Column(name = "default_index")
    @ApiModelProperty("是否默认索引 1：是，0：否")
    private Integer defaultindex;

    /**
     * 索引字段
     */
    @Column(name = "index_fields")
    @ApiModelProperty("索引字段")
    private String indexfields;


    /**
     * 类别
     */
    @Column(name = "category")
    @ApiModelProperty("类别")
    private String  category;

    /**
     * 安全域字段
     */
    @ApiModelProperty("安全域字段")
    @Column(name = "domain_field_name")
    private String domainFieldName;


    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取索引id
     *
     * @return indexId - 索引id
     */
    public String getIndexid() {
        return indexid;
    }

    /**
     * 设置索引id
     *
     * @param indexid 索引id
     */
    public void setIndexid(String indexid) {
        this.indexid = indexid;
    }

    /**
     * 获取索引名称
     *
     * @return indexName - 索引名称
     */
    public String getIndexname() {
        return indexname;
    }

    /**
     * 设置索引名称
     *
     * @param indexname 索引名称
     */
    public void setIndexname(String indexname) {
        this.indexname = indexname;
    }

    /**
     * 获取索引类型
     *
     * @return type - 索引类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置索引类型
     *
     * @param type 索引类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取索引名称
     *
     * @return title - 索引名称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置索引名称
     *
     * @param title 索引名称
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取索引描述
     *
     * @return titleDesc - 索引描述
     */
    public String getTitledesc() {
        return titledesc;
    }

    /**
     * 设置索引描述
     *
     * @param titledesc 索引描述
     */
    public void setTitledesc(String titledesc) {
        this.titledesc = titledesc;
    }

    /**
     * 获取时间字段
     *
     * @return timeFieldName - 时间字段
     */
    public String getTimefieldname() {
        return timefieldname;
    }

    /**
     * 设置时间字段
     *
     * @param timefieldname 时间字段
     */
    public void setTimefieldname(String timefieldname) {
        this.timefieldname = timefieldname;
    }

    /**
     * 获取是否默认索引 1：是，0：否
     *
     * @return defaultIndex - 是否默认索引 1：是，0：否
     */
    public Integer getDefaultindex() {
        return defaultindex;
    }

    /**
     * 设置是否默认索引 1：是，0：否
     *
     * @param defaultindex 是否默认索引 1：是，0：否
     */
    public void setDefaultindex(Integer defaultindex) {
        this.defaultindex = defaultindex;
    }

    /**
     * 获取索引字段
     *
     * @return indexFields - 索引字段
     */
    public String getIndexfields() {
        return indexfields;
    }

    /**
     * 设置索引字段
     *
     * @param indexfields 索引字段
     */
    public void setIndexfields(String indexfields) {
        this.indexfields = indexfields;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDomainFieldName() {
        return domainFieldName;
    }

    public void setDomainFieldName(String domainFieldName) {
        this.domainFieldName = domainFieldName;
    }
}
