package com.vrv.vap.syslog.common.utils;

import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.syslog.common.constant.SyslogConstant;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.SyslogSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 封装直接调用发送数据
 *
 * @author wh1107066
 * @date 2021/7/2 20:54
 */
public class SyslogSenderUtils {

    private static Logger logger = LoggerFactory.getLogger(SyslogSenderUtils.class);

    /**
     * 支持手动发送,自己构建SystemLog对象
     */
    public static void sendSyslogManually(SystemLog systemLog) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendSyslogManually获取ApplicationContextUtil获取对象为空，syslogSender值为空");
        }
        syslogSender.sendSysLog(systemLog);
    }

    /**
     * 查询时，无转义
     * 用户登录后，查询时发送syslog数据
     */
    public static void sendSelectSyslog() {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendSelectSyslog获取ApplicationContextUtil获取对象为空，syslogSender值为空");
        }
        syslogSender.sendSysLog(ActionType.SELECT, null, null, SyslogConstant.SUCCESS);
    }

    /**
     * 查询时，带转义字段
     * @param newData 查询对象
     * @param title 查询标题
     * @param transferredFields 转义字段
     */
    public static void sendSelectSyslogAndTransferredField(Object newData, String title, Map<String, Object> transferredFields) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendSelectSyslogAndTransferredField获取ApplicationContextUtil获取对象为空，syslogSender值为空");
        }
        String context = CompareObjectUtil.objectDescription(newData, title, transferredFields);
        String operationObject = CompareObjectUtil.getOperationObject(newData);
        syslogSender.sendSysLog(ActionType.SELECT, context, operationObject, SyslogConstant.SUCCESS);
    }


    /**
     * 新增对象，增加审计日志，增加插入时的数据
     *
     * @param saveData 新对象
     * @param title    描述信息 如： 新增用户
     */
    public static void sendAddSyslog(Object saveData, String title) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendAddSyslog获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        String context = CompareObjectUtil.objectDescription(saveData, title);
        String operationObject = CompareObjectUtil.getOperationObject(saveData);
        syslogSender.sendSysLog(ActionType.ADD, context, operationObject, SyslogConstant.SUCCESS);
    }

    /**
     * 新增时，需要转义字段进行转义。把新增的数据一同写入到描述信息中
     *
     * @param newData           新对象
     * @param title             操作方式, 如：新增用户
     * @param transferredFields 转义字段， 转义字段map  例如： map.put("sex", {"1":"男", 0":"女"})  用于性别转义.
     *                          KEY(字段) value(json 对应的值转义{ \"值\":\"说明\""})
     */
    public static void sendAddSyslogAndTransferredField(Object newData, String title, Map<String, Object> transferredFields) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendAddSyslogAndTransferredField获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        String context = CompareObjectUtil.objectDescription(newData, title, transferredFields);
        String operationObject = CompareObjectUtil.getOperationObject(newData);
        syslogSender.sendSysLog(ActionType.ADD, context, operationObject, SyslogConstant.SUCCESS);
    }

    /**
     * 更新时，增加对比数据，不使用转义字段
     *
     * @param oldData 老对象
     * @param newData 新对象
     * @param title   描述信息如： 更新用户
     */
    public static void sendUpdateSyslog(Object oldData, Object newData, String title) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendUpdateSyslog获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        String context = CompareObjectUtil.compareObject(oldData, newData, title);
        String operationObject = CompareObjectUtil.getOperationObject(newData);
        syslogSender.sendSysLog(ActionType.UPDATE, context, operationObject, SyslogConstant.SUCCESS);
    }

    /**
     * 修改时，用于比较原始对象和新对象的差异属性，使用转义字段的map集合进行转义
     *
     * @param oldData           原始对象
     * @param newData           新对象
     * @param title             操作方式，如：修改用户
     * @param transferredFields 转义字段， 转义字段map  例如： map.put("sex", {"1":"男", 0":"女"})  用于性别转义.
     *                          KEY(字段) value(json 对应的值转义{ \"值\":\"说明\""})
     */
    public static void sendUpdateAndTransferredField(Object oldData, Object newData, String title, Map<String, Object> transferredFields) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendUpdateAndTransferredField获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        String context = CompareObjectUtil.compareObject(oldData, newData, title, transferredFields);
        String operationObject = CompareObjectUtil.getOperationObject(newData);
        syslogSender.sendSysLog(ActionType.UPDATE, context, operationObject, SyslogConstant.SUCCESS);
    }


    /**
     * 删除时，用与删除数据，无转义字段
     *
     * @param deleteData 删除对象
     * @param title      描述信息如： 删除用户
     */
    public static void sendDeleteSyslog(Object deleteData, String title) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendDeleteSyslog获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        String context = CompareObjectUtil.objectDescription(deleteData, title);
        String operationObject = CompareObjectUtil.getOperationObject(deleteData);
        syslogSender.sendSysLog(ActionType.DELETE, context, operationObject, SyslogConstant.SUCCESS);
    }

    /**
     * 删除时，用与删除数据，有转义字段
     *
     * @param deleteData 删除对象
     * @param title      描述信息如： 删除用户
     */
    public static void sendDeleteAndTransferredField(Object deleteData, String title, Map<String, Object> transferredFields) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendDeleteAndTransferredField获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        String context = CompareObjectUtil.objectDescription(deleteData, title, transferredFields);
        String operationObject = CompareObjectUtil.getOperationObject(deleteData);
        syslogSender.sendSysLog(ActionType.DELETE, context, operationObject, SyslogConstant.SUCCESS);
    }

    /**
     * 导入时，发送日志
     * 通过AOP切面获取描述信息，进行插入
     */
    public static void sendExportSyslog(){
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendExportSyslog获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        syslogSender.sendSysLog(ActionType.EXPORT, null, null, SyslogConstant.SUCCESS);
    }

    /**
     * 下载时，发送日志
     * 通过AOP切面获取描述信息，进行插入
     */
    public static void sendDownLosdSyslog(){
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        if (syslogSender == null) {
            logger.error("sendDownLosdSyslog获取ApplicationContextUtil获取对象为空，syslogSender值为空");
            return;
        }
        syslogSender.sendSysLog(ActionType.DOWNLOAD, null, null, SyslogConstant.SUCCESS);
    }
}
