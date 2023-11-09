package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.ServiceApi;
import com.vrv.vap.admin.model.ServiceApiData;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ServiceApiMapper extends BaseMapper<ServiceApi> {

    @Select("SELECT sa.id, sa.path, sa.method, sm.prefix FROM service_api sa LEFT JOIN service_module sm ON sa.service_id = sm.id")
    List<ServiceApiData> getServiceApiInfo();

    @Select("SELECT api_id FROM role_resource rr,resource_api ra WHERE rr.resource_id = ra.resource_id AND rr.role_id = #{roleId}")
    List<Integer> getApiListByRoleId(Integer roleId);

    @Delete("delete from service_api where 1=1")
    void deleteAll();
}
