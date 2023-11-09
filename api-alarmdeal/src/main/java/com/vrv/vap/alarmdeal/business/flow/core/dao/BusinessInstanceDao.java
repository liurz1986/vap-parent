package com.vrv.vap.alarmdeal.business.flow.core.dao;

import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年2月12日 上午11:47:53 
* 类说明   流程实例Dao层 
*/
public interface BusinessInstanceDao {
     
	/**
	 * 工单分类统计
	 * @return
	 */
	public List<Map<String,Object>> queryBusinessInstanceStatics();
}
