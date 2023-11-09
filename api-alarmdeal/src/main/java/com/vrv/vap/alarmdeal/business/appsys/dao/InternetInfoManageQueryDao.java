package com.vrv.vap.alarmdeal.business.appsys.dao;

import com.vrv.vap.alarmdeal.business.appsys.enums.InternetTypeNum;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.InternetInfoManageQueryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 互联单位统计列表报表接口
 *
 * @author liurz
 */
@Repository
public class InternetInfoManageQueryDao {
    private static Logger logger = LoggerFactory.getLogger(InternetInfoManageQueryDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<InternetInfoManageQueryVO> tabulation(String protectLevelParentType, String secretlevelParentType) {
        String sql ="select inter.internet_name as internetName,inter.internet_type as internetType,dict.code_value as secretLevel ,dicp.code_value as protectLevel from internet_info_manage as inter " +
                "inner join   base_dict_all as dict on inter.secret_level = dict.code " +
                "inner join  base_dict_all as dicp on inter.protect_level = dicp.code where dict.parent_type='{0}' and dicp.parent_type='{1}'";
        sql = sql.replace("{0}",secretlevelParentType).replace("{1}",protectLevelParentType);
        logger.info("互联单位统计列表sql:"+sql);
        List<InternetInfoManageQueryVO> details = jdbcTemplate.query(sql, new InternetInfoManageVOMapper());
        return details;
    }

    public class InternetInfoManageVOMapper implements RowMapper<InternetInfoManageQueryVO> {
        @Override
        public InternetInfoManageQueryVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            InternetInfoManageQueryVO detail = new InternetInfoManageQueryVO();
            detail.setId(rowNum+1);
            detail.setInternetName(rs.getString("internetName"));
            String internetType = rs.getString("internetType");
            detail.setInternetType(InternetTypeNum.getNameByCode(internetType));
            detail.setProtectLevel(rs.getString("protectLevel"));
            detail.setSecretLevel(rs.getString("secretLevel"));
            return detail;
        }
    }
}
