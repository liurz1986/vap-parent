package com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.alarmsave;

import com.lmax.disruptor.WorkHandler;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataEntryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.analysis.server.core.bean.WarnResultLogVo;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common.ReformAbsConsumer;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common.ReformModel;
import com.vrv.vap.jpa.spring.SpringUtil;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/12/15 11:00
 * @description:
 */
@Component
public class AlarmSaveEventConsumer extends ReformAbsConsumer {
    @Autowired
    private AlarmDataEntryService alarmDataEntryService;

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

    @Autowired
    private MapperUtil mapperUtil;

    @Override
    public void onEvent(ReformModel messageModel){
        if(alarmDataEntryService == null){
            alarmDataEntryService = SpringUtil.getBean(AlarmDataEntryService.class);
        }
        if(alarmDataHandleService == null){
            alarmDataHandleService = SpringUtil.getBean(AlarmDataHandleService.class);
        }
        if(mapperUtil == null){
            mapperUtil = SpringUtil.getBean(MapperUtil.class);
        }
        WarnResultLogVo vo = (WarnResultLogVo)messageModel.getMessage();
        WarnResultLogTmpVO warnResultLogTmpVO = mapperUtil.map(vo,WarnResultLogTmpVO.class);
        // 处理数据
//        List<AlarmEventAttribute> resultList = alarmDataEntryService.handleAlarmDataEntry(warnResultLogTmpVO);
    }
}
