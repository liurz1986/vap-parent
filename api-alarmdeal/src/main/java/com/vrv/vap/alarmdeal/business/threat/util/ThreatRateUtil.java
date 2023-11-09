package com.vrv.vap.alarmdeal.business.threat.util;


import com.vrv.vap.alarmdeal.business.threat.contract.ThreatContract;
import com.vrv.vap.jpa.common.DateUtil;

import java.util.Date;

/**
 * @author: Administrator
 * @since: 2022/8/29 15:16
 * @description:
 */
public class ThreatRateUtil {
    /**
     * 通过时间判断威胁频率获取威胁值
     *
     * @param time 时间
     * @return 威胁值
     * @throws Exception
     */
    public static int getRateByTime(String time) throws Exception {
        long num = DateUtil.getBetweenDays(time,DateUtil.format(new Date()),DateUtil.DEFAULT_DATE_PATTERN);
        if(num <= ThreatContract.WEEK){
            return ThreatContract.FIVE;
        }else if(num <= ThreatContract.MONTH){
            return ThreatContract.FOUR;
        }else if(num <= ThreatContract.THREEMONTH){
            return ThreatContract.THREE;
        }else if(num <= ThreatContract.YEAR){
            return ThreatContract.TWO;
        }else if(num > ThreatContract.YEAR){
            return ThreatContract.ONE;
        }else{
            return ThreatContract.ONE;
        }
    }
}
