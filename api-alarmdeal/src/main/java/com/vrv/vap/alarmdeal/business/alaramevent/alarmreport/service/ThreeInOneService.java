package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne.ThreeInOneEvent;

/**
 * @author wudi
 * @date 2022/4/20 16:47
 */
public interface ThreeInOneService {

    /**
     * 查询三合一违规事件
     * @param requestBean
     * @return
     */
    ThreeInOneEvent searchThreeInOneEvent(RequestBean requestBean);

    /**
     * 查询三合一策略变更事件
     * @param requestBean
     * @return
     */
    ThreeInOneEvent searchThreeInOneChange(RequestBean requestBean);

}
