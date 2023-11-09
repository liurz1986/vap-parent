package com.vrv.vap.line.statistics;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.model.BaseLinePage;
import com.vrv.vap.line.service.KafkaSenderService;
import com.vrv.vap.line.tools.SqlTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageLoopTask extends BaseStatisticsTask{

    private KafkaSenderService kafkaSenderService = VapLineApplication.getApplicationContext().getBean(KafkaSenderService.class);
    private static DataSource dataSource = VapLineApplication.getApplicationContext().getBean(DataSource.class);
    public Connection conn =null;
    private String MESSAGE_TOPIC = "filter-data-baseline";
    private PreparedStatement insertSysUser=null;
    private PreparedStatement insertUser=null;
    private String now;
    private String pageTopic = "base-line-page";
    private String userSysSumSql = "SELECT user_ip,sys_id,ROUND(SUM(distinct_size)/SUM(time_total),4) AS frequency,ROUND(SUM(invalid_num)/SUM(size),4) AS inefficiency,ROUND(SUM(resource_num)/SUM(size),4) AS purity,data_time,SUM(invalid_num) invalid_num,SUM(resource_num) resource_num,SUM(time_total) time_total,SUM(size) size FROM base_line_sequence WHERE data_time IN dataTimes GROUP BY user_ip,sys_id,data_time";
    private String userSumSql = "SELECT user_ip,ROUND(SUM(distinct_size)/SUM(time_total),4) AS frequency,ROUND(SUM(invalid_num)/SUM(size),4) AS inefficiency,ROUND(SUM(resource_num)/SUM(size),4) AS purity,data_time,SUM(invalid_num) invalid_num,SUM(resource_num) resource_num,SUM(time_total) time_total,SUM(size) size FROM base_line_sequence WHERE data_time IN dataTimes GROUP BY user_ip,data_time";
    private String deletePageDaysSql = "DELETE FROM base_line_page_day WHERE data_time in dataTimes";
    private int days = 30;
    private float confidence = 2;

    public PageLoopTask() {
    }


    @Override
    public void execute(Map<String, Object> params) {
        log.info("遍历页面任务开始");
        KafkaProducer kafkaProducer = this.kafkaSenderService.buildProducer();
        now = TimeTools.format2(new java.util.Date());
        if(params != null && params.size() > 0){
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(params));
            confidence = jsonObject.getFloat("confidence") != null ? jsonObject.getFloat("confidence") : 2;
            days = jsonObject.getInteger("days") != null ? jsonObject.getInteger("days") : 30;
        }
        open();
        sumUser();
        sumUserSys();
        //推送消息
        Map<String,Object> kafkadata = new HashMap<>();
        kafkadata.put("index","base-line-page");
        kafkadata.put("time",now);

        Map<String,Object> kafkadatare = new HashMap<>();
        kafkadatare.put("index","base-line-page-result");
        kafkadatare.put("time",now);
        log.info("消息推送："+JSONObject.toJSONString(kafkadata));
        kafkaProducer.send(new ProducerRecord<>(MESSAGE_TOPIC, JSONObject.toJSONString(kafkadata)));
        doUserResult();
        doUserSysResult();
        log.info("消息推送："+JSONObject.toJSONString(kafkadatare));
        kafkaProducer.send(new ProducerRecord<>(MESSAGE_TOPIC, JSONObject.toJSONString(kafkadatare)));
        kafkaProducer.close();
        close();
        log.info("遍历页面任务结束");
    }

    public void open(){
        try{
            //userSysQuery = conn.prepareStatement("SELECT `user_ip`,sys_id,SUM(`size`) total, AVG(frequency) frequency_avg,STDDEV(frequency) frequency_dev ,AVG(inefficiency) inefficiency_avg,STDDEV(inefficiency) inefficiency_dev,AVG(purity) purity_avg,STDDEV(purity) purity_dev from base_line_page WHERE data_time >= ? and data_time <= ? and type = ? GROUP BY `user_ip`,sys_id");
            //userQuery = conn.prepareStatement("SELECT `user_ip`,SUM(`size`) total, AVG(frequency) frequency_avg,STDDEV(frequency) frequency_dev ,AVG(inefficiency) inefficiency_avg,STDDEV(inefficiency) inefficiency_dev,AVG(purity) purity_avg,STDDEV(purity) purity_dev from base_line_page WHERE data_time >= ? and data_time <= ? and type = ? GROUP BY `user_ip`");
//            insert = conn.prepareStatement("INSERT INTO base_line_page_result (`user_ip`, sys_id, `size`,frequency_avg,frequency_dev,inefficiency_avg,inefficiency_dev,purity_avg,purity_dev,`type`,insert_time) VALUES (?, ?, ?,?, ?, ?,?, ?, ?,?, ?)");
            conn = dataSource.getConnection();
            insertSysUser = conn.prepareStatement("INSERT INTO base_line_page_result (`user_ip`, sys_id, `size`,frequency_avg,frequency_max,frequency_min,frequency_dev,inefficiency_avg,inefficiency_dev,inefficiency_min,inefficiency_max,purity_avg,purity_dev,purity_min,purity_max,`type`,insert_time,confidence)  SELECT `user_ip`,sys_id, SUM(`size`) `size`, AVG(frequency) frequency_avg,ROUND(AVG(frequency)+'"+confidence+"'*STDDEV(frequency),4) AS frequency_max,ROUND(AVG(frequency)-'"+confidence+"'*STDDEV(frequency),4) AS frequency_min,STDDEV(frequency) frequency_dev ,AVG(inefficiency) inefficiency_avg,STDDEV(inefficiency) inefficiency_dev,ROUND(AVG(inefficiency)-'"+confidence+"'*STDDEV(inefficiency),4) AS inefficiency_min,ROUND(AVG(inefficiency)+'"+confidence+"'*STDDEV(inefficiency),4) AS inefficiency_max,AVG(purity) purity_avg,STDDEV(purity) purity_dev,ROUND(AVG(purity)-'"+confidence+"'*STDDEV(purity),4) AS purity_min,ROUND(AVG(purity)+'"+confidence+"'*STDDEV(purity),4) AS purity_max,'2' AS `type`,'"+now+"' AS insert_time,'"+confidence+"' AS confidence from base_line_page WHERE data_time >= ? and data_time <= ? and `type` = ? GROUP BY `user_ip`,sys_id");
            insertUser = conn.prepareStatement("INSERT INTO base_line_page_result (`user_ip`, `size`,frequency_avg,frequency_max,frequency_min,frequency_dev,inefficiency_avg,inefficiency_dev,inefficiency_min,inefficiency_max,purity_avg,purity_dev,purity_min,purity_max,`type`,insert_time,confidence)  SELECT  `user_ip`, SUM(`size`) `size`, AVG(frequency) frequency_avg,ROUND(AVG(frequency)+'"+confidence+"'*STDDEV(frequency),4) AS frequency_max,ROUND(AVG(frequency)-'"+confidence+"'*STDDEV(frequency),4) AS frequency_min,STDDEV(frequency) frequency_dev ,AVG(inefficiency) inefficiency_avg,STDDEV(inefficiency) inefficiency_dev,ROUND(AVG(inefficiency)-'"+confidence+"'*STDDEV(inefficiency),4) AS inefficiency_min,ROUND(AVG(inefficiency)+'"+confidence+"'*STDDEV(inefficiency),4) AS inefficiency_max,AVG(purity) purity_avg,STDDEV(purity) purity_dev,ROUND(AVG(purity)-'"+confidence+"'*STDDEV(purity),4) AS purity_min,ROUND(AVG(purity)+'"+confidence+"'*STDDEV(purity),4) AS purity_max,'1' AS `type`,'"+now+"' AS insert_time ,'"+confidence+"' AS confidence from base_line_page WHERE data_time >= ? and data_time <= ? and `type` = ? GROUP BY `user_ip`");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doUserSysResult(){
        //open();
        Date endTime = new java.sql.Date(TimeTools.getNowBeforeByDay(1).getTime());
        Date startTime = new java.sql.Date(TimeTools.getNowBeforeByDay(days).getTime());
        try{
            insertSysUser.setDate(1,startTime);
            insertSysUser.setDate(2,endTime);
            insertSysUser.setString(3,"2");
            log.info("计算主机+应用结果并入库开始");
            insertSysUser.execute();
            log.info("计算主机+应用结果并入库结束");
            /*
            userQuery.setDate(1,startTime);
            userQuery.setDate(2,endTime);
            userQuery.setString(3,"1");

            ResultSet userSysSet = userSysQuery.executeQuery();
            while (userSysSet.next()){
                insert.setString(1,userSysSet.getString("key"));
                insert.setString(2,userSysSet.getString("sys_id"));
                insert.setInt(3,userSysSet.getInt(""));
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
        //close();
    }

    public String parseDatetime(int day){
        StringBuffer times = new StringBuffer();
        times.append("(");
        times.append("'");
        times.append(TimeTools.format(TimeTools.getNowBeforeByDay(day),TimeTools.DATE_FMT));
        times.append("'");
        /*
        for(int i = 1 ;i<= day ;i++){
            if(i != 1){
                times.append(",");
            }
            times.append("'");
            times.append(TimeTools.format(TimeTools.getNowBeforeByDay(i),TimeTools.DATE_FMT));
            times.append("'");
        }*/
        times.append(")");
        return times.toString();
//        return "('2022-05-26')";
    }

    public void sumUserSys(){
        List<BaseLinePage> models = new ArrayList<>();
        KafkaProducer producer = null;
        Statement statement = null;
        try{
            statement = conn.createStatement();
            log.info("连接开始");
            ResultSet resultSet = statement.executeQuery(userSysSumSql.replace("dataTimes",parseDatetime(1)));
            log.info("连接结束");
            producer = this.kafkaSenderService.buildProducer();
            while (resultSet.next()){
                BaseLinePage m = new BaseLinePage();
                m.setType("2");
                m.setSysId(resultSet.getString("sys_id"));
                m.setUserIp(resultSet.getString("user_ip"));
                m.setDateTime(resultSet.getString("data_time"));
                m.setTimeTotal(resultSet.getInt("time_total"));
                m.setResourceNum(resultSet.getInt("resource_num"));
                m.setInvalidNum(resultSet.getInt("invalid_num"));
                m.setSize(resultSet.getInt("size"));
                m.setPurity(resultSet.getFloat("purity"));
                m.setInefficiency(resultSet.getFloat("inefficiency"));
                m.setFrequency(resultSet.getFloat("frequency"));
                models.add(m);
                String message = JSONObject.toJSONString(m);
                log.info("kafka发送："+message);
                producer.send(new ProducerRecord<>(pageTopic, message));
            }
            if(CollectionUtils.isNotEmpty(models)){
                //统计数据入库mysql
                String insertDatas = SqlTools.buildPageSql(models, now);
                statement.execute(insertDatas);
            }
            producer.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(statement != null){
                    statement.close();
                }
            }catch (Exception e){

            }
        }
    }

    public void sumUser(){
        List<BaseLinePage> models = new ArrayList<>();
        KafkaProducer producer = null;
        Statement statement = null;
        try{
            statement = conn.createStatement();
            log.info("连接开始");
            ResultSet resultSet = statement.executeQuery(userSumSql.replace("dataTimes",parseDatetime(1)));
            log.info("连接结束");
            producer = this.kafkaSenderService.buildProducer();
            while (resultSet.next()){
                BaseLinePage m = new BaseLinePage();
                m.setType("1");
                m.setUserIp(resultSet.getString("user_ip"));
                m.setDateTime(resultSet.getString("data_time"));
                m.setTimeTotal(resultSet.getInt("time_total"));
                m.setResourceNum(resultSet.getInt("resource_num"));
                m.setInvalidNum(resultSet.getInt("invalid_num"));
                m.setSize(resultSet.getInt("size"));
                m.setPurity(resultSet.getFloat("purity"));
                m.setInefficiency(resultSet.getFloat("inefficiency"));
                m.setFrequency(resultSet.getFloat("frequency"));
                models.add(m);
                String message = JSONObject.toJSONString(m);
                log.info("kafka发送："+message);
                producer.send(new ProducerRecord<>(pageTopic, message));
            }
            if(CollectionUtils.isNotEmpty(models)){
                //统计数据入库mysql
                String insertDatas = SqlTools.buildPageSql(models, now);
                statement.execute(insertDatas);
            }
            producer.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(statement != null){
                    statement.close();
                }
            }catch (Exception e){

            }
        }
    }


    public void doUserResult(){
        //open();
        Date endTime = new java.sql.Date(TimeTools.getNowBeforeByDay(1).getTime());
        Date startTime = new java.sql.Date(TimeTools.getNowBeforeByDay(days).getTime());
        try{
            insertUser.setDate(1,startTime);
            insertUser.setDate(2,endTime);
            insertUser.setString(3,"1");
            log.info("计算主机结果并入库开始");
            insertUser.execute();
            log.info("计算主机结果并入库结束");
            /*
            userQuery.setDate(1,startTime);
            userQuery.setDate(2,endTime);
            userQuery.setString(3,"1");

            ResultSet userSysSet = userSysQuery.executeQuery();
            while (userSysSet.next()){
                insert.setString(1,userSysSet.getString("key"));
                insert.setString(2,userSysSet.getString("sys_id"));
                insert.setInt(3,userSysSet.getInt(""));
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
        //close();
    }

    public void deletePageDays(int cycle){
        String times = parseDatetime(cycle);
        log.info("删除base_line_page_day表开始，条件："+times);
        Statement statement = null;
        try{
            statement = conn.createStatement();
            statement.execute(deletePageDaysSql.replace("dataTimes",times));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(statement != null){
                    statement.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void close(){
        try{
            if(insertSysUser != null){
                insertSysUser.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(insertUser != null){
                insertUser.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
