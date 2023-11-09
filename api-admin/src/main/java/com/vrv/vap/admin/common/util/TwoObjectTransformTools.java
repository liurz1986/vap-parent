package com.vrv.vap.admin.common.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class TwoObjectTransformTools {

    /**
     * 适用于两相同对象
     *
     * 复制对象属性（对象类型必须相同）
     *
     * @param orig 源对象
     * @param dest 目标对象
     * @param clazz 源对象类
     * @param ignoreNull 是否忽略空（true:忽略，false：不忽略）。true就是源为空，不复制，常用取false
     * @param ignoreExist  是否只复制dest值为空的数据  true 是，false 否。true就是目标不为空，不复制，常用true。
     * @return
     */
    public static <T> T copyProperties(T orig, T dest, Class<?> clazz, boolean ignoreNull,boolean ignoreExist) {
        if (orig == null || dest == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(orig.getClass())) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                //field.setAccessible(true);
                ReflectionUtils.makeAccessible(field);
                Object value = field.get(orig);
                Object value2 = field.get(dest);
                if(!java.lang.reflect.Modifier.isFinal(field.getModifiers())){
                    if(!(ignoreNull && value == null)){
                        if(ignoreExist && value2 != null){
                        }else{
                            field.set(dest, value);
                        }
                    }
                }
                //field.setAccessible(false);
            } catch (Exception e) {
            }
        }
        if(clazz.getSuperclass() == Object.class){
            return dest;
        }
        return copyProperties(orig, dest, clazz.getSuperclass(), ignoreNull,ignoreExist);
    }

}
