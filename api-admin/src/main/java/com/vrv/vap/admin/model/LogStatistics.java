package com.vrv.vap.admin.model;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年8月30日 上午9:50:55
 * 类说明：Log Statistics
 */
@Table(name="log_statistics")
public class LogStatistics{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name="category")
	private String category;
	@Column(name="area_name")
	private String areaName;
	@Column(name="channel")
	private String channel;
	@Column(name="source_position")
	private String sourcePosition;
	@Column(name="source_company")
	private String sourceCompany;
	@Column(name="source_ip")
	private String sourceIp;
	@Column(name="storage_size")
	private BigInteger storageSize;
	@Column(name="storage_count")
	private BigInteger storageCount;
	@Column(name="storage_date")
	private String storageDate;
	@Column(name="category_number")
	private String categoryNumber;
	@Column(name="sub_category_number")
	private String subCategoryNumber;
	@Column(name="small_category_number")
	private String smallCategoryNumber;
	@Column(name="category_name")
	private String categoryName;
	@Column(name="sub_category_name")
	private String subCategoryName;
	@Column(name="small_category_name")
	private String smallCategoryName;
	@Column(name="area_code")
	private String areaCode;
	@Column(name="small_category_tablename")
	private String smallCategoryTablename;
	
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSmallCategoryTablename() {
		return smallCategoryTablename;
	}
	public void setSmallCategoryTablename(String smallCategoryTablename) {
		this.smallCategoryTablename = smallCategoryTablename;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getSourceCompany() {
		return sourceCompany;
	}
	public void setSourceCompany(String sourceCompany) {
		this.sourceCompany = sourceCompany;
	}
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public BigInteger getStorageSize() {
		return storageSize;
	}
	public void setStorageSize(BigInteger storageSize) {
		this.storageSize = storageSize;
	}
	public BigInteger getStorageCount() {
		return storageCount;
	}
	public void setStorageCount(BigInteger storageCount) {
		this.storageCount = storageCount;
	}
	public String getStorageDate() {
		return storageDate;
	}
	public void setStorageDate(String storageDate) {
		this.storageDate = storageDate;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
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
	
	
}
