package com.vrv.vap.toolkit.interfaces;

/**
 * 函数式接口
 * Created by lizj on 2021/3/10
 */
@FunctionalInterface
public interface StringFunction {

    /**
     * 函数调用方法
     *
     * @param s
     * @return
     */
    String apply(String s);

}

