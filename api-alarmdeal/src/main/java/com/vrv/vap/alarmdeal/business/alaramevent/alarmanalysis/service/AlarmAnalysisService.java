package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service;

import java.util.List;
import java.util.Map;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.DimensionTableColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ParamsColumn;
import com.vrv.vap.jpa.web.Result;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月27日 下午3:20:20 
* 类说明     威胁大屏业务类
*/
public interface AlarmAnalysisService {
	/**
	 * 按威胁类型统计威胁值的和
	 * @return
	 */
	public Result<List<Map<String,Object>>> queryThreatValueByThreatType();
	
	
	/**
	 * 根据威胁等级统计威胁等级的个数
	 * @return
	 */
	public Result<List<Map<String,Object>>> queryThreatLevelCountByThreatLevel();
	
	/**
	 * 根据部门进行威胁排分组
	 * @return
	 */
	public Result<List<Map<String,Object>>> queryThreatRankByDepartMent();
	
	/**
	 * 根据负责人进行威胁排名
	 * @return
	 */
	public Result<List<Map<String,Object>>> queryThreatRankByEmployee();
	
	
	public List<DimensionTableColumn> getDimensionTableColumns(String dimensionTableName);

	public List<DataRow> getDimensionTableData(String dimensionTableName, String ruleId, List<ParamsColumn> columns);

	public List<DataRow> getDimensionTableData(String dimensionTableName, String ruleId,String filterCode, List<ParamsColumn> columns);

	public void saveDimensionTableData(String dimensionTableName,String  ruleId,List<DataRow> rows);

	public void saveDimensionTableData(String dimensionTableName,String  ruleId,String filterCode,List<DataRow> rows);

	public void saveDimensionTableData(String dimensionTableName,String  ruleId,String filterCode,List<DataRow> rows,boolean isSync);

	public List<DataRow> getBaselineDataRows(String dimensionTableName,  List<ParamsColumn> columns);
	public List<DataRow> getBaselineDataRows(String dimensionTableName);
	public List<Map<String, Object>> getEventRuleStartedStatistics();
	public List<DataRow> getDataRows(String dimensionTableName, List<Map<String,Object>> data);
}
