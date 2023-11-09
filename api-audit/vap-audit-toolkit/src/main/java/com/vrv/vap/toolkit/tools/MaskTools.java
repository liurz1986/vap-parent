package com.vrv.vap.toolkit.tools;

import com.vrv.vap.toolkit.annotations.LogColumn;
import com.vrv.vap.toolkit.annotations.MaskType;
import com.vrv.vap.toolkit.interfaces.StringFunction;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by lizj on 2021/3/10
 */
public class MaskTools {
    /**
     * 隐藏手机号
     *
     * @param mobile
     * @return
     */
    public static String maskMobile(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 隐藏邮箱信息
     *
     * @param email
     * @return
     */
    public static String maskEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return email;
        }
        return email.replaceAll("(\\w+)\\w{3}@(\\w+)", "$1***@$2");
    }

    /**
     * 隐藏身份证号码
     *
     * @param id
     * @return
     */
    public static String maskIdCardNo(String id) {
        if (StringUtils.isEmpty(id) || (id.length() < 8)) {
            return id;
        }
        return id.replaceAll("(?<=\\w{6})\\w(?=\\w{6})", "*");
    }

    /**
     * 对实体列表脱敏
     *
     * @param datas
     * @param <T>
     * @return
     */
    @SuppressWarnings("uncheck")
    public static <T> List<T> maskList(List<T> datas) {
        datas.forEach(MaskTools::maskObject);
        return datas;
    }

    /**
     * 对单实体脱敏
     *
     * @param data
     * @param <T>
     * @return
     */
    @SuppressWarnings("uncheck")
    public static <T> T maskObject(T data) {
        Class clazz = data.getClass();
        Stream.of(clazz.getDeclaredFields()).forEach(f -> {
            LogColumn mask = f.getAnnotation(LogColumn.class);
            if (mask != null) {
                String fName = f.getName();
                try {
                    String methodName = fName.substring(0, 1).toUpperCase() + fName.substring(1);
                    Method getMethod = clazz.getMethod("get" + methodName, null);
                    Method setMethod = clazz.getMethod("set" + methodName, String.class);
                    StringFunction sf = null;
                    if (mask.mask() == MaskType.MOBILE) {
                        sf = MaskTools::maskMobile;
                    } else if (mask.mask() == MaskType.EMAIL) {
                        sf = MaskTools::maskEmail;
                    } else if (mask.mask() == MaskType.ID_CARD) {
                        sf = MaskTools::maskIdCardNo;
                    }
                    if (sf != null) {
                        setMethod.invoke(data, sf.apply(getMethod.invoke(data, null).toString()));
                    }
                } catch (Exception e) {

                }
            }
        });
        return data;
    }

}
