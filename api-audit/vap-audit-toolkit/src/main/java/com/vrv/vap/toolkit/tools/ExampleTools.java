package com.vrv.vap.toolkit.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * 查询条件构造辅助工具
 *
 * @author xw
 * @date 2018年4月23日
 */
public final class ExampleTools {

    /**
     * 查询条件需手动设值
     *
     * @param example
     * @return
     */
    public static Qexample buildByExample(Object example) {
        return new Qexample(null, example, null);
    }

    /**
     * 查询条件不需手动设值
     *
     * @param example
     * @return
     */
    public static Qexample buildByExample(Object record, Object example) {
        return new Qexample(record, example, null);
    }

    /**
     * 查询条件需手动设值
     *
     * @param example
     * @return
     */
    public static Qexample buildByCriteria(Object criteria) {
        return new Qexample(null, null, criteria);
    }

    /**
     * 查询条件不需手动设值
     *
     * @param example
     * @return
     */
    public static Qexample buildByCriteria(Object record, Object criteria) {
        return new Qexample(record, null, criteria);
    }

    public static class Qexample {
        private static final Log log = LogFactory.getLog(Qexample.class);

        private static final String CREATE_CRITERIA = "createCriteria";

        private static final String LIKE_FIX = "%";

        private static final int TYPE_AND = 0;
        private static final int TYPE_OR = 1;

        private static final String AND = "and";
        private static final String OR = "or";
        private static final String GET = "get";

        private static final String LIKE = "Like";
        private static final String NOT_LIKE = "NotLike";
        private static final String EQUAL = "EqualTo";
        private static final String NOT_EQUAL = "NotEqualTo";
        private static final String BETWEEN = "Between";
        private static final String NOT_BETWEEN = "NotBetween";
        private static final String IS_NULL = "IsNull";
        private static final String IS_NOT_NULL = "IsNotNull";
        private static final String IN = "In";
        private static final String NOT_IN = "NotIn";
        private static final String GREAT_THAN = "GreaterThan";
        private static final String GREAT_THAN_OR_EQUAL = "GreaterThanOrEqualTo";
        private static final String LESS_THAN = "LessThan";
        private static final String LESS_THAN_OR_EQUAL = "LessThanOrEqualTo";

        private Object record;
        private Object criteria;
        private Class<?> criteriaClazz;

