package com.vrv.vap.admin.vo.supervise.putdata;

import com.vrv.vap.admin.config.PutField;

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
	@PutField("unit_geo_name")
	@ApiModelProperty(value = "地区名称")
	String unitGeoName;
	
	
	// 单位名称
	@PutField("unit_name")
	@ApiModelProperty(value = "单位名称")
	String unitName;
	// 单位标识
	@PutField("unit_ident")
	@ApiModelProperty(value = "单位标识")
	String unitIdent;
	
	
	
	// 部门名称
	@PutField("unit_depart_name")
	@ApiModelProperty(value = "部门名称")
	String unitDepartName;
	// 部门标识
	@PutField("unit_geo_ident")
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