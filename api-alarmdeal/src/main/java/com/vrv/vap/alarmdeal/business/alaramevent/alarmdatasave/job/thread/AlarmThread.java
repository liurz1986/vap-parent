package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job.thread;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataEntryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年05月07日 10:47
 */
public class AlarmThread implements Runnable{

    private static Logger logger= LoggerFactory.getLogger(AlarmThread.class);
    private AlarmDataEntryService alarmDataEntryService;
    private AlarmDataHandleService alarmDataHandleService;
    private List<WarnResultLogTmpVO> saveList;

    public AlarmThread(AlarmDataEntryService alarmDataEntryService, AlarmDataHandleService alarmDataHandleService, List<WarnResultLogTmpVO> saveList){
        this.alarmDataEntryService = alarmDataEntryService;
        this.alarmDataHandleService = alarmDataHandleService;
        this.saveList = saveList;
    }

    @Override
    public void run() {
       try{
           List<AlarmEventAttribute> resultList = alarmDataEntryService.handleAlarmDataEntry(saveList);
           // 上传数据
           alarmDataHandleService.pushAlarmData(resultList);
           // 上报数据（监管事件+事件处置）
           resultList.clear();
       }catch (Exception e){
           logger.error("消费日志报错，报错信息：{}",e);
       }
    }
}
