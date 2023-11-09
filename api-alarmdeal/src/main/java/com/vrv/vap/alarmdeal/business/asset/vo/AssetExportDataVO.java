package com.vrv.vap.alarmdeal.business.asset.vo;


import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import lombok.Data;

@Data
public class AssetExportDataVO extends Asset {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3117520155287508722L;
	String extendInfos;
}

