package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name="asset_operation_log")
@Entity
@ApiModel(value = "资产操作日志")
public class AssetOperationLog implements Serializable {

    @Id
    @Column(name="guid")
	private String guid;
    
	@Column(name="asset_guid")
	private String assetGuid;  
	
	@Column(name="operation_type")
	private String operationType;  
	
	@Column(name="description")
	private String description;  
	
	@Column(name="operate_time")
	private Date operateTime;  
}
