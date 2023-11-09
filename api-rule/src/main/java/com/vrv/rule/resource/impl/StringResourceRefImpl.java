package com.vrv.rule.resource.impl;

import com.vrv.rule.resource.StringResourceRef;

import java.util.Arrays;
import java.util.List;

/**
 * @author wudi
 * @date 2022/7/7 10:15
 */
public class StringResourceRefImpl implements StringResourceRef {

    private Object content;    //比较对象的值

    /**
     * 构造函数
     *
     * @param content
     */
    public StringResourceRefImpl(Object content) {
        this.content = content;
    }


    @Override
    public boolean computer(Object fieldValue, Boolean opt) {
        boolean result = false;
        if (fieldValue instanceof String && this.content instanceof String) {
            //TODO 转换成为String类型
            String fieldValueStr = (String) fieldValue;
            String contentStr = (String) this.content;
            String[] fieldStrArr = fieldValueStr.split(",");  //输入对象
            List<String> contentList = Arrays.asList(contentStr.split(","));  //被比较对象集合
            if (opt) {  //如果是包含关系：即输入对象全部都在被比较对象当中， result = true,否则result为false
                for (String field : fieldStrArr) {
                    if (contentList.contains(field)) {
                        result = true;
                    }else{
                        result = false;
                        break;
                    }
                }
            } else {   //如果是不包含关系：只要输入的对象有一个在被比较对象集合中， result = false,否则result为true
                for (String field : fieldStrArr) {
                    if (contentList.contains(field)) {
                        result = false;
                        break;
                    }else{
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