        /**
         * @param record   数据实体类
         * @param example  查询实体类
         * @param criteria 查询条件实体类
         */
        public Qexample(Object record, Object example, Object criteria) {
            try {
                this.record = record;
                if (null == criteria && null != example) {
                    Optional<Method> md = getMethod(example.getClass(), CREATE_CRITERIA);
                    if (md.isPresent()) {
                        this.criteria = md.get().invoke(example);
                    }
                } else {
                    this.criteria = criteria;
                }
                if (this.criteria != null) {
                    this.criteriaClazz = this.criteria.getClass();
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.error("", e);
            }
        }

        /**
         * 设置查询数据实体类
         *
         * @param record
         * @return
         */
        public Qexample setRecord(Object record) {
            this.record = record;
            return this;
        }

        /**
         * 自动添加'%'前后通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andLike(String field) {
            String value = (String) invokeValue(getGetMethod(field));
            if (StringUtils.isNotEmpty(value)) {
                String name = getMethodName(field, LIKE, TYPE_AND);
                invoke(name, new StringBuilder().append(LIKE_FIX).append(value).append(LIKE_FIX).toString());
            }
            return this;
        }

        /**
         * 自动添加'%'前后通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andLikes(String... fields) {
            for (String field : fields) {
                String value = (String) invokeValue(getGetMethod(field));
                if (StringUtils.isNotEmpty(value)) {
                    String name = getMethodName(field, LIKE, TYPE_AND);
                    invoke(name, new StringBuilder().append(LIKE_FIX).append(value).append(LIKE_FIX).toString());
                }
            }
            return this;
        }

        /**
         * 自动添加'%'前后通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andLike(String field, String value) {
            if (StringUtils.isNotEmpty(value)) {
                String name = getMethodName(field, LIKE, TYPE_AND);
                invoke(name, new StringBuilder().append(LIKE_FIX).append(value).append(LIKE_FIX).toString());
            }
            return this;
        }

        /**
         * 自动添加'%'前通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andLikePrefix(String field, String value) {
            if (StringUtils.isNotEmpty(value)) {
                String name = getMethodName(field, LIKE, TYPE_AND);
                invoke(name, new StringBuilder().append(LIKE_FIX).append(value).toString());
            }
            return this;
        }

        /**
         * 自动添加'%'后通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andLikeSuffix(String field, String value) {
            if (StringUtils.isNotEmpty(value)) {
                String name = getMethodName(field, LIKE, TYPE_AND);
                invoke(name, new StringBuilder().append(value).append(LIKE_FIX).toString());
            }
            return this;
        }

        /**
         * 自动添加'%'前后通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andNotLike(String field, String value) {
            if (StringUtils.isNotEmpty(value)) {
                String name = getMethodName(field, NOT_LIKE, TYPE_AND);
                invoke(name, new StringBuilder().append(LIKE_FIX).append(value).append(LIKE_FIX).toString());
            }
            return this;
        }

        /**
         * 自动添加'%'前后通配符,空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample orLike(String field, String value) {
            if (StringUtils.isNotEmpty(value)) {
                String name = getMethodName(field, LIKE, TYPE_OR);
                invoke(name, new StringBuilder().append(LIKE_FIX).append(value).append(LIKE_FIX).toString());
            }
            return this;
        }

        /**
         * 空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andEqual(String field) {
            Object value = invokeValue(getGetMethod(field));
            if (null != value && !"".equals(value)) {
                String name = getMethodName(field, EQUAL, TYPE_AND);
                invoke(name, value);
            }
            return this;
        }

        /**
         * 空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andEquals(String... fields) {
            for (String field : fields) {
                Object value = invokeValue(getGetMethod(field));
                if (null != value && !"".equals(value)) {
                    String name = getMethodName(field, EQUAL, TYPE_AND);
                    invoke(name, value);
                }
            }
            return this;
        }

        /**
         * 空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andNotEqual(String field) {
            Object value = invokeValue(getGetMethod(field));
            if (null != value) {
                String name = getMethodName(field, NOT_EQUAL, TYPE_AND);
                invoke(name, value);
            }
            return this;
        }

        /**
         * 空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andEqual(String field, Object value) {
            if (null != value && !"".equals(value)) {
                String name = getMethodName(field, EQUAL, TYPE_AND);
                invoke(name, value);
            }
            return this;
        }

        /**
         * 空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample andNotEqual(String field, Object value) {
            if (null != value) {
                String name = getMethodName(field, NOT_EQUAL, TYPE_AND);
                invoke(name, value);
            }
            return this;
        }

        /**
         * 空值不添加
         *
         * @param field
         * @param value
         * @return
         */
        public Qexample orEqual(String field, Object value) {
            if (null != value && !"".equals(value)) {
                String name = getMethodName(field, EQUAL, TYPE_OR);
                invoke(name, value);
            }
            return this;
        }

        public Qexample andBetween(String field, Object value1, Object value2) {
            if (null != value1 && null != value2) {
                String name = getMethodName(field, BETWEEN, TYPE_AND);
                invoke(name, value1, value2);
            }
            return this;
        }

        public Qexample andNotBetween(String field, Object value1, Object value2) {
            if (null != value1 && null != value2) {
                String name = getMethodName(field, NOT_BETWEEN, TYPE_AND);
                invoke(name, value1, value2);
            }
            return this;
        }

        public Qexample orBetween(String field, Object value1, Object value2) {
            if (null != value1 && null != value2) {
                String name = getMethodName(field, BETWEEN, TYPE_OR);
                invoke(name, value1, value2);
            }
            return this;
        }

        public Qexample andIsNull(String field) {
            String name = getMethodName(field, IS_NULL, TYPE_AND);
            invoke(name);
            return this;
        }

        public Qexample orIsNull(String field) {
            String name = getMethodName(field, IS_NULL, TYPE_OR);
            invoke(name);
            return this;
        }

        public Qexample andIn(String field, List<?> values) {
            String name = getMethodName(field, IN, TYPE_AND);
            invoke(name, values, List.class);
            return this;
        }

        public Qexample orIn(String field, List<String> values) {
            String name = getMethodName(field, IN, TYPE_OR);
            invoke(name, values);
            return this;
        }

        public Qexample andNotIn(String field, List<String> values) {
            String name = getMethodName(field, NOT_IN, TYPE_AND);
            invoke(name, values);
            return this;
        }

        public Qexample orNotIn(String field, List<String> values) {
            String name = getMethodName(field, NOT_IN, TYPE_OR);
            invoke(name, values);
            return this;
        }

        public Qexample andIsNotNull(String field) {
            String name = getMethodName(field, IS_NOT_NULL, TYPE_AND);
            invoke(name);
            return this;
        }

        public Qexample orIsNotNull(String field) {
            String name = getMethodName(field, IS_NOT_NULL, TYPE_OR);
            invoke(name);
            return this;
        }

        public Qexample andGreaterThan(String field, Object value) {
            String name = getMethodName(field, GREAT_THAN, TYPE_AND);
            invoke(name, value);
            return this;
        }

        public Qexample orGreaterThan(String field, Object value) {
            String name = getMethodName(field, GREAT_THAN, TYPE_OR);
            invoke(name, value);
            return this;
        }

        public Qexample andGreaterThanOrEqual(String field, Object value) {
            String name = getMethodName(field, GREAT_THAN_OR_EQUAL, TYPE_AND);
            invoke(name, value);
            return this;
        }

        public Qexample orGreaterThanOrEqual(String field, Object value) {
            String name = getMethodName(field, GREAT_THAN_OR_EQUAL, TYPE_OR);
            invoke(name, value);
            return this;
        }

        public Qexample andLessThan(String field, Object value) {
            String name = getMethodName(field, LESS_THAN, TYPE_AND);
            invoke(name, value);
            return this;
        }

        public Qexample orLessThan(String field, Object value) {
            String name = getMethodName(field, LESS_THAN, TYPE_OR);
            invoke(name, value);
            return this;
        }

        public Qexample andLessThanOrEqual(String field, Object value) {
            String name = getMethodName(field, LESS_THAN_OR_EQUAL, TYPE_AND);
            invoke(name, value);
            return this;
        }

        public Qexample orLessThanOrEqual(String field, Object value) {
            String name = getMethodName(field, LESS_THAN_OR_EQUAL, TYPE_OR);
            invoke(name, value);
            return this;
        }

        /**
         * @param field 查询字段
         * @param type1 查询类型 like == 等等
         * @param type2 0是and 其他是or
         * @return
         */
        private String getMethodName(String field, String type1, int type2) {
            if (0 == type2) {
                return new StringBuilder().append(AND).append(CommonTools.upperCaseFirstLetter(field)).append(type1)
                        .toString();
            }
            return new StringBuilder().append(OR).append(CommonTools.upperCaseFirstLetter(field)).append(type1)
                    .toString();
        }

        /**
         * 获取字段的get方法
         *
         * @param field
         * @return
         */
        private String getGetMethod(String field) {
            return new StringBuilder().append(GET).append(CommonTools.upperCaseFirstLetter(field)).toString();
        }

        private Optional<Method> getMethod(String name, Class<?>... clazz) {
            return getMethod(this.criteriaClazz, name, clazz);
        }

        private Optional<Method> getMethod(Class<?> obj, String name, Class<?>... clazz) {
            try {
                return Optional.of(obj.getDeclaredMethod(name, clazz));
            } catch (NoSuchMethodException | SecurityException e) {
                log.error("", e);
            }
            return Optional.empty();
        }

        private Object invokeValue(String name) {
            Optional<Method> method = getMethod(record.getClass(), name);
            if (method.isPresent()) {
                try {
                    return method.get().invoke(record);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("", e);
                }
            }
            return null;
        }

        private Object invoke(String name) {
            Optional<Method> method = getMethod(name);
            if (method.isPresent()) {
                try {
                    return method.get().invoke(criteria);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("", e);
                }
            }
            return null;
        }

        private void invoke(String name, Object value) {
            if (null == value) {
                return;
            }
            Optional<Method> method = getMethod(name, value.getClass());
            if (method.isPresent()) {
                try {
                    method.get().invoke(criteria, value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("", e);
                }
            }
        }

        private void invoke(String name, Object value, Class vclass) {
            if (null == value) {
                return;
            }
            Optional<Method> method = getMethod(name, vclass);
            if (method.isPresent()) {
                try {
                    method.get().invoke(criteria, value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("", e);
                }
            }
        }

        private void invoke(String name, Object value1, Object value2) {
            if (null == value1 || null == value2) {
                return;
            }
            Optional<Method> method = getMethod(name, value1.getClass(), value2.getClass());
            if (method.isPresent()) {
                try {
                    method.get().invoke(criteria, value1, value2);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("", e);
                }
            }
        }
    }
}
