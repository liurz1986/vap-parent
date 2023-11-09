package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产额外属性
 */
@Data
@TableName("asset_extend")
@ApiModel(value = "资产额外属性")
public class AssetExtend implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 资产guid
	 */
	private String assetGuid;
	/**
	 * 资产额外属性
	 */
	private String extendInfos;

}
