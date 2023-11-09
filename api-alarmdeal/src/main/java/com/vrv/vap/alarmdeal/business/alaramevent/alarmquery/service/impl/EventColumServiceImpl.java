package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventColumService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.EventColumRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventColumnVO;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSourceField;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.common.UUIDUtils;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventColumServiceImpl extends BaseServiceImpl<EventColumn, String> implements EventColumService {
	@Autowired
	private EventColumRespository eventTabelRespository;
	@Override
	public EventColumRespository getRepository() {
		return eventTabelRespository;
	}
	@Autowired
	private MapperUtil mapper;
	@Autowired
    private EventTabelService eventTabelService;
	@Autowired
	private DataSourceFegin dataSourceFegin;

	public List<DataSourceField> queryDataSourceFieldById(String id){
		ResultObjVO<List<DataSourceField>> fields = dataSourceFegin.getFieldBySourceId(id);
		List<DataSourceField> result = fields.getData().stream().filter(item->item.getAnalysisSort() != null).collect(Collectors.toList());
		return result;
	}

	public List<EventColumn> getDataSourceFieldToEventColumn(List<DataSourceField> dataSourceFields,String sourceId){
		List<EventColumn> result = new ArrayList<>();

		ResultObjVO<List<DataSource>> dataSourceObj = dataSourceFegin.getSource();
		List<DataSource> dataSources = dataSourceObj.getData();
		dataSources = dataSources.stream().filter(item->sourceId.equals(String.valueOf(item.getId()))).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(dataSources)){
			return result;
		}
		DataSource dataSource = dataSources.get(0);
		String timeField = dataSource.getTimeField();
		for(int i =0 ;i<dataSourceFields.size();i++){
			DataSourceField item = dataSourceFields.get(i);
			if(!"@timestamp".equals(item.getField())){
				EventColumn eventColumn = new EventColumn();
				eventColumn.setId(String.valueOf(item.getId()));
				eventColumn.setLabel(StringUtils.isNotEmpty(item.getName())?item.getName(): item.getField());
				eventColumn.setLen(String.valueOf(item.getAnalysisTypeLength()));
				eventColumn.setName(item.getField());
				eventColumn.setType(item.getAnalysisType());
				eventColumn.setEventTableId(String.valueOf(item.getSourceId()));
				eventColumn.setOrder(i);
				eventColumn.setIsShow(item.getShow());
				eventColumn.setDstIp(false);
				eventColumn.setSrcIp(false);
				eventColumn.setRelateIp(false);
				eventColumn.setTimeLine(false);
				if(item.getField().equals(timeField)){
					eventColumn.setEventTime(true);
				}else{
					eventColumn.setEventTime(false);
				}
				result.add(eventColumn);
			}
		}
		return result;
	}

	@Override
	public List<EventColumn> getEventColumnById(String id) {
		List<DataSourceField> dataSourceFields = queryDataSourceFieldById(id);
		List<EventColumn> list = getDataSourceFieldToEventColumn(dataSourceFields,id);
		return list;
	}

	@Override
	public List<EventColumn> getEventColumnCurr(String guid) {
		// 查询字段列表
		List<DataSourceField> dataSourceFields = queryDataSourceFieldById(guid);

		// 过滤需要的字段
//		dataSourceFields = dataSourceFields.stream().filter(item-> item.getAnalysisShow() == 1).collect(Collectors.toList());
		dataSourceFields = dataSourceFields.stream().filter(item-> item.getAnalysisSort() != null).collect(Collectors.toList());

		// 根据英文名称排序
		Collections.sort(dataSourceFields, Comparator.comparing(DataSourceField::getField));

		// 转换并设置order
		List<EventColumn> list = getDataSourceFieldToEventColumn(dataSourceFields,guid);
		return list;
	}

	/**
	 * 通过eventTableId获取对应的EventColumn数据
	 * @param guid
	 * @return
	 */
	@Override
	public List<EventColumn> getEventColumnByEventTableId(String guid) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("eventTableId",guid));
		List<EventColumn> eventColumns = this.findAll(conditions);
		return eventColumns;
	}


	@Override
	public Result<EventColumn> saveEventColumns(EventColumnVO eventColumnVO) {
		EventColumn eventColumn = mapper.map(eventColumnVO, EventColumn.class);
		eventColumn.setId(UUIDUtils.get32UUID());
		try{
			updateEventColumnValue(eventColumn);
			save(eventColumn);
			return ResultUtil.success(eventColumn);
		}catch(Exception e){
			throw new RuntimeException("保存事件列出错", e);
		}
		
	}
	
	
	/**
	 * 更新表当中对应属性的字段
	 * @param eventColumn
	 */
	private void updateEventColumnValue(EventColumn eventColumn) {
		String eventTableId = eventColumn.getEventTableId();
		Boolean srcIp = eventColumn.getSrcIp();
		if(srcIp==null) {
			srcIp = false;
			eventColumn.setSrcIp(srcIp);
		}
		if(srcIp){
			List<QueryCondition> srcIpConditions = new ArrayList<>();
			srcIpConditions.add(QueryCondition.eq("srcIp", true));
			srcIpConditions.add(QueryCondition.eq("eventTableId", eventTableId));
			// List<EventColumn> list = findAll(srcIpConditions);
			// if(list.size()>0){
			// 	for (EventColumn eventColumn2 : list) {
			// 		eventColumn2.setSrcIp(false);
			// 		save(eventColumn2);
			// 	}
			// }
			
		}
		Boolean dstIp = eventColumn.getDstIp();
		if(dstIp==null) {
			dstIp = false;
			eventColumn.setDstIp(dstIp);
		}
		if(dstIp){
			List<QueryCondition> dstIpConditions = new ArrayList<>();
			dstIpConditions.add(QueryCondition.eq("dstIp", true));
			dstIpConditions.add(QueryCondition.eq("eventTableId", eventTableId));
			// List<EventColumn> list = findAll(dstIpConditions);
			// if(list.size()>0){
			// 	for (EventColumn eventColumn2 : list) {
			// 		eventColumn2.setDstIp(false);
			// 		save(eventColumn2);
			// 	}
			// }
			
		}
		
		Boolean relateIp = eventColumn.getRelateIp();
		if(relateIp==null) {
			relateIp = false;
			eventColumn.setRelateIp(dstIp);
		}
		if(relateIp){
			List<QueryCondition> relateIpConditions = new ArrayList<>();
			relateIpConditions.add(QueryCondition.eq("relateIp", true));
			relateIpConditions.add(QueryCondition.eq("eventTableId", eventTableId));
			// List<EventColumn> list = findAll(relateIpConditions);
			// if(list.size()>0){
			// 	for (EventColumn eventColumn2 : list) {
			// 		eventColumn2.setRelateIp(false);
			// 		save(eventColumn2);
			// 	}
			// }
		}
		
		Boolean timeLine = eventColumn.getTimeLine();
		if(timeLine==null) {
			timeLine = false;
			eventColumn.setTimeLine(timeLine);
		}
		if(timeLine){
			List<QueryCondition> timeConditions = new ArrayList<>();
			timeConditions.add(QueryCondition.eq("timeLine", true));
			timeConditions.add(QueryCondition.eq("eventTableId", eventTableId));
			// List<EventColumn> list = findAll(timeConditions);
			// if(list.size()>0){
			// 	for (EventColumn eventColumn2 : list) {
			// 		eventColumn2.setTimeLine(false);
			// 		save(eventColumn2);
			// 	}
			// }
		}
		Boolean eventTime=eventColumn.getEventTime();
		if(eventTime==null) {
			eventTime = false;
			eventColumn.setTimeLine(timeLine);
		}
		if(eventTime){
			List<QueryCondition> timeConditions = new ArrayList<>();
			timeConditions.add(QueryCondition.eq("eventTime", true));
			timeConditions.add(QueryCondition.eq("eventTableId", eventTableId));
			// List<EventColumn> list = findAll(timeConditions);
			// if(list.size()>0){
			// 	for (EventColumn eventColumn2 : list) {
			// 		eventColumn2.setEventTime(false);
			// 		save(eventColumn2);
			// 	}
			// }
		}
		
	}
	

	@Override
	public Result<EventColumn> editEventColumns(EventColumnVO eventColumnVO) {

		String id = eventColumnVO.getId();
		EventColumn eventColumn = getOne(id);
		mapper.copy(eventColumnVO, eventColumn);
		try{
			EventColumn newEventColumn=new EventColumn();
		 	mapper.copy(eventColumn, newEventColumn);
		 	updateEventColumnValue(eventColumn);
		 	save(newEventColumn);
			eventTabelService.addVersion(eventColumn.getEventTableId());
		 	return ResultUtil.success(eventColumn);
		 }catch(Exception e){
		 	throw new RuntimeException("编辑事件列出错", e);
		}
	}

	@Override
	public Result<Boolean> delEventColumns(String id) {
        try {
        	delete(id);
        	return ResultUtil.success(true);        	
        }catch(Exception e){
        	throw new RuntimeException("删除事件列报错", e);
        }
		
	}

	@Override
	public Result<Boolean> judgeIsExistRepeatColumn(String eventTableId, String field) {
		List<DataSourceField> fields = queryDataSourceFieldById(eventTableId);
		fields = fields.stream().filter(item->item.getField().equals(field)).collect(Collectors.toList());
		long count = fields.size();
		if(count>0) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"已存在"+field+"字段，是否将此字段设为新"+field+"字段?");
		}
		return ResultUtil.success(true);
	}

	@Override
	public Result<Boolean> checkEventTableIsExistTimeField(String eventTableId) {
		int count = 0;
		List<EventColumn> list = getEventColumnCurr(eventTableId);
		for (EventColumn eventColumn : list) {
			Boolean timeLine = eventColumn.getTimeLine();
			if(timeLine!=null){
				if(timeLine){
					count++;					
				}
			}
		}
		if(count==0){
			return ResultUtil.error(ResultCodeEnum.FORM_VALIDATE_ERROR.getCode(),"该日志没有时间字段，无法开启监控字段");
		}else if(count>1){
			return ResultUtil.error(ResultCodeEnum.FORM_VALIDATE_ERROR.getCode(),"该日志时间字段个数大于1，无法开启监控字段");
		}else{
			return ResultUtil.success(true);
		}
	}
	@Override
    public String getTimeField(String eventTableId) {
		String field="";
		List<EventColumn> list = getEventColumnCurr(eventTableId);
		for (EventColumn eventColumn : list) {
			Boolean eventTime = eventColumn.getEventTime();
			if(eventTime!=null){
				if(eventTime){
					field=eventColumn.getName();
					break;
				}
			}
		}
        return field;
	}


}
