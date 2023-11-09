package com.vrv.vap.toolkit.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.ValidateTools.RetMsg;

import java.util.List;

/**
 * 返回构造器
 *
 * @author xw
 * @date 2018年4月12日
 */
public class VoBuilder {

    /**
     * 自定义返回内容
     *
     * @RetMsgEnum rm
     * @return
     */
    public static Result result(RetMsgEnum rm) {
        return new Result(rm.getCode(), rm.getMsg());
    }

    /**
     * 自定义返回内容
     *
     * @RetMsg rm
     * @return
     */
    public static Result result(RetMsg rm) {
        return new Result(rm.getCode(), rm.getMsg());
    }

    /**
     * 自定义返回内容
     *
     * @Result result
     * @return
     */
    public static Result result(Result result) {
        return result;
    }

    public static Result result(RetMsgEnum rm, String message) {
        return new Result(rm.getCode(), message);
    }

    /**
     * 返回成功
     *
     * @return
     */
    public static Result success() {
        return result(RetMsgEnum.SUCCESS);
    }

    public static Result fail() {
        return result(RetMsgEnum.FAIL);
    }

    /**
     * 返回失败
     *
     * @return
     */
    public static Result fail(String message) {
        return result(RetMsgEnum.FAIL, message);
    }

    public static Result failOnParam(){
        return result(RetMsgEnum.ERROR_PARAM);
    }

    public static Result failUnknown(){
        return result(RetMsgEnum.EMPTY_PARAM);
    }

    /**
     * 返回服务异常
     *
     * @return
     */
    public static Result error() {
        return result(RetMsgEnum.SERVER_ERROR);
    }

    /**
     * 返回服务异常
     *
     * @return
     */
    public static <T> VList<T> errorVlist() {
        return rvl(RetMsgEnum.SERVER_ERROR);
    }

    /**
     * 返回服务异常
     *
     * @return
     */
    public static <T> VData<T> errorVdata() {
        return rvd(RetMsgEnum.SERVER_ERROR);
    }

    /**
     * 自定义提示ist返回
     *
     * @RetMsgEnum rm
     * @return
     */
    public static <T> VList<T> rvl(RetMsgEnum rm) {
        return new VList<T>(0, null, rm);
    }

    /**
     * list成功返回
     *
     * @int total
     * @List list
     * @return
     */
    public static <T> VList<T> vl(int total, List<T> list) {
        return new VList<T>(total, list);
    }

    /**
     * list成功返回
     *
     * @long total
     * @List list
     * @return
     */
    public static <T> VList<T> vl(long total, List<T> list) {
        return new VList<T>((int) total, list);
    }

    /**
     * list成功返回
     *
     * @long total
     * @List list
     * @return
     */
    public static <T> VList<T> vl(IPage<T> page) {
        return new VList<T>((int) page.getTotal(), page.getRecords());
    }

    /**
     * 自定义提示ist返回
     *
     * @int total
     * @List list
     * @RetMsgEnum rm
     * @return
     */
    public static <T> VList<T> vl(int total, List<T> list, RetMsgEnum rm) {
        return new VList<T>(total, list, rm);
    }

    /**
     * 自定义提示ist返回
     *
     * @int total
     * @List list
     * @RetMsg rm
     * @return
     */
    public static <T> VList<T> vl(int total, List<T> list, RetMsg rm) {
        return new VList<T>(total, list, rm);
    }

    /**
     * 自定义数据返回
     *
     * @RetMsgEnum rm
     * @return
     */
    public static <T> VData<T> rvd(RetMsgEnum rm) {
        return new VData<T>(null, rm);
    }

    /**
     * 自定义数据返回
     *
     * @RetMsg rm
     * @return
     */
    public static <T> VData<T> rvd(RetMsg rm) {
        return new VData<T>(null, rm);
    }

    /**
     * 自定义数据返回
     *
     * @T data
     * @return
     */
    public static <T> VData<T> vd(T data) {
        return new VData<T>(data);
    }

    /**
     * 自定义数据返回
     *
     * @T data
     * @return
     */
    public static <T> VData<T> vd(T data, RetMsgEnum rm) {
        return new VData<T>(data, rm);
    }

    /**
     * 自定义数据返回
     *
     * @return
     */
    public static <T> VData<T> vd(T data, RetMsg rm) {
        return new VData<T>(data, rm);
    }

    /**
     * 自定义数据返回 Vdata2
     *
     * @return
     */
    public static <T> VData2<T> vd2(T data, RetMsgEnum rm, boolean success) {
        return new VData2<T>(data, rm, success);
    }
}
