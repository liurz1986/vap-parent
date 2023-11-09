package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.asset.vo.ExcelValidationData;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/18
 */

public interface AbstractBaseService<T,ID extends Serializable> extends BaseService<T,ID> {


    /**
     * 校验字段 true-正确，false-错误
     * @param key-需要校验的字段，
     * @param value-对应的值
     * @return
     */
    public Boolean checkParam(String key, String value);

    /**
     * 应用系统关联的账号,资源，服务器需现在在单个应用
     * 校验字段 true-正确，false-错误
     * @param key-需要校验的字段，
     * @param value-对应的值,
     * @param appId-应用id
     * @return
     */
    public Boolean checkParam(String key, String value, Integer appId);


    public void saveMap(Map<String, Object> map, Class<T> classOfT);

    /**
     * excel 每列格式
     * @param exportType
     * @param index
     * @param colName
     * @param sheetName
     * @return
     */
    public ExcelValidationData getExcelValidationData(String exportType, int index, String colName, String sheetName, String[] classifiedLevels ,String[]  protectLevelValues,List<String> domainNames);






}
