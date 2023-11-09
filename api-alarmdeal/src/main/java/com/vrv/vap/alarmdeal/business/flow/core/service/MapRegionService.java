package com.vrv.vap.alarmdeal.business.flow.core.service;
import com.vrv.vap.alarmdeal.business.flow.core.repository.MapRegionRespository;
import com.vrv.vap.alarmdeal.business.flow.core.model.Mapregion;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 上午10:35:56 
* 类说明     区域级联实现
*/
@Service
public class MapRegionService  extends BaseServiceImpl<Mapregion, String> {

	@Autowired
	private MapRegionRespository mapRegionRespository;
	
	@Override
	public MapRegionRespository getRepository() {
		return mapRegionRespository;
	}

	/**
	 * 获得本地级联
	 * @return
	 */
	public Mapregion getLocalMapregion() {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("mainServer", true));
		List<Mapregion> list = findAll(conditions);
		if(list.size()==1){
			Mapregion mapregion = list.get(0);
			return mapregion;
		}else {
			throw new RuntimeException("没有本机级联或者存在多个本地级联请检查！");
		}
	}
	
	
	/**
	 * 获得外派级联平台对应的树
	 * @return
	 */
	public List<Mapregion>  getMapRegionList(){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("mainServer", false));
		List<Mapregion> list = findAll(conditions);
		return list;
		
	}
	
	
	
	
	
}
