package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventTableTreeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventTableVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventTabelService extends BaseService<EventTable, String>{

	public EventTable getOne(String id);

	public List<EventTable> getEventTableByTitle(String title);

	public List<EventTable> getEventTables();

	/**
	 * 获得事件表列表
	 * @param eventTableVO
	 * @return
	 */
	public PageRes<EventTable> getEventTablePager(EventTableVO eventTableVO,Pageable pageable);

	/**
	 * 分页获取事件源: 2023-09-22
	 *  直接查询EventTable
	 * @param eventTableVO
	 * @param pageable
	 * @return
	 */
	public PageRes<EventTable> getEventTableCurPager(EventTableVO eventTableVO,Pageable pageable);
	/**
	 * 获得基线表列表
	 * @param eventTableVO
	 * @return
	 */
	public PageRes<EventTable> getBaseLinePager(EventTableVO eventTableVO,Pageable pageable);
	
	/**
	 * 获得事件树
	 * @param tableName
	 * @return
	 */
	public List<EventTableTreeVO> getEventTableTree(String tableName);
	
	/**
	 * 通过表名获得Eventtable
	 * @param name
	 * @return
	 */
	public EventTable getEventTableByName(String name);
	
	/**
	 * 保存事件表
	 * @param eventTableVO
	 * @return
	 */
	public Result<EventTable> saveEventTable(EventTableVO eventTableVO);
	
	/**
	 * 编辑事件表
	 * @param eventTableVO
	 * @return
	 */
	public Result<EventTable> editEventTable(EventTableVO eventTableVO);
	
	/**
	 * 删除事件表
	 * @param id
	 * @return
	 */
	public Result<EventTable> delEventTable(String id);
	
	
	/**
	 * 获得eventtable全部数据//TODO 暂时没有带筛选条件
	 * @return
	 */
	public Result<List<EventTable>> getAllEventTable();

	/**
	 * 版本号+1
	 */
	public Result<EventTable> addVersion(String guid);

	public List<EventTable> getSourceData(String topicAlias);

}
