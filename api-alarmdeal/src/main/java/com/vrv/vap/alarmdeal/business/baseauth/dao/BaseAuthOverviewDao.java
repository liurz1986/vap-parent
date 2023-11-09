package com.vrv.vap.alarmdeal.business.baseauth.dao;
import com.vrv.vap.alarmdeal.business.baseauth.vo.CoordinateVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendExtendVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 2023-09
 * @author liurz
 */
@Repository
public class BaseAuthOverviewDao {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthOverviewDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取总数
     * @param istoday 是不是包括今天
     * @return
     */
    public int getTotal(boolean istoday,boolean isAll,int opt){
        String sql="select count(*) as number from base_auth_config where 1=1  ";
        if(!isAll){
            sql = sql+"and opt="+opt;
        }
        sql = sql+" @flter@";
        if(istoday){
           sql =  sql.replace("@flter@","");
        }else{
           sql= sql.replace("@flter@"," and date_format(create_time,'%Y-%m-%d') <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)");
        }
        logger.debug("审批信息概览获取相关总数sql:"+sql);
        Map<String,Object> result =  jdbcTemplate.queryForMap(sql);
        Object numObj = result.get("number");
        if(null == numObj){
            return 0;
        }
        return Integer.parseInt(String.valueOf(numObj));
    }

    /**
     * 按opt条件筛选插叙近一个月的趋势情况
     *
     * select count(*) as number,date_format(create_time,'%Y-%m-%d') as name from base_auth_config where 1=1 and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(create_time,'%Y-%m-%d')
     * group by date_format(create_time,'%Y-%m-%d')
     * @param opt
     * @return
     */
    public List<TrendVO> getTrendMonthByOpt(int opt,boolean isAll){
       String sql="select count(*) as number,date_format(create_time,'%Y-%m-%d') as name from base_auth_config where @flter@ DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(create_time,'%Y-%m-%d')" +
               "group by date_format(create_time,'%Y-%m-%d')";
       if(isAll){
           sql =  sql.replace("@flter@","");
       }else{
           sql= sql.replace("@flter@","opt ="+opt +" and ");
       }
        logger.debug("按opt条件筛选插叙近一个月的趋势情况sql:"+sql);
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }
    /**
     * 目的对象为文件统计
     *
     * select count(data.id) as number from base_auth_config as data
     * inner join base_auth_type_config as config on data.type_id=config.id
     * where config.dst_obj_type='dataInfoManage' and data.opt=1
     * @param opt
     * @param istoday 是不是包括今天
     * @return
     */
    public long getFileTotalByOpt(int opt, boolean istoday) {
        String sql ="select count(data.id) as number from base_auth_config as data inner join" +
                " base_auth_type_config as config on data.type_id=config.id " +
                "where config.dst_obj_type='dataInfoManage' and data.opt="+opt;
        if(!istoday){
            sql = sql + " and date_format(data.create_time,'%Y-%m-%d') <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
        }
        logger.debug("目的对象为文件统计sql:"+sql);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        Object obj = result.get("number");
        if(null == obj){
            return 0L;
        }
        return Long.parseLong(String.valueOf(result.get("number")));
    }

    /**
     * 目的对象为文件，近一个月按天统计
     * opt : 1 打印 2 刻录
     * select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as name from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.dst_obj_type='dataInfoManage' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d')
     * and data.opt=1 group by date_format(data.create_time,'%Y-%m-%d')
     * @param opt
     * @return
     */
    public List<TrendVO> getFileTrend(int opt) {
        String sql="select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as name from base_auth_config as data inner join " +
                "base_auth_type_config as config on data.type_id=config.id " +
                " where config.dst_obj_type='dataInfoManage' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d') and data.opt=" +opt +
                " group by date_format(data.create_time,'%Y-%m-%d')";
        logger.debug("目的对象为文件，近一个月按天统计sql:"+sql);
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }

