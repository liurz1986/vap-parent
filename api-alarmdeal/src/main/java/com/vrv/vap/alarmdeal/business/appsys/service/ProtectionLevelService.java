package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;

import java.util.List;

public interface ProtectionLevelService {


    /**
     * 互联所有
     *
     * @return
     */
    public List<BaseDictAll> getInternetAll();


    /**
     * 互联所有value的数据
     * @return
     */
    public  String[] getInternetAllValues();

    /**
     * 网络所有
     * @return
     */
    public List<BaseDictAll> getNtworkAll();


    /**
     * 网络所有value的数据
     * @return
     */
    public  String[] getNtworkAllValues();


}
