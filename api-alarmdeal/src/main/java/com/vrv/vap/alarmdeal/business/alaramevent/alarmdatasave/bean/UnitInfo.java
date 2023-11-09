package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 单位信息
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitInfo {
	/**
	 * 地区名称
	 */
	@SerializedName(value = "unit_geo_name",alternate = {"unitGeoName"})
	private String unit_geo_name;
	/**
	 * 单位名称
	 */
	@SerializedName(value = "unit_name",alternate = {"unitName"})
	private String unit_name;
	/**
	 * 单位标识
	 */
	@SerializedName(value = "unit_ident",alternate = {"unitIdent"})
	private String unit_ident;
	/**
	 * 部门名称
	 */
	@SerializedName(value = "unit_depart_name",alternate = {"unitDepartName"})
	private String unit_depart_name;
	/**
	 * 部门标识
	 */
	@SerializedName(value = "unit_geo_ident",alternate = {"unitGeoIdent"})
	private String unit_geo_ident;
}
