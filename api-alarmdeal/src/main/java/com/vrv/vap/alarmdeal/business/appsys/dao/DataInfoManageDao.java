package com.vrv.vap.alarmdeal.business.appsys.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DataInfoManageDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 数据信息查询(文件查询)---审批类型功能
     * 查询所有文件名称、数据标识
     * @date 2023-08
     * @return
     */
    public List<Map<String, Object>> getFilesAuth() {
        String sql= "select file_name as name,data_flag as flag from data_info_manage";
        return jdbcTemplate.queryForList(sql);
    }
}