    /**
     * 系统访问权限统计(内部用户终端)数量统计
     * 1. 目的对象是用户终端(终端)
     * 2. 操作类型为访问
     * select count(data.id) as number from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.dst_obj_type='assetHost'
     * and data.opt=3
     * @param istoday 是不是包括今天
     * @return
     */
    public long getAccessHostTotal(boolean istoday) {
        String sql ="select count(data.id) as number from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.dst_obj_type='assetHost' " +
                "and data.opt=3  ";
        if(!istoday){
            sql = sql + " and date_format(data.create_time,'%Y-%m-%d') <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
        }
        logger.debug("系统访问权限统计(内部用户终端)数量统计sql:"+sql);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        Object obj = result.get("number");
        if(null == obj){
            return 0L;
        }
        return Long.parseLong(String.valueOf(result.get("number")));
    }
    /**
     * 系统访问权限统计(内部用户终端)：近一个月按天统计
     * 1.目的对象是用户终端(终端)
     * 2. 操作类型为访问
     * select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as dataName from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.dst_obj_type='assetHost' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d')
     * and data.opt=3 group by date_format(data.create_time,'%Y-%m-%d')
     * @return
     */
    public List<TrendVO> getAccessHostTrend() {
        String sql="select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as name from base_auth_config as data  " +
                "inner join base_auth_type_config as config on data.type_id=config.id where config.dst_obj_type='assetHost' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d') " +
                "and data.opt=3 group by date_format(data.create_time,'%Y-%m-%d')";
        logger.debug("系统访问权限统计(内部用户终端),近一个月按天统计sql:"+sql);
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }
    /**
     * 系统访问权限统计(外部IP)数量统计
     * @param istoday 是不是包括今天
     * 1. 源对象是外部Ip
     * 2. 操作类型为访问
     *  select count(data.id) as number from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='externalAsset'
     * and data.opt=3
     * @return
     */
    public long getExternalAssetTotal(boolean istoday) {
        String sql ="select count(data.id) as number from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='externalAsset' " +
                "and data.opt=3   ";
        if(!istoday){
            sql = sql + " and date_format(data.create_time,'%Y-%m-%d') <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
        }
        logger.debug("系统访问权限统计(外部IP)数量统计sql:"+sql);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        Object obj = result.get("number");
        if(null == obj){
            return 0L;
        }
        return Long.parseLong(String.valueOf(result.get("number")));
    }
    /**
     * 系统访问权限统计(外部IP)：近一个月按天统计
     *  1. 源对象是外部Ip
     *  2. 操作类型为访问
     *  select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as dataName from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='externalAsset' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d')
     * and data.opt=3 group by date_format(data.create_time,'%Y-%m-%d')
     * @return
     */
    public List<TrendVO> getExternalAssetTrend() {
        String sql="select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as name from base_auth_config as data " +
                "inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='externalAsset' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d') " +
                "and data.opt=3 group by date_format(data.create_time,'%Y-%m-%d')";
        logger.debug("系统访问权限统计(外部IP),近一个月按天统计sql:"+sql);
        List<TrendVO> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }
    /**
     * 运维权限统计
     * ip数量统计
     *
     * 1. 源对象是运维终端
     * 2. 操作类型为运维
     *
     *  select data.src_obj as dataX, count(*) as dataY from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='maintenHost'
     *  group by data.src_obj
     * @return
     */
    public List<CoordinateVO> getMaintenFlagCountStatistics() {
        String sql=" select data.src_obj as dataX, count(*) as dataY from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='maintenHost' " +
                " group by data.src_obj";
        logger.debug("运维权限统计(数量统计)sql:"+sql);
        List<CoordinateVO> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CoordinateVO>(CoordinateVO.class));
        return result;

    }
    /**
     * 运维权限统计(月度变化)
     *  1. 源对象是运维终端
     *  2. 操作类型为运维
     *  select data.src_obj,date_format(data.create_time,'%Y-%m-%d') as dataName , count(*) as number from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='maintenHost' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d')
     * and data.opt=4 group by date_format(data.create_time,'%Y-%m-%d'),data.src_obj
     * @return
     */
    public List<TrendExtendVO> getMaintenFlagMonthStatistics() {
        String sql="select data.src_obj as flag,date_format(data.create_time,'%Y-%m-%d') as name , count(*) as number from base_auth_config as data inner join base_auth_type_config as config on data.type_id=config.id where config.src_obj_type='maintenHost' and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d') " +
                "   and data.opt=4 group by date_format(data.create_time,'%Y-%m-%d'),data.src_obj";
        logger.debug("运维权限统计(月度变化)sql:"+sql);
        List<TrendExtendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendExtendVO>(TrendExtendVO.class));
        return result;
    }


}
