package com.vrv.vap.common.controller;

import com.github.pagehelper.PageHelper;
import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.common.utils.SqlUtil;
import com.vrv.vap.common.vo.PageSupport;
import com.vrv.vap.common.vo.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * @author wh1107066
 * @date 2021/6/27 7:29
 */
public /*abstract*/ class BaseController extends ApiController {

    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

//    /**
//     * 封装，统一走这里
//     *
//     * @param t
//     * @param <T>
//     * @return
//     */
//    @GetMapping
//    protected <T> VList<T> list(T t) {
//
//        return null;
//    }

//    /**
//     * @param t
//     * @param bindingResult
//     * @param <T>
//     * @return
//     */
//    @PostMapping
//    protected <T> Result addSave(@Validated @RequestBody T t, BindingResult bindingResult) {
//        beforeAdd(t);
//        save(t);
//        afterAdd(t);
//        return this.result(true);
//    }
//
//
//    protected abstract <T> void beforeAdd(T t);
//
//    protected abstract <T> void save(T t);
//
//    protected abstract <T> void afterAdd(T t);


//    @PatchMapping
//    public <T> Result editSave(@RequestBody T t) {
//
//        return null;
//    }

//    @DeleteMapping
//    public Result remove(String ids) {
//        return this.result(true);
//    }


    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        Query query;
        try {
            query = PageSupport.buildPageRequest();
        } catch (ServletRequestBindingException e) {
            logger.error("转化异常", e);
            throw new RuntimeException("获取参数异常", e);
        }
        int pageNum = query.getStart_();
        int pageSize = query.getCount_();
        if (StringUtils.isNotBlank(query.getOrderByColumn())) {
            String orderBy = SqlUtil.escapeOrderBySql(query.getOrderByColumn());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        } else {
            PageHelper.startPage(pageNum, pageSize);
        }
    }
}
