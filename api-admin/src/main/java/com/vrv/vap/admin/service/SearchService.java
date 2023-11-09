package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.model.HeatModel;
import com.vrv.vap.admin.vo.ConditionGenerateQuery;
import com.vrv.vap.admin.vo.EsSearchQuery;
import com.vrv.vap.admin.vo.QueryModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface SearchService {

	/**
	 * 查询结果
	 * @param esSearchQuery
	 * @return
	 */
	String searchGlobalContent(EsSearchQuery esSearchQuery);

	/**
	 * 查询结果
	 * @param indexList
	 * @param queryJsonStr
	 * @return
	 */
	String searchGlobalContent(List<String> indexList, String queryJsonStr);

	/**
	 * 查询主题数据量
	 * @param esSearchQuery
	 * @return
	 */
	List<Map<String,Object>> searchTopicCount(EsSearchQuery esSearchQuery);

	/**
	 * 查询指定字段内容列表
	 * @param esSearchQuery
	 * @return
	 */
	List<Map<String,Object>> searchFiledList(EsSearchQuery esSearchQuery);

	/**
	 * 查询指定字段内容列表
	 * @param indexList
	 * @param queryJsonStr
	 * @return
	 */
	List<Map<String,Object>> searchFiledContent(List<String> indexList, String queryJsonStr);

	/**
	 * 根据时间查询索引列表
	 * @param index
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<String> queryIndexListByTime(final String index, final String startTime, final String endTime);

	/**
	 * 获取分段的时间区间索引
	 * @param index
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<Object> querySegIndexListByTime(final String index, final String startTime, final String endTime);

	/**
	 * 导出excel
	 * @param esSearchQuery
	 * @return
	 */
	Export.Progress export(EsSearchQuery esSearchQuery);

	Map exportCSV(EsSearchQuery esSearchQuery);

	void importData(MultipartFile file);

	void downloadFile(String fileName, HttpServletResponse response);

	/**
	 * 搜索关键词特殊字符转义
	 * @param queryJsonStr
	 * @return
	 */
	String escapeQueryStr(String queryJsonStr);

	/**
	 * 生成查询语句
	 * @param query
	 * @return
	 */
	Map<String, Object> generateQueryCondition(ConditionGenerateQuery query);

	/**
	 * 查询今日日志总量（安全域过滤）
	 * @return
	 */
	Long queryTodayCount();

	/**
	 * 查询总量
	 * @param queryModel
	 * @return
	 */
	Long queryTotal(QueryModel queryModel);

	/**
	 * 求和
	 * @param queryModel
	 * @return
	 */
	Long querySum(QueryModel queryModel);

	/**
	 * 热力图
	 * @return
	 */
	List<Map<String,Object>> queryHeat(HeatModel heatModel);

	/**
	 * 总量趋势
	 * @param queryModel
	 * @return
	 */
	List<Map<String, Object>> queryTotalTrend(QueryModel queryModel);

	Long query24Total(QueryModel queryModel);

	Map<String, Object> queryDayTrend(QueryModel queryModel);
}
