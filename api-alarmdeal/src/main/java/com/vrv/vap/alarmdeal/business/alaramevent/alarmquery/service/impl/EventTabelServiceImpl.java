package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.EventTabelRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventTableTreeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventTableVO;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventTabelServiceImpl extends BaseServiceImpl<EventTable, String> implements  EventTabelService {
	private Logger logger = LoggerFactory.getLogger(EventTabelServiceImpl.class);

	@Autowired
	private MapperUtil mapper;

	@Autowired
	private DataSourceFegin dataSourceFegin;
	@Autowired
	private EventTabelRespository eventTabelRespository;
	@Override
	public EventTabelRespository getRepository() {
		return eventTabelRespository;
	}
	/**
	 * 获取全部数据源
	 * @return
	 */
	public List<DataSource> querySourceList(){
		ResultObjVO<List<DataSource>> resultObjVO = dataSourceFegin.getSource();
		List<DataSource> result = resultObjVO.getData();
		return result;
	}


	@Override
	public List<EventTable> getSourceData(String topicAlias){
		Map<String,Object> map = new HashMap<>();
		map.put("topicAlias",topicAlias);
		ResultObjVO<List<DataSource>> resultObjVO = dataSourceFegin.getSourceByParam(map);
		List<DataSource> dataSources = resultObjVO.getList();
		List<EventTable> result = getDataSourceToEventTable(dataSources);
		return result;
	}

	/**
	 * 通过条件筛选数据源（可分页）
	 * @param param
	 * @return
	 */
	public List<DataSource> querySourceByParam(Map<String,Object> param){
		ResultObjVO<List<DataSource>> resultObjVO = dataSourceFegin.getSourceByParam(param);
		List<DataSource> result = resultObjVO.getList();
		return result;
	}

	/**
	 * dataSource转EventTable
	 * @param dataSources
	 * @return
	 */
	public List<EventTable> getDataSourceToEventTable(List<DataSource> dataSources){
		List<EventTable> result = new ArrayList<>();
		dataSources.stream().forEach(item->{
			EventTable eventTable = new EventTable();
			eventTable.setId(String.valueOf(item.getId()));
			eventTable.setName(item.getTopicAlias());
			eventTable.setDescription(item.getDescription());
			eventTable.setGroupName(item.getTitle());
			eventTable.setLabel(item.getTitle());
			eventTable.setIndexName(item.getName().substring(0,item.getName().length()-2));
			eventTable.setTopicName(item.getTopicName());
			eventTable.setFormatter(item.getTimeFormat());
			eventTable.setDataType(String.valueOf(item.getType()));
			result.add(eventTable);
		});
		return result;
	}

	/**
	 * 通过ID获取数据源
	 * @param id
	 * @return
	 */
	@Override
	public EventTable getOne(String id) {
		List<DataSource> dataSources = querySourceList();
		if(CollectionUtils.isNotEmpty(dataSources)){
			dataSources = dataSources.stream().filter(item->item.getId().intValue() == Integer.valueOf(id).intValue()).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(dataSources)){
				List<EventTable> eventTables = getDataSourceToEventTable(dataSources);
				return eventTables.get(0);
			}
		}
		return null;
	}

	/**
	 * 通过title筛选数据源
	 * @param title
	 * @return
	 */
	@Override
	public List<EventTable> getEventTableByTitle(String title) {
		List<DataSource> dataSources = querySourceList();
		dataSources = dataSources.stream().filter(item->item.getTitle().equals(title)).collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(dataSources)){
			List<EventTable> eventTables = getDataSourceToEventTable(dataSources);
			return eventTables;
		}
		return null;
	}

	/**
	 * 获取全部数据源
	 * @return
	 */
	@Override
	public List<EventTable> getEventTables() {
		List<DataSource> dataSources = querySourceList();
		List<EventTable> eventTables = getDataSourceToEventTable(dataSources);
		return eventTables;
	}

	/**
	 * 分页获取事件源
	 * @param eventTableVO
	 * @param pageable
	 * @return
	 */
	@Override
	public PageRes<EventTable> getEventTablePager(EventTableVO eventTableVO,Pageable pageable) {
		//查询条件
		String eventTabelName = eventTableVO.getName();
		Map<String,Object> param = new HashMap<>();
		param.put("dataType",1);
		param.put("count_",eventTableVO.getCount_());
		param.put("start_",eventTableVO.getStart_());
		if(StringUtils.isNotBlank(eventTabelName)){
			param.put("name",eventTabelName);
		}
		List<DataSource> list = querySourceByParam(param);
		list = list.stream().filter(item -> StringUtils.isNotBlank(item.getTopicName())).collect(Collectors.toList());
		List<EventTable> eventTables = getDataSourceToEventTable(list);
		PageRes<EventTable> res = new PageRes<>();
		res.setTotal(Long.valueOf(eventTables.size()));
		res.setList(eventTables);
		res.setCode("0");
		res.setMessage("success");
		return res;
	}


	/**
	 * 分页获取事件源: 2023-09-22
	 *  直接查询EventTable
	 * @param eventTableVO
	 * @param pageable
	 * @return
	 */
	@Override
	public PageRes<EventTable> getEventTableCurPager(EventTableVO eventTableVO,Pageable pageable) {
		String eventTabelName = eventTableVO.getName();
		List<QueryCondition> conditions = new ArrayList<>();
		if(StringUtils.isNotBlank(eventTabelName)){
			conditions.add(QueryCondition.eq("name",eventTabelName));
		}
		Page<EventTable> pages = this.findAll(conditions,pageable);
		PageRes<EventTable> res = new PageRes<>();
		res.setTotal(pages.getTotalElements());
		res.setList(pages.getContent());
		res.setCode("0");
		res.setMessage("success");
		return res;

	}
	/**
	 * 分页获取基线数据源
	 * @param eventTableVO
	 * @param pageable
	 * @return
	 */
	@Override
	public PageRes<EventTable> getBaseLinePager(EventTableVO eventTableVO,Pageable pageable) {
		//查询条件
		String eventTabelName = eventTableVO.getName();
		Map<String,Object> param = new HashMap<>();
		param.put("dataType",2);
		param.put("count_",eventTableVO.getCount_());
		param.put("start_",eventTableVO.getStart_());
		if(StringUtils.isNotBlank(eventTabelName)){
			param.put("name",eventTabelName);
		}
		List<DataSource> list = querySourceByParam(param);
		list = list.stream().filter(item -> StringUtils.isNotBlank(item.getTopicName())).collect(Collectors.toList());
		List<EventTable> eventTables = getDataSourceToEventTable(list);
		PageRes<EventTable> res = new PageRes<>();
		res.setTotal(Long.valueOf(eventTables.size()));
		res.setList(eventTables);
		res.setCode("0");
		res.setMessage("success");
		return res;

	}

	/**
	 * 通过名称获取事件源
	 * @param tableName
	 * @return
	 */
	@Override
	public List<EventTableTreeVO> getEventTableTree(String tableName) {
		Map<String,Object> param = new HashMap<>();
		param.put("namne",tableName);
		param.put("type",1);
		List<DataSource> list = querySourceByParam(param);
		List<EventTable> eventTables = getDataSourceToEventTable(list);
		List<EventTableTreeVO> eventTableTreeByGroup = getEventTableTreeByGroup(eventTables);
		return eventTableTreeByGroup;
	}

	/**
	 * getRootTableVo
	 * @param list
	 * @param gIndex
	 * @return
	 */
	private EventTableTreeVO getRootTableVo(List<EventTableTreeVO> list, String gIndex){
		for (EventTableTreeVO vo : list) {
			if(vo.getKey().equals(gIndex) && !vo.isTable()){
				return vo;
			}
		}
		EventTableTreeVO rootGroupVO = EventTableTreeVO.builder()
				.key(gIndex).title(gIndex).isTable(false)
				.children(new ArrayList<EventTableTreeVO>()).build();
		list.add(rootGroupVO);
		return rootGroupVO;
	}

	/**
	 * getEventTableTreeByGroup
	 * @param eventtables
	 * @return
	 */
	private List<EventTableTreeVO> getEventTableTreeByGroup(List<EventTable> eventtables) {
		List<EventTableTreeVO> result = new ArrayList<EventTableTreeVO>();
		EventTableTreeVO noGroupVO = EventTableTreeVO.builder()
				.key("其他").title("其他").parentId("0").isTable(false)
				.children(new ArrayList<EventTableTreeVO>()).build();
		
		for (EventTable eventTable : eventtables) {
			String groupName = eventTable.getGroupName();
			if(groupName.isEmpty()){
				// 当group为空时，直接插入到“其他”中
				EventTableTreeVO noGroupTable = EventTableTreeVO.builder()
				.key(eventTable.getId().toString())
				.title(eventTable.getLabel())
				.isTable(true)
				.parentId(noGroupVO.getKey())
				.children(new ArrayList<EventTableTreeVO>()).build();
				noGroupVO.getChildren().add(noGroupTable);
			} else{
				// 进行链路查找和创建过程
				String[] split = groupName.split("\\.");
				String rootIndex = split[0];
				EventTableTreeVO rootTableVo = getRootTableVo(result, rootIndex);
				rootTableVo.setParentId("0");
				EventTableTreeVO indexTableVO = rootTableVo;
				for (int i = 1; i < split.length; i++) {
					String gIndex = split[i];
					EventTableTreeVO nextVO = getRootTableVo(indexTableVO.getChildren(), gIndex);
					nextVO.setParentId(indexTableVO.getKey());
					indexTableVO = nextVO;
				}
				
				// 当group的链路建立好后，直接把当前节点加入到最后的indexTableVO中
				EventTableTreeVO tableVO = EventTableTreeVO.builder()
						.key(eventTable.getId().toString())
						.title(eventTable.getLabel())
						.isTable(true)
						.parentId(indexTableVO.getKey())
						.children(new ArrayList<EventTableTreeVO>()).build();
				indexTableVO.getChildren().add(tableVO);
			}
		}
		// 当有其他节点的时候，添加到树种
		if(noGroupVO.getChildren().size()>0){
			result.add(noGroupVO);
		}
		return result;
	}

	/**
	 * 通过名称获取事件源
	 * @param name
	 * @return
	 */
	@Override
	public EventTable getEventTableByName(String name) {
		Map<String,Object> param = new HashMap<>();
		param.put("namne",name);
		param.put("type",1);
		List<DataSource> list = querySourceByParam(param);
		List<EventTable> eventTables = getDataSourceToEventTable(list);
		if(eventTables.size()==1){
			return eventTables.get(0);
		}
		return null;
	}

	@Override
	public Result<EventTable> saveEventTable(EventTableVO eventTableVO) {
		EventTable eventTable = mapper.map(eventTableVO, EventTable.class);
		eventTable.setId(UUID.randomUUID().toString());
		eventTable.setMonitor(false);
		eventTable.setFormatter("-yyyy.MM");
		if(eventTableVO.getMultiTable()==null){
			eventTable.setMultiTable(false);
		}
		eventTable.setVersion(0);
		eventTable.setFormatter("-yyyy.MM");
		if(eventTableVO.getMultiTable()==null){
            eventTable.setMultiTable(false);
        }
		try{			
			save(eventTable);
			return ResultUtil.success(eventTable);		
		}catch(Exception e){
			throw new RuntimeException("保存事件表失败", e);
		}
	}

	@Override
	public Result<EventTable> editEventTable(EventTableVO eventTableVO) {

		try{
			EventTable eventTable = this.eventTabelRespository.findById(eventTableVO.getId()).get();
			if(null == eventTable){
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑的对象不存在");
			}
			mapper.copy(eventTableVO, eventTable);
			eventTable.setVersion(eventTable.getVersion()+1);
			save(eventTable);
			return ResultUtil.success(eventTable);
		}catch(Exception e){
			logger.error("编辑事件表异常:{}",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑事件表异常");
		}
	}

	@Override
	public Result<EventTable> delEventTable(String id) {
		try{
			this.delete(id);
			return ResultUtil.success(null);
		}catch(Exception e){
			throw new RuntimeException("删除事件表失败", e);
		}
	}

	@Override
	public Result<List<EventTable>> getAllEventTable() {
		List<DataSource> dataSources = querySourceList();
		dataSources = dataSources.stream().filter(item -> item.getTopicAlias()!= null).collect(Collectors.toList());
		List<EventTable> list = getDataSourceToEventTable(dataSources);
		Result<List<EventTable>> result = ResultUtil.successList(list);
		return result;
	}

	/**
	 * 版本号+1
	 */
	@Override
    public Result<EventTable> addVersion(String guid){
		return ResultUtil.success(null);
	}
	
	
}
