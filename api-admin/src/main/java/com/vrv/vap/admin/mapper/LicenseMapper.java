package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.License;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface LicenseMapper extends BaseMapper<License> {
    int  updateResources(@Param("uids")List<String> uids);
    int  freeResource();
    int  forbiddenResource();
    int  deleteLicenseInfo();
    List<Map<String,String>> queryResources(@Param("modules") List<String> modules);
}
