package com.vrv.vap.admin.dao;

import java.util.List;
import java.util.Map;

import com.vrv.vap.admin.vo.LogStasticsVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月8日 下午4:05:25 
* 类说明  数据运维查询
*/
public interface LogStatisticsDao {

	/**
	 * 查询大类，和大类的几个数据。结构是大类，总数量，昨日条数，总量，昨日总量
	 * @return
	 */
	public List<Map<String,Object>> logCategorySearch();
	
	/**
	 * 汇总行
	 * @return
	 */
	public Map<String,Object>  logCategorySearchSum();
	
	/**
	 * 查询某个大类下的区域数据。结构是区域，总数量，昨日条数，总量，昨日总量
	 * @return
	 */
	public List<Map<String,Object>> logCategorySearchByCategory(String categoryName,String snoName);
	
	
	
	/**
	 * 查询某大类下，某区域的小类数据。结构：小类，总数量，昨日条数，总量，昨日总量
	 * @param categoryName
	 * @param areaName
	 * @return
	 */
	public List<Map<String,Object>> logCategorySearchByCategoryAndArea(String categoryName, String areaName,String snoName);
	
	
	
	/**
	 * 根据分类名称查询趋势图
	 * @param logStasticsVO
	 * @return
	 */
	public List<Map<String,Object>> logCategoryTrend(LogStasticsVO logStasticsVO);
	
	/**
	 * 查询趋势图
	 * @return
	 */
	public List<Map<String, Object>> logCategoryTrend();
	
	/**
	 * 根据分类名称查询柱状图
	 * @param sourceCompany
	 * @return
	 */
	public List<Map<String,Object>> logCatetoryBar(String categoryname,String sourceCompany);


	public List<Map<String, Object>> logCatetoryAllBar();

   
	
}
