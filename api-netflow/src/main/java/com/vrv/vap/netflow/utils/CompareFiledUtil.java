package com.vrv.vap.netflow.utils;

import com.github.xtool.util.ObjectUtil;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;


/**
 * @author sj
 * @version 1.0
 * @date 2023/10/9 17:42
 * @program: api-netflow
 * @description: 比较两个实体属性值
 */
public class CompareFiledUtil {


    /**
     * 比较两个实体属性值，返回一个map以有差异的属性名为key，value为一个list分别存obj1,obj2此属性名的值
     *
     * @param obj1       进行属性比较的对象1
     * @param obj2       进行属性比较的对象2
     * @param ignoreList 选择不需要比较的属性名集合
     * @return 属性差异比较结果map
     */
    public static Map<String, List<Object>> compareFields(Object obj1, Object obj2, List<String> ignoreList) {
        try {
            Map<String, List<Object>> map = new HashMap<String, List<Object>>();
            // 只有两个对象都是同一类型的才有可比性
            if (obj1.getClass() == obj2.getClass()) {
                Class claz = obj1.getClass();
                // 获取object的属性描述
                PropertyDescriptor[] pds = Introspector.getBeanInfo(claz, Object.class).getPropertyDescriptors();
                // 这里就是所有的属性了
                for (PropertyDescriptor pd : pds) {
                    // 属性名
                    String name = pd.getName();
                    // 如果当前属性选择忽略比较，跳到下一次循环
                    if (ignoreList != null && ignoreList.contains(name)) {
                        continue;
                    }
                    // get方法
                    Method readMethod = pd.getReadMethod();
                    // 在obj1上调用get方法等同于获得obj1的属性值
                    Object o1 = readMethod.invoke(obj1);
                    // 在obj2上调用get方法等同于获得obj2的属性值
                    Object o2 = readMethod.invoke(obj2);
                    if (o1 instanceof Timestamp) {
                        o1 = new Date(((Timestamp) o1).getTime());
                    }
                    if (o2 instanceof Timestamp) {
                        o2 = new Date(((Timestamp) o2).getTime());
                    }
                    if (o2 instanceof BigDecimal) {
                        o2 = ((BigDecimal) o2).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }

                    if ((ObjectUtil.isEmpty(o1) || o1.equals("null")) && (ObjectUtil.isEmpty(o2) || o2.equals("null"))) {
                        continue;
                    }
                    if (o1 == null && o2 != null) {
                        List list = new ArrayList();
                        list.add(o1);
                        list.add(o2);
                        map.put(name, list);
                        continue;
                    }
                    // 比较这两个值是否相等,不等就可以放入map了
                    if (!o1.equals(o2)) {
                        List list = new ArrayList();
                        list.add(o1);
                        list.add(o2);
                        map.put(name, list);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
