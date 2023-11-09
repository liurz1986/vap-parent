package com.vrv.vap.common.vo;

import com.vrv.vap.common.utils.ContextHolderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author wh1107066
 * @date 2021/6/27 7:44
 */
public class PageSupport implements Serializable {
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "start_";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "count_";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "order_";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "by_";

    /**
     * 封装分页对象
     */
    public static Query pagination(ServletWebRequest request) throws ServletRequestBindingException {
        Query query = new Query();
        query.setStart_(ServletRequestUtils.getIntParameter(request.getRequest(), PageSupport.PAGE_NUM, 0));
        query.setCount_(ServletRequestUtils.getIntParameter(request.getRequest(), PageSupport.PAGE_SIZE, 10));
        query.setOrder_(ServletRequestUtils.getStringParameter(request.getRequest(), PageSupport.ORDER_BY_COLUMN));
        query.setBy_(ServletRequestUtils.getStringParameter(request.getRequest(), PageSupport.IS_ASC));
        query.setOrderByColumn(getOrderBy(query.getOrder_(), query.getBy_()));
        return query;
    }

    public static Query buildPageRequest() throws ServletRequestBindingException {
        HttpServletRequest request = ContextHolderUtil.getRequest();
        return pagination(new ServletWebRequest(request));
    }

    public static String getOrderBy(String orderByColumn, String isAsc) {
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return StringUtils.upperCase(orderByColumn) + " " + isAsc;
    }

}
