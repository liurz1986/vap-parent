package com.vrv.vap.admin.dao.impl;

import java.sql.ResultSet;
import java.util.List;

import com.vrv.vap.admin.common.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.dao.BaseKoalOrgDao;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.vo.IpRangeQuery;

@Repository
public class BaseKoalOrgDaoImpl implements BaseKoalOrgDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	@Override
	public Page<BaseKoalOrg> getOrgPageByIpRange(IpRangeQuery iprange){
		
		 
 		if(iprange.getBeginNum()==null||iprange.getBeginNum().intValue()==0) {
			iprange.setBeginNum(IPUtils.ip2int(iprange.getBeginIp()));
		}
		if(iprange.getEndNum()==null||iprange.getEndNum().intValue()==0) {
			iprange.setEndNum(IPUtils.ip2int(iprange.getEndIp()));
		}
	 
		
		
		String sql=" SELECT  {0}  FROM  base_koal_org  LEFT JOIN base_org_ip_segment ON base_koal_org.`code`=base_org_ip_segment.`area_code` " + 
				" WHERE base_org_ip_segment.id IS NOT NULL  ";
		String whereSql=" (base_org_ip_segment.`start_ip_num` >= {0} " +  " AND base_org_ip_segment.`end_ip_num`<={1}) "
						//+"or (base_org_ip_segment.`start_ip_num` <= {0} " +  "AND base_org_ip_segment.`end_ip_num`>={1})"
						+" or (base_org_ip_segment.`start_ip_num` <= {0} " +  " AND base_org_ip_segment.`end_ip_num`>={0}) "
						+" or (base_org_ip_segment.`start_ip_num` <= {1} " +  " AND base_org_ip_segment.`end_ip_num`>={1}) ";
		
		whereSql=whereSql.replaceAll("\\{0\\}",Long.toString(iprange.getBeginNum()) )
						 .replaceAll("\\{1\\}",Long.toString(iprange.getEndNum()));
		
		String sqlOrder=" order by base_koal_org.sort asc ";
		
		String sqlLimit=" limit "+iprange.getStart_()+","+iprange.getCount_();
		
		sql=sql+" and ("+whereSql+")";
		String countSql=sql.replace("{0}", " count(distinct  base_koal_org.uu_id) ");
		Long count = jdbcTemplate.queryForObject(countSql, Long.class);
		
		String pageSql=sql.replace("{0}", " distinct  base_koal_org.* ") + sqlOrder + sqlLimit;
		List<BaseKoalOrg> query = jdbcTemplate.query(pageSql, new RowMapper<BaseKoalOrg>() {
			@Override
			public BaseKoalOrg mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
				BaseKoalOrg org = new BaseKoalOrg();
				org.setCode(rs.getString("code"));
				org.setEndDate(rs.getString("end_date"));
				org.setName(rs.getString("name"));
				org.setOldCode(rs.getString("old_code"));
				org.setOldCodeEnd(rs.getString("old_code_end"));
				org.setOrghierarchy(rs.getByte("org_hierarchy"));
				org.setOtherName(rs.getString("other_name"));
				org.setParentCode(rs.getString("parent_code"));
				org.setShortName(rs.getString("short_name"));
				org.setSort(rs.getInt("sort"));
				org.setStartDate(rs.getString("start_date"));
				org.setStatus(rs.getString("status"));
				org.setType(rs.getString("type"));
				org.setUpdatetime(rs.getDate("update_time"));
				org.setUuId(rs.getInt("uu_id"));
				return org;
			}
		});
		
		Page<BaseKoalOrg> page=new Page<BaseKoalOrg>();
		page.addAll(query);
		page.setTotal(count);

		return page;
	}
}
