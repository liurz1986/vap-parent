package com.vrv.vap.alarmdeal.business.asset.config;

import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;

import com.vrv.vap.jpa.common.ArrayUtil;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.YmlUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import java.util.*;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年2月13日 上午11:44:40 
* 类说明    资产监控文件修改 
*/
@Component
public class AssetMonitorFileChangeListener {

	private static Logger logger = LoggerFactory.getLogger(AssetMonitorFileChangeListener.class);
	
	
	private static final String ASSET_FILE_NAME = "assets.yml";
	@Autowired
	private AssetService assetService;
	@Autowired
	private AssetTypeService assetTypeService;
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	@Value("${asset_file_path}")
	private String asset_file_path; //文件路径
	
	
	
	/**
	 * 进行文件比对，并执行对应的asset.yml替换工作
	 */
	public void compareYamlAndExecuteReplace(){
		try {
			  writeAssetFile();
		}catch(Exception e) {
             logger.error("进行文件比对，并执行对应的asset.yml替换工作异常",e);
		}

	}


	/**
	 * 写入资产文件
	 *
	 */
	private void writeAssetFile() {
		String monitorAssetStr = getMonitorAssetStr().trim();  //监控资产文件内容
		logger.info("monitorAssetStr的值为："+monitorAssetStr);
		FileUtil.writeFile(monitorAssetStr, asset_file_path, false);
	}


	/**
	 * 获得现在处于监控状态的资产
	 * @return
	 */
	public String getMonitorAssetStr(){
		Set<String> ip_set = new HashSet<>();
		Sort sort = Sort.by(Direction.ASC, "createTime");
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("special", "on"));
		List<Asset> list = assetService.findAll(conditions, sort);
		logger.info("资产统计个数"+list.size());
		//获得对应的监控的资产IP并进行去重处理
		for (Asset asset : list) {
			String ip = asset.getIp();
			ip_set.add(ip);
		}
		//根据ip查询获得对应的资产
		String asset_type = null;
		try {
			StringBuilder stringBuilder=new StringBuilder();
			for (String ip : ip_set) {
				stringBuilder = stringBuilder.append(ip).append(": ").append("\n").append("  "); //拼接IP
				List<QueryCondition> ip_condition = new ArrayList<>();
				ip_condition.add(QueryCondition.eq("ip", ip));
				List<Asset> asset_list = assetService.findAll(ip_condition, sort);  //该IP下所有的资产
				String assetTypeStr = getAssetTypeStr(asset_list);
				stringBuilder = stringBuilder.append("type").append(": ").append(assetTypeStr).append("\n");
			}
			asset_type = stringBuilder.toString();    //资产yaml语句
			logger.info("监控状态下资产类型yaml类型："+asset_type);			
		}catch(Exception e) {
			logger.error("构造数据产生异常：{}", e);
		}
		return asset_type;
	}
	
	
	
	


	/**
	 * 获得资产类型字符串
	 * @param asset_list
	 */
	private String getAssetTypeStr(List<Asset> asset_list) {
		List<String> asset_content = new ArrayList<>(); //资产类型集合
		for (Asset asset : asset_list) {				
			String assetTypeGuid = asset.getAssetType(); //资产类型
			String assetTypeSnoGuid = asset.getAssetTypeSnoGuid(); //资产品牌型号
			String assetTypeUniqueCode = getAssetTypeUniqueCode(assetTypeGuid, assetTypeSnoGuid);
			asset_content.add(assetTypeUniqueCode);
		}
		String[] assettype_array = asset_content.toArray(new String[asset_content.size()]);  //资产类型数据
		String assettype_str = ArrayUtil.join(assettype_array, ", ");
		assettype_str = "["+assettype_str+"]";
		return assettype_str;
	}


    /**
     * 返回资产类型和资产品牌型号的拼接对象
     * @param assetTypeGuid
     * @param assetTypeSnoGuid
     * @return
     */
	private String getAssetTypeUniqueCode(String assetTypeGuid, String assetTypeSnoGuid) {
		String result = "";
		AssetType assetType = assetTypeService.getOne(assetTypeGuid);  //资产类型
		AssetTypeSno assetTypeSno = null;
		if(StringUtils.isNotEmpty(assetTypeSnoGuid)){
			assetTypeSno = assetTypeSnoService.getOne(assetTypeSnoGuid); //资产品牌
		}
		if(assetType!=null){
			result = assetType.getTreeCode(); //资产类型uniquecode
		}
		if(assetTypeSno!=null){
			result = assetTypeSno.getTreeCode(); //资产品牌型号uniquecode
		}
		return result;
	}
	
	
	
}
