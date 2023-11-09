package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job.thread;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataEntryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年05月07日 10:47
 */
public class AlarmThread implements Runnable{
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
        // 处理数据
        List<AlarmEventAttribute> resultList = alarmDataEntryService.handleAlarmDataEntry(saveList);
        // 上传数据
        alarmDataHandleService.pushAlarmData(resultList);
        // 上报数据（监管事件+事件处置）
        alarmDataHandleService.pushSuperviseData(resultList);
        resultList.clear();
    }
}
