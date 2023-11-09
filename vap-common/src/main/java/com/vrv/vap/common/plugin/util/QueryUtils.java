package com.vrv.vap.common.plugin.util;

import com.vrv.vap.common.plugin.annotaction.*;
import com.vrv.vap.common.vo.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.FieldHelper;

import javax.persistence.Column;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class QueryUtils {
    private static Logger logger = LoggerFactory.getLogger(QueryUtils.class);

    /**
     * 空参数，用于获取 get 方法
     */
    private final static Class[] EMPTY = new Class[0];
    /**
     * VO 反射出的条件缓存
     */
    private static Map<Class<? extends Query>, List<QueryColumn>> QueryCache = new HashMap<>();

    /**
     * 不用加入条件的字段
     */
    private final static Set<String> DefQuery = new HashSet<>(Arrays.asList(new String[]{"start_", "count_", "order_", "by_"}));

    /**
     * 根据 VO 类型获取列信息， 从缓存中获取
     */
    private static List<QueryColumn> getQueryColumns(Class<? extends Query> clazz) {

        List<EntityField> fields = FieldHelper.getFields(clazz);
        List<QueryColumn> columns = new ArrayList<>();
        for (EntityField field : fields) {
            String fieldName = field.getName();
            if (DefQuery.contains(fieldName)) {
                continue;
            }
            Method getter = null;
            String upCase = StringUtils.capitalize(fieldName);
            try {
                if (field.getJavaType() == java.lang.Boolean.class) {
                    getter = clazz.getMethod("is" + upCase, EMPTY);
                } else {
                    getter = clazz.getMethod("get" + upCase, EMPTY);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }
            if (getter == null) {
                continue;
            }
            Column columnName = field.getAnnotation(Column.class);
            if (columnName != null) {
                fieldName = columnName.name();
            }
            QueryColumn column = new QueryColumn();
            column.setFieldName(fieldName);
            if (field.getAnnotation(QueryLike.class) != null) {
                column.setCondition(QueryTypes.LIKE);
            } else if (field.getAnnotation(QueryIn.class) != null) {
                QueryIn in = field.getAnnotation(QueryIn.class);
                column.setCondition(QueryTypes.IN);
                column.setSymbol(in.value());
            } else if (field.getAnnotation(QueryLikeLeft.class) != null) {
                column.setCondition(QueryTypes.LIKE_LEFT);
            } else if (field.getAnnotation(QueryLikeRight.class) != null) {
                column.setCondition(QueryTypes.LIKE_RIGHT);
            } else if (field.getAnnotation(QueryLessThan.class) != null) {
                column.setCondition(QueryTypes.LESS_THAN);
            } else if (field.getAnnotation(QueryMoreThan.class) != null) {
                column.setCondition(QueryTypes.MORE_THAN);
            } else if (field.getAnnotation(QueryBetween.class) != null) {
                QueryBetween between = field.getAnnotation(QueryBetween.class);
                column.setSymbol(between.value());
                column.setCondition(QueryTypes.BETWEEN);
            }else if (field.getAnnotation(QueryNotEmpty.class) != null) {
                column.setCondition(QueryTypes.NOT_EMPTY);
            } else {
                column.setCondition(QueryTypes.EQ);
            }
            column.setGetter(getter);
            columns.add(column);
        }
        QueryCache.put(clazz, columns);
        return columns;
    }

    /**
     * 进行条件构造
     */
    public static void buildCondition(Example example, Query query) {
        Example.Criteria criteria = example.createCriteria();
        List<QueryColumn> columns = getQueryColumns(query.getClass());
        for (QueryColumn column : columns) {
            try {
                Object val = column.getGetter().invoke(query);
                if (val == null) {
                    continue;
                }
                QueryTypes queryType = column.getCondition();
                String field = column.getFieldName();
                if (queryType == QueryTypes.LIKE) {
                    criteria.andLike(field, "%" + val.toString() + "%");
                } else if (queryType == QueryTypes.EQ) {
                    criteria.andEqualTo(field, val);
                } else if (queryType == QueryTypes.IN) {
                    criteria.andIn(field, Arrays.asList(val.toString().split(column.getSymbol())));
                } else if (queryType == QueryTypes.LIKE_LEFT) {
                    criteria.andLike(field,   val.toString() + "%");
                } else if (queryType == QueryTypes.LIKE_RIGHT) {
                    criteria.andLike(field, "%" + val.toString());
                } else if (queryType == QueryTypes.MORE_THAN) {
                    criteria.andGreaterThan(field, val);
                } else if (queryType == QueryTypes.LESS_THAN) {
                    criteria.andLessThan(field, val);
                } else if (queryType == QueryTypes.BETWEEN) {
                    String[] patterns = val.toString().split(column.getSymbol());
                    if (patterns.length == 2) {
                        criteria.andBetween(field, patterns[0], patterns[1]);
                    }
                } else if (queryType == QueryTypes.NOT_EMPTY) {
                    if("1".equals(val)||"true".equals(val)){
                        criteria.andIsNotNull(field);
                        criteria.andNotEqualTo(field,"");
                    }
                } else {
                    criteria.andEqualTo(field, val);
                }
            } catch (IllegalAccessException e) {
                logger.error("异常1！", e);
            } catch (InvocationTargetException e) {
                logger.error("异常2！", e);
            }
        }
    }


}
