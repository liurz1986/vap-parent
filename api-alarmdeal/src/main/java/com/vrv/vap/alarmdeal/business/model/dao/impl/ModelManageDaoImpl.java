package com.vrv.vap.alarmdeal.business.model.dao.impl;
import com.vrv.vap.alarmdeal.business.model.dao.ModelManageDao;
import com.vrv.vap.alarmdeal.business.model.vo.ModelVersionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class ModelManageDaoImpl implements ModelManageDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void deleteByGuids(List<String> guids) {
        String sql ="update model_manage set is_delete=-1 where guid in ('" + StringUtils.join(guids, "','") + "')";
        jdbcTemplate.execute(sql);
    }

    @Override
    public List<ModelVersionVO> queryModelVersions(String modelId) {
        String sql="select guid,version,model_id as modelId,model_name as modelName,version_desc as versionDesc ," +
                "model_version_create_time as modelVersionCreateTime from model_manage where model_id='"+modelId+"' order by create_time desc";
        List<ModelVersionVO> list = jdbcTemplate.query(sql, new ModelVersionMapper());
        return list;
    }

    @Override
    public boolean existModelName(String modelName) {
        String sql="select count(*) from model_manage where model_name='"+modelName+"'";
        long count = jdbcTemplate.queryForObject(sql, Long.class);
        if(count > 0){
            return true;
        }
        return false;
    }

    public class ModelVersionMapper implements RowMapper<ModelVersionVO> {
        @Override
        public ModelVersionVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ModelVersionVO modelVersion = new ModelVersionVO();
            modelVersion.setGuid(rs.getString("guid"));
            modelVersion.setVersion(rs.getString("version"));
            modelVersion.setModelId(rs.getString("modelId"));
            modelVersion.setModelName(rs.getString("modelName"));
            modelVersion.setVersionDesc(rs.getString("versionDesc"));
            modelVersion.setModelVersionCreateTime(rs.getDate("modelVersionCreateTime"));
            return modelVersion;
        }
    }
}
