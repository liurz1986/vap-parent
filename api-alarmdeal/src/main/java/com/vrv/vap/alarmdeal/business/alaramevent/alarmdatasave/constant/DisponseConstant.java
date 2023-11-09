package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant;

/**
 * 上报事件处置的5中情况，对应的5种状态常数
 */
public class DisponseConstant {
    //待处置
    public static final String WAIT_DISPONSE = "0";
    //保密办督办,督促
    public static final String SECRET_SUPERVISE = "1";
    //上级督办
    public static final String SUPER_SUPERVISE = "2";
    //处置完成
    public static final String DISPONSE_COMPLETE = "3";
    //协办处置
    public static final String COO_DISPONSE = "4";
}
