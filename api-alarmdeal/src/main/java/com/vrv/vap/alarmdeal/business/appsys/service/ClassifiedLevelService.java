package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;

import java.util.List;

public interface ClassifiedLevelService {
    /**
     * 互联所有
     *
     * @return
     */
    public List<BaseDictAll> getInternetAll();
    /**
     * 网络所有
     *
     * @return
     */
    public List<BaseDictAll> getNetWorkAll();

    /**
     * 应用系统所有
     *
     * @return
     */
    public List<BaseDictAll> getAppAll();


    /**
     * 数据信息
     *
     * @return
     */
    public List<BaseDictAll> getDataInfoAll();




    public String[] getAppSecretLevelAllValues();

    public String[] getDataInfoSecretLevelAllValues();

    public List<String> getDataInfoSecretLevelAllCodes();

    public String[] getInternetSecretLevelAllValues();

    public String[] getNetWorkSecretLevelAllValues();
}
