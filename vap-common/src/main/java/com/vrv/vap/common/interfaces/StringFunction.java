package com.vrv.vap.common.interfaces;


/**
 * 函数式接口
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