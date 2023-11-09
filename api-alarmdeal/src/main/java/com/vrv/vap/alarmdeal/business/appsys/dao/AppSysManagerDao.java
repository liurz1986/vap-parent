package com.vrv.vap.alarmdeal.business.appsys.dao;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AppSysManagerCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AppAccountManageService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerQueryVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoNewVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppQueryTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.common.model.User;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lps 2021/8/9
 */

@Repository
public class AppSysManagerDao {

    private static Logger logger = LoggerFactory.getLogger(AppSysManagerDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SelfConcernAssetService selfConcernAssetService;

    public List<AppSysManagerCacheVo> getAppSysManagerList(){
        String sql = "select service_id as serviceId,app_no as appNo,app_name as appName from app_sys_manager where service_id is not null";
        List<AppSysManagerCacheVo> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AppSysManagerCacheVo>(AppSysManagerCacheVo.class));
        return list;
    }


    // 查询慢修改 2023-08-04
    public PageRes<AppSysManagerVo> getAppSysManagerPage(AppSysManagerQueryVo appSysManagerQueryVo){
        // 条件
        String  appName=appSysManagerQueryVo.getAppName();
        String sqlWhere="";
        if(StringUtils.isNotBlank(appName)){
            sqlWhere+=" AND s.app_name like'%"+appName+"%'";
        }
        String departmentName=appSysManagerQueryVo.getDepartmentName();
        if(StringUtils.isNotBlank(departmentName)){
            sqlWhere+=" AND s.department_name like'%"+departmentName+"%'";
        }
        String secretLevel=appSysManagerQueryVo.getSecretLevel();
        if(StringUtils.isNotBlank(secretLevel)){
            sqlWhere+=" AND s.secret_level ='"+secretLevel+"'";
        }
        // 统计总数
        String countSql= " select  count(1) from app_sys_manager s where 1=1"+sqlWhere;
        Long count = jdbcTemplate.queryForObject(countSql, Long.class);
        // 分页查询
        String sql ="SELECT s.id,s.app_no AS appNo,s.app_name AS  appName,s.department_name AS  departmentName,s.department_guid AS departmentGuid,s.domain_name AS domainName,s.secret_level AS secretLevel,s.secret_company AS secretCompany,s.service_id AS serviceId , s.app_url as appUrl ,s.operation_url as operationUrl from app_sys_manager as s  where 1=1 ";
        sql=sql+sqlWhere+"  limit " +appSysManagerQueryVo.getStart_()+","+appSysManagerQueryVo.getCount_();
        List<AppSysManagerVo> list=jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<AppSysManagerVo>(AppSysManagerVo.class));
        // 分别获取账号、资源、角色数量
        if(CollectionUtils.isNotEmpty(list)){
            addOtherCount(list);
        }
        PageRes<AppSysManagerVo> page=new PageRes<>();
        page.setList(list);
        page.setTotal(count);
        page.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        page.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        return page;
    }

    /**
     * 增加账号、资源、角色数量
     * @param list
     */
    private void addOtherCount(List<AppSysManagerVo> list) {
        List<String> ids = list.stream().map(item -> item.getId()+"").collect(Collectors.toList());
        // 账号
        String sql = "select app_id,count(*) as number from app_account_manage where app_id in ("+StringUtils.join (ids,",") +") group by app_id;" ;
        List<Map<String, Object>>  accounts = jdbcTemplate.queryForList(sql);
        // 资源
        sql = "select app_id,count(*) as number from app_resource_manage where app_id in ("+StringUtils.join (ids,",") +") group by app_id;" ;
        List<Map<String, Object>>  resources = jdbcTemplate.queryForList(sql);
        // 角色
        sql = "select app_id,count(*) as number from app_role_manage where app_id in ("+StringUtils.join (ids,",") +") group by app_id;" ;
        List<Map<String, Object>>  roles = jdbcTemplate.queryForList(sql);

        // 组装数据
        for(AppSysManagerVo vo : list){
            vo.setAccountCount(getCount(accounts,vo.getId()+""));
            vo.setResourceCount(getCount(resources,vo.getId()+""));
            vo.setRoleCount(getCount(roles,vo.getId()+""));
        }
    }

    private Integer getCount(List<Map<String, Object>> list, String appId) {
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
        for (Map<String, Object> map: list){
            String appIdCur = String.valueOf(map.get("app_id"));
            Object value = map.get("number");
            Integer count = value == null?0:Integer.parseInt(String.valueOf(value));
            if(appId.equals(appIdCur)){
                return count;
            }
        }
        return 0;
    }
    public PageRes<AppSysManagerVo> getAppSysManagerImgPage(AppSysManagerQueryVo appSysManagerQueryVo) {
        String sql="SELECT s.id,s.app_no AS appNo,s.app_name AS  appName,s.department_name AS  departmentName,s.department_guid AS departmentGuid,s.domain_name AS domainName,s.secret_level AS secretLevel,s.secret_company AS secretCompany,s.service_id AS serviceId ,a.steal_leak_value as stealLeakValue ,s.app_url as appUrl , s.operation_url as operationUrl" +
                " from app_sys_manager s " +
                " left join app_steal_leak_value a on a.app_id= s.id " +
                " where 1=1 and  a.type=0";
        String  appName=appSysManagerQueryVo.getAppName();
        String sqlWhere="";
        if (Boolean.TRUE.equals(appSysManagerQueryVo.getIsJustAssetOfConcern())) {
            User currentUser = SessionUtil.getCurrentUser();
//            User currentUser=new User();
//            currentUser.setId(33);
            SelfConcernAsset selfConcernAsset=new SelfConcernAsset();
            selfConcernAsset.setType(1);
            selfConcernAsset.setUserId(String.valueOf(currentUser.getId()));
            List<SelfConcernAsset> selfConcernAssets = selfConcernAssetService.getSelfConcernAssets(selfConcernAsset);
            if (selfConcernAssets.size()>0){
                String collect = selfConcernAssets.stream().map(p -> p.getIp()).collect(Collectors.joining(",", "(", ")"));
                sqlWhere+=" AND  s.id  in "+collect+"";
            }else {
                sqlWhere+=" AND s.id is null ";
            }
        }
        if(StringUtils.isNotBlank(appName)){
            sqlWhere+=" AND s.app_name like'%"+appName+"%'";
        }
        String departmentName=appSysManagerQueryVo.getDepartmentName();
        if(StringUtils.isNotBlank(departmentName)){
            sqlWhere+=" AND s.department_name like'%"+departmentName+"%'";
        }
        String secretLevel=appSysManagerQueryVo.getSecretLevel();
        if(StringUtils.isNotBlank(secretLevel)){
            sqlWhere+=" AND s.secret_level ='"+secretLevel+"'";
        }
        if (StringUtils.isNotBlank(appSysManagerQueryVo.getBeginValue())){
            sqlWhere+=" AND a.steal_leak_value >='"+appSysManagerQueryVo.getBeginValue()+"'";
        }
        if (StringUtils.isNotBlank(appSysManagerQueryVo.getEndValue())){
            sqlWhere+=" AND a.steal_leak_value <='"+appSysManagerQueryVo.getEndValue()+"'";
        }
        if (StringUtils.isNotBlank(appSysManagerQueryVo.getPersonName())){
            String sql1=" SELECT app_account_manage.app_id FROM app_account_manage WHERE name like '%"+appSysManagerQueryVo.getPersonName()+"%'";
            List<String> strings = jdbcTemplate.queryForList(sql1, String.class);
            if (strings.size()>0){
                String inExpression = strings.stream().collect(Collectors.joining(",", "(", ")"));
                sqlWhere+=" AND  s.id  in "+inExpression+"";
            }else {
                sqlWhere+=" AND s.id is null ";
            }
        }
        String countSql= " select  count(1) from app_sys_manager s left join app_steal_leak_value a on a.app_id= s.id where 1=1"+sqlWhere;
        Long count = jdbcTemplate.queryForObject(countSql, Long.class);
        sql=sql+sqlWhere+" group by s.id " +" order by a.steal_leak_value desc limit "+appSysManagerQueryVo.getStart_()+","+appSysManagerQueryVo.getCount_();
        List<AppSysManagerVo> list=jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<AppSysManagerVo>(AppSysManagerVo.class));
        PageRes<AppSysManagerVo> page=new PageRes<>();
        page.setList(list);
        page.setTotal(count);
        page.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        page.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        return page;
    }
    public List<AppSysManagerVo> getAppSysManagerImgList(AppSysManagerQueryVo appSysManagerQueryVo) {
        String sql="SELECT s.id,s.app_no AS appNo,s.app_name AS  appName,s.department_name AS  departmentName,s.department_guid AS departmentGuid,s.domain_name AS domainName,s.secret_level AS secretLevel,s.secret_company AS secretCompany,s.service_id AS serviceId ,a.steal_leak_value as stealLeakValue, s.app_url as appUrl , s.operation_url as operationUrl" +
                " from app_sys_manager s " +
                " left join app_steal_leak_value a on a.app_id= s.id " +
                " where 1=1 ";
        String  appName=appSysManagerQueryVo.getAppName();
        String sqlWhere="";
        if (Boolean.TRUE.equals(appSysManagerQueryVo.getIsJustAssetOfConcern())) {
            User currentUser = SessionUtil.getCurrentUser();
//            User currentUser=new User();
//            currentUser.setId(33);
            SelfConcernAsset selfConcernAsset=new SelfConcernAsset();
            selfConcernAsset.setType(1);
            selfConcernAsset.setUserId(String.valueOf(currentUser.getId()));
            List<SelfConcernAsset> selfConcernAssets = selfConcernAssetService.getSelfConcernAssets(selfConcernAsset);
            if (selfConcernAssets.size()>0){
                String collect = selfConcernAssets.stream().map(p -> p.getIp()).collect(Collectors.joining(",", "(", ")"));
                sqlWhere+=" AND  s.id  in "+collect+"";
            }else {
                sqlWhere+=" AND s.id is null ";
            }
        }
        if(StringUtils.isNotBlank(appName)){
            sqlWhere+=" AND s.app_name like'%"+appName+"%'";
        }
        String departmentName=appSysManagerQueryVo.getDepartmentName();
        if(StringUtils.isNotBlank(departmentName)){
            sqlWhere+=" AND s.department_name like'%"+departmentName+"%'";
        }
        String secretLevel=appSysManagerQueryVo.getSecretLevel();
        if(StringUtils.isNotBlank(secretLevel)){
            sqlWhere+=" AND s.secret_level ='"+secretLevel+"'";
        }
        if (StringUtils.isNotBlank(appSysManagerQueryVo.getBeginValue())){
            sqlWhere+=" AND a.steal_leak_value >='"+appSysManagerQueryVo.getBeginValue()+"'";
        }
        if (StringUtils.isNotBlank(appSysManagerQueryVo.getEndValue())){
            sqlWhere+=" AND a.steal_leak_value <='"+appSysManagerQueryVo.getEndValue()+"'";
        }
        if (StringUtils.isNotBlank(appSysManagerQueryVo.getPersonName())){
            String sql1=" SELECT app_account_manage.app_id FROM app_account_manage WHERE name like '%"+appSysManagerQueryVo.getPersonName()+"%'";
            List<String> strings = jdbcTemplate.queryForList(sql1, String.class);
            if (strings.size()>0){
                String inExpression = strings.stream().collect(Collectors.joining(",", "(", ")"));
                sqlWhere+=" AND  s.id  in "+inExpression+"";
            }else {
                sqlWhere+=" AND s.id is null ";
            }
        }
        sql=sql+sqlWhere+" group by s.id  " ;
        List<AppSysManagerVo> list=jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<AppSysManagerVo>(AppSysManagerVo.class));
        return list;
    }
    /**
     * 应用系统基本信息查询
     * @param id
     * @return
     */
    public AppSysManagerVo queryOne(Integer id){
        String sql="SELECT s.id,s.app_no AS appNo,s.app_name AS  appName,s.department_name AS  departmentName,s.department_guid AS departmentGuid,s.domain_name AS domainName,s.secret_level AS secretLevel,s.secret_company AS secretCompany,s.service_id AS serviceId,count(DISTINCT a.guid) AS accountCount ,count(DISTINCT r.guid) AS roleCount,count(DISTINCT m.guid) AS resourceCount," +
                "(LENGTH(service_id) - LENGTH(REPLACE(service_id,',','')) + 1) AS serverCount"+
                " from app_sys_manager s" +
                " LEFT JOIN app_account_manage a ON s.id=a.app_id " +
                " LEFT JOIN app_role_manage r ON r.app_id=s.id "+
                " LEFT JOIN app_resource_manage m ON m.app_id=s.id "+
                " where s.id="+id;
        RowMapper<AppSysManagerVo> rowMapper = new BeanPropertyRowMapper<AppSysManagerVo>(AppSysManagerVo.class);
        AppSysManagerVo appSysManagerVo=jdbcTemplate.queryForObject(sql,rowMapper);
        String serverId = appSysManagerVo.getServiceId();
        if (StringUtils.isNotBlank(serverId)) {
            appSysManagerVo.setServerCount(serverId.split(",").length);
        } else {
            appSysManagerVo.setServerCount(0);
        }
        return appSysManagerVo;
    }

    /**
     * 某应用服务器厂商分布数据
     * @return
     */
    public Map<String,Object>  countServerGroupByType(Integer id){
        String sql="SELECT count( a.Type_Guid ) AS 'count',( SELECT t.NAME FROM asset_type t WHERE t.Guid = a.Type_Guid ) AS typeName FROM app_sys_manager s" +
                " LEFT JOIN asset a ON s.service_id LIKE concat( '%', a.Guid,'%' ) " +
                "WHERE s.id = "+id+
                " GROUP BY a.Type_Guid";

        List<Map<String,Object>> list=jdbcTemplate.queryForList(sql) ;
        Map<String,Object> result=new HashMap<>();
        for(Map<String,Object> map : list){
            if((map.get("typeName")!=null)){
                result.put(map.get("typeName").toString(),map.get("count"));
            }else{
                result.put("未知",map.get("count"));
            }

        }
        return result;
    }

    /**
     * 某应用资源类型分布数据
     * @return
     */
    public Map<String,Object>  countResourceGroupByType(Integer id){
        String sql="SELECT count( r.resource_type ) AS 'count',IF(r.resource_type = 1, '业务', '管理' ) AS 'type' " +
                "FROM app_sys_manager s " +
                "LEFT JOIN app_resource_manage r ON s.id = r.app_id " +
                "WHERE s.id ="+id+
                " GROUP BY r.resource_type";
        List<Map<String,Object>> list=jdbcTemplate.queryForList(sql) ;
        Map<String,Object> result=new HashMap<>();
        for(Map<String,Object> map : list){
            result.put(map.get("type").toString(),map.get("count"));
        }
        return result;
    }


    /**
     * 服务器列表数据
     * @param id
     * @return
     */
    public  List<Map<String,Object>> getServerList(Integer id){
        String sql="SELECT a.Guid AS guid,a.Name AS name ,a.IP AS ip,a.mac AS mac,a.serial_number AS serialNumber,( SELECT t.NAME FROM asset_type t WHERE t.Guid = a.Type_Guid ) AS typeName,(SELECT extendInfos from asset_extend d where d.assetGuid=a.Guid) AS assetExtend" +
                " FROM app_sys_manager s " +
                "left JOIN asset a ON s.service_id LIKE concat('%', a.Guid,'%')" +
                " WHERE s.id="+id;
        List<Map<String,Object>> list=jdbcTemplate.queryForList(sql) ;
        for(Map<String,Object> map : list){
            if(map.get("assetExtend")!=null){
                Map<String,Object> extend = JSONObject.parseObject(String.valueOf(map.get("assetExtend")),Map.class);
                // Map<String,Object> extend1 =new Gson().fromJson(map.get("assetExtend").toString(),Map.class);
                map.put("assetExtend",extend);
            }

        }
        return list;
    }


    /**
     * 账号应用
     * @param appSysManagerQueryVo
     * @return
     */
    public PageRes<Map<String,Object>> getAppAccountAssetPage(AppSysManagerQueryVo appSysManagerQueryVo){

        String sql="select a.account_name AS accountName,s.app_name,a.create_time AS createTime,a.cancel_time AS cancelTime  from  app_account_manage a " +
                "INNER JOIN app_sys_manager s ON a.app_id=s.id"+
                 " where 1=1";
        String sqlWhere="";
        String appAccountName=appSysManagerQueryVo.getAppAccount();
        String personNo=appSysManagerQueryVo.getPersonNo();
        if(StringUtils.isNotBlank(appAccountName)){
            sqlWhere+=" AND a.account_name='"+appAccountName+"'";
        }
        if(StringUtils.isNotBlank(personNo)){
            sqlWhere+=" AND a.person_no='"+personNo+"'";
        }
        String countSql= "select count(1)  from  app_account_manage a INNER JOIN app_sys_manager s ON a.app_id=s.id where 1=1"+sqlWhere;
        Long count = jdbcTemplate.queryForObject(countSql, Long.class);
        sql=sql+sqlWhere+" group by s.id limit " +appSysManagerQueryVo.getStart_()+","+appSysManagerQueryVo.getCount_();
        List<Map<String,Object>> list=jdbcTemplate.queryForList(sql);
        PageRes<Map<String,Object>> page=new PageRes<>();
        page.setList(list);
        page.setTotal(count);
        page.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        page.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        return page;
    }


    /**
     * 应用系统汇总 ：非涉密：等级为非密 ，其他：涉密
     * @return
     */
    public AppQueryTotalVO appQueryTotal(String appParentType) {
        String sql="SELECT count(1) AS allCount,count( CASE WHEN dict.code_value = '非密' THEN 1 END ) AS level1,count( CASE WHEN dict.code_value != '非密' THEN 1 END ) AS level2 FROM app_sys_manager " +
                " as app inner join base_dict_all as dict on app.secret_level = dict.code where dict.parent_type='{0}'";
        sql = sql.replace("{0}",appParentType);
        AppQueryTotalVO data = jdbcTemplate.queryForObject(sql, new AppQueryTotalVOMapper());
        return data;
    }

    /**
     * 获取最大的id
     * 2022-07-14
     * @return
     */
    public int getCurrentMaxId() {
        String sql="select max(id) from app_sys_manager";
        Integer maxId = jdbcTemplate.queryForObject(sql,Integer.class);
        if(null == maxId){
            return 0;
        }
        return  maxId;
    }

    public class AppQueryTotalVOMapper implements RowMapper<AppQueryTotalVO> {
        @Override
        public AppQueryTotalVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppQueryTotalVO detail = new AppQueryTotalVO();
            detail.setAppTotalNum(rs.getString("allCount")==null?0:Integer.parseInt(rs.getString("allCount")));
            detail.setNoSecretNum(rs.getString("level1")==null?0:Integer.parseInt(rs.getString("level1")));
            detail.setSecretNum(rs.getString("level2")==null?0:Integer.parseInt(rs.getString("level2")));
            return detail;
        }
    }

    /**
     * 应用数量按类型统计 ；f5a4ae5b-3cee-a84f-7471-8f23ezjg1100
     * @return
     */
    public  List<AssetStatisticsVO> queryAppSecretlevelTotal(String appParentType) {
        String sql ="select dict.code_value as typeName,count(app.secret_level) as number from app_sys_manager as app inner join base_dict_all as dict on app.secret_level = dict.code" +
                " where dict.parent_type='{0}' group by dict.code_value";
        sql = sql.replace("{0}",appParentType);
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }

    public class AssetStatisticsVOMapper implements RowMapper<AssetStatisticsVO> {
        @Override
        public AssetStatisticsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetStatisticsVO detail = new AssetStatisticsVO();
            detail.setName(rs.getString("typeName") );
            detail.setCount(rs.getString("number")==null?0:Integer.parseInt(rs.getString("number")));
            return detail;
        }
    }

    /**
     * 应用信息统计
     * @return
     */
    public List<AppInfoVO> queryAppInfoTotal(String appParentType) {
       String sql ="select  app.app_name as appName, app.secret_company AS company,dict.code_value as secretLevel from app_sys_manager as app inner join base_dict_all as dict on app.secret_level = dict.code" +
                " where dict.parent_type='{0}'";
        sql = sql.replace("{0}",appParentType);
        List<AppInfoVO> datas = jdbcTemplate.query(sql, new AppInfoVOMapper());
        return datas;
    }

    public class AppInfoVOMapper implements RowMapper<AppInfoVO> {
        @Override
        public AppInfoVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppInfoVO detail = new AppInfoVO();
            detail.setAppName(rs.getString("appName"));
            detail.setSecretLevel(rs.getString("secretLevel"));
            detail.setCompany(rs.getString("company"));
            return detail;
        }
    }

    public List<AppInfoNewVO> queryAppTabulation(String appParentType) {
        String sql ="select  app.app_no as appNo,app.app_name as appName, app.service_id AS serviceId,dict.code_value as secretLevel from app_sys_manager as app left join base_dict_all as dict on app.secret_level = dict.code" +
                " where dict.parent_type='{0}'  order by app.app_no asc ";
        sql = sql.replace("{0}",appParentType);
        List<AppInfoNewVO> datas = jdbcTemplate.query(sql, new AppInfoNewVOMapper());
        return datas;
    }
    public class AppInfoNewVOMapper implements RowMapper<AppInfoNewVO> {
        @Override
        public AppInfoNewVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppInfoNewVO detail = new AppInfoNewVO();
            detail.setId(rowNum+1);
            detail.setAppNo(rs.getString("appNo"));
            detail.setAppName(rs.getString("appName"));
            detail.setSecret(rs.getString("secretLevel"));
            String serviceId = rs.getString("serviceId");
            if(StringUtils.isNotEmpty(serviceId)){
                detail.setServerNumber(getServerCount(serviceId));
            }else{
                detail.setServerNumber(0);
            }
            return detail;
        }
    }
    private int getServerCount(String serverIds) {
        String[] idArr = serverIds.split(",");
        List<String> idList=new ArrayList<>(Arrays.asList(idArr));
        Set<String> ids = new HashSet<>();
        for(int i= 0;i < idList.size();i++){
            ids.add(idList.get(i));
        }
        return ids.size();
    }
    public List<NameValue> countAppSecretStatistics(String appParentType) {
        String sql ="select dict.code_value as typeName,count(app.secret_level) as number from app_sys_manager as app right join base_dict_all as dict on app.secret_level = dict.code" +
                " where dict.parent_type='" +appParentType+ "' group by dict.code_value";
        List<NameValue> details = jdbcTemplate.query(sql, new NameValueMapper());
        return details;
    }

    public List<NameValue> countAppOgrStatistics() {
        String sql ="SELECT app_sys_manager.department_name as typeName, count(department_guid) as number FROM `app_sys_manager`  GROUP BY department_guid";
        List<NameValue> details = jdbcTemplate.query(sql, new NameValueMapper());
        return details;
    }

    public List<NameValue> countAppRoleStatistics() {
        String sql ="SELECT app_role_manage.app_role_name as typeName, count(app_sys_manager.app_no) as number FROM `app_sys_manager` INNER JOIN app_role_manage on app_sys_manager.id=app_role_manage.app_id\n" +
                "GROUP BY app_role_manage.app_role_name";
        List<NameValue> details = jdbcTemplate.query(sql, new NameValueMapper());
        return details;
    }



    public class NameValueMapper implements RowMapper<NameValue> {
        @Override
        public NameValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            NameValue detail = new NameValue();
            detail.setName(rs.getString("typeName") );
            detail.setValue(rs.getString("number")==null?"0":rs.getString("number"));
            return detail;
        }
    }
    public void deleteRefByAppIds(List<Integer> appIds) {
        String sql1 ="delete from app_role_manage where app_id in("+StringUtils.join(appIds,",")+")";
        jdbcTemplate.execute(sql1);
        String sql2 ="delete from app_account_manage where app_id in("+StringUtils.join(appIds,",")+")";
        jdbcTemplate.execute(sql2);
        String sql3 ="delete from app_resource_manage where app_id in("+StringUtils.join(appIds,",")+")";
        jdbcTemplate.execute(sql3);
    }
    /**
     * 应用系统统计---审批类型功能
     *
     * @date 2023-08
     * @return
     */
    public List<Map<String, Object>> getAppsAuth() {
        String sql= "select app_name as name,app_no as flag from app_sys_manager";
        return jdbcTemplate.queryForList(sql);
    }
}
