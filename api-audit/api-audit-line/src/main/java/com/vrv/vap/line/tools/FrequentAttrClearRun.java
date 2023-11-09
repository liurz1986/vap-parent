package com.vrv.vap.line.tools;

import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FrequentAttrClearRun implements Runnable{
    public static final Log log = LogFactory.getLog(FrequentAttrClearRun.class);
    private static DataSource dataSource = VapLineApplication.getApplicationContext().getBean(DataSource.class);
    private Connection conn =null;
    private static Environment env = VapLineApplication.getApplicationContext().getBean(Environment.class);
    //String sql = "INSERT INTO base_line_frequent_attr(ukey,sys_id,item,pck,`hour`,times,`type`,insert_time) SELECT ukey,sys_id,item,ROUND(AVG(pck),2) pck, ROUND(AVG(`hour`)) `hour`,GROUP_CONCAT(RIGHT(start_time,13),',',RIGHT(end_time,13) SEPARATOR ';') times, 1 AS `type` , MAX(insert_time) insert_time FROM base_line_frequent_attr WHERE insert_time >= '#startTime' and insert_time <= '#endTime' GROUP BY ukey,sys_id,item";
    String delete = "DELETE from base_line_frequent_attr WHERE type = '0' and insert_time = '#startTime'";
    String sysSql = "SELECT sys_id from base_line_frequent_attr WHERE insert_time = '#startTime' and type = '0' GROUP BY sys_id";
    String sysQuery = "INSERT INTO base_line_frequent_attr(ukey,sys_id,item,pck,`hour`,times,`type`,insert_time) SELECT ukey,'#sys_id' AS sys_id,item,ROUND(AVG(pck),2) pck, ROUND(AVG(`hour`)) `hour`,GROUP_CONCAT(RIGHT(start_time,13),',',RIGHT(end_time,13) SEPARATOR ';') times, 1 AS `type` , MAX(insert_time) insert_time FROM base_line_frequent_attr WHERE type = '0' and insert_time = '#startTime' and sys_id = '#sys_id' GROUP BY ukey,item";
    String outSql = "SELECT item,ROUND(AVG(`hour`)) `hour`,ROUND(AVG(pck),2) pck,ukey,'#sys_id' AS sys_id,'1',GROUP_CONCAT(RIGHT(start_time,13),',',RIGHT(end_time,13) SEPARATOR ';') times,MAX(insert_time) insert_time FROM base_line_frequent_attr WHERE type = '0' and insert_time = '#startTime' and sys_id = '#sys_id' GROUP BY ukey,item INTO OUTFILE '#fileName' FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\\n'";
    String MYSQL_BIN_PATH = "/var/lib/mysql/ajb_vap/";
    String loadSql ="load data local infile '"+MYSQL_BIN_PATH+"#filename' into table ajb_vap.base_line_frequent_attr FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\\\"' LINES TERMINATED BY '\\n' (item,hour,pck,ukey,sys_id,type,times,insert_time)";
    static String USER;
    static String PASSWORD;
    private String sysId;
    private String startTime;

    public FrequentAttrClearRun(String sysId, String startTime) {
        this.sysId = sysId;
        this.startTime = startTime;
    }

    static {
        USER = env.getProperty("spring.datasource.dynamic.datasource.core.username");
        PASSWORD = env.getProperty("spring.datasource.dynamic.datasource.core.password");
        log.info("mysql user :"+USER);
        log.info("mysql pwd :"+PASSWORD);
    }

    public void open(){
        try{
            if(conn == null || conn.isClosed()){
                conn = dataSource.getConnection();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        try{
            if(conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        open();
        Statement statement = null;
        try{
            statement = conn.createStatement();
            log.info("处理系统id："+sysId);
            String filename = UUID.randomUUID().toString().replaceAll("-", "")+".csv";
            String temsql = outSql.replace("#startTime",startTime).replaceAll("#sys_id",sysId).replace("#fileName",filename);
            log.info("执行："+temsql);
            statement.execute(temsql);
            log.info("导出数据成功："+filename);
            String load = loadSql.replace("#filename",filename);
            LoadTool.importDatabase2("localhost",USER,PASSWORD,load);
            try{
                File temp = new File(MYSQL_BIN_PATH+filename);
                if(temp.exists()){
                    temp.delete();
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(statement != null){
                try{
                    statement.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        close();
    }
}
