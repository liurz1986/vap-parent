package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventColumnVO;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;

import java.util.List;

public interface EventColumService extends BaseService<EventColumn, String>{

	public List<EventColumn> getEventColumnById(String id);

	/**
	 * 获得可排序的列
	 * @param guid
	 * @return
	 */
	public List<EventColumn> getEventColumnCurr(String guid);

	List<EventColumn> getEventColumnByEventTableId(String guid);
	/**
	 * 保存事件列
	 * @param eventColumnVO
	 * @return
	 */
	public Result<EventColumn> saveEventColumns(EventColumnVO eventColumnVO);
	
	/**
	 * 编辑事件列
	 * @param eventColumnVO
	 * @return
	 */
	public Result<EventColumn> editEventColumns(EventColumnVO eventColumnVO);
	
	/**
	 * 删除事件列
	 * @param id
	 * @return
	 */
	public Result<Boolean> delEventColumns(String id);
	
	/**
	 * 判断是否存在重复的列名设置
	 * @param eventTableId
	 * @param field
	 * @return
	 */
	public Result<Boolean> judgeIsExistRepeatColumn(String eventTableId,String field);
	

	/**
	 * 检查事件表是否存在时间字段
	 * @param eventTableId
	 * @return
	 */
	public Result<Boolean> checkEventTableIsExistTimeField(String eventTableId);

	/**
	 * 获取时间字段
	 * @param eventTableId
	 * @return
	 */
	public String getTimeField(String eventTableId);


}
