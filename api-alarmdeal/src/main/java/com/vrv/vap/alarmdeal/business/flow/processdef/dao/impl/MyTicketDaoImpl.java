

package com.vrv.vap.alarmdeal.business.flow.processdef.dao.impl;

import com.vrv.vap.alarmdeal.business.flow.processdef.dao.MyTicketDao;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FlowQueryVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * * 
 *
 * @author wudi   E‐mail:wudi@vrvmail.com.cn
 *          @version 创建时间：2018年10月18日 下午5:55:50  类说明
 */
@Repository
public class MyTicketDaoImpl implements MyTicketDao {

	private static Logger logger = LoggerFactory.getLogger(MyTicketDaoImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> queryRootProcessName(FlowQueryVO flowQueryVO) {
		String sql="select name from(" +
				"select name,max(order_num) as ordernum from my_ticket where 1=1 AND ticket_status!='deleted' AND ticket_status!='deleting' and ticket_status!='realDelete'" ;
		String processName = flowQueryVO.getProcessName();
		List<Map<String, Object>> queryForList =null;
		if (StringUtils.isNotEmpty(processName)) {
			sql += "and name like ? group by name) ticket ORDER BY ordernum asc";
			queryForList =jdbcTemplate.queryForList(sql, new Object[] {"%"+processName+"%"});
		}else{
			sql += "group by name) ticket ORDER BY ordernum asc";
			queryForList = jdbcTemplate.queryForList(sql);
		}
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> queryRootProcess(String processName) {
		String sql = "SELECT * FROM my_ticket  WHERE 1=1 AND ticket_status!='deleted' AND ticket_status!='deleting' and ticket_status!='realDelete'";
		List<Map<String, Object>> queryForList =null;
		if (StringUtils.isNotEmpty(processName)) {
			sql += " and name =? order BY order_num asc ";
			queryForList =jdbcTemplate.queryForList(sql, new Object[] {processName});
		}else{
			sql += " order BY order_num asc ";
			queryForList = jdbcTemplate.queryForList(sql);
		}
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> queryChildrenProcess(String processName) {
		List<Object> list = new ArrayList<>();
		String sql = "SELECT * FROM my_ticket WHERE 1=1 AND ticket_status!='deleted' and  ticket_status!='deleting' and ticket_status!='realDelete' and my_ticket.`name` = ? order BY ticket_version desc";
		list.add(processName);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}

	@Override
	public Long queryRootProcessCount() {
		String sql = "SELECT count(*) FROM my_ticket WHERE ticket_status = 'used'";
		Long count = jdbcTemplate.queryForObject(sql, Long.class);
		return count;
	}

	@Override
	public Integer getMaxOrderNum() {
		String sql = "SELECT MAX(order_num) as max_num FROM my_ticket";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
		return count;
	}

	@Override
	public List<MyTicket> queryMonitorTicket() {
		String sql = "select * from my_ticket ticket where ticket_status ='used' and name in(select name from my_ticket  where ticket_status ='used' group by  name)";
		List<MyTicket> list = jdbcTemplate.query(sql, new RowMapper<MyTicket>() {
			@Override
			public MyTicket mapRow(ResultSet rs, int rowNum) throws SQLException {
				MyTicket myTicket = new MyTicket();
				myTicket.setGuid(rs.getString("guid"));
				myTicket.setName(rs.getString("name"));
				myTicket.setTicketType(rs.getString("ticket_type"));
				return myTicket;
			}
		});
		return list;
	}

	@Override
	public Integer getMaxVersion(String processName) {
		List<Object> list = new ArrayList<>();
		String sql = "SELECT MAX(ticket_version) as max_version FROM my_ticket where 1=1 AND name = ?";
		list.add(processName);
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, list.toArray());
		return count;
	}

	@Override
	public List<Map<String, Object>> queryRootUsedProcess(String processName) {
		String sql = "SELECT * FROM my_ticket  WHERE 1=1 AND ticket_status!='deleting' and ticket_status!='realDelete' and ticket_status='used'";
		if (StringUtils.isNotEmpty(processName)) {
			sql += " and name = ? order BY order_num asc";
			List<Object> list = new ArrayList<>();
			list.add(processName);
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
			return queryForList;
		}else{
			sql += " order BY order_num asc ";
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
			return queryForList;
		}
	}

	@Override
	public List<Map<String, Object>> queryMyTaskTicket(String userId) {
		List<Object> list = new ArrayList<>();
		list.add(userId);
		String sql=" select count(*) AS count," +
				"  business_intance.process_def_name as process_def_name from business_intance where guid in(select distinct business_intance.guid " +
				"  FROM business_intance  " +
				"  INNER JOIN business_task ON business_intance.guid = business_task.instance_guid" +
				"  INNER JOIN business_task_candidate ON business_task_candidate.task_id = business_task.task_id" +
				"  INNER join my_ticket as myticket on business_intance.process_def_guid=myticket.guid" +
				"  WHERE business_task_candidate.candidate = ?)" +
				"  GROUP BY business_intance.process_def_name ";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> queryRelateTicket(String userId) {
		List<Object> list = new ArrayList<>();
		list.add("%"+userId+"%");
		String sql = "SELECT" +
				" count(*) AS count," +
				" business_intance.process_def_name as process_def_name" +
				" FROM business_intance  inner join my_ticket as myticket on business_intance.process_def_guid=myticket.guid " +
				"  WHERE business_intance.deal_peoples  LIKE ? GROUP BY business_intance.process_def_name";
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> queryRelateTicketBySec(Set<String> userIds) {
		List<Object> list = new ArrayList<>();
		for (String userId : userIds) {
			list.add("%"+userId+"%");
		}
		String baseSql = "SELECT" +
				" count(*) AS count," +
				" business_intance.process_def_name as process_def_name" +
				" FROM business_intance WHERE deal_peoples  LIKE ? GROUP BY process_def_name";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < userIds.size(); i++) {
			if(i==userIds.size()-1) {
				sb = sb.append(" "+baseSql);
			}else {
				sb = sb.append(baseSql).append(" union ");
			}
		}
		String sql = "SELECT sum(count) as count,process_def_name FROM "+"("+sb.toString()+")"+" as TEST GROUP BY process_def_name ";
		logger.info("区域查询树sql：{}", sql);
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql, list.toArray());
		return queryForList;
	}

	@Override
	public List<Map<String, Object>> queryMonitorExistTicket() {
       String sql = "SELECT" +
		       		" count(*) AS count," +
		       		" business_intance.process_def_name as process_def_name" +
		       		" FROM business_intance GROUP BY process_def_name";
       List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
		return queryForList;
	}

	@Override
	public void updateNameMyTicket(String oldName, String newName) {
		String sql= "update my_ticket set name=?  where name=? ";
		jdbcTemplate.update(sql, new Object[] {newName, oldName});
		return;
	}
	@Override
	public String getTicktType(String processDefName){
		String sql= "select  ticket_type from my_ticket where name='"+processDefName+"' and ticket_type is not null ORDER BY create_time desc  ";
		List<String> ticketType = jdbcTemplate.queryForList(sql,String.class);
		if(CollectionUtils.isEmpty(ticketType)){
			return "0";
		}
		return ticketType.get(0);
	}

}
