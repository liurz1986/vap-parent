package com.vrv.vap.toolkit.tools;

import com.vrv.vap.toolkit.annotations.Effective;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 字段校验
 *
 * @author xw
 * @date 2018年4月17日
 */
public final class ValidateTools {

    private static Log log = LogFactory.getLog(ValidateTools.class);

    /**
     * 全部校验
     *
     * @Object obj
     * @return
     */
    public static <T> Check<T> checkAll(Object obj) {
        Check<T> check = checkNUll(obj);
        if (check.isOk) {
            check = checkEffect(obj);
        }
        return check;
    }

    /**
     * 空值校验
     *
     * @Object obj
     * @return
     */
    public static <T> Check<T> checkNUll(Object obj) {
        for (Field field : getFields(obj)) {
            Method method = getGMethod(obj, field);
            if (method != null) {
                Object v = getValue(obj, method);
                NotNull nn = field.getAnnotation(NotNull.class);
                RetMsg rme = null;
                if (null != nn) {
                    if (null == v || StringUtils.isEmpty(v.toString())) {
                        rme = new RetMsg(field.getName() + RetMsgEnum.EMPTY_PARAM.getMsg(),
                                RetMsgEnum.EMPTY_PARAM.getCode());
                        return new Check<T>(field.getName(), rme);
                    }
                }
            }
        }
        return new Check<T>();
    }

    /**
     * 有效值校验
     *
     * @Object obj
     * @return
     */
    public static <T> Check<T> checkEffect(Object obj) {
        for (Field field : getFields(obj)) {
            Method method = getGMethod(obj, field);
            if (method != null) {
                Object v = getValue(obj, method);
                if (v != null) {
                    RetMsg rme = new RetMsg();
                    Effective eff = field.getAnnotation(Effective.class);
                    if (null != eff) {
                        if (null == obj) {
                            continue;
                        }
                        int i = 0;
                        for (String ev : eff.value()) {
                            if (ev.equals(v.toString())) {
                                break;
                            }
                            i++;
                        }
                        if (i == eff.value().length) {
                            rme = new RetMsg(field.getName() + RetMsgEnum.ERROR_PARAM.getMsg(),
                                    RetMsgEnum.ERROR_PARAM.getCode());
                            return new Check<T>(field.getName(), rme);
                        }
                    }
                }
            }
        }
        return new Check<T>();
    }

    private static Field[] getFields(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.getDeclaredFields();
    }

    private static Method getGMethod(Object obj, Field field) {
        try {
            return obj.getClass().getDeclaredMethod("get" + CommonTools.upperCaseFirstLetter(field.getName()));
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("", e);
        }
        return null;
    }

    private static Object getValue(Object obj, Method method) {
        try {
            return method.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("", null);
        }
        return null;
    }

    public static class Check<T> {
        private String field;
        private RetMsg ret;
        private boolean isOk;

        public Check() {
            this(null, new RetMsg(RetMsgEnum.SUCCESS.getMsg(), RetMsgEnum.SUCCESS.getCode()), true);
        }

        public Check(String field, RetMsg ret) {
            this(field, ret, false);
        }

        /**Check*/
        public Check(String field, RetMsg ret, boolean isOk) {
            this.field = field;
            this.ret = ret;
            this.isOk = isOk;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public RetMsg getRet() {
            return ret;
        }

        public Result getResult() {
            return VoBuilder.result(this.ret);
        }

        public VData<T> getVData() {
            return VoBuilder.vd(null, this.ret);
        }

        public VList<T> getVList() {
            return VoBuilder.vl(0, null, this.ret);
        }

        public void setRet(RetMsg ret) {
            this.ret = ret;
        }

        public boolean isOk() {
            return isOk;
        }

        public void setOk(boolean isOk) {
            this.isOk = isOk;
        }
    }

    public static class RetMsg {
        private String msg;
        private String code;

        public RetMsg() {
            this.msg = RetMsgEnum.SUCCESS.getMsg();
            this.code = RetMsgEnum.SUCCESS.getCode();
        }

        public RetMsg(String msg, String code) {
            this.msg = msg;
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
