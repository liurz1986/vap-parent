package com.vrv.vap.alarmdeal.business.appsys.dao;

import com.vrv.vap.alarmdeal.business.asset.dao.impl.query.AssetQueryDaoImpl;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 网路信息报表接口
 *
 * @author liurz
 */
@Repository
public class NetInfoManageQueryDao {
    private static Logger logger = LoggerFactory.getLogger(NetInfoManageQueryDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 网络类型划分(局域网、广域网)统计
     * @return
     */
    public List<AssetStatisticsVO> netInfoType() {
        String sql = "select  " +
                "CASE net_type  " +
                "        WHEN 'lan' THEN '局域网' " +
                "        WHEN 'wan' THEN '广域网' " +
                "end as typeName," +
                "count(id) as number " +
                "from net_info_manage " +
                "GROUP BY " +
                "CASE net_type  " +
                "       WHEN 'lan' THEN '局域网' " +
                "       WHEN 'wan' THEN '广域网' "+
                "end";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 网络密级统计
     * 2023-07-04
     * *@return
     */
    public List<AssetStatisticsVO> secretlevelTotal(String secretlevelParentType) {
        String sql ="select dict.code_value as typeName,count(net.id) as number from net_info_manage as net inner join base_dict_all as dict on net.secret_level = dict.code" +
                " where dict.parent_type='" +secretlevelParentType+ "' group by dict.code_value";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 网络防护等级统计
     * 2023-07-04
     * *@return
     */
    public List<AssetStatisticsVO> protectLevelTotal(String protectLevelParentType) {
        String sql ="select dict.code_value as typeName,count(net.id) as number from net_info_manage as net inner join base_dict_all as dict on net.protect_level = dict.code" +
                " where dict.parent_type='" +protectLevelParentType+ "' group by dict.code_value";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }
    /**
     * 网络按安全域统计
     * @return
     */
    public List<AssetStatisticsVO> domainTotal() {
        String sql ="select domain as typeName ,count(*) as number from net_info_manage where domain is not null and domain !='' group by domain";
        List<AssetStatisticsVO> details = jdbcTemplate.query(sql, new AssetStatisticsVOMapper());
        return details;
    }

    public class AssetStatisticsVOMapper implements RowMapper<AssetStatisticsVO> {
        @Override
        public AssetStatisticsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetStatisticsVO detail = new AssetStatisticsVO();
            detail.setName(rs.getString("typeName") );
            detail.setCount(rs.getString("number")==null?0:Integer.parseInt(rs.getString("number")));
            return detail;
        }
    }
}
