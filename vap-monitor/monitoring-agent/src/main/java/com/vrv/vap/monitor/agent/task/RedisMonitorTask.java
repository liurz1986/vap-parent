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
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Slf4j
public class RedisMonitorTask extends MonitorBaseTask {
    private BaseProperties baseProperties;
    ServerManager serverManager;
    private JedisPool jedisPool = null;
    private Jedis jedis = null;
    private String host;
    private String password;
    private Integer port;
    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        baseProperties=applicationContext.getBean(BaseProperties.class);
        serverManager=applicationContext.getBean(ServerManager.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String,MonitorConfig> monitorConfigMap=(Map<String,MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig monitorConfig = monitorConfigMap.get("redis");
        //获取组件名
        String name = monitorConfig.getName();
        log.debug("开始监控组件任务：{}",name);
        //获取组件连接信息
        Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
        //连接配置
        Map<String, String> redis = (Map<String, String>) connectConfig.get("redis");
        host = redis.get("host");
        password = redis.get("password");
        port = Integer.valueOf(redis.get("port"));
        //本机ip
        String localIp = baseProperties.getLocalIp();
        MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
        metricInfo.setStatus(1);
        long l = System.currentTimeMillis();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxTotal(100);
        try {
            jedisPool = new JedisPool(config, host, port, 2000, password);
            //连接成功
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                String redisKey = "monitortest" + l;
                String set = jedis.set(redisKey, "monitortest");
                String s = jedis.get(redisKey);
                if (!set.equals("OK")) {
                    metricInfo.setStatus(0);
                    if (monitorConfig.getAlarm()){
                        pushAlarm(AlarmTypeEnum.ALARM_REDIS_WRITE.getCode(), AlarmTypeEnum.ALARM_REDIS_WRITE.getDesc(),localIp,name);
                    }
                    if (monitorConfig.getHandler()){
                        Boolean dealResult = dealRedisAlarm("redis", localIp,monitorConfig);
                        metricInfo.setStatus(dealResult?1:0);
                    }
                }
                if (StringUtils.isBlank(s)) {
                    metricInfo.setStatus(0);
                    if (monitorConfig.getAlarm()){
                        pushAlarm(AlarmTypeEnum.ALARM_REDIS_READ.getCode(), AlarmTypeEnum.ALARM_REDIS_READ.getDesc(),localIp,name);
                    }
                    if (monitorConfig.getHandler()){
                        Boolean dealResult = dealRedisAlarm("redis", localIp,monitorConfig);
                        metricInfo.setStatus(dealResult?1:0);
                    }
                }
                if (StringUtils.isNotBlank(s)) {
                    jedis.del(redisKey);
                    log.info("redis监控结束，无异常。");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            } else {
                log.info("redis连接失败");
                metricInfo.setStatus(0);
                if (monitorConfig.getAlarm()){
                    pushAlarm(AlarmTypeEnum.ALARM_REDIS_LINK.getCode(), AlarmTypeEnum.ALARM_REDIS_LINK.getDesc(),localIp,name);
                }
                //判断执行自动处置
                try {
                    if (monitorConfig.getHandler()){
                        Boolean dealResult = dealRedisAlarm("redis", localIp,monitorConfig);
                        metricInfo.setStatus(dealResult?1:0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (jedisPool != null) {
                jedisPool.close();
            }
        }
        log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");
        pushMetric(metricInfo);
    }

    private Boolean dealRedisAlarm(String redis, String localIp,MonitorConfig config) {
        boolean status = false;
        //查询redis服务状态
        boolean b = ServiceCheckUtil.checkServiceStatus(redis);
        if (!b) {
            log.info("redis自动执行重启");

            //发送自动处理消息
            pushHandler(AlarmTypeEnum.ALARM_REDIS_DEAL.getCode(), AlarmTypeEnum.ALARM_REDIS_DEAL.getDesc(), localIp, "redis", 0);
            //重启服务
            ServiceCheckUtil.restartService(redis);
            if (checkRestartStatus(config)) {
                pushHandler(AlarmTypeEnum.ALARM_REDIS_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_REDIS_RESULT.getDesc(), "成功"), localIp, "redis", 1);
                status = true;
            } else {
                pushHandler(AlarmTypeEnum.ALARM_REDIS_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_REDIS_RESULT.getDesc(), "失败"), localIp, "redis", 0);
            }


        }
        return status;
    }

    @Override
    public void run(String jobName) {
    }



    @Override
    public Boolean restart(MonitorConfig config) {
        try {
            ServiceCheckUtil.restartService(config.getName());
        }catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig monitorConfig) {
        boolean status = false;

        try {
            Thread.currentThread().sleep(10 * 1000);

            //获取组件连接信息
            Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
            //连接配置
            Map<String, String> redis = (Map<String, String>) connectConfig.get("redis");
            host = redis.get("host");
            password = redis.get("password");
            port = Integer.valueOf(redis.get("port"));

            //服务重新检测
            if (ServiceCheckUtil.checkServiceStatus(monitorConfig.getName())) {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxIdle(100);
                config.setMaxTotal(100);
                jedisPool = new JedisPool(config, host, port, 2000, password);
                jedis = jedisPool.getResource();
                long l = System.currentTimeMillis();
                String redisKey = "monitortest" + l;
                String set = jedis.set(redisKey, "monitortest");
                String s = jedis.get(redisKey);
                if (!set.equals("OK")) {
                   status =false;
                }
                if (StringUtils.isBlank(s)) {
                    status =false;
                }
                if (StringUtils.isNotBlank(s)) {
                    jedis.del(redisKey);
                    log.info("redis监控自动处理成功。");

                    status = true;
                }
            }

        } catch (Exception e) {
            log.error("redis尝试重启失败");
            status =false;
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (jedisPool != null) {
                jedisPool.close();
            }
        }

        return status;
    }
}
