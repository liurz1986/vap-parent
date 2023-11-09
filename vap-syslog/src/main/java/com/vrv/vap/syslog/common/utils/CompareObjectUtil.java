package com.vrv.vap.syslog.common.utils;


import com.alibaba.fastjson.JSON;
import com.vrv.vap.syslog.common.annotation.LogField;
import com.vrv.vap.syslog.model.FieldDescrDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 平台操作日志管理优化
 * https://docs.qq.com/doc/DUFlVcGlaR2ZreUxK?
 * @author wh1107066
 * @date  2021/3/4
 */
public class CompareObjectUtil {
    private static final Logger logger = LoggerFactory.getLogger(CompareObjectUtil.class);
    private static final String SERIAL_VERSION_UID = "serialVersionUID";
    private static final int SIZE = 6;
    private static final String SYMBOL = "*";

    public static String getOperationObject(Object object) {
        if (object == null) {
            return "";
        }
        Annotation annotation = object.getClass().getAnnotation(ApiModel.class);
        if (annotation != null) {
            String value = ((ApiModel) (annotation)).value();
            String description = ((ApiModel) (annotation)).description();
            return StringUtils.isEmpty(value) ? description : value;
        }
        return "";
    }

    /**
     * 不带转义字段进行比较
     *
     * @param origBean        老对象
     * @param targetBean      新对象
     * @param methodStatement 操作类型：例如，新增用户
     * @param <T>             泛型
     * @return string
     */
    public static <T> String compareObject(Object origBean, Object targetBean, String methodStatement) {
        try {
            return description(origBean, targetBean, methodStatement, null);
        } catch (Exception e) {
            logger.error("异常！", e);
        }
        return null;
    }


    /**
     * 新增的日志审计，需要转义字段进行转义。
     *
     * @param targetBean
     * @param methodStatement
     * @param fieldTransferredMeaning
     * @param <T>
     * @return
     */
    public static <T> String objectDescription(Object targetBean, String methodStatement, Map<String, Object> fieldTransferredMeaning) {
        try {
            return description(null, targetBean, methodStatement, fieldTransferredMeaning);
        } catch (Exception e) {
            logger.error("异常！", e);
        }
        return null;
    }

    /**
     * 新增的日志审计，不带转义字段。
     *
     * @param targetBean      要保存的对象
     * @param methodStatement 用户操作的描述信息：  如：添加用户
     * @param <T>             返回拼接后的message的内容
     * @return
     */
    public static <T> String objectDescription(Object targetBean, String methodStatement) {
        try {
            return description(null, targetBean, methodStatement, null);
        } catch (Exception e) {
            logger.error("异常！", e);
        }
        return null;
    }

    /**
     * 用户的修改操作，用于比较原始对象和新对象的差异属性，然后进行拼接
     *
     * @param origBean                原始对象
     * @param targetBean              新对象
     * @param methodStatement         操作方式， 如：修改用户
     * @param fieldTransferredMeaning 转义字段， 转义字段map  例如： map.put("sex", {"1":"男", 0":"女"})  用于性别转义.
     *                                KEY(字段) value(json 对应的值转义{ \"值\":\"说明\""})
     * @param <T>                     返回String类型
     * @return
     */
    public static <T> String compareObject(Object origBean, Object targetBean, String methodStatement, Map<String, Object> fieldTransferredMeaning) {
        try {
            return description(origBean, targetBean, methodStatement, fieldTransferredMeaning);
        } catch (Exception e) {
            logger.error("异常！", e);
        }
        return null;
    }


