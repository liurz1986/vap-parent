package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.vo.BasePersonZjgExcel;
import com.vrv.vap.admin.vo.BasePersonZjgQuery;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by CodeGenerator on 2021/08/09.
 */
public interface BasePersonZjgService extends BaseService<BasePersonZjg> {
    List<Map> queryBasePersonTrend(BasePersonZjgQuery basePersonZjgQuery);

    void importOrg( List<BasePersonZjgExcel> basePersonZjgs );

    String sync();

    void deleteAllPerson();

    void cachePerson();

    Map<String,Object> validateImportPerson(String id);

    void sendChangeMessage();

    List<Map> getAllUsersAuth();
}
