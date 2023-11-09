package com.vrv.vap.alarmdeal.business.asset.dao.impl;


import com.vrv.vap.alarmdeal.business.asset.dao.BaseKoalOrgDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BaseKoalOrgDaoImpl implements BaseKoalOrgDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取下级单位及应用
     */
    public List<Map<String,Object>> organizationByParentCode(String code){
        String sql = "select b.domain_name,b.code AS orgCode,s.`name`,s.system_id AS code FROM base_security_domain b LEFT JOIN base_sysinfo s ON b.code=s.security_domain_code WHERE b.parent_code='"+code+"'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;

    }

    /**
     * 获取级单位及应用
     */
    public List<Map<String,Object>> organizationByCode(String code){
        String sql = "select b.domain_name,b.code AS orgCode,s.`name`,s.system_id AS code FROM base_security_domain b LEFT JOIN base_sysinfo s ON b.code=s.security_domain_code WHERE b.code='"+code+"'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;

    }
}
