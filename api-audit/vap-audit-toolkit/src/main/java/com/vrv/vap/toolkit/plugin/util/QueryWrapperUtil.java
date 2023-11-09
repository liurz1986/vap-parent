package com.vrv.vap.toolkit.plugin.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.vo.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * @Description 拼接查询条件工具类
 * Created by lizj on 2021/5/13
 */
public class QueryWrapperUtil {

    /**
     * 不用加入条件的字段
     */
    private final static Set<String> DefQuery = new HashSet<>(Arrays.asList(new String[]{"start", "count", "order", "by", "startTime", "endTime"}));

    /**
     * 拼接查询条件
     *
     * @param queryWrapper 条件对象
     * @param query        数据实体
     * @return void 返回参数说明
     * @exception/throws
     */
    public static void convertQuery(QueryWrapper queryWrapper, Query query) {
        // 通用时间过滤
        Date startTime = query.getMyStartTime();
        Date endTime = query.getMyEndTime();
        try {
            Class clazz = query.getClass();
            // 反射遍历属性
            for (Field field : clazz.getDeclaredFields()) {
                // 获取属性名
                String fieldName = field.getName();
                if (DefQuery.contains(fieldName)) {
                    continue;
                }
                // 抑制Java对修饰符的检查
                field.setAccessible(true);
                // 获取属性值
                Object fieldValue = field.get(query);
//            String fieldValue = getFieldValue(obj ,field.getName()).toString();
                // 查询注解
                QueryWapper queryWapperAnnotation = AnnotationUtils.getAnnotation(field, QueryWapper.class);
                /*if (ObjectUtils.isEmpty(queryWapperAnnotation)) {
                    continue;
                }*/

                // 驼峰转下划线
                fieldName = CommonTools.camelToUnderLine(fieldName);
                // 获取枚举
                QueryWapperEnum queryWapperEnum = queryWapperAnnotation != null ? queryWapperAnnotation.queryWapperEnum() : QueryWapperEnum.EQ;
                // 拼接查询条件
                switch (queryWapperEnum) {
                    case EQ:
                        queryWrapper.eq(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                    case LIKE:
                        queryWrapper.like(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                    case TIME_RANGE:
//                        if field.getAnnotatedType()
                        if (startTime != null && endTime != null) {
                            queryWrapper.between(fieldName, startTime, endTime);
                        } else if (startTime != null) {
                            queryWrapper.ge(fieldName, startTime);
                        } else if (endTime != null) {
                            queryWrapper.le(fieldName, endTime);
                        }
                        break;
                    case IN:
                        queryWrapper.in(fieldName, fieldValue);
                        break;
                    case LIKE_LEFT:
                        queryWrapper.likeLeft(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                    case LIKE_RIGHT:
                        queryWrapper.likeRight(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                    case IGNORE:
                        break;
                    case NOT_EMPTY:
                        queryWrapper.isNotNull(fieldName);
                        break;
                    case MORE_THAN:
                        queryWrapper.gt(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                    case LESS_THAN:
                        queryWrapper.lt(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                    default:
                        queryWrapper.eq(!ObjectUtils.isEmpty(fieldValue), fieldName, fieldValue);
                        break;
                }
            }
            // 拼接排序条件
            if (StringUtils.isNotEmpty(query.getOrder()) && StringUtils.isNotEmpty(query.getBy())) {
                String[] orders = query.getOrder().split(",");
                for (int i = 0; i < orders.length; i++) {
                    orders[i] = CommonTools.camelToUnderLine(orders[i]);
                }
                if ("desc".equals(query.getBy())) {
                    queryWrapper.orderByDesc(orders);
                } else if ("asc".equals(query.getBy())) {
                    queryWrapper.orderByAsc(orders);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取属性名
     *
     * @exception/throws
     */
    private static String getFieldValue(Object owner, String fieldName) {
        try {
            return invokeMethod(owner, fieldName, null).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
