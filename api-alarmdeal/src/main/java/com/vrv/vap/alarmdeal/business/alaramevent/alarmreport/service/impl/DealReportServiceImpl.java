package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.DealTotalReponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.DealTypeResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.DealReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:03
 */
@Service
public class DealReportServiceImpl implements DealReportService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public DealTotalReponse queryDealTotal(RequestBean req) {
        String sql = "SELECT " +
                "count( CASE WHEN notice_type = 2 THEN 1 END ) AS issueAlertTaskNum," +
                "count( CASE WHEN deal_status = 1 AND notice_type = 2 THEN 1 END ) AS isWarningTask," +
                "count( CASE WHEN deal_status = 0 AND notice_type = 2 THEN 1 END ) AS isNotWarningTask," +
                "count( CASE WHEN notice_type = 1 THEN 1 END ) AS superviseCount," +
                "count( CASE WHEN deal_status = 1 AND notice_type = 1 THEN 1 END ) AS isSuperviseNum," +
                "count( CASE WHEN deal_status = 0 AND notice_type = 1 THEN 1 END ) AS isNotSuperviseNum," +
                "count( CASE WHEN notice_type = 3 THEN 1 END ) AS assistingCount," +
                "count( CASE WHEN deal_status = 1 AND notice_type = 3 THEN 1 END ) AS isAssistingNum," +
                "count( CASE WHEN deal_status = 0 AND notice_type = 3 THEN 1 END ) AS isNotAssistingNum " +
                "FROM" +
                " supervise_task " +
                "WHERE" +
                " task_create = 'down'";
        DealTotalReponse result = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<DealTotalReponse>(DealTotalReponse.class));
        return result;
    }


    @Override
    public List<DealTypeResponse> queryDealListByType(RequestBean req, String type) {
        List<DealTypeResponse> result = new ArrayList<>();
        switch (type){
            case "supervise":
                String superviseSql = "SELECT notice_name as taskName, send_time as downTime, response_time as resTime,  IF(deal_status =1,'已处理','未处理') AS taskStatus " +
                        "FROM supervise_task WHERE notice_type = 1";
                result = jdbcTemplate.query(superviseSql,new BeanPropertyRowMapper<DealTypeResponse>(DealTypeResponse.class));
                break;
            case "warning":
                String warningSql = "SELECT notice_name as taskName, send_time as downTime, response_time as resTime,  IF(deal_status =1,'已处理','未处理') AS taskStatus " +
                        "FROM supervise_task WHERE notice_type = 2";
                result = jdbcTemplate.query(warningSql,new BeanPropertyRowMapper<DealTypeResponse>(DealTypeResponse.class));
                break;
            case "assisting":
                String assistingSql = "SELECT notice_name as taskName, send_time as downTime, response_time as resTime,  IF(deal_status =1,'已处理','未处理') AS taskStatus " +
                        "FROM supervise_task WHERE notice_type = 3";
                result = jdbcTemplate.query(assistingSql,new BeanPropertyRowMapper<DealTypeResponse>(DealTypeResponse.class));
                break;
            default:
                break;
        }
        return result;
    }
}
