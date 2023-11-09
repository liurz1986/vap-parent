package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;

/**
 * 索引字段实体类
 * 
 * @author lilang
 * @date 2018年2月2日
 */
@ApiModel("索引字段对象")
public class DiscoverIndexField {

	private String name;

	private String nameDesc;

	private String type;

	private String format;

	private int count;

	private boolean scripted;

	private String script;

	private String lang;

	private boolean displayed;

	private boolean filtered;

	private boolean indexed;

	private boolean analyzed;

	private boolean doc_values;

	private String entityId;

	private String linkType;

	private String tag;

	private Integer sort;

	private Integer size;

	private String unit;

	private String jsonFields;

	private String detailed;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameDesc() {
		return nameDesc;
	}

	public void setNameDesc(String nameDesc) {
		this.nameDesc = nameDesc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isScripted() {
		return scripted;
	}

	public void setScripted(boolean scripted) {
		this.scripted = scripted;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public boolean isAnalyzed() {
		return analyzed;
	}

	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	public boolean isDoc_values() {
		return doc_values;
	}

	public void setDoc_values(boolean doc_values) {
		this.doc_values = doc_values;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}


	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getJsonFields() {
		return jsonFields;
	}

	public void setJsonFields(String jsonFields) {
		this.jsonFields = jsonFields;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getDetailed() {
		return detailed;
	}

	public void setDetailed(String detailed) {
		this.detailed = detailed;
	}
}
