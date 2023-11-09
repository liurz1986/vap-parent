package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;

import java.util.Map;

public interface AlarmNoticeService {

    public  boolean  sendNoticeList(Map<String,WarnResultLogTmpVO> map);


    public  boolean  sendNotice(WarnResultLogTmpVO warnResultLogTmpVO);


}
