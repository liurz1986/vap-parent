package com.vrv.vap.syslog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.vrv.vap.common.utils.text.UUID;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.enums.ActionTypeEnums;
import com.vrv.vap.syslog.common.utils.SyslogContextHolder;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.impl.BuildAutoStrategy;
import com.vrv.vap.syslog.service.impl.BuildManuallyStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author wh1107066
 * @date 2022/8/15 11:18
 */
public abstract class AbstractSyslogSender<C> implements SyslogSender<C> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ObjectMapper objectMapper;

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("vap-syslog-pool-%d").build();
    protected Gson gson = new Gson();
    protected static ExecutorService pool = new ThreadPoolExecutor(20, 1024,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 操作审计发送到flume组件， 这个通过AOP切面给予resResult的值，所以不需要自动给
     * <p>
     * 自动构建模式，自动构建参数并进行存储
     *
     * @param systemLog syslogLog
     */
    @Override
    public void sendSysLog(C systemLog) {
        Map<String, Object> systemLogMap = new BuildAutoStrategy().buildSyslogVO((SystemLog) systemLog, null);
        pool.execute(() -> messageFlush(systemLogMap));
    }

    /**
     * 手动构建模式
     * 需要从ThreadLocal中获取之前AOP切面的数据
     * 发送审计日志到flume中进行解析与入库， 比较内容手动提交，返回的结果是success or fail
     *
     * @param actionType      操作类型
     * @param context         具体内容信息
     * @param operationObject 操作对象
     * @param resResult       操作状态
     */
    @Override
    public void sendSysLog(ActionType actionType, String context, String operationObject, String resResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("手动发送日志: {}", operationObject);
        }
        SystemLog systemLog = SyslogContextHolder.getSyslog();
        if (systemLog == null) {
            logger.warn("无法从ThreadLocal中获取数据, systemLog为空");
            systemLog = new SystemLog();
        }
        /**
         * fix bugs： 当@Ignore使用改注解，获取的id为空, 需要自动设置
         */
        if (StringUtils.isEmpty(systemLog.getId())) {
            systemLog.setId(UUID.randomUUID().toString());
        }
        systemLog.setType(ActionTypeEnums.actionTypeEnumsEscape(actionType.getName()));
        // 把原来的AOP切面的description重新覆盖一次，这次覆盖是对比后的值
        if (StringUtils.isEmpty(context)) {
            context = systemLog.getLogContext();
        }

        if (StringUtils.isNotEmpty(operationObject)) {
            systemLog.setOperationObject(operationObject);
        }

        systemLog.setDescription(context);
        Map<String, Object> systemLogMap = new BuildManuallyStrategy().buildSyslogVO(systemLog, resResult);
        if (logger.isDebugEnabled()) {
            logger.debug("sendSysLog发送操作日志信息:" + gson.toJson(systemLogMap));
        }
//        logger.info("map的值{}", ReflectionToStringBuilder.toString(systemLogMap, ToStringStyle.MULTI_LINE_STYLE));
        // TODO  fix bugs. 在feign接口调用时获取不到session的user对象时，不发送
        Object user_id = systemLogMap.get("user_id");
        if (user_id != null && StringUtils.isNotEmpty(String.valueOf(user_id))) {
            pool.execute(() -> messageFlush(systemLogMap));
        } else {
            logger.warn(String.format("syslog没有获取到user_id，值为空! 【URL】:%s, 【操作】:%s, 【内容】:%s",
                    systemLogMap.get("request_url"), actionType.getName(), context));
        }
    }

    /**
     * 消息缓存进行发送
     *
     * @param systemLogMap
     */
    public abstract void messageFlush(Map<String, Object> systemLogMap);
}