    public static <T> Object getFiledValue(T newEntity, Field field) throws IntrospectionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Class clazz = newEntity.getClass();
        PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
        Method getMethod = pd.getReadMethod();
        Object target = getMethod.invoke(newEntity);
        return target;
    }


    /**
     * 从ApiModelProperty注解中获取描述信息
     *
     * @param apiModelProperty 注解
     * @return 对象描述信息
     */
    private static String getValueFromApiModelProperty(ApiModelProperty apiModelProperty) {
        String descriptor = null;
        if (apiModelProperty != null) {
            descriptor = !StringUtils.isEmpty(apiModelProperty.value()) ? apiModelProperty.value() : apiModelProperty.name();
        }
        return descriptor;
    }

    /**
     * 从LogField注解中获取信息
     *
     * @param logField 注解
     * @return 名称
     */
    private static FieldDescrDTO getValueFromLogField(LogField logField) {
        if (logField != null) {
            String name = !StringUtils.isEmpty(logField.name()) ? logField.name() : "";
            String descriptor = !StringUtils.isEmpty(logField.description()) ? logField.description() : "";
            Boolean desensitization = logField.desensitization();
            FieldDescrDTO fieldDescrDTO = new FieldDescrDTO(name, descriptor, desensitization);
            return fieldDescrDTO;
        }
        return new FieldDescrDTO();
    }


    private static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class;
    }

    /**
     * @param origBean                oldEntity  原始的对象
     * @param targetBean              newEntity   新的对象
     * @param methodStatement         描述信息， 例如： 修改用户， 新增用户等
     * @param fieldTransferredMeaning 字段转义   转义字段， 转义字段map  例如： map.put("sex", {"1":"男", 0":"女"})  用于性别转义.
     * @param <T>
     * @return
     */
    private static <T> String description(Object origBean, Object targetBean, String methodStatement, Map<String, Object> fieldTransferredMeaning) throws Exception {
        T oldEntity = (origBean != null) ? (T) origBean : null;
        T newEntity = (targetBean != null) ? (T) targetBean : null;
        if (newEntity == null) {
            throw new RuntimeException("传入的对象为空，请检查参数列表");
        }
        StringBuilder description = new StringBuilder();
        Field[] fields = newEntity.getClass().getDeclaredFields();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (Field field : fields) {
            if (needContinue(field)) {
                continue;
            }
            Object oldFieldValue = (oldEntity != null) ? getFiledValue(oldEntity, field) : null;
            Object newFieldValue = getFiledValue(newEntity, field);
            if (logger.isDebugEnabled()) {
                logger.info("打印:" + field.getName() + "-->原始值：oldFieldValue:" + oldFieldValue + "， newFieldValue:" + newFieldValue);
            }
            LogField logField = field.getAnnotation(LogField.class);
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            String apiModelPropertyDescriptor = getValueFromApiModelProperty(apiModelProperty);
            FieldDescrDTO valueFromLogField = getValueFromLogField(logField);
            Boolean desensitization = valueFromLogField.getDesensitization() == null ? false : valueFromLogField.getDesensitization();
            String logFieldDescription = valueFromLogField.getDescription();
            // 以LogField注解的元注解为主，当LogField缺失时，以swagger的元注解为主。LogField的description优先级高。所有都有空时，需要跳过，否则界面显示null。
            String descriptor = StringUtils.isEmpty(logFieldDescription) ? apiModelPropertyDescriptor : logFieldDescription;
            if (StringUtils.isEmpty(descriptor)) {
                continue;
            }
            Object oldTransFieldValue = null;
            Object newTransFieldValue = null;
            if (fieldTransferredMeaning != null && !fieldTransferredMeaning.isEmpty()) {
                Object value = fieldTransferredMeaning.get(field.getName());
                if (value != null) {
                    // 转义
                    Map map = JSON.parseObject(String.valueOf(value), Map.class);
                    // TODO oldEntity的值
                    oldTransFieldValue = getTransferredFiledValue(oldFieldValue, desensitization, map);
                    newTransFieldValue = getTransferredFiledValue(newFieldValue, desensitization, map);
                } else {
                    // 非转义, 脱敏处理方式
                    oldTransFieldValue = getNotTransformFiledValue(oldFieldValue, desensitization);
                    newTransFieldValue = getNotTransformFiledValue(newFieldValue, desensitization);
                }
            } else {
                oldTransFieldValue = getNotTransformFiledValue(oldFieldValue, desensitization);
                newTransFieldValue = getNotTransformFiledValue(newFieldValue, desensitization);
            }
            if (logger.isDebugEnabled()) {
                logger.info(String.format("转义或脱敏后 -> newTransFieldValue: %s, oldTransFieldValue: %s", newTransFieldValue, oldTransFieldValue));
            }

//           TODO 对比值，加上管道符， 之前数据|之后数据.
            // 此处存在一个问题，如果User中的数据为封装类型Integer，Long类型，  传递的参数中为空。 数据库中的字段为int，long类型（有默认值），此处比较会有问题。跳过newFieldValue为空的情况
            if (oldFieldValue != null) {
                String newResult = newFieldValue != null ? newFieldValue.toString() : "";
                if (newFieldValue != null && !oldFieldValue.toString().equals(newResult)) {
                    if (!StringUtils.isEmpty(descriptor)) {
                        if (atomicInteger.get() != 1) {
                            description.append(";");
                        }
                        description.append(descriptor).append(":");
                        description.append(oldTransFieldValue + "|" + newTransFieldValue);
                        atomicInteger.getAndAdd(1);
                    }
                }
            } else {
                // 反射的name不能为空，newFieldValue值不能为空
                if (!StringUtils.isEmpty(descriptor) && newFieldValue != null) {
                    if (atomicInteger.get() != 1) {
                        description.append(";");
                    }
                    description.append(descriptor).append(":");
                    description.append(newTransFieldValue);
                    atomicInteger.getAndAdd(1);
                }
            }
        }

        if (!StringUtils.isEmpty(description.toString())) {
            String s = methodStatement + ":【" + description.toString() + "】";
            logger.info(s);
            return s;
        }
        return methodStatement;
    }

    /**
     * 未转化之前的字段的值
     *
     * @param newFieldValue   新对象的值
     * @param desensitization 脱敏true or false
     * @return 脱敏后的值
     */
    private static Object getNotTransformFiledValue(Object newFieldValue, Boolean desensitization) {
        if (newFieldValue != null) {
            if (desensitization) {
                newFieldValue = desensitization(String.valueOf(newFieldValue));
            }
        }
        return newFieldValue;
    }

    /**
     * 转化后字段的值
     *
     * @param newFieldValue   新对象的值
     * @param desensitization 脱敏true or false
     * @return 脱敏时需要的枚举字典
     */
    private static Object getTransferredFiledValue(Object newFieldValue, Boolean desensitization, Map map) {
        if (newFieldValue != null) {
            if (desensitization) {
                if (isPrimitive(newFieldValue.getClass())) {
                    newFieldValue = desensitization(map.get(String.valueOf(newFieldValue)).toString());
                } else {
                    logger.info("传入的map的key类型不一致，联系管理员！");
                }
            } else {
                newFieldValue = map.get(String.valueOf(newFieldValue));
            }
        }
        return newFieldValue;
    }

    /**
     * 属性字段是否需要跳过，不需要操作日志审计
     *
     * @param field 字段
     * @return 返回true 或者 false
     */
    private static boolean needContinue(Field field) {
        if (SERIAL_VERSION_UID.equalsIgnoreCase(field.getName())) {
            return true;
        }
        Ignore ignore = field.getAnnotation(Ignore.class);
        if (ignore != null) {
            return true;
        }
        return false;
    }


    /**
     * 脱敏方法
     *
     * @param value 脱敏数据对象
     * @return 返回脱敏后数据
     */
    public static String desensitization(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }

}
