package com.vrv.vap.toolkit.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.toolkit.annotations.LogColumn;
import com.vrv.vap.toolkit.annotations.MaskType;
import com.vrv.vap.toolkit.interfaces.StringFunction;
import com.vrv.vap.toolkit.model.FieldInfo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;


/**
 * Created by lizj on 2021/3/10
 */
public class LogAssistTools {
    private static Map<String, Map<String, String>> mappingCache = new HashMap<>();

    //获取对象差异
    public static <T> List<FieldInfo> compare(T preInfo, T info) {
        if (preInfo == null || info == null) {
            return null;
        }
        if (!preInfo.getClass().isAssignableFrom(info.getClass())) {
            return null;
        }
        List<FieldInfo> fieldInfos = new ArrayList<>();
        Field[] fields = info.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                LogColumn logColumn = field.getAnnotation(LogColumn.class);
                if (logColumn != null && !logColumn.show()) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(preInfo);
                Object value2 = field.get(info);
                boolean change = compareTwo(value, value2);
                field.setAccessible(false);
                if (!change) {
                    continue;
                }
                String fieldName = field.getName();
                String description = fieldName;
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null && StringUtils.isNotEmpty(apiModelProperty.value())) {
                    description = apiModelProperty.value();
                }
                if (logColumn != null && StringUtils.isNotEmpty(logColumn.name())) {
                    description = logColumn.name();
                }
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setName(fieldName);
                fieldInfo.setDescription(description);
                String cacheKey = info.getClass().getName() + "|" + fieldName;
                fieldInfo.setPreValue(transferValue(value, logColumn, cacheKey));
                fieldInfo.setValue(transferValue(value2, logColumn, cacheKey));
                if (logColumn != null && StringUtils.isNotEmpty(logColumn.mapping())) {
                    fieldInfo.setMapping(logColumn.mapping());
                }
                if (logColumn != null && logColumn.mask() != MaskType.NONE) {
                    fieldInfo.setMask(logColumn.mask());
                }
                fieldInfos.add(fieldInfo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldInfos;
    }

    //比对差异直接输出描述
    public static <T> String compareDesc(T preInfo, T info, String operDesc) {
        List<FieldInfo> fieldInfos = compare(preInfo, info);
        return compareDesc(fieldInfos, operDesc);
    }

    //比对差异直接输出描述
    public static <T> String compareDesc(T preInfo, T info) {
        List<FieldInfo> fieldInfos = compare(preInfo, info);
        return compareDesc(fieldInfos);
    }


