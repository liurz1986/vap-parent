package com.vrv.vap.admin.mapper;


import com.vrv.vap.admin.model.SysLog;
import com.vrv.vap.admin.vo.ListSysLogQuery;
import com.vrv.vap.admin.vo.LoginThirtyDayVO;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
   List<SysLog> querySysLog(@Param("listSysLogQuery")ListSysLogQuery listSysLogQuery , @Param("orgNameList")List<String> orgNameList);
   long querySysLogCount(@Param("listSysLogQuery")ListSysLogQuery listSysLogQuery,@Param("orgNameList")List<String> orgNameList);
   List<LoginThirtyDayVO> loginThirtyDay(@Param("orgNameList")List<String> orgNameList,@Param("roleNameList")List<String> roleNameList);

   long getActiceUserCount();

   List<Map> getLoginCount(@Param("roleCode")String roleCode,@Param("day") Integer day);

   List<Map> loginTrend(@Param("roleCode")String roleCode,@Param("day") Integer day);

   List<Map> getResponsResultCount(@Param("roleCode")String roleCode);

   List<Map> getCommonVisitPageCount(@Param("roleCode")String roleCode,@Param("day") Integer day);

   List<Map> getUnCommonVisitPageCount(@Param("roleCode")String roleCode,@Param("day") Integer day);

   List<Map> getResponseErrorCount(@Param("roleCode")String roleCode,@Param("day") Integer day);

   List<Map> getOperateTypeCount(@Param("roleCode")String roleCode,@Param("day") Integer day);

   List<Map> getOperateTypeDetail(@Param("roleCode")String roleCode,@Param("day") Integer day,@Param("type") Integer type);

   @Delete("delete from sys_log where TO_DAYS(NOW()) - TO_DAYS(request_time) >= #{cleanDate}")
   void cleanSyslog(Integer cleanDate);
}
