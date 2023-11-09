package com.vrv.vap.alarmdeal.business.flow.processdef.dao.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.flow.processdef.dao.BusinessTaskDao;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class BusinessTaskDaoImpl implements BusinessTaskDao {
    private static Logger logger = LoggerFactory.getLogger(BusinessTaskDaoImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getCurrentTaskByUserIdAndBusinessId(String businessId) {
        String sql = "SELECT distinct task.`id` as guid  FROM business_intance instance" +
                " INNER JOIN business_task  task ON instance.`guid`=task.`instance_guid`" +
                " WHERE instance.`guid`='" + businessId+"'";
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
        if (null == queryForList || queryForList.size() == 0) {
            return "";
        }
        Map<String, Object> param = queryForList.get(0);
        return param.get("guid") == null ? "" : String.valueOf(param.get("guid"));
    }
}
