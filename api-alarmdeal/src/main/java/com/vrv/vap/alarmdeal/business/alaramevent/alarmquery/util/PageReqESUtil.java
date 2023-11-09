package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util;

import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.jpa.web.page.PageReq;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月23日 10:05
 */
public class PageReqESUtil {
    public static PageReq_ES getPageReq_ES(PageReq pageReq) {
        PageReq_ES pageQuery = new PageReq_ES();
        pageQuery.setBy_(pageReq.getBy_());
        pageQuery.setCount_(pageReq.getCount_());
        pageQuery.setOrder_(pageReq.getOrder_());
        pageQuery.setStart_(pageReq.getStart_());

        if (pageQuery.getCount_() == null) {
            pageQuery.setCount_(15);
        }
        if (pageQuery.getStart_() == null) {
            pageQuery.setStart_(0);
        }
        return pageQuery;
    }
}
