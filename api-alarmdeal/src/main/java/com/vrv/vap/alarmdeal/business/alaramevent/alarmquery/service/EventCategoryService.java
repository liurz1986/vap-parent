package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service;

import java.io.IOException;
import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.ThreatLibraryVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventCategoryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventCategoryVRVTreeVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

public interface EventCategoryService extends BaseService<EventCategory, String> {

	/**
	 * 预警事件分类树
	 * @return
	 */
	public List<EventCategoryVRVTreeVO> getEventCateTree();

	/**
	 * 添加事件分类
	 * @param eventCategory
	 * @return
	 */
	public 	Result<Boolean> addEventCategory(EventCategory eventCategory);

	/**
	 * 编辑事件分类
	 * @param eventCategory
	 * @return
	 */
	public Result<EventCategory> editEventCategory(EventCategory eventCategory);
	
	
	/**
	 * 获得对应的事件类型分类
	 * @param eventCategoryVO
	 * @param pageable
	 * @return
	 */
	public PageRes<EventCategoryVO> getEventCategoryPager(EventCategoryVO eventCategoryVO,Pageable pageable);
	
	
	/**
	 * 根据事件ID获得对应的事件树数据
	 * @param eventCode
	 * @return
	 */
	public List<EventCategoryVRVTreeVO> getEventCategoryTreeByEventCode(String eventCode);
	
	/**
	 * 导出威胁库(事件分类)
	 * @param threatLibraryVO
	 * @return
	 */
	public Result<Boolean> exportThreatLibrary(ThreatLibraryVO threatLibraryVO);
	
	/**
	 * 导入对应的威胁库（事件分类）
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Result<Boolean> importThreatLibraryInfo(CommonsMultipartFile file) throws IOException;

	/**
	 * 查询全部的分类
	 *
	 * @return List
	 */
	public List<EventCategory> queryAllEventCategory();
	
}
