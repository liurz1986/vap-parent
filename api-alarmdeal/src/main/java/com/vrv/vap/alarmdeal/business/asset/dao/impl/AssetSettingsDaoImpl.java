package com.vrv.vap.alarmdeal.business.asset.dao.impl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetSettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AssetSettingsDaoImpl implements AssetSettingsDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	Gson  gson=new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
			.create();
	
	public  List<Map<String, Object>> getAssetTemplateAttribute(List<String> guids){
		
		String sql="SELECT   " +
				"`asset_type_group`.`Guid` as groupGuid"+
				",`asset_type`.`Guid`   " +
				",`asset_type_template`.`asset_type_guid` " + 
				",JSON_EXTRACT(`asset_type_template`.`form_data`,'$.*[*].context.name') AS 'codes'   " + 
				",JSON_EXTRACT(`asset_type_template`.`form_data`,'$.*[*].context.title') AS 'fields'   " + 
				",JSON_EXTRACT(`asset_type_template`.`form_data`,'$.*[*].context.type') AS 'types'   " + 
				"FROM  `asset_type_template`   " + 
				"LEFT JOIN `asset_type_sno` ON `asset_type_sno`.`Guid`=`asset_type_template`.`asset_type_guid`   " + 
				"LEFT JOIN `asset_type`  ON `asset_type_sno`.`TreeCode` LIKE CONCAT(`asset_type`.`TreeCode`,'-%')   " +
				"LEFT JOIN `asset_type_group`  ON `asset_type`.`TreeCode` LIKE CONCAT(`asset_type_group`.`TreeCode`,'-%')"+
		"WHERE `asset_type`.`status`=FALSE  AND `asset_type_sno`.`status`=FALSE ";
		if(null !=guids && guids.size()> 0){
			sql = sql +" and asset_type_guid in ('" + org.apache.commons.lang3.StringUtils.join(guids, "','") + "')";
		}
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		return queryForList;
	}
	

}
