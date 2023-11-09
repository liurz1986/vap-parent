package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

@Table(name = "log_statistics")
@ApiModel("日志统计对象")
public class LogStatisticsModel {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 类别
     */
    @Column(name = "category")
    private String category;

    /**
     * 区域名称
     */
    @Column(name = "area_name")
    private String areaName;


    /**
     * 渠道
     */
    @Column(name = "channel")

    private String channel;

    /**
     * 来源位置
     */
    @Column(name = "source_position")
    private String sourcePosition;


    /**
     * 来源厂商
     */
    @Column(name = "source_company")
    private String sourceCopmany;

    /**
     * 来源IP
     */
    @Column(name = "source_ip")
    private String sourceIp;

    /**
     * 数据大小
     */
    @Column(name = "storage_size")
    private Long storageSize;

    /**
     * 数据条数
     */
    @Column(name = "storage_count")
    private Long storageCount;

    /**
     * 日期
     */
    @Column(name = "storage_date")
    private String storageDate;


    /**
     * 日志分类编号
     */
    @Column(name = "category_number")
    private String categoryNumber;

    /**
     * 日志子类别编号
     */
    @Column(name = "sub_category_number")
    private String subCategoryNumber;

    /**
     * 日志小类别编号
     */
    @Column(name = "small_category_number")
    private String smallCategoryNumber;
    /**
     * 日志分类名称
     */
    @Column(name = "category_name")
    private String categoryName;

    /**
     * 日志子类别名称
     */
    @Column(name = "sub_category_name")
    private String subCategoryName;

    /**
     * 日志小类别名称
     */
    @Column(name = "small_category_name")
    private String smallCategoryName;

    /**
     * 区域编码
     */
    @Column(name = "area_code")
    private String areaCode;

    /**
     * 日志小类别表名称
     */
    @Column(name = "small_category_tablename")
    private String smallCategoryTablename;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    public String getSourceCopmany() {
        return sourceCopmany;
    }

    public void setSourceCopmany(String sourceCopmany) {
        this.sourceCopmany = sourceCopmany;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

    public Long getStorageCount() {
        return storageCount;
    }

    public void setStorageCount(Long storageCount) {
        this.storageCount = storageCount;
    }

    public String getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(String storageDate) {
        this.storageDate = storageDate;
    }

    public String getCategoryNumber() {
        return categoryNumber;
    }

    public void setCategoryNumber(String categoryNumber) {
        this.categoryNumber = categoryNumber;
    }

    public String getSubCategoryNumber() {
        return subCategoryNumber;
    }

    public void setSubCategoryNumber(String subCategoryNumber) {
        this.subCategoryNumber = subCategoryNumber;
    }

    public String getSmallCategoryNumber() {
        return smallCategoryNumber;
    }

    public void setSmallCategoryNumber(String smallCategoryNumber) {
        this.smallCategoryNumber = smallCategoryNumber;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getSmallCategoryTablename() {
        return smallCategoryTablename;
    }

    public void setSmallCategoryTablename(String smallCategoryTablename) {
        this.smallCategoryTablename = smallCategoryTablename;
    }
}