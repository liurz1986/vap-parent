package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.jpa.web.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 资产导入和
 * @author wd-pc
 *
 */
public interface AssetExportAndImportService {
     
	/**
	 * 筛选资产，并导出
	 * @param assetSearchVO
	 * @param types
	 * @return
	 */
	public Result<String> exportAssetInfo(AssetSearchVO assetSearchVO,List<String> types);

	public Result<String> exportAssetInfo(AssetSearchVO assetSearchVO,List<String> types,String token);

	public Result<String> exportAssetInfoTemplate(List<String> types);
	
	/**
	 * 下载资产文件
	 * @param fileName
	 * @param response
	 */
	public void exportAssetFile(String fileName, HttpServletResponse response);
	
	/**
	 * 导入资产文件信息
	 * @return
	 */
	public Result<Boolean> importAssetFile(List<Map<String,Object>> list);


}
