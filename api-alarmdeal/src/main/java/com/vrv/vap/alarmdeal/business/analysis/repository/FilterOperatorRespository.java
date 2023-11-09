package com.vrv.vap.alarmdeal.business.analysis.repository;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 过滤器的Respository
 * @author wd-pc
 *
 */
public interface FilterOperatorRespository extends BaseRepository<FilterOperator,String> {
}
