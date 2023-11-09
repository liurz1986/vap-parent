package com.vrv.vap.alarmdeal.business.asset.dao.impl;

import com.vrv.vap.alarmdeal.business.appsys.vo.AppServerVo;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.dao.impl.query.AssetQueryDaoImpl;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.model.TerminalAssteInstallTime;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetExportDataVO;
import com.vrv.vap.alarmdeal.business.asset.util.AssetDomainCodeUtil;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.SafeDeviceListVO;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class AssetDaoImpl implements AssetDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private  NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static Logger logger = LoggerFactory.getLogger(AssetDaoImpl.class);

	@Override
	public List<String[]> getAllTopTagsType() {
		List<String[]> results = new ArrayList<>();
		String sql = "SELECT tags tags_ , COUNT(1) count_ FROM asset where tags <> '' GROUP BY tags order by tags";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			String[] str = new String[2];
			str[0] = map.get("tags_").toString();
			str[1] = map.get("count_").toString();
			results.add(str);
		}
		return results;
	}

	@Override
	public List<Map<String, Object>> getStaticsByAssetType() {
		String sql = "SELECT typeUnicode,Type_Guid FROM asset GROUP BY asset.typeUnicode,asset.Type_Guid";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	/**
	 * 获取资产数据统计 按照资产类型（大类分组）
	 * @return
	 */
	public List<Map<String, Object>> getAssetCountByAssetType(){
		String sql=" SELECT  `asset_type_group`.`Guid`,SUM(CASE WHEN asset.`Guid` IS NOT NULL {0} and `asset_type`.`status` = 0 THEN 1 ELSE 0 END)  'count' FROM  `asset_type_group` " +
				" LEFT JOIN `asset_type` ON  `asset_type`.`TreeCode` LIKE CONCAT(`asset_type_group`.`TreeCode`,'-%') " +
				" LEFT JOIN  asset   ON asset.`Type_Guid` =`asset_type`.`Guid` "+
				" GROUP BY  `asset_type_group`.`Guid` " ;

		if(SessionUtil.getCurrentUser()!=null&& SessionUtil.getauthorityType()) {
			List<String> userDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
			if(userDomainCodes==null||userDomainCodes.size()==0) {
				sql=sql.replace("{0}", " and 1=2 ");
			}else {
				sql=sql.replace("{0}", " and asset.securityGuid in ('"+StringUtils.join(userDomainCodes, "','")+"') ");
			}
		}else {
			sql=sql.replace("{0}", "");
		}

		//实现排序


		sql=" SELECT  `asset_type_group`.`Guid` 'typeGuid',`asset_type_group`.`Name` 'typeName',IFNULL(t.count,0) 'count'  FROM `asset_type_group` LEFT JOIN ( "+
				sql+
		" ) t ON  `asset_type_group`.`Guid`=t.`Guid` "+
		" WHERE `asset_type_group`.`status`=0 "+
		" ORDER BY  `asset_type_group`.`orderNum`,`asset_type_group`.`Name`  ASC ";


        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    public List<Map<String, Object>> getAssetCountByAnyColumn(String columnName) {
        List<String> columnList = Arrays.asList("ifnull(securityGuid,'')","ifnull(`securityGuid`,'')");
        int index = columnList.indexOf(columnName);
        if (index == -1) {
            return null;
        }
        String sql = "SELECT " + columnList.get(index) + " 'key' ,COUNT(1) 'value' FROM  asset where 1=1 {0} GROUP BY  " + columnList.get(index);
        if (SessionUtil.getCurrentUser() != null && SessionUtil.getauthorityType()) {
            List<String> userDomainCodes = AssetDomainCodeUtil.getUserAuthorityDomainCodes();
            if (userDomainCodes == null || userDomainCodes.size() == 0) {
                return new LinkedList<>();
            } else {
                sql = sql.replace("{0}", " and asset.securityGuid in ('" + StringUtils.join(userDomainCodes, "','") + "') ");
            }
        } else {
            sql = sql.replace("{0}", "");
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }


    public List<Map<String, Object>> getAssetCreateCountByTime(String timeType) {

		String strWhere = " where ";
		String sql = "  as 'key',count(1) as 'value' from  asset ";
		String select = " select ";
		String groupBy = " group by ";
		switch (timeType) {
		case "year":
			strWhere = " WHERE asset.`CreateTime` >=  DATE_ADD(NOW(), INTERVAL -1 YEAR)  AND asset.`CreateTime` <NOW() ";
			select = " select date_format(`CreateTime`,'%Y%m')";
			groupBy = " group by date_format(`CreateTime`,'%Y%m')";
			break;
		case "week":
			strWhere = " WHERE asset.`CreateTime` >=  DATE_ADD(NOW(), INTERVAL -7 DAY)  AND asset.`CreateTime` <NOW() ";
			select = " select date_format(`CreateTime`,'%Y%m%d')";
			groupBy = " group by date_format(`CreateTime`,'%Y%m%d')";
			break;
		case "month":
			strWhere = " WHERE asset.`CreateTime` >=  DATE_ADD(NOW(), INTERVAL -1 MONTH)  AND asset.`CreateTime` <NOW() ";
			select = " select date_format(`CreateTime`,'%Y%m%d')";
			groupBy = " group by date_format(`CreateTime`,'%Y%m%d')";
			break;

		default:
			return null;
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(select + sql + strWhere+groupBy);
		Date now = new Date();
		Date beginTime = null;

		List<Map<String, Object>> result = new LinkedList<>();
		switch (timeType) {
		case "year":

			beginTime = DateUtils.addYears(now, -1);
			for (Date i = now; i.getTime() > beginTime.getTime(); i = DateUtils.addMonths(i, -1)) {
				// (new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date())
				Map<String, Object> map = new HashMap<>();
				map.put("key", (new java.text.SimpleDateFormat("yyyyMM")).format(i));
				map.put("value", 0);
				for (Map<String, Object> item : list) {
					if (item.get("key").equals(map.get("key"))) {
						map.put("value", item.get("value"));
						break;
					}
				}
				result.add(0, map);
			}
			return result;

		case "week":

			beginTime = DateUtils.addDays(now, -7);
			for (Date i = now; i.getTime() > beginTime.getTime(); i = DateUtils.addDays(i, -1)) {
				// (new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date())
				Map<String, Object> map = new HashMap<>();
				map.put("key", (new java.text.SimpleDateFormat("yyyyMMdd")).format(i));
				map.put("value", 0);
				for (Map<String, Object> item : list) {
					if (item.get("key").equals(map.get("key"))) {
						map.put("value", item.get("value"));
						break;
					}
				}
				result.add(0, map);
			}
			return result;

		case "month":
			beginTime = DateUtils.addMonths(now, -1);
			for (Date i = now; i.getTime() > beginTime.getTime(); i = DateUtils.addDays(i, -1)) {
				// (new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date())
				Map<String, Object> map = new HashMap<>();
				map.put("key", (new java.text.SimpleDateFormat("yyyyMMdd")).format(i));
				map.put("value", 0);
				for (Map<String, Object> item : list) {
					if (item.get("key").equals(map.get("key"))) {
						map.put("value", item.get("value"));
						break;
					}
				}
				result.add(0, map);
			}
			return result;
		}
		return list;
	}

	public List<Map<String, Object>> getAssetIpAndIds() {
		String sql="SELECT asset.`IP`   'Ip', GROUP_CONCAT( asset.`Guid`)  'Ids'  FROM asset WHERE asset.`IP` IS NOT NULL AND asset.`IP`<>''  GROUP BY asset.`IP`";

		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;

	}

	public List<Map<String, Object>> getAssetIpAndIds(String oldIp,String newIp) {

		 Map<String,Object> params = new HashMap<String,Object>();
		 params.put("oldIp", oldIp);
		 params.put("newIp", newIp);

		String sql="SELECT asset.`IP`   'Ip', GROUP_CONCAT( asset.`Guid`)  'Ids'  FROM asset WHERE asset.`IP` IS NOT NULL AND asset.`IP`<>'' "
				+ "  and (asset.`IP`= :oldIp or asset.`IP`= :newIp ) "
				+ " GROUP BY asset.`IP`";


		List<Map<String,Object>> list = namedParameterJdbcTemplate.queryForList(sql,params);
		return list;

	}


	public List<AssetExportDataVO> getAllData(List<QueryCondition> conditions)
	{
		List<String> cols = new ArrayList<>();
		Class<? extends Object> classZ = Asset.class;
		Field[] fields = classZ.getDeclaredFields();

		for (Field field : fields) {
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			Column annotation = field.getAnnotation(Column.class);
			if (annotation != null) {
				cols.add("asset." + annotation.name() + " as '" + field.getName() + "' ");
			} else {
				cols.add("asset." + field.getName());
			}
		}

		String sql = "SELECT " + StringUtils.join(cols, ",")
				+ " ,`asset_extend`.`extendInfos`  FROM `asset` LEFT JOIN `asset_extend` \r\n"
				+ "ON `asset`.`Guid`=`asset_extend`.`assetGuid` ";

		String whereStr = "";
		if (!conditions.isEmpty()) {
			List<String> ands = new ArrayList<>();
			conditions.forEach(a -> {
				ands.add(a.toString());
			});
			whereStr = " where " + StringUtils.join(ands, " and ") + " ";

			for (Field field : fields) {
				if ("serialVersionUID".equals(field.getName())) {
					continue;
				}
				Column annotation = field.getAnnotation(Column.class);
				if (annotation != null) {
					if (whereStr.contains(" " + field.getName() + " ")) {
						whereStr = whereStr.replace(" " + field.getName() + " ", " `" + annotation.name() + "` ");
					}
				}
			}

			whereStr = whereStr.replace(", employeeCode1) ", ", asset.`employee_Code1`) ");
		}
		sql=sql + whereStr;
		jdbcTemplate.setFetchSize(1000);
		//List<AssetExportDataVO> query = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetExportDataVO>(AssetExportDataVO.class));

		RowMapperResultSetExtractor<AssetExportDataVO> action = new RowMapperResultSetExtractor<AssetExportDataVO>(
				new BeanPropertyRowMapper<AssetExportDataVO>(AssetExportDataVO.class));


		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql);
		pscf.setUpdatableResults(false);
		pscf.setResultSetType(ResultSet.TYPE_FORWARD_ONLY);

		PreparedStatementCreator psc=  pscf.newPreparedStatementCreator(Collections.emptyList());

		List<AssetExportDataVO> result = jdbcTemplate.query(psc, action);


		return result;
	}


	// 统计不同资产类型下的资产数量（一级资产类型）2021- 8- 19
	public List<Map<String, Object>> queryAssetTypeNumber(){
        String sql="select tgroup.`Name` as typeName,count(*) as number" +
				" from asset as asset inner join " +
				" asset_type as type on type.Guid=asset.Type_Guid" +
				" inner join" +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode" +
				" GROUP BY tgroup.TreeCode ";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		return result;
	}

	// 统计不同部门下的资产数量
	public List<Map<String, Object>> queryDepartmentNumber() {
		String sql = "select org_name as typeName,count(*) as number from asset as asset  where org_name is not null and org_name != '' group by org_code";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		return result;
	}

	// 基础数据查询终端
	@Override
	public List<Map<String, Object>> queryAssetHostsPage(String userCode,int stratpage,int endpage) {
         String sql="select " +
				 " asset.name as assetName,asset.equipment_intensive as equipmentIntensive,asset.responsible_name as responsibleName," +
				 " asset.IP as ip,asset.mac,asset.serial_number,extend.extendInfos " +
				 " from  asset as asset " +
				 " inner join " +
				 " asset_type as type on type.Guid=asset.Type_Guid " +
				 " inner join " +
				 " asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode" +
				 " LEFT join asset_extend extend on extend.assetGuid=asset.Guid " +
				 " where tgroup.TreeCode='asset-Host' and asset.responsible_code='"+userCode+ "' order by asset.createtime desc "+
				 " limit "+stratpage+","+endpage;
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		return result;
	}

	// 基础数据查询终端总数量
	@Override
	public Long queryAssetHostsTotal(String userCode) {
		String sql="select count(asset.guid) as number " +
				" from  asset as asset " +
				" inner join " +
				" asset_type as type on type.Guid=asset.Type_Guid " +
				" inner join " +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode" +
				" where tgroup.TreeCode='asset-Host' and asset.responsible_code='"+userCode+ "'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if(null == result || result.size() == 0){
			return 0L;
		}
		return Long.valueOf(result.get("number")==null?"0":String.valueOf(result.get("number")));
	}

	// 查询usb设备存储介质（支持分页）
	@Override
	public List<Map<String, Object>> queryUSBMemorysPage(String responsibleCode, int stratpage, Integer endpage) {
		String sql = "SELECT" +
				" asset.org_name AS orgName," +
				" asset.org_code AS orgCode," +
				" asset.equipment_intensive AS equipmentIntensive," +
				" asset.responsible_name AS responsibleName," +
				" asset.responsible_code AS responsibleCode," +
				" asset.serial_number AS serialNumber," +
				" extend.extendInfos" +
				" FROM " +
				" asset AS asset" +
				" INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid " +
				" INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX(type.TreeCode, '-', 2) = tgroup.TreeCode" +
				" LEFT JOIN asset_extend extend ON extend.assetGuid = asset.Guid " +
				" WHERE " +
				" tgroup.TreeCode = 'asset-USBMemory'" +
				" AND asset.responsible_code ='" + responsibleCode + "'" +
				" ORDER BY asset.createtime DESC" +
				" limit " + stratpage + "," + endpage;
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		return result;
	}
	// 查询usb设备存储介质总记录数
	@Override
	public Long queryUSBMemorysTotale(String userCode) {
		String sql = "SELECT count(asset.Guid) as number FROM asset AS asset " +
				" INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid" +
				" INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX(type.TreeCode, '-', 2) = tgroup.TreeCode" +
				" WHERE tgroup.TreeCode = 'asset-USBMemory'  and asset.responsible_code='" + userCode + "'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0L;
		}
		return Long.valueOf(result.get("number") == null ? "0" : String.valueOf(result.get("number")));
	}

	@Override
	public Long queryUSBPeripheralsTotal(String userCode) {
		String sql = "SELECT count(asset.Guid) as number FROM asset AS asset " +
				" INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid" +
				" INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX(type.TreeCode, '-', 2) = tgroup.TreeCode" +
				" LEFT JOIN asset_extend extend ON extend.assetGuid = asset.Guid " +
				"WHERE tgroup.TreeCode = 'asset-USBPeripheral'  and asset.responsible_code='" + userCode + "'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0L;
		}
		return Long.valueOf(result.get("number") == null ? "0" : String.valueOf(result.get("number")));
	}

	@Override
	public List<Map<String, Object>> queryUSBPeripheralsPage(String responsibleCode, int stratpage, Integer endpage) {
		String sql = "SELECT" +
				" asset.name AS assetName," +
				" asset.responsible_name AS responsibleName," +
				" asset.responsible_code AS responsibleCode," +
				" asset.serial_number AS serialNumber" +
				" FROM " +
				" asset AS asset" +
				" INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid " +
				" INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX(type.TreeCode, '-', 2) = tgroup.TreeCode" +
				" LEFT JOIN asset_extend extend ON extend.assetGuid = asset.Guid " +
				" WHERE " +
				" tgroup.TreeCode ='asset-USBPeripheral'" +
				" AND asset.responsible_code ='" + responsibleCode + "'" +
				" ORDER BY asset.createtime DESC" +
				" limit " + stratpage + "," + endpage;
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		return result;
	}

	// 终端上安装安全保密产品数量
	@Override
	public Long queryAssetHostSafeNums(String responsibleCode) {
		String sql = "select count(safe.guid) as number " +
				" from  asset as asset " +
				" inner join " +
				" asset_type as type on type.Guid=asset.Type_Guid " +
				" inner join " +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode" +
				" INNER JOIN safe_secret_produce safe ON safe.asset_guid= asset.guid " +
				" where tgroup.TreeCode='asset-Host' and asset.responsible_code='" + responsibleCode + "'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0L;
		}
		return Long.valueOf(result.get("number") == null ? "0" : String.valueOf(result.get("number")));
	}

	// 获取安全保密产品安装情况--改为获取终端的安全产品详情 2022-08-23
	@Override
	public List<Map<String, Object>> querySafeProductInfo(String responsibleCode) {
		String sql = "SELECT safe.manufacturer,safe.`name`,safe.version " +
				" from  asset as asset " +
				" inner join " +
				" asset_type as type on type.Guid=asset.Type_Guid " +
				" inner join " +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode" +
				" INNER JOIN safe_secret_produce safe ON safe.asset_guid= asset.guid " +
				" where tgroup.TreeCode='asset-Host' and asset.responsible_code='" + responsibleCode + "'";

		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<Map<String, Object>> terminalAssetInstallCount(){
		String sql = "select " +
				" CASE asset.ismonitor_agent  " +
				"        WHEN '1' THEN 'isinstall' " +
				"        WHEN '2' THEN 'uninstall' " +
				" end   typeName, " +
				" count(*) as number " +
				" from  asset as asset inner join " +
				" asset_type as type on type.Guid=asset.Type_Guid " +
				" inner join " +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
				" where  tgroup.TreeCode='asset-Host' " +
				" group by " +
				" CASE asset.ismonitor_agent " +
				"   WHEN '1' THEN 'isinstall' " +
				"   WHEN '2' THEN 'uninstall' " +
				"end ";

		return jdbcTemplate.queryForList(sql);
	}

	public int terminalAssetByTypeUniqueCode(String typeUniqueCode){
         String sql="select count(type.guid) as number from asset_type as type  inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
				 "where tgroup.TreeCode='asset-Host' and type.uniqueCode='"+typeUniqueCode+"'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0;
		}
		return Integer.valueOf(result.get("number") == null ? "0" : String.valueOf(result.get("number")));
	}

	@Override
	public List<Map<String, Object>> allAssetDataValidata() {
        String sql=" select asset.`Guid`as guid,asset.`Name`as assetName,asset.IP as ip,asset.mac as mac,asset.serial_number as serialNumber,type.TreeCode AS typeTreeCode,sno.`Name` as snoName " +
				" from asset asset left join asset_type type on asset.type_guid=type.guid" +
				" left join asset_type_sno sno on sno.Guid=asset.Type_Sno_Guid";
		return jdbcTemplate.queryForList(sql);
	}

	public List<Map<String,Object>> getAllAssetTypes(){
		String sql="select type.guid,type.TreeCode as typeTreeCode,type.name as typeName,tgroup.TreeCode as groupTreeCode from asset_type type " +
				" left join asset_type_group tgroup on tgroup.TreeCode=SUBSTRING_INDEX(type.TreeCode, '-', 2)";
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public long queryWorkplatformnNum(String groupTreeCode){
		String sql="SELECT COUNT(type.guid) AS number FROM asset AS asset INNER JOIN asset_type AS type  ON asset.Type_Guid=type.Guid  " +
				" inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
				" where tgroup.TreeCode='"+groupTreeCode+"'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0;
		}
		return result.get("number")==null?0:Long.parseLong(String.valueOf(result.get("number")));
	}

	@Override
	public int queryAuhtPointByAssetTypeSno(List<String> guids){
		String sql="select (canSyslog.num+canMonitor.num) as total from " +
				" (select count(1)as num  from asset_type_sno where canSyslog='true'  and Guid in ('" + StringUtils.join(guids, "','") + "') ) as canSyslog , " +
				" (select count(1) as num from asset_type_sno where canMonitor='true' and Guid in ('" + StringUtils.join(guids, "','") + "') ) as canMonitor";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0;
		}
		return result.get("total")==null?0:Integer.parseInt(String.valueOf(result.get("total")));
	}

	/**
	 * 终端下所有二级资产类型的uniqueCode
	 * @return
	 */
	@Override
	public List<String> getTypeUnicodesIsAssetHost() {
		String sql="select type.uniqueCode from asset_type as type  inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode where tgroup.TreeCode='asset-Host'";
		List<String> uniqueCodes = jdbcTemplate.queryForList(sql,String.class);
		return uniqueCodes;
	}



	/**
	 *  根据asset_terminal_install_time表中current_install_time的值与assset表os_setup_time比较，没有变化将last_install_time的值更新为当前的系统安装时间
	 *  update(
	 * select asset.guid,asset.os_setup_time from asset_terminal_install_time term inner join asset as asset on asset.guid=term.asset_id where asset.os_setup_time = term.current_install_time  and term.current_install_time is not null)as ase,
	 * asset_terminal_install_time as te set te.last_install_time=ase.os_setup_time where ase.guid=te.asset_id
	 */
	@Override
	public void updateLastInstallTime() {
		String querysql ="select asset.guid,asset.os_setup_time from asset_terminal_install_time term inner join asset as asset on asset.guid=term.asset_id where asset.os_setup_time = term.current_install_time  and term.current_install_time is not null";
	    String updateSql=" update (" + querysql + ") as ase,asset_terminal_install_time as te set te.last_install_time=ase.os_setup_time where ase.guid=te.asset_id";
		jdbcTemplate.execute(updateSql);
	}

	/**
	 * 将资产表中
	 * 更新asset_terminal_install_time中current_install_time的值为asset中系统安装时间
	 */
	@Override
	public void updateCurrentInstallTime() {
		String sql ="update asset_terminal_install_time as term, asset as asset set term.current_install_time=asset.os_setup_time where  asset.guid=term.asset_id";
		jdbcTemplate.execute(sql);
	}

	/**
	 * 获取待新增的操作系统安装时间数据
	 * @return
	 */
	@Override
	public List<TerminalAssteInstallTime> getTerminalAssteInstallTime(){
		String sql ="select  asset.guid as guid,asset.os_setup_time as osSetupTime from  asset as asset " +
				" inner join asset_type as type on type.Guid=asset.Type_Guid inner join " +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode" +
				" where tgroup.TreeCode='asset-Host' and asset.guid not in(select asset_id from  asset_terminal_install_time)";
		List<TerminalAssteInstallTime> details = jdbcTemplate.query(sql, new TerminalAssteInstallTimeMapper());
		return details;
	}
	public class TerminalAssteInstallTimeMapper implements RowMapper<TerminalAssteInstallTime>{

		@Override
		public TerminalAssteInstallTime mapRow(ResultSet resultSet, int i) throws SQLException {
			TerminalAssteInstallTime terminalAssteInstallTime = new TerminalAssteInstallTime();
			terminalAssteInstallTime.setGuid(UUIDUtils.get32UUID());
			terminalAssteInstallTime.setCurrentInstallTime(resultSet.getTimestamp("osSetupTime"));
			terminalAssteInstallTime.setAssetGuid(resultSet.getString("guid"));
			return terminalAssteInstallTime;
		}
	}

	/**
	 * 更新LastInstallTime通过资产系统安装时间，主要为了资产修改后的情况
	 */
	@Override
	public void updateLastInstallTimeByAsset(){
		String sql ="update asset_terminal_install_time as term, asset as asset set term.last_install_time=asset.os_setup_time  where  asset.guid=term.asset_id";
		jdbcTemplate.execute(sql);
	}

	@Override
	public List<AppServerVo> getAssetServer(String[] assetGuis){
		List<String> assetguids = Arrays.asList(assetGuis);
		String querySQL="select asset.ip,asset.mac,asset.name,asset.equipment_intensive as secretLevel,asset.org_name as orgName,asset.responsible_name as responsibleName," +
				"JSON_UNQUOTE(JSON_EXTRACT(extend.extendInfos,'$.extendSystem')) AS extendSystem " +
				"from asset as asset " +
				"left join  asset_extend extend on extend.assetGuid=asset.guid " +
				"where asset.guid in('" + StringUtils.join(assetguids, "','") + "')";
		List<AppServerVo> details = jdbcTemplate.query(querySQL, new AssetServerVOMapper());
		return details;
	}


	public class AssetServerVOMapper implements RowMapper<AppServerVo> {
		@Override
		public AppServerVo mapRow(ResultSet rs, int rowNum) throws SQLException {
			AppServerVo detail = new AppServerVo();
			detail.setIp(rs.getString("ip") );
			detail.setMac(rs.getString("mac") );
			detail.setName(rs.getString("name") );
			detail.setSecretLevel(rs.getString("secretLevel") );
			detail.setOrgName(rs.getString("orgName") );
			detail.setResponsibleName(rs.getString("responsibleName") );
			detail.setExtendSystem(rs.getString("extendSystem") );
			return detail;
		}
	}


	// 数据同步：获取所有资产信息 2022-06-21
	public List<AssetQueryVO> getAllAssetSync(){
		String querySQL="select asset.`Guid`as guid,asset.`Name`as assetName,asset.IP as ip,asset.mac as mac,asset.serial_number as serialNumber,type.TreeCode AS typeTreeCode,asset.type_guid as assetType,asset.equipment_intensive as equipmentIntensive,asset.org_name as orgName,asset.org_code as orgCode,asset.responsible_name as responsibleName,asset.responsible_code as responsibleCode,asset.domain_name as domainName,asset.domain_sub_code as domainSubCode,asset.securityGuid as securityGuid,asset.CreateTime as createTime,asset.os_setup_time as osSetupTime,asset.os_list as osList,asset.data_source_type as dataSourceType,asset.sync_source as syncSource," +
				" asset.term_type as termType,asset.terminal_type as terminalType,asset.ismonitor_agent as isMonitorAgent " +
				" from asset as asset left join asset_type as type on asset.type_guid=type.guid ";
		List<AssetQueryVO> details = jdbcTemplate.query(querySQL, new AllAssetSyncMapper());
		return details;
	}


	public class AllAssetSyncMapper implements RowMapper<AssetQueryVO> {
		@Override
		public AssetQueryVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			AssetQueryVO asset = new AssetQueryVO();
			asset.setGuid(rs.getString("guid"));
			asset.setName(rs.getString("assetName"));
			asset.setIp(rs.getString("ip"));
			asset.setMac(rs.getString("mac"));
			asset.setSerialNumber(rs.getString("serialNumber"));
			asset.setTypeTreeCode(rs.getString("typeTreeCode"));
			asset.setAssetType(rs.getString("assetType"));
			asset.setEquipmentIntensive(rs.getString("equipmentIntensive"));
			asset.setOrgName(rs.getString("orgName"));
			asset.setOrgCode(rs.getString("orgCode"));
			asset.setResponsibleName(rs.getString("responsibleName") );
			asset.setResponsibleCode(rs.getString("responsibleCode"));
			asset.setDomainName(rs.getString("domainName"));
			asset.setDomainSubCode(rs.getString("domainSubCode"));
			asset.setSecurityGuid(rs.getString("securityGuid"));
			asset.setCreateTime(rs.getTimestamp("createTime"));
			asset.setOsSetuptime(rs.getTimestamp("osSetupTime"));
			asset.setOsList(rs.getString("osList"));
			asset.setDataSourceType(rs.getInt("dataSourceType"));
			asset.setSyncSource(rs.getString("syncSource"));
			asset.setTermType(rs.getString("termType"));
			asset.setTerminalType(rs.getString("terminalType"));
			asset.setIsMonitorAgent(rs.getString("isMonitorAgent"));
			return asset;
		}
	}


	/**
	 * cas大屏：根据一级资产类型获取对应资产总数
	 * 2023-037-07
	 * @param groupTypeCode
	 * @return
	 */
	@Override
	public int getAssetTotalByGroupType(String groupTypeCode) {
		String sql ="select count(*) as number " +
				" FROM  asset AS asset " +
				" INNER JOIN asset_type AS type ON type.Guid = asset.Type_Guid " +
				" INNER JOIN asset_type_group AS tgroup ON SUBSTRING_INDEX(type.TreeCode, '-', 2) = tgroup.TreeCode " +
				" where  tgroup.TreeCode = '"+groupTypeCode+"'";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		if (null == result || result.size() == 0) {
			return 0;
		}
		return Integer.valueOf(result.get("number") == null ? "0" : String.valueOf(result.get("number")));
	}
	/**
	 * 根据ip获取二级资产类型treeCode及图标
	 * @param ips
	 * @return  2023-08-04
	 */
	@Override
	public List<Map<String, Object>> getAssetTypeAndIcon(List<String> ips) {
		String sql="select asset.ip,asType.TreeCode as treeCode,asType.Icon as icon from asset left join asset_type as asType on asset.Type_Guid=asType.Guid where asset.ip in('"+StringUtils.join(ips,"','")+"')";
		logger.info("getAssetTypeAndIcon sql:"+sql);
		return jdbcTemplate.queryForList(sql);
	}
	/**
	 * 审批类型功能 202308
	 *
	 * 资产查询接口(终端、运维终端、网络设备、服务器、安全保密设备)
	 * 网络设备	NetworkDevice
	 * 服务器	service
	 * 安全保密设备	SafeDevice
	 * 终端	assetHost
	 * 运维终端	maintenHost
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getAssetMsg(String code) {
		String sql="select asset.name ,asset.ip as flag from asset inner join asset_type as type on type.Guid=asset.Type_Guid  " +
				"inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
				"where 1=1 ";
		switch (code){
			case "assetHost":
				sql= sql+" and tgroup.TreeCode='asset-Host'";
				break;
			case "maintenHost":
				sql= sql+" and tgroup.TreeCode='asset-MaintenHost'";
				break;
			case "SafeDevice":
				sql= sql+" and tgroup.TreeCode='asset-SafeDevice'";
				break;
			case "service":
				sql= sql+" and tgroup.TreeCode='asset-service'";
				break;
			case "NetworkDevice":
				sql= sql+" and tgroup.TreeCode='asset-NetworkDevice'";
				break;
		}
		return jdbcTemplate.queryForList(sql);
	}

	/**
	 * 审批类型功能 202308
	 * USB查询接口(USB存储、USB外设设备)
	 * USB存储	USBMemory
	 * USB外设设备	USBPeripheral
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getUsb(String code) {
		String sql="";
		switch (code){
			case "USBMemory":
				sql= "select asset.serial_number as name ,asset.serial_number as flag from asset inner join asset_type as type on type.Guid=asset.Type_Guid  " +
						" inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
						" where tgroup.TreeCode='asset-USBMemory'";
				return jdbcTemplate.queryForList(sql);
			case "USBPeripheral":
				sql= "select asset.Name as name ,asset.serial_number as flag from asset inner join asset_type as type on type.Guid=asset.Type_Guid  " +
						" inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
						" where tgroup.TreeCode='asset-USBPeripheral' ";
				return jdbcTemplate.queryForList(sql);
		}
		return null;
	}
	/**
	 * 终端类型获取未安装列表数据
	 *
	 * 2023-09-26
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getUnInstallList() {
		String sql="select asset.ip,asset.name,asset.responsible_name,asset.org_name " +
				" from  asset as asset inner join asset_type as type on type.Guid=asset.Type_Guid " +
				" inner join " +
				" asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode " +
				" where  tgroup.TreeCode='asset-Host' and asset.ismonitor_agent='2'";
		return jdbcTemplate.queryForList(sql);
	}

}
