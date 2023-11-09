package com.vrv.vap.admin.vo;

import java.util.List;


/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年9月25日 下午6:52:34
 * 类说明：BaseDictAllTreeVO
 */
public class BaseDictAllTreeVO{
	
	private Integer id;
	private String code;
	private String codeValue;
	private String type;
	private String parentType;
	private String leaf;
	private String description;
	private String creatId;
	private String createTime;
	private String updateId;
	private String updateTime;
	private String sort;
	private List<BaseDictAllTreeVO> children;
	private boolean isLeaf;
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
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
	public String getCreatId() {
		return creatId;
	}
	public void setCreatId(String creatId) {
		this.creatId = creatId;
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
	public List<BaseDictAllTreeVO> getChildren() {
		return children;
	}
	public void setChildren(List<BaseDictAllTreeVO> children) {
		this.children = children;
	}
//	@Override
//	public String getKey() {
//		return type;
//	}
//	@Override
//	public void setKey(String id) {
//		this.type=id;
//	}
//	@Override
//	public String getParentId() {
//		return parentType;
//	}
//	@Override
//	public void setParentId(String parentId) {
//		this.parentType=parentId;
//		
//	}
	
}
