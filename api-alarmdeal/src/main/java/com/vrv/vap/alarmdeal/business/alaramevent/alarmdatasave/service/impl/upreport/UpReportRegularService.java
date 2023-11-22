package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    private static Gson gsonNotIgnoreNull = new GsonBuilder().serializeNulls().registerTypeAdapter(String.class,new StringNullAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();

    @Override
    public void upEventToKafka(UpEventDTO eventDTO) {
        try {
            //1 构造数据
            AbstractUpEvent upEvent = constructUpEvent(eventDTO);
            //2 发送数据
            kafkaTemplate.send(getTopicName(), gsonNotIgnoreNull.toJson(upEvent));
        } catch (Exception e) {
            logger.error("##############上报监管事件发生了异常，异常原因为{}",e);
        }
    }

    @Override
    public String getTopicName() {
        return "SuperviseDataSubmit";
    }

    @Override
    public AbstractUpEvent constructUpEvent(UpEventDTO eventDTO) {
        AlarmEventAttribute item = eventDTO.getDoc();
        //1,构造上报监管事件数据
        Alert alert = constractDataRegular(item);
        RegularEvent regularEvent = new RegularEvent();
        AlertInfo alertInfo = new AlertInfo();
        alertInfo.setAlert(alert);
        //补全原始日志数据
        completeUpLogData(alertInfo, item);
        regularEvent.setAlert_info(alertInfo);
        return regularEvent;
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
        }else{
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
    private void completeUpLogData(AlertInfo alertInfo, AlarmEventAttribute doc) {
        List<String> logs = new ArrayList<>();
        List<LogIdVO> logIdVOS = doc.getLogs();
        for (LogIdVO logIdVO : logIdVOS) {
            List<QueryCondition_ES> conditions = new CopyOnWriteArrayList<>();
            List<String> ids = logIdVO.getIds();
            conditions.add(QueryCondition_ES.in("guid", ids));
            QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
            SearchResponse response = elasticSearchRestClient.getDocs(new String[]{logIdVO.getIndexName() + "*"}, queryBuilder, null, null, 0, ids.size());
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                logs.add(hit.getSourceAsString());
                break;
            }
        }
        alertInfo.setLogs(logs);
    }



}
