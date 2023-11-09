package com.vrv.vap.alarmdeal.business.asset.datasync.dao;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyExportVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifySearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AssetSyncDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 数据同步：获取所有资产待审库信息 2022-06-21
    public List<AssetQueryVO> getAllAssetVerifySync(){
        String querySQL="select Guid as guid,Name as name,IP as ip, mac as mac, serial_number as serialNumber, type_guid as assetType,equipment_intensive as equipmentIntensive,org_name as orgName,org_code as orgCode,responsible_name as responsibleName,responsible_code as responsibleCode,domain_name as domainName,domain_sub_code as domainSubCode,securityGuid as securityGuid,CreateTime as createTime,data_source_type as dataSourceType,sync_source as syncSource,term_type as termType,terminal_type as terminalType,ismonitor_agent as isMonitorAgent,os_list as osList,os_setup_time as osSetupTime " +
                " from asset_verify";
        List<AssetQueryVO> details = jdbcTemplate.query(querySQL, new AllAssetSyncMapper());
        return details;
    }



    public class AllAssetSyncMapper implements RowMapper<AssetQueryVO> {
        @Override
        public AssetQueryVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetQueryVO asset = new AssetQueryVO();
            asset.setGuid(rs.getString("guid"));
            asset.setName(rs.getString("name"));
            asset.setIp(rs.getString("ip"));
            asset.setMac(rs.getString("mac"));
            asset.setSerialNumber(rs.getString("serialNumber"));
            asset.setAssetType(rs.getString("assetType"));
            asset.setEquipmentIntensive(rs.getString("equipmentIntensive"));
            asset.setOrgName(rs.getString("orgName"));
            asset.setOrgCode(rs.getString("orgCode"));
            asset.setResponsibleName(rs.getString("responsibleName") );
            asset.setResponsibleCode(rs.getString("responsibleCode"));
            asset.setDomainName(rs.getString("domainName"));
            asset.setDomainSubCode(rs.getString("domainSubCode"));
            asset.setSecurityGuid(rs.getString("securityGuid"));
            asset.setCreateTime(rs.getDate("createTime"));
            asset.setDataSourceType(rs.getInt("dataSourceType"));
            asset.setSyncSource(rs.getString("syncSource"));
            asset.setOsSetuptime(rs.getTimestamp("osSetupTime"));
            asset.setOsList(rs.getString("osList"));
            asset.setTermType(rs.getString("termType"));
            asset.setTerminalType(rs.getString("terminalType"));
            asset.setIsMonitorAgent(rs.getString("isMonitorAgent"));
            return asset;
        }
    }

    /**
     * 手动入库时，与正式库比对时资产所有字段
     * @return
     */
    public List<AssetBookDetail> getAllAssetComparison(){
        String querySQL="select Guid as guid,Name as name,IP as ip, mac as mac, serial_number as serialNumber, type_guid as typeGuid," +
                "equipment_intensive as equipmentIntensive,org_name as orgName,org_code as orgCode,responsible_name as responsibleName,responsible_code as responsibleCode," +
                "location ,remarkInfo ,assetNum,Type_Sno_Guid as typeSnoGuid,os_list as osList,os_setup_time as osSetupTime ," +
                "JSON_UNQUOTE(JSON_EXTRACT(extend.extendInfos,'$.extendDiskNumber')) AS extendDiskNumber" +
                " from asset left join  asset_extend extend on extend.assetGuid=asset.guid";
        List<AssetBookDetail> details = jdbcTemplate.query(querySQL, new AllAssetComparisonMapper());
        return details;
    }

    public class AllAssetComparisonMapper implements RowMapper<AssetBookDetail> {
        @Override
        public AssetBookDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetBookDetail asset = new AssetBookDetail();
            asset.setGuid(rs.getString("guid"));
            asset.setName(rs.getString("name"));
            asset.setIp(rs.getString("ip"));
            asset.setMac(rs.getString("mac"));
            asset.setSerialNumber(rs.getString("serialNumber"));
            asset.setTypeGuid(rs.getString("typeGuid"));
            asset.setTypeSnoGuid(rs.getString("typeSnoGuid"));
            asset.setEquipmentIntensive(rs.getString("equipmentIntensive"));
            asset.setOrgName(rs.getString("orgName"));
            asset.setOrgCode(rs.getString("orgCode"));
            asset.setResponsibleName(rs.getString("responsibleName") );
            asset.setResponsibleCode(rs.getString("responsibleCode"));
            asset.setOsSetuptime(rs.getTimestamp("osSetupTime"));
            asset.setOsList(rs.getString("osList"));
            asset.setExtendDiskNumber(rs.getString("extendDiskNumber"));
            asset.setLocation(rs.getString("location"));
            asset.setRemarkInfo(rs.getString("remarkInfo"));
            asset.setAssetNum(rs.getString("assetNum"));
            return asset;
        }
    }

    /**
     * 获取明细表中所有外部数据源
     * @return
     */
    public List<String> queryDataSources() {
        String sql="select distinct sync_source from asset_book_detail";
        List<String> dataSources = jdbcTemplate.queryForList(sql,String.class);
        return dataSources;
    }

    /**
     * 获取手动配置中统一台账导出数据
     * @param assetVerifySearch
     * @return
     */
    public List<AssetVerifyExportVO> queryExportData(AssetVerifySearchVO assetVerifySearch){
        String sql="select a.name as name,a.typeUnicode,a.IP as ip,a.mac as mac,a.serial_number as serialNumber," +
                "a.equipment_intensive as equipmentIntensive,a.location as location,a.responsible_name as responsibleName,a.org_name as orgName," +
                "a.assetNum as assetNum,a.Type_Sno_Guid as typeSnoGuid,a.os_list as osList,a.os_setup_time as osSetuptime,a.register_time as registerTime,a.remarkInfo as remarkInfo," +
                "a.device_desc as deviceDesc,a.device_arch as deviceArch" +
                ",JSON_UNQUOTE(JSON_EXTRACT(b.extendInfos,'$.extendDiskNumber')) AS extendDiskNumber from asset_verify as a left join " +
                "asset_extend_verify as b on a.Guid=b.assetGuid where 1=1 ";

        // type
        if(!StringUtils.isEmpty(assetVerifySearch.getType())){
            sql = sql+" and a.type like '%"+assetVerifySearch.getType()+"%'";
        }
        // ip
        if(!StringUtils.isEmpty(assetVerifySearch.getIp())){
            sql = sql+" and a.ip like '%"+assetVerifySearch.getIp()+"%'";
        }
        // name
        if(!StringUtils.isEmpty(assetVerifySearch.getName())){
            sql = sql+" and a.name like '%"+assetVerifySearch.getName()+"%'";
        }
        // SyncStatus
        if(assetVerifySearch.getSyncStatus() > 0){
            sql = sql+" and a.sync_status like '%"+assetVerifySearch.getSyncStatus()+"%'";
        }
        List<AssetVerifyExportVO> details = jdbcTemplate.query(sql, new AssetVerifyExportVOMapper());
        return details;
    }

    public class AssetVerifyExportVOMapper implements RowMapper<AssetVerifyExportVO> {
        @Override
        public AssetVerifyExportVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetVerifyExportVO asset = new AssetVerifyExportVO();
            asset.setName(rs.getString("name"));
            asset.setIp(rs.getString("ip"));
            asset.setMac(rs.getString("mac"));
            asset.setSerialNumber(rs.getString("serialNumber"));
            asset.setTypeUnicode(rs.getString("typeUnicode"));
            asset.setTypeSnoGuid(rs.getString("typeSnoGuid"));
            asset.setEquipmentIntensive(rs.getString("equipmentIntensive"));
            asset.setOrgName(rs.getString("orgName"));
            asset.setResponsibleName(rs.getString("responsibleName") );
            asset.setOsSetuptime(rs.getTimestamp("osSetupTime"));
            asset.setRegisterTime(rs.getTimestamp("registerTime"));
            asset.setOsList(rs.getString("osList"));
            asset.setExtendDiskNumber(rs.getString("extendDiskNumber"));
            asset.setLocation(rs.getString("location"));
            asset.setRemarkInfo(rs.getString("remarkInfo"));
            asset.setAssetNum(rs.getString("assetNum"));
            asset.setDeviceDesc(rs.getString("deviceDesc"));
            asset.setDeviceArch(rs.getString("deviceArch"));
            return asset;
        }
    }
    /**
     * 清除统一台账明细表 非当前批次数据
     * 2023 -4 -20
     * @param curBatchNo
     */
    public void deleteBookDetailBatchNo(String curBatchNo,String syncSource) {
        String sql="delete from asset_book_detail where batch_no !='"+curBatchNo+"' and sync_source = '"+syncSource+"'";
        jdbcTemplate.execute(sql);
        return ;
    }

}
