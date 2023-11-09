package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.MonitorManager;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.MetricInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.*;
import java.util.Date;
import java.util.Map;

@Slf4j
public class DatabaseMonitorTask extends MonitorBaseTask {
    private BaseProperties baseProperties;
    ServerManager serverManager;
    ResultSet rs = null;
    Connection con = null;
    Statement st = null;
    String sqlSelect = "select * from test_database";
    String sqlUpdate = "update test_database set num = 1 where id = 1";

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        baseProperties=applicationContext.getBean(BaseProperties.class);
        serverManager=applicationContext.getBean(ServerManager.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String,MonitorConfig> monitorConfigMap=(Map<String,MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig monitorConfig = monitorConfigMap.get("mysql");
        String name = monitorConfig.getName();
        log.debug("开始监控组件任务：{}",name);
        //获取组件连接信息
        Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
        //本机ip
        String localIp = baseProperties.getLocalIp();
        MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
        metricInfo.setStatus(1);
        Map<String, String> datasource = (Map<String, String>)connectConfig.get("datasource");
        String driver = datasource.get("driver-class-name");
        String url =  datasource.get("url");
        String user = datasource.get("username");
        String ppp = datasource.get("password");
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, ppp);
            st = con.createStatement();
            rs = st.executeQuery(sqlSelect);
            if (!rs.next()) {
                log.info("database监控异常");
                metricInfo.setStatus(0);
                if (monitorConfig.getAlarm()){
                    pushAlarm(AlarmTypeEnum.ALARM_DATABASE_READ.getCode(), AlarmTypeEnum.ALARM_DATABASE_READ.getDesc(),localIp,name);
                }
                if (monitorConfig.getHandler()){
                    Boolean dealResult=dealDataBaseAlram("mysqld",localIp,monitorConfig);
                    metricInfo.setStatus(dealResult?1:0);
                }
            }
            int res = st.executeUpdate(sqlUpdate);
            if (res < 1) {
                metricInfo.setStatus(0);
                if (monitorConfig.getAlarm()){
                    pushAlarm(AlarmTypeEnum.ALARM_DATABASE_WRITE.getCode(), AlarmTypeEnum.ALARM_DATABASE_WRITE.getDesc(),localIp,name);
                }
                if (monitorConfig.getHandler()){
                    Boolean dealResult=dealDataBaseAlram("mysqld",localIp,monitorConfig);
                    metricInfo.setStatus(dealResult?1:0);
                }
            }
        } catch (Exception e) {
            metricInfo.setStatus(0);
            if (monitorConfig.getAlarm()){
                pushAlarm(AlarmTypeEnum.ALARM_DATABASE_LINK.getCode(), AlarmTypeEnum.ALARM_DATABASE_LINK.getDesc(),localIp,name);
            }
            if (monitorConfig.getHandler()){
                Boolean dealResult=dealDataBaseAlram("mysqld",localIp,monitorConfig);
                metricInfo.setStatus(dealResult?1:0);
            }
            e.printStackTrace();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }

            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {

                }
            }
        }
        log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");
        pushMetric(metricInfo);
    }

    private Boolean dealDataBaseAlram(String mysqld, String localIp,MonitorConfig config) {
        boolean status=false;
        boolean b = ServiceCheckUtil.checkServiceStatus(mysqld);
        if (!b){
            log.info("database尝试重启...");
            //发送预处理信息
            pushHandler(AlarmTypeEnum.ALARM_DATABASE_DEAL.getCode(),AlarmTypeEnum.ALARM_DATABASE_DEAL.getDesc(),localIp,"mysql",0);
            ServiceCheckUtil.restartService(mysqld);
            log.info("database尝试重启中。。。");
            if(checkRestartStatus(config)){
                pushHandler(AlarmTypeEnum.ALARM_DATABASE_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_DATABASE_RESULT.getDesc(), "成功"),localIp,"mysql",1);
                status = true;
            }else {
                pushHandler(AlarmTypeEnum.ALARM_DATABASE_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_DATABASE_RESULT.getDesc(), "失败"),localIp,"mysql",0);
            }

        }
        return status;
    }

    @Override
    public void run(String jobName) {
      run(jobName,null);
    }


    @Override
    public Boolean restart(MonitorConfig config) {
        try {
            ServiceCheckUtil.restartService("mysqld");
        }catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, String> datasource = (Map<String, String>)config.getConnectConfig().get("datasource");
        if (ServiceCheckUtil.checkServiceStatus("mysqld")){
            log.info("database重启中完成，重新检测中");
            try {
                String driver = datasource.get("driver-class-name");
                String url =  datasource.get("url");
                String user = datasource.get("username");
                String ppp = datasource.get("password");
                Class.forName(driver);
                con = DriverManager.getConnection(url, user, ppp);
                st = con.createStatement();
                rs = st.executeQuery(sqlSelect);
                if (rs.next()){
                    int res = st.executeUpdate(sqlUpdate);
                    if (res>=1){
                        log.info("database自动重启完成");
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            } catch (Exception e) {
                log.error("database尝试重启失败");
                e.printStackTrace();
                return false;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {

                    }
                }

                if (st != null) {
                    try {
                        st.close();
                    } catch (SQLException e) {
                    }
                }

                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {

                    }
                }
            }
        }
        return false;
    }
}
