package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.dao.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.dao.EventCategoryDao;
import com.vrv.vap.alarmdeal.frameworks.util.VulCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/9/28
 */

@Repository
public class EventCategoryDaoImpl implements EventCategoryDao {


    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getGetSecondLevelEvent() {
        String sql = "select * from event_category where code_level=Concat('/safer/',code)";
        //过滤漏洞字符
        sql = VulCheckUtil.vaildLog(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

}
