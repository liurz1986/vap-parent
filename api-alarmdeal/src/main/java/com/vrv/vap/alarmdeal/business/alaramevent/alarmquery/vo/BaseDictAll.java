package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年9月25日 下午5:37:47
 * 类说明：字典表
 */
@Table(name="base_dict_all")
public class BaseDictAll {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name="code")
	private String code;
	@Column(name="code_value")
	private String codeValue;
	@Column(name="type")
	private String type;
	@Column(name="parent_type")
	private String parentType;
	@Column(name="leaf")
	private String leaf;
	@Column(name="description")
	private String description;
	@Column(name="create_id")
	private String createId;
	@Column(name="create_time")
	private String createTime;
	@Column(name="update_id")
	private String updateId;
	@Column(name="update_time")
	private String updateTime;
	@Column(name="sort")
	private String sort;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	public String getLeaf() {
		return leaf;
	}
	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreateId() {
		return createId;
	}
	public void setCreateId(String creatId) {
		this.createId = creatId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateId() {
		return updateId;
	}
	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
}
