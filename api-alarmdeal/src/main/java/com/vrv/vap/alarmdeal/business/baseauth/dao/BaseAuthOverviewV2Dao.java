package com.vrv.vap.alarmdeal.business.baseauth.dao;
import com.vrv.vap.alarmdeal.business.baseauth.enums.OptEnum;
import com.vrv.vap.alarmdeal.business.baseauth.util.BaseAuthUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.CoordinateVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendExtendVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendVO;
import com.vrv.vap.jpa.web.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class BaseAuthOverviewV2Dao {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthOverviewV2Dao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getPrintBrunTotal(Integer type, Integer b,Boolean istoday) {
        String sql="select count(*) as number from base_auth_print_burn where 1=1 and type= "+type;
        sql = sql+" @flter@";
        if (b==1){
            sql = sql+"and decide=0";
        }
        if(istoday){
            sql =  sql.replace("@flter@","");
        }else{
            sql= sql.replace("@flter@"," and date_format(create_time,'%Y-%m-%d') <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)");
        }
        logger.debug("审批信息sql:"+sql);
        Map<String,Object> result =  jdbcTemplate.queryForMap(sql);
        Object numObj = result.get("number");
        if(null == numObj){
            return 0;
        }
        return Integer.parseInt(String.valueOf(numObj));
    }

    public List<TrendVO> getPrintBrunTrend(Integer type, Integer b) {
        String sql="select count(*) as number,date_format(data.create_time,'%Y-%m-%d') as name from base_auth_print_burn as data  " +
                " where  DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d') and type={1}  {2}" +
                " group by date_format(data.create_time,'%Y-%m-%d')";
        logger.debug("sql:"+sql);
        sql = sql.replace("{1}","'"+type+"'");
        if (b==1){
            sql = sql.replace("{2}","and  decide=0");
        }else {
            sql = sql.replace("{2}","");
        }
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }

    public int getTotal(boolean istoday,String table) {
        String sql="select count(*) as number from "+ table +" where 1=1 ";
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

    public List<TrendVO> getTrend(String table) {
        String sql="select count(*) as number,date_format(create_time,'%Y-%m-%d') as name from " + table +
                " where  DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(create_time,'%Y-%m-%d') " +
                " group by date_format(create_time,'%Y-%m-%d')";
        logger.debug("sql:"+sql);
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }

    public int getAllTotal(boolean b) {
      String sql="SELECT sum(number)  AS number from (select count(*) as number from base_auth_internet where 1=1  " +
              "@flter@ UNION ALL select count(*) as number from base_auth_app where 1=1  " +
              "@flter@ UNION ALL select count(*) as number from base_auth_operation where 1=1  " +
              "@flter@  UNION ALL select count(*) as number from base_auth_print_burn where 1=1  @flter@) a";
        if(b){
            sql =  sql.replaceAll("@flter@","");
        }else{
            sql= sql.replaceAll("@flter@"," and date_format(create_time,'%Y-%m-%d') <= DATE_SUB(CURDATE(), INTERVAL 1 DAY)");
        }
        logger.debug("审批信息概览获取相关总数sql:"+sql);
        Map<String,Object> result =  jdbcTemplate.queryForMap(sql);
        Object numObj = result.get("number");
        if(null == numObj){
            return 0;
        }
        return Integer.parseInt(String.valueOf(numObj));
    }

    public List<TrendVO> getAllTrend() {
        String sql="SELECT\n" +
                "\tsum( number )  AS number ,\n" +
                "NAME \n" +
                "FROM\n" +
                "\t(\n" +
                "SELECT\n" +
                "\tcount( * ) AS number,\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) AS NAME \n" +
                "FROM\n" +
                "\tbase_auth_app AS DATA \n" +
                "WHERE\n" +
                "\tDATE_SUB( CURDATE( ), INTERVAL 1 MONTH ) <= date_format( DATA.create_time, '%Y-%m-%d' ) \n" +
                "GROUP BY\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) UNION ALL\n" +
                "SELECT\n" +
                "\tcount( * ) AS number,\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) AS NAME \n" +
                "FROM\n" +
                "\tbase_auth_internet AS DATA \n" +
                "WHERE\n" +
                "\tDATE_SUB( CURDATE( ), INTERVAL 1 MONTH ) <= date_format( DATA.create_time, '%Y-%m-%d' ) \n" +
                "GROUP BY\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) UNION ALL\n" +
                "SELECT\n" +
                "\tcount( * ) AS number,\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) AS NAME \n" +
                "FROM\n" +
                "\tbase_auth_print_burn AS DATA \n" +
                "WHERE\n" +
                "\tDATE_SUB( CURDATE( ), INTERVAL 1 MONTH ) <= date_format( DATA.create_time, '%Y-%m-%d' ) \n" +
                "GROUP BY\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) UNION ALL\n" +
                "SELECT\n" +
                "\tcount( * ) AS number,\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) AS NAME \n" +
                "FROM\n" +
                "\tbase_auth_operation AS DATA \n" +
                "WHERE\n" +
                "\tDATE_SUB( CURDATE( ), INTERVAL 1 MONTH ) <= date_format( DATA.create_time, '%Y-%m-%d' ) \n" +
                "GROUP BY\n" +
                "\tdate_format( DATA.create_time, '%Y-%m-%d' ) \n" +
                "\t) a \n" +
                "GROUP BY\n" +
                "NAME";
        logger.debug("sql:"+sql);
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }

    public int getAppTotal(boolean istoday, int i) {
        String sql="select count(*) as number from base_auth_app where 1=1 and type="+i;
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

    public List<TrendVO> getAppTrend(int i) {
        String sql="select count(*) as number,date_format(create_time,'%Y-%m-%d') as name from  base_auth_app" +
                " where  DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(create_time,'%Y-%m-%d') and type=" +i +
                " group by date_format(create_time,'%Y-%m-%d')";
        logger.debug("sql:"+sql);
        List<TrendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendVO>(TrendVO.class));
        return result;
    }

    public  List<CoordinateVO> getMaintenFlagCountStatistics() {
        String sql=" select data.ip as dataX, count(*) as dataY from base_auth_operation as data " +
                "                 group by data.ip";
        logger.debug("运维权限统计(数量统计)sql:"+sql);
        List<CoordinateVO> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CoordinateVO>(CoordinateVO.class));
        return result;
    }

    public List<TrendExtendVO> getMaintenFlagMonthStatistics() {
        String sql="select data.ip as flag,date_format(data.create_time,'%Y-%m-%d') as name , count(*) as number from base_auth_operation as data where DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date_format(data.create_time,'%Y-%m-%d') \n" +
                "                  group by date_format(data.create_time,'%Y-%m-%d'),data.ip";
        logger.debug("运维权限统计(月度变化)sql:"+sql);
        List<TrendExtendVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<TrendExtendVO>(TrendExtendVO.class));
        return result;
    }
}
