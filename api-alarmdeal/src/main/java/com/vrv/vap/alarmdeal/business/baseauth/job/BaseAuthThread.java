package com.vrv.vap.alarmdeal.business.baseauth.job;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataEntryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthService;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年05月07日 10:47
 */
public class BaseAuthThread implements Runnable{
    private BaseAuthService authService;
    private List<Integer> saveList;

    public BaseAuthThread(BaseAuthService authService,  List<Integer> saveList){
        this.authService = authService;
        this.saveList = saveList;
    }

    @Override
    public void run() {
        // 处理数据
       authService.dealData(saveList);

    }
}
