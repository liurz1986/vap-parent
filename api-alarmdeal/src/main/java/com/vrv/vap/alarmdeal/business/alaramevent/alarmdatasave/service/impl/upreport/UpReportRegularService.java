package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.NumericBooleanSerializer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfoSupervise;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UnitInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.StringNullAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 上报类型一：监管事件上报 type=1
 */
@Service("UpReportRegularService")
public class UpReportRegularService implements IUpReportEventService {
    private static Logger logger = LoggerFactory.getLogger(UpReportRegularService.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ElasticSearchRestClient elasticSearchRestClient;
    @Autowired
    private IUpReportCommonService upReportCommonService;
    /**
     * 空值也不会忽略
     */
    private static Gson gsonNotIgnoreNull = new GsonBuilder().serializeNulls().registerTypeAdapter(String.class, new StringNullAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();

    @Override
    public void upEventToKafka(UpEventDTO eventDTO) {
        try {
            //1 构造数据
            AbstractUpEvent upEvent = constructUpEvent(eventDTO);
            //2 发送数据
            kafkaTemplate.send(getTopicName(), gsonNotIgnoreNull.toJson(upEvent));
        } catch (Exception e) {
            logger.error("##############上报监管事件发生了异常，异常原因为{}", e);
        }
    }

    @Override
    public String getTopicName() {
        return "SuperviseDataSubmit";
    }

    @Override
    public AbstractUpEvent constructUpEvent(UpEventDTO eventDTO) {
        List<String> logs = getLogs(eventDTO);
        List<AlarmEventAttribute> docs = eventDTO.getDocs();
        Map<String, List<String>> logsGroup = groupIdByLogs(logs, docs);
        RegularEvent regularEvent = new RegularEvent();
        List<AlertInfo> alert_infos = new ArrayList<>();
        for (AlarmEventAttribute item : docs) {
            Alert alert = constractDataRegular(item);
            AlertInfo alertInfo = new AlertInfo();
            alertInfo.setAlert(alert);
            String eventId = item.getEventId();
            List<String> eventLogs = logsGroup.getOrDefault(eventId,new ArrayList<>());
            alertInfo.setLogs(eventLogs);
            alert_infos.add(alertInfo);
        }
        regularEvent.setAlert_info(alert_infos);
        return regularEvent;
    }





    /**
     * 拿到该批次所有的日志数据
     *
     * @param eventDTO
     * @return
     */
    private List<String> getLogs(UpEventDTO eventDTO) {
        List<String> logs = new ArrayList<>();
        List<String> guids = getGuids(eventDTO);
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.in("guid", guids));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
        String indexName = getIndexName(eventDTO);
        SearchResponse response = elasticSearchRestClient.getDocs(new String[]{indexName + "*"}, queryBuilder, null, null, 0, guids.size());
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            logs.add(hit.getSourceAsString());
        }
        return logs;
    }

    /**
     * 对相关内容进行分组
     *
     * @param logs
     * @param docs
     */
    public Map<String, List<String>> groupIdByLogs(List<String> logs, List<AlarmEventAttribute> docs) {
        Gson gson = new Gson();
        List<Map<String, Object>> maps = transLogStrToObj(logs);
        //maps当中单个元素包含guid字段，docs单个元素包含logs当中包含guids集合，将maps当中日志查询归属到对应的AlarmEventAttribute当中
        Map<String, List<String>> result = new HashMap<>();
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            String guid = (String) map.get("guid");
            for (AlarmEventAttribute doc : docs) {
                List<LogIdVO> logIds = doc.getLogs();
                for (LogIdVO logIdVO : logIds) {
                    List<String> logGuids = logIdVO.getIds();
                    if (logGuids.contains(guid)) {
                        List<String> stringList = result.get(doc.getEventId());
                        if (stringList == null) { //新的元素
                            stringList = new ArrayList<>();
                        }
                        stringList.add(gson.toJson(map));
                        result.put(doc.getEventId(), stringList);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 将日志信息转换成为日志对象信息
     *
     * @param logs
     * @return
     */
    private static List<Map<String, Object>> transLogStrToObj(List<String> logs) {
        Gson gson = new Gson();
        List<Map<String, Object>> lists = new ArrayList<>();
        for (String log : logs) {
            Map<String, Object> map = gson.fromJson(log, Map.class);
            lists.add(map);
        }
        return lists;
    }


    /**
     * 获得索引名称
     *
     * @return
     */
    private String getIndexName(UpEventDTO eventDTO) {
        String indexName = "";
        List<AlarmEventAttribute> docs = eventDTO.getDocs();
        if (docs.size() > 0) {
            AlarmEventAttribute alarmEventAttribute = docs.get(0);
            List<LogIdVO> logs = alarmEventAttribute.getLogs();
            if (logs != null && logs.size() > 0) {
                LogIdVO logIdVO = logs.get(0);
                indexName = logIdVO.getIndexName();
            }
        }
        return indexName;
    }

    /**
     * 获得本批次所有数据源的guids
     *
     * @param eventDTO
     * @return
     */
    private List<String> getGuids(UpEventDTO eventDTO) {
        List<String> guids = new ArrayList<>();
        List<AlarmEventAttribute> docs = eventDTO.getDocs();
        if (docs != null && docs.size() > 0) {
            for (AlarmEventAttribute alarmEventAttribute : docs) {
                List<LogIdVO> logs = alarmEventAttribute.getLogs();
                for (LogIdVO logIdVO : logs) {
                    guids.addAll(logIdVO.getIds());
                }
            }

        }
        return guids;
    }

    /**
     * 构造上报监管数据
     *
     * @param item 告警事件数据
     * @return 返回发送的数据
     */
    private Alert constractDataRegular(AlarmEventAttribute item) {
        Alert alert = new Alert();
        alert.setType(IUpReportEventService.UPREPORT_REGULAR_TYPE);
        //告警日志字段映射对象
        DataRegular dataRegular = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(item), DataRegular.class);
        if (item.getUnitList() != null) {
            //单位信息映射
            UnitInfo[] unitInfo = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(item.getUnitList()), UnitInfo[].class);
            dataRegular.setUnit_list(Arrays.asList(unitInfo));
        }
        dataRegular.setUnit_num(dataRegular.getUnit_list().size());
        //从es中获取的数量不对，所以有这个做法
        if (dataRegular.getStaff_list() != null) {
            dataRegular.setStaff_num(dataRegular.getStaff_list().size());
        }
        if (dataRegular.getDevice_list() != null) {
            dataRegular.setDevice_count(dataRegular.getDevice_list().size());
        }
        if (dataRegular.getApplication_list() != null) {
            dataRegular.setDevice_app_count(dataRegular.getApplication_list().size());
        }
        if (dataRegular.getFile_list() != null) {
            dataRegular.setFile_count(dataRegular.getFile_list().size());
        } else {
            dataRegular.setFile_list(new ArrayList<>());
        }
        List<AbstractUpData> data = new ArrayList<>();
        if (dataRegular.getStaff_list() != null) {
            for (StaffInfoSupervise staffInfoSupervise : dataRegular.getStaff_list()) {
                staffInfoSupervise.setStaff_level(upReportCommonService.getPersonLevelName(staffInfoSupervise.getStaff_level()));
            }
        }
        data.add(dataRegular);
        alert.setData(data);
        alert.setClient_id(upReportCommonService.getClientId());
        alert.setUpdate_time(new Date());
        return alert;
    }

    /**
     * 补全监管上报原始日志数据,这里存在问题
     *
     * @param alertInfo
     * @param doc       告警事件数据
     */



}