    //差异数组转译为标准描述
    public static String compareDesc(List<FieldInfo> fieldInfos, String operDesc) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(operDesc).append(":");
        if (fieldInfos == null || fieldInfos.size() == 0) {
            stringBuffer.append("[无修改]");
            return stringBuffer.toString();
        }
        stringBuffer.append("[");
        fieldInfos.stream().forEach(p -> {
            stringBuffer.append(p.getName());
            stringBuffer.append(":");
            stringBuffer.append(p.getPreValue());
            stringBuffer.append("|");
            stringBuffer.append(p.getValue());
            stringBuffer.append(",");
        });
        String tmpStr = stringBuffer.toString();
        if (tmpStr.endsWith(",")) {
            tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
        }
        return tmpStr + "]";
    }

    //差异数组转译为标准描述
    public static String compareDesc(List<FieldInfo> fieldInfos) {
        StringBuilder stringBuffer = new StringBuilder();
        if (fieldInfos == null || fieldInfos.size() == 0) {
            stringBuffer.append("【无修改】");
            return stringBuffer.toString();
        }
        stringBuffer.append("【");
        fieldInfos.stream().forEach(p -> {
            stringBuffer.append(p.getName());
            stringBuffer.append(":");
            stringBuffer.append(p.getPreValue());
            stringBuffer.append("|");
            stringBuffer.append(p.getValue());
            stringBuffer.append(";");
        });
        String tmpStr = stringBuffer.toString();
        if (tmpStr.endsWith(";")) {
            tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
        }
        return tmpStr + "】";
    }


    //model转译为标准描述
    public static <T> String transferDesc(T model, String operDesc) {
        List<T> list = new ArrayList<>();
        list.add(model);
        String desc = transferDesc(list, operDesc);
        return desc;
    }

    //model转译为标准描述
    public static <T> String transferDesc(List<T> models, String operDesc) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(operDesc);
        if (models == null || models.size() == 0) {
            return stringBuffer.toString();
        }
        models.stream().forEach(m -> {
            stringBuffer.append("[");
            String desc = transferModelDesc(m);
            stringBuffer.append(desc);
            stringBuffer.append("]");
        });

        return stringBuffer.toString();
    }

    //map转译为标准描述
    public static String transferDesc(Map map, String operDesc) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(operDesc);
        if (map == null || map.size() == 0) {
            return stringBuffer.toString();
        }
        stringBuffer.append("[");
        map.keySet().forEach(k -> {
            stringBuffer.append(k);
            stringBuffer.append(":");
            stringBuffer.append(map.get(k));
            stringBuffer.append(",");
        });
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    private static <T> String transferModelDesc(T model) {
        StringBuilder stringBuffer = new StringBuilder();
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                LogColumn logColumn = field.getAnnotation(LogColumn.class);
                if (logColumn != null && !logColumn.show()) {
                    continue;
                }
                field.setAccessible(true);
                Object value2 = field.get(model);
                if (value2 instanceof String && StringUtils.isEmpty((String) value2)) {
                    continue;
                }
                String fieldName = field.getName();
                String description = fieldName;
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null && StringUtils.isNotEmpty(apiModelProperty.value())) {
                    description = apiModelProperty.value();
                }
                if (logColumn != null && StringUtils.isNotEmpty(logColumn.name())) {
                    description = logColumn.name();
                }
                stringBuffer.append(description);
                stringBuffer.append(":");
                String cacheKey = model.getClass().getName() + "|" + fieldName;
                stringBuffer.append(transferValue(value2, logColumn, cacheKey));
                stringBuffer.append(",");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }

    private static String transferValue(Object value, LogColumn logColumn, String cacheKey) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            //时间转化
        }
        if (logColumn != null && StringUtils.isNotEmpty(logColumn.mapping())) {
            if (!mappingCache.containsKey(cacheKey)) {
                mappingCache.put(cacheKey, getMapping(logColumn.mapping()));
            }
            if (mappingCache.get(cacheKey) != null && mappingCache.get(cacheKey).containsKey(value.toString())) {
                return mappingCache.get(cacheKey).get(value.toString());
            }
        }
        StringFunction sf = null;
        if (logColumn != null && logColumn.mask() != MaskType.NONE) {
            if (logColumn.mask() == MaskType.MOBILE) {
                sf = MaskTools::maskMobile;
            } else if (logColumn.mask() == MaskType.EMAIL) {
                sf = MaskTools::maskEmail;
            } else if (logColumn.mask() == MaskType.ID_CARD) {
                sf = MaskTools::maskIdCardNo;
            }
            String desc = value.toString();
            if (StringUtils.isNotEmpty(desc)) {
                return sf.apply(desc);
            }
        }

        return value.toString();
    }


    /**
     * 对比两个数据是否内容相同
     *
     * @return boolean类型
     */
    private static boolean compareTwo(Object object1, Object object2) {
        if (object1 == null && object2 == null) {
            return false;
        }
        if (object1 == null && object2 != null) {
            return true;
        }
        if (object1 instanceof String && StringUtils.isEmpty((String) object1) && StringUtils.isEmpty((String) object2)) {
            return false;
        }
        return !object1.equals(object2);
    }


    private static Map<String, String> getMapping(String mapping) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> map = objectMapper.readValue(mapping, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
