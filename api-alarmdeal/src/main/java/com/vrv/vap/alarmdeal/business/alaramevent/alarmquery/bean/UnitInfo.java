package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 单位信息
 * 
 * @author sj100d
 *
 */
@Data
public class UnitInfo {
	// 地区名称
	@EsField("unit_geo_name")
	@ApiModelProperty(value = "地区名称")
	String unitGeoName;
	
	
	// 单位名称
	@EsField("unit_name")
	@ApiModelProperty(value = "单位名称")
	String unitName;
	// 单位标识
	@EsField("unit_ident")
	@ApiModelProperty(value = "单位标识")
	String unitIdent;
	
	
	
	// 部门名称
	@EsField("unit_depart_name")
	@ApiModelProperty(value = "部门名称")
	String unitDepartName;
	// 部门标识
	@EsField("unit_geo_ident")
	@ApiModelProperty(value = "部门标识")
	String unitGeoIdent;
	
	@ApiModelProperty(value = "部门subCode")
	String unitDepartSubCode;
	
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.unitGeoIdent); 
        char[] charArr = sb.toString().toCharArray();
        int hash = 0;
        for(char c : charArr) {
            hash = hash * 131 + c;
        }
        return hash;
    }
}