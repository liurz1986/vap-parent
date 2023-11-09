package com.vrv.vap.data.service;

import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.data.model.Maintain;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.data.model.SourceField;

import java.util.List;
import java.util.Map;


public interface MaintainService extends BaseService<Maintain>{
    /**
     * SQL insert
     */
    Integer execInsert(String tableName, List<SourceField> sourceFields, Map data) throws ApiException;

    /**
     * SQL Update
     */
    Integer execUpdate(String tableName, String pk, List<SourceField> sourceFields, Map data) throws ApiException;

    /**
     * SQL Delete
     */
    Integer execDelete(String tableName, String pk, String[] ids) throws ApiException;
}
